package com.messmanager.app.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.messmanager.app.data.repository.AuthRepository
import com.messmanager.app.data.repository.MessRepository
import com.messmanager.app.domain.model.Mess
import com.messmanager.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val activeMess: Mess? = null,
    val error: String? = null,
    val isMessCreatedOrJoined: Boolean = false,
    val isAuthInitializing: Boolean = true
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messRepository: MessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeCurrentUser()
    }

    private var activeMessJob: Job? = null

    private fun observeCurrentUser() {
        viewModelScope.launch {
            authRepository.observeCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(user = user)
                var activeMessId = user?.activeMessId

                if (activeMessId == null && !user?.messIds.isNullOrEmpty()) {
                    val fallbackMessId = user!!.messIds.first()
                    authRepository.updateActiveMess(fallbackMessId)
                    activeMessId = fallbackMessId
                }

                if (activeMessId != null) {
                    observeActiveMess(activeMessId)
                } else {
                    activeMessJob?.cancel()
                    _uiState.value = _uiState.value.copy(
                        activeMess = null,
                        isMessCreatedOrJoined = false,
                        isAuthInitializing = false
                    )
                }
                if (user == null) {
                    _uiState.value = _uiState.value.copy(isAuthInitializing = false)
                }
            }
        }
    }

    private fun observeActiveMess(messId: String) {
        activeMessJob?.cancel()
        activeMessJob = viewModelScope.launch {
            messRepository.observeMess(messId).collect { mess ->
                if (mess == null) {
                    val user = _uiState.value.user
                    val remainingMessIds = user?.messIds?.filter { it != messId } ?: emptyList()
                    if (remainingMessIds.isNotEmpty()) {
                        val nextMessId = remainingMessIds.first()
                        authRepository.updateActiveMess(nextMessId)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            activeMess = null,
                            isMessCreatedOrJoined = false,
                            isAuthInitializing = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        activeMess = mess,
                        isMessCreatedOrJoined = true,
                        isAuthInitializing = false
                    )
                }
            }
        }
    }

    fun handleGoogleSignIn(credential: AuthCredential) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signInWithCredential(credential)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(isLoading = false, user = user)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage ?: "Sign in failed")
            }
        }
    }

    fun createMess(name: String, month: Int = LocalDate.now().monthValue, year: Int = LocalDate.now().year) {
        val userId = authRepository.currentUserId ?: run {
            _uiState.value = _uiState.value.copy(error = "User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = messRepository.createMess(name = name, month = month, year = year, userId = userId)
            result.onSuccess { mess ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activeMess = mess,
                    isMessCreatedOrJoined = true
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage ?: "Failed to create mess")
            }
        }
    }

    fun joinMess(inviteCode: String) {
        val userId = authRepository.currentUserId ?: run {
            _uiState.value = _uiState.value.copy(error = "User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = messRepository.joinMess(inviteCode = inviteCode, userId = userId)
            result.onSuccess { mess ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activeMess = mess,
                    isMessCreatedOrJoined = true
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage ?: "Failed to join mess")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState(isAuthInitializing = false)
    }
}
