package com.clinic.app.presentation.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clinic.domain.model.AppointmentStatus
import com.clinic.domain.model.EnrichedAppointment
import com.clinic.domain.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState: StateFlow<AppointmentsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        // مراقبة جميع المواعيد
        viewModelScope.launch {
            combine(
                appointmentRepository.getAllAppointments(),
                _searchQuery
            ) { allAppointments, query ->
                if (query.isBlank()) {
                    allAppointments
                } else {
                    allAppointments.filter { it.matchesSearch(query) }
                }
            }.catch { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "حدث خطأ أثناء تحميل المواعيد",
                    isLoading = false
                )
            }.collect { appointments ->
                _uiState.value = _uiState.value.copy(
                    appointments = appointments,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun searchAppointments(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            _uiState.value = _uiState.value.copy(isLoading = true)
        }
    }

    fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus) {
        viewModelScope.launch {
            val currentAppointment = _uiState.value.appointments
                .find { it.appointment.id == appointmentId }
                ?.appointment

            currentAppointment?.let { appointment ->
                val updatedAppointment = appointment.copy(status = newStatus)
                appointmentRepository.updateAppointment(updatedAppointment)
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "فشل في تحديث حالة الموعد"
                        )
                    }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AppointmentsUiState(
    val appointments: List<EnrichedAppointment> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

