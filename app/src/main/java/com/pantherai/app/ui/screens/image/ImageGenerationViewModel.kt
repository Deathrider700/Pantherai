package com.pantherai.app.ui.screens.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantherai.app.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageGenerationViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ImageGenerationUiState())
    val uiState: StateFlow<ImageGenerationUiState> = _uiState.asStateFlow()
    
    private var currentModel: String = ""
    private var currentSize: String = "1024x1024"
    
    fun setModel(modelId: String) {
        currentModel = modelId
    }
    
    fun updatePrompt(prompt: String) {
        _uiState.update { it.copy(prompt = prompt) }
    }
    
    fun updateSize(size: String) {
        currentSize = size
    }
    
    fun generateImage() {
        val prompt = _uiState.value.prompt.trim()
        if (prompt.isBlank() || currentModel.isBlank()) return
        
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val result = aiRepository.generateImage(
                    model = currentModel,
                    prompt = prompt,
                    n = 1,
                    size = currentSize
                )
                
                result.fold(
                    onSuccess = { response ->
                        val imageUrls = response.data.map { it.url }
                        _uiState.update { 
                            it.copy(
                                generatedImages = it.generatedImages + imageUrls,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                error = error.message ?: "Failed to generate image",
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "An unexpected error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun clearImages() {
        _uiState.update { it.copy(generatedImages = emptyList()) }
    }
}

data class ImageGenerationUiState(
    val prompt: String = "",
    val generatedImages: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 