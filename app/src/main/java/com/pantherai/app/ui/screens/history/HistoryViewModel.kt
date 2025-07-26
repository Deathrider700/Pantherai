package com.pantherai.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantherai.app.data.model.HistoryItem
import com.pantherai.app.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    fun loadHistory() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val result = aiRepository.getHistory()
                result.fold(
                    onSuccess = { historyItems ->
                        _uiState.update { 
                            it.copy(
                                historyItems = historyItems,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                error = "Failed to load history: ${error.message}",
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Error loading history: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun deleteHistoryItem(itemId: String) {
        viewModelScope.launch {
            try {
                val result = aiRepository.deleteHistoryItem(itemId)
                result.fold(
                    onSuccess = {
                        // Reload history after successful deletion
                        loadHistory()
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                error = "Failed to delete history item: ${error.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Error deleting history item: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class HistoryUiState(
    val historyItems: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 