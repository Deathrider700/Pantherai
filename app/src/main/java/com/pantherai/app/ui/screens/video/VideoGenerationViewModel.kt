package com.pantherai.app.ui.screens.video

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantherai.app.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VideoGenerationViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(VideoGenerationUiState())
    val uiState: StateFlow<VideoGenerationUiState> = _uiState.asStateFlow()
    
    fun setModel(modelId: String) {
        _uiState.update { it.copy(currentModel = modelId) }
    }
    
    fun updatePrompt(prompt: String) {
        _uiState.update { it.copy(prompt = prompt) }
    }
    
    fun updateDuration(duration: Int) {
        // Ensure duration is within limits (1-4 seconds)
        val clampedDuration = duration.coerceIn(1, 4)
        _uiState.update { it.copy(duration = clampedDuration) }
    }
    
    fun updateResolution(resolution: String) {
        // Ensure resolution doesn't exceed 480p
        val validResolution = when (resolution) {
            "1080p", "720p" -> "480p"
            else -> resolution
        }
        _uiState.update { it.copy(resolution = validResolution) }
    }
    
    fun updateQuality(quality: String) {
        _uiState.update { it.copy(quality = quality) }
    }
    
    fun updateRatio(ratio: String) {
        _uiState.update { it.copy(ratio = ratio) }
    }
    
    fun updateModel(model: String) {
        _uiState.update { it.copy(currentModel = model) }
    }
    
    fun generateVideo() {
        val prompt = _uiState.value.prompt
        val model = _uiState.value.currentModel
        val duration = _uiState.value.duration
        val resolution = _uiState.value.resolution
        val quality = _uiState.value.quality
        val ratio = _uiState.value.ratio
        
        if (prompt.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a video prompt") }
            return
        }
        
        if (duration > 4) {
            _uiState.update { it.copy(error = "Duration cannot exceed 4 seconds") }
            return
        }
        
        if (resolution != "480p") {
            _uiState.update { it.copy(error = "Resolution is limited to 480p maximum") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val result = aiRepository.generateVideo(
                    model = model,
                    prompt = prompt,
                    duration = duration,
                    resolution = resolution,
                    quality = quality,
                    ratio = ratio
                )
                
                result.fold(
                    onSuccess = { videoUrl ->
                        _uiState.update { 
                            it.copy(
                                generatedVideos = it.generatedVideos + videoUrl,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                error = "Failed to generate video: ${error.message}",
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Error generating video: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun downloadVideo(context: Context, videoUrl: String) {
        viewModelScope.launch {
            try {
                // Create a file in the downloads directory
                val downloadsDir = context.getExternalFilesDir(null) ?: return@launch
                val fileName = "panther_ai_video_${System.currentTimeMillis()}.mp4"
                val file = File(downloadsDir, fileName)
                
                // TODO: Download video from URL and save to file
                // This would typically involve downloading the video data from the URL
                
                _uiState.update { 
                    it.copy(
                        downloadSuccess = "Video saved to ${file.absolutePath}"
                    )
                }
                
                // Clear success message after 3 seconds
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(downloadSuccess = null) }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to download video: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class VideoGenerationUiState(
    val prompt: String = "",
    val currentModel: String = "provider-6/wan-2.1",
    val duration: Int = 4, // Default to max 4 seconds
    val resolution: String = "480p", // Default to max 480p
    val quality: String = "standard",
    val ratio: String = "16:9",
    val generatedVideos: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val downloadSuccess: String? = null
) 