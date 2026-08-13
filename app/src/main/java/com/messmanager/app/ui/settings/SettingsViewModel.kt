package com.messmanager.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.BuildConfig
import com.messmanager.app.data.repository.AuthRepository
import com.messmanager.app.data.repository.DashboardRepository
import com.messmanager.app.data.repository.MessRepository
import com.messmanager.app.data.repository.UpdateInfo
import com.messmanager.app.data.repository.UpdateRepository
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.Mess
import com.messmanager.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val isCheckingUpdate: Boolean = false,
    val user: User? = null,
    val userMesses: List<Mess> = emptyList(),
    val activeMess: Mess? = null,
    val isManager: Boolean = false,
    val members: List<Member> = emptyList(),
    val updateInfo: UpdateInfo? = null,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messRepository: MessRepository,
    private val dashboardRepository: DashboardRepository,
    private val updateRepository: UpdateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var activeMessJob: Job? = null
    private var userMessesJob: Job? = null

    init {
        loadSettingsData()
    }

    private fun loadSettingsData() {
        val currentUid = authRepository.currentUserId ?: return

        viewModelScope.launch {
            authRepository.observeCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(user = user)
                if (user != null) {
                    observeUserMesses(user.messIds)
                    val activeMessId = user.activeMessId
                    if (activeMessId != null) {
                        observeMess(activeMessId, currentUid)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            activeMess = null,
                            isManager = false,
                            members = emptyList()
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    private fun observeUserMesses(messIds: List<String>) {
        userMessesJob?.cancel()
        userMessesJob = viewModelScope.launch {
            messRepository.observeUserMesses(messIds).collect { list ->
                _uiState.value = _uiState.value.copy(userMesses = list)
            }
        }
    }

    private fun observeMess(messId: String, currentUid: String) {
        activeMessJob?.cancel()
        activeMessJob = viewModelScope.launch {
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

    fun checkForUpdates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingUpdate = true, error = null)
            val info = updateRepository.checkForUpdates()
            _uiState.value = _uiState.value.copy(
                isCheckingUpdate = false,
                updateInfo = if (info.hasUpdate) info else null
            )
            if (!info.hasUpdate) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "You are on the latest version (v${BuildConfig.VERSION_NAME})!"
                )
            }
        }
    }

    fun dismissUpdateDialog() {
        _uiState.value = _uiState.value.copy(updateInfo = null)
    }

    fun switchActiveMess(messId: String) {
        if (_uiState.value.activeMess?.id == messId) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, error = null)
            val result = authRepository.updateActiveMess(messId)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    successMessage = "Switched active mess."
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    error = e.localizedMessage ?: "Failed to switch mess"
                )
            }
        }
    }

    fun createMess(name: String, month: Int = LocalDate.now().monthValue, year: Int = LocalDate.now().year) {
        val currentUid = authRepository.currentUserId ?: run {
            _uiState.value = _uiState.value.copy(error = "User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, error = null)
            val result = messRepository.createMess(name = name, month = month, year = year, userId = currentUid)
            result.onSuccess { newMess ->
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    successMessage = "Created new mess '${newMess.name}'!"
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    error = e.localizedMessage ?: "Failed to create mess"
                )
            }
        }
    }

    fun joinMess(inviteCode: String) {
        val currentUid = authRepository.currentUserId ?: run {
            _uiState.value = _uiState.value.copy(error = "User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, error = null)
            val result = messRepository.joinMess(inviteCode = inviteCode, userId = currentUid)
            result.onSuccess { joinedMess ->
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    successMessage = "Joined mess '${joinedMess.name}'!"
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    error = e.localizedMessage ?: "Failed to join mess"
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
                val remainingMesses = _uiState.value.userMesses.filter { it.id != mess.id }
                authRepository.updateActiveMess(remainingMesses.firstOrNull()?.id)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to leave mess")
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
