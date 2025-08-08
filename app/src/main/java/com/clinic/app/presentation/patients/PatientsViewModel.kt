package com.clinic.app.presentation.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clinic.domain.model.Patient
import com.clinic.domain.repository.PatientRepository
import com.clinic.domain.usecase.SearchPatientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientsViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val searchPatientsUseCase: SearchPatientsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientsUiState())
    val uiState: StateFlow<PatientsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        // مراقبة جميع المرضى
        viewModelScope.launch {
            combine(
                patientRepository.getAllPatients(),
                _searchQuery
            ) { allPatients, query ->
                if (query.isBlank()) {
                    allPatients
                } else {
                    allPatients.filter { it.matchesSearch(query) }
                }
            }.catch { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "حدث خطأ أثناء تحميل المرضى",
                    isLoading = false
                )
            }.collect { patients ->
                _uiState.value = _uiState.value.copy(
                    patients = patients,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun searchPatients(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            _uiState.value = _uiState.value.copy(isLoading = true)
        }
    }

    fun addPatient(patient: Patient) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            patientRepository.addPatient(patient)
                .onSuccess {
                    // سيتم تحديث القائمة تلقائياً من خلال Flow
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "فشل في إضافة المريض",
                        isLoading = false
                    )
                }
        }
    }

    fun updatePatient(patient: Patient) {
        viewModelScope.launch {
            patientRepository.updatePatient(patient)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "فشل في تحديث المريض"
                    )
                }
        }
    }

    fun deletePatient(patientId: String) {
        viewModelScope.launch {
            patientRepository.deletePatient(patientId)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "فشل في حذف المريض"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class PatientsUiState(
    val patients: List<Patient> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

