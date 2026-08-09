package com.messmanager.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.data.repository.AuthRepository
import com.messmanager.app.data.repository.DashboardRepository
import com.messmanager.app.data.repository.GroceryRepository
import com.messmanager.app.data.repository.MessRepository
import com.messmanager.app.domain.model.Grocery
import com.messmanager.app.domain.model.Mess
import com.messmanager.app.domain.model.MessRole
import com.messmanager.app.domain.model.MessSettlement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val currentUserId: String = "",
    val activeMess: Mess? = null,
    val isManager: Boolean = false,
    val settlement: MessSettlement? = null,
    val recentGroceries: List<Grocery> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messRepository: MessRepository,
    private val dashboardRepository: DashboardRepository,
    private val groceryRepository: GroceryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val currentUid = authRepository.currentUserId ?: return
        _uiState.value = _uiState.value.copy(currentUserId = currentUid)

        viewModelScope.launch {
            authRepository.observeCurrentUser().collect { user ->
                val activeMessId = user?.activeMessId
                if (activeMessId != null) {
                    observeMessData(activeMessId, currentUid)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    private fun observeMessData(messId: String, currentUid: String) {
        viewModelScope.launch {
            messRepository.observeMess(messId).collect { mess ->
                if (mess == null) return@collect
                val isManager = mess.managerId == currentUid
                _uiState.value = _uiState.value.copy(
                    activeMess = mess,
                    isManager = isManager
                )

                // Observe real-time settlement calculations
                observeSettlement(mess)

                // Observe recent groceries for Member view
                observeRecentGroceries(mess)
            }
        }
    }

    private fun observeSettlement(mess: Mess) {
        viewModelScope.launch {
            dashboardRepository.observeMessSettlement(mess).collect { settlement ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    settlement = settlement
                )
            }
        }
    }

    private fun observeRecentGroceries(mess: Mess) {
        viewModelScope.launch {
            groceryRepository.observeGroceries(mess.id, mess.month, mess.year).collect { list ->
                _uiState.value = _uiState.value.copy(
                    recentGroceries = list.take(5) // Bottom section recent grocery
                )
            }
        }
    }
}
