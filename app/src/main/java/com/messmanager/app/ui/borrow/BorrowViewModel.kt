package com.messmanager.app.ui.borrow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.data.repository.AuthRepository
import com.messmanager.app.data.repository.BorrowRepository
import com.messmanager.app.data.repository.DashboardRepository
import com.messmanager.app.data.repository.MessRepository
import com.messmanager.app.domain.model.BorrowRequest
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.Mess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BorrowUiState(
    val isLoading: Boolean = true,
    val currentUserId: String = "",
    val activeMess: Mess? = null,
    val isManager: Boolean = false,
    val members: List<Member> = emptyList(),
    val borrowRequests: List<BorrowRequest> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class BorrowViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messRepository: MessRepository,
    private val borrowRepository: BorrowRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BorrowUiState())
    val uiState: StateFlow<BorrowUiState> = _uiState.asStateFlow()

    init {
        loadBorrows()
    }

    private fun loadBorrows() {
        val currentUid = authRepository.currentUserId ?: return
        _uiState.value = _uiState.value.copy(currentUserId = currentUid)

        viewModelScope.launch {
            authRepository.observeCurrentUser().collect { user ->
                val messId = user?.activeMessId ?: return@collect
                messRepository.observeMess(messId).collect { mess ->
                    if (mess == null) return@collect
                    val isManager = mess.managerId == currentUid
                    val members = dashboardRepository.getMessMembers(mess)

                    _uiState.value = _uiState.value.copy(
                        activeMess = mess,
                        isManager = isManager,
                        members = members
                    )

                    observeBorrowRequests(mess.id)
                }
            }
        }
    }

    private fun observeBorrowRequests(messId: String) {
        viewModelScope.launch {
            borrowRepository.observeBorrows(messId).collect { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    borrowRequests = list
                )
            }
        }
    }

    fun sendBorrowRequest(itemName: String, quantity: String, date: String) {
        val mess = _uiState.value.activeMess ?: return
        val user = _uiState.value.members.find { it.uid == _uiState.value.currentUserId } ?: return

        viewModelScope.launch {
            val request = BorrowRequest(
                requesterUid = user.uid,
                requesterName = user.displayName,
                itemName = itemName,
                quantity = quantity,
                date = date
            )
            val result = borrowRepository.requestBorrow(mess.id, request)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to send borrow request")
            }
        }
    }

    fun resolveBorrow(borrowId: String, accept: Boolean, dueDate: String = "") {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = borrowRepository.resolveBorrow(mess.id, borrowId, accept, dueDate)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to resolve borrow request")
            }
        }
    }

    fun markReturned(borrowId: String) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = borrowRepository.markReturned(mess.id, borrowId)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to mark item returned")
            }
        }
    }
}
