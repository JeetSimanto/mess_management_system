package com.messmanager.app.ui.contribution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.data.repository.AuthRepository
import com.messmanager.app.data.repository.ContributionRepository
import com.messmanager.app.data.repository.DashboardRepository
import com.messmanager.app.data.repository.MessRepository
import com.messmanager.app.domain.model.Contribution
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.Mess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContributionUiState(
    val isLoading: Boolean = true,
    val activeMess: Mess? = null,
    val isManager: Boolean = false,
    val members: List<Member> = emptyList(),
    val contributions: List<Contribution> = emptyList(),
    val totalContributionPaisa: Long = 0,
    val error: String? = null
)

@HiltViewModel
class ContributionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messRepository: MessRepository,
    private val contributionRepository: ContributionRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContributionUiState())
    val uiState: StateFlow<ContributionUiState> = _uiState.asStateFlow()

    private var messJob: Job? = null
    private var contributionJob: Job? = null

    init {
        loadContributions()
    }

    private fun loadContributions() {
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
                        contributions = emptyList(),
                        totalContributionPaisa = 0
                    )
                }
            }
        }
    }

    private fun cancelJobs() {
        messJob?.cancel()
        contributionJob?.cancel()
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

                observeContributionEntries(mess)
            }
        }
    }

    private fun observeContributionEntries(mess: Mess) {
        contributionJob?.cancel()
        contributionJob = viewModelScope.launch {
            contributionRepository.observeContributions(mess.id, mess.month, mess.year).collect { list ->
                val totalPaisa = list.sumOf { it.amountPaisa }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    contributions = list,
                    totalContributionPaisa = totalPaisa
                )
            }
        }
    }

    fun addContribution(memberUid: String, memberName: String, amountPaisa: Long, date: String, purpose: String) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val contribution = Contribution(
                memberUid = memberUid,
                memberName = memberName,
                amountPaisa = amountPaisa,
                date = date,
                purpose = purpose,
                month = mess.month,
                year = mess.year
            )
            val result = contributionRepository.addContribution(mess.id, contribution)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to add deposit")
            }
        }
    }

    fun updateContribution(contribution: Contribution) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = contributionRepository.updateContribution(mess.id, contribution)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to update deposit")
            }
        }
    }

    fun deleteContribution(contributionId: String) {
        val mess = _uiState.value.activeMess ?: return
        viewModelScope.launch {
            val result = contributionRepository.deleteContribution(mess.id, contributionId)
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.localizedMessage ?: "Failed to delete deposit")
            }
        }
    }
}
