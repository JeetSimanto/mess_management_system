package com.messmanager.app.ui.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.data.repository.AuthRepository
import com.messmanager.app.data.repository.DashboardRepository
import com.messmanager.app.data.repository.MealRepository
import com.messmanager.app.data.repository.MessRepository
import com.messmanager.app.domain.model.Meal
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.Mess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class MealUiState(
    val isLoading: Boolean = true,
    val currentUserId: String = "",
    val activeMess: Mess? = null,
    val isManager: Boolean = false,
    val members: List<Member> = emptyList(),
    val meals: List<Meal> = emptyList(),
    val totalMeals: Double = 0.0,
    val selectedDate: LocalDate = LocalDate.now(),
    val error: String? = null
)

@HiltViewModel
class MealViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messRepository: MessRepository,
    private val mealRepository: MealRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealUiState())
    val uiState: StateFlow<MealUiState> = _uiState.asStateFlow()

    private var messJob: Job? = null
    private var mealJob: Job? = null

    init {
        loadMeals()
    }

    private fun loadMeals() {
        val currentUid = authRepository.currentUserId ?: return
        _uiState.value = _uiState.value.copy(currentUserId = currentUid)

        viewModelScope.launch {
            authRepository.observeCurrentUser().collect { user ->
                val messId = user?.activeMessId
                if (messId != null) {
                    observeMess(messId, currentUid)
                } else {
                    cancelJobs()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeMess = null,
                        meals = emptyList(),
                        totalMeals = 0.0
                    )
                }
            }
        }
    }

    private fun cancelJobs() {
        messJob?.cancel()
        mealJob?.cancel()
    }

    private fun observeMess(messId: String, currentUid: String) {
        cancelJobs()
        messJob = viewModelScope.launch {
            messRepository.observeMess(messId).collect { mess ->
                if (mess == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, activeMess = null)
                    return@collect
                }
                val isManager = mess.managerId == currentUid
                val members = dashboardRepository.getMessMembers(mess)

                _uiState.value = _uiState.value.copy(
                    activeMess = mess,
                    isManager = isManager,
                    members = members
                )

                observeMealEntries(mess)
            }
        }
    }

    private fun observeMealEntries(mess: Mess) {
        mealJob?.cancel()
        mealJob = viewModelScope.launch {
            mealRepository.observeMeals(mess.id, mess.month, mess.year).collect { list ->
                val totalCount = list.sumOf { it.count }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    meals = list,
                    totalMeals = totalCount
                )
            }
        }
    }

    fun setMealCount(memberUid: String, memberName: String, dateIso: String, count: Double) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val meal = Meal(
                memberUid = memberUid,
                memberName = memberName,
                date = dateIso,
                count = count,
                month = mess.month,
                year = mess.year
            )
            val result = mealRepository.setMeal(mess.id, meal)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to update meal")
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }
}
