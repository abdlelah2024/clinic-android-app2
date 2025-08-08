package com.clinic.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clinic.domain.model.Patient
import com.clinic.domain.usecase.SearchPatientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickSearchViewModel @Inject constructor(
    private val searchPatientsUseCase: SearchPatientsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickSearchUiState())
    val uiState: StateFlow<QuickSearchUiState> = _uiState.asStateFlow()

    fun searchPatients(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val patients = searchPatientsUseCase(query)
                _uiState.value = _uiState.value.copy(
                    patients = patients,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "حدث خطأ أثناء البحث",
                    isLoading = false
                )
            }
        }
    }
}

data class QuickSearchUiState(
    val patients: List<Patient> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

