package com.messmanager.app.ui.utility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.data.repository.AuthRepository
import com.messmanager.app.data.repository.MessRepository
import com.messmanager.app.data.repository.UtilityRepository
import com.messmanager.app.domain.model.Mess
import com.messmanager.app.domain.model.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UtilityUiState(
    val isLoading: Boolean = true,
    val activeMess: Mess? = null,
    val isManager: Boolean = false,
    val utilities: List<Utility> = emptyList(),
    val totalUtilityPaisa: Long = 0,
    val error: String? = null
)

@HiltViewModel
class UtilityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messRepository: MessRepository,
    private val utilityRepository: UtilityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UtilityUiState())
    val uiState: StateFlow<UtilityUiState> = _uiState.asStateFlow()

    init {
        loadUtilities()
    }

    private fun loadUtilities() {
        val currentUid = authRepository.currentUserId ?: return

        viewModelScope.launch {
            authRepository.observeCurrentUser().collect { user ->
                val messId = user?.activeMessId ?: return@collect
                messRepository.observeMess(messId).collect { mess ->
                    if (mess == null) return@collect
                    val isManager = mess.managerId == currentUid
                    _uiState.value = _uiState.value.copy(
                        activeMess = mess,
                        isManager = isManager
                    )

                    observeUtilityEntries(mess)
                }
            }
        }
    }

    private fun observeUtilityEntries(mess: Mess) {
        viewModelScope.launch {
            utilityRepository.observeUtilities(mess.id, mess.month, mess.year).collect { list ->
                val totalPaisa = list.sumOf { it.costPaisa }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    utilities = list,
                    totalUtilityPaisa = totalPaisa
                )
            }
        }
    }

    fun addUtility(title: String, category: String, costPaisa: Long, date: String) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val utility = Utility(
                title = title,
                category = category,
                costPaisa = costPaisa,
                date = date,
                month = mess.month,
                year = mess.year
            )
            val result = utilityRepository.addUtility(mess.id, utility)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to add utility")
            }
        }
    }

    fun updateUtility(utility: Utility) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = utilityRepository.updateUtility(mess.id, utility)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to update utility")
            }
        }
    }

    fun deleteUtility(utilityId: String) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = utilityRepository.deleteUtility(mess.id, utilityId)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to delete utility")
            }
        }
    }
}
