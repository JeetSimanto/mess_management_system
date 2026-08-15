package com.messmanager.app.ui.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.data.repository.AuthRepository
import com.messmanager.app.data.repository.DashboardRepository
import com.messmanager.app.data.repository.GroceryRepository
import com.messmanager.app.data.repository.MessRepository
import com.messmanager.app.domain.model.Grocery
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.Mess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroceryUiState(
    val isLoading: Boolean = true,
    val activeMess: Mess? = null,
    val isManager: Boolean = false,
    val members: List<Member> = emptyList(),
    val groceries: List<Grocery> = emptyList(),
    val totalGroceryPaisa: Long = 0,
    val error: String? = null
)

@HiltViewModel
class GroceryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messRepository: MessRepository,
    private val groceryRepository: GroceryRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroceryUiState())
    val uiState: StateFlow<GroceryUiState> = _uiState.asStateFlow()

    private var messJob: Job? = null
    private var groceryJob: Job? = null

    init {
        loadGroceries()
    }

    private fun loadGroceries() {
        val currentUid = authRepository.currentUserId ?: return

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
                        groceries = emptyList(),
                        totalGroceryPaisa = 0
                    )
                }
            }
        }
    }

    private fun cancelJobs() {
        messJob?.cancel()
        groceryJob?.cancel()
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

                observeGroceryEntries(mess)
            }
        }
    }

    private fun observeGroceryEntries(mess: Mess) {
        groceryJob?.cancel()
        groceryJob = viewModelScope.launch {
            groceryRepository.observeGroceries(mess.id, mess.month, mess.year).collect { list ->
                val totalPaisa = list.sumOf { it.costPaisa }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    groceries = list,
                    totalGroceryPaisa = totalPaisa
                )
            }
        }
    }

    fun addGrocery(
        itemName: String,
        quantity: Double,
        unit: String,
        costPaisa: Long,
        buyerUid: String = "",
        buyerName: String = "",
        date: String = "",
        note: String = ""
    ) {
        val mess = _uiState.value.activeMess ?: return
        val currentUid = authRepository.currentUserId ?: ""
        val currentMember = _uiState.value.members.find { it.uid == currentUid }

        val finalBuyerUid = if (buyerUid.isNotBlank()) buyerUid else currentUid
        val finalBuyerName = if (buyerName.isNotBlank()) buyerName else (currentMember?.displayName ?: "Manager")
        val finalDate = if (date.isNotBlank()) date else com.messmanager.app.util.DateUtils.todayIso()

        viewModelScope.launch {
            val grocery = Grocery(
                itemName = itemName,
                quantity = quantity,
                unit = unit,
                costPaisa = costPaisa,
                buyerUid = finalBuyerUid,
                buyerName = finalBuyerName,
                date = finalDate,
                note = note,
                month = mess.month,
                year = mess.year
            )
            val result = groceryRepository.addGrocery(mess.id, grocery)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to add grocery")
            }
        }
    }

    fun updateGrocery(grocery: Grocery) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = groceryRepository.updateGrocery(mess.id, grocery)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to update grocery")
            }
        }
    }

    fun deleteGrocery(groceryId: String) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = groceryRepository.deleteGrocery(mess.id, groceryId)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to delete grocery")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
