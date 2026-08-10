package com.messmanager.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.data.repository.AuthRepository
import com.messmanager.app.data.repository.DashboardRepository
import com.messmanager.app.data.repository.MessRepository
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.Mess
import com.messmanager.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val activeMess: Mess? = null,
    val isManager: Boolean = false,
    val members: List<Member> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messRepository: MessRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettingsData()
    }

    private fun loadSettingsData() {
        val currentUid = authRepository.currentUserId ?: return

        viewModelScope.launch {
            authRepository.observeCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(user = user)
                val activeMessId = user?.activeMessId
                if (activeMessId != null) {
                    observeMess(activeMessId, currentUid)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    private fun observeMess(messId: String, currentUid: String) {
        viewModelScope.launch {
            messRepository.observeMess(messId).collect { mess ->
                if (mess == null) return@collect
                val isManager = mess.managerId == currentUid
                val members = dashboardRepository.getMessMembers(mess)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activeMess = mess,
                    isManager = isManager,
                    members = members
                )
            }
        }
    }

    fun transferManager(newManagerId: String) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = messRepository.transferManager(mess.id, newManagerId)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(successMessage = "Manager role transferred successfully.")
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to transfer manager role")
            }
        }
    }

    fun removeMember(memberId: String) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = messRepository.removeMember(mess.id, memberId)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(successMessage = "Member removed.")
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to remove member")
            }
        }
    }

    fun leaveMess() {
        val mess = _uiState.value.activeMess ?: return
        val currentUid = authRepository.currentUserId ?: return

        viewModelScope.launch {
            val result = messRepository.leaveMess(mess.id, currentUid)
            result.onSuccess {
                authRepository.updateActiveMess(null)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to leave mess")
            }
        }
    }

    fun updateFixedMealCount(count: Double) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = messRepository.updateFixedMealCount(mess.id, count)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(successMessage = "Fixed meal count updated.")
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to update fixed meal count")
            }
        }
    }

    fun confirmSettlement() {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = messRepository.confirmSettlement(mess.id)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(successMessage = "Settlement confirmed. Mess activities closed.")
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to confirm settlement")
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
