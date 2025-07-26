package com.pantherai.app.ui.screens.audio

import android.content.Context
import android.media.MediaPlayer
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
class TTSViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TTSUiState())
    val uiState: StateFlow<TTSUiState> = _uiState.asStateFlow()
    
    private var mediaPlayer: MediaPlayer? = null
    
    fun setModel(modelId: String) {
        _uiState.update { it.copy(currentModel = modelId) }
    }
    
    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }
    
    fun updateVoice(voice: String) {
        _uiState.update { it.copy(selectedVoice = voice) }
    }
    
    fun updateModel(model: String) {
        _uiState.update { it.copy(currentModel = model) }
    }
    
    fun generateTTS() {
        val text = _uiState.value.inputText
        val model = _uiState.value.currentModel
        val voice = _uiState.value.selectedVoice
        
        if (text.isBlank()) {
            _uiState.update { it.copy(error = "Please enter some text to convert to speech") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val result = aiRepository.textToSpeech(
                    model = model,
                    input = text,
                    voice = voice
                )
                
                result.fold(
                    onSuccess = { audioData ->
                        _uiState.update { 
                            it.copy(
                                generatedAudio = audioData,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                error = "Failed to generate speech: ${error.message}",
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Error generating speech: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun playAudio() {
        val audioData = _uiState.value.generatedAudio ?: return
        
        try {
            // Stop any currently playing audio
            stopAudio()
            
            // Create a temporary file for the audio
            val tempFile = File.createTempFile("tts_audio", ".mp3")
            tempFile.writeBytes(audioData)
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
            }
            
            _uiState.update { it.copy(isPlaying = true) }
            
            // Set up completion listener
            mediaPlayer?.setOnCompletionListener {
                _uiState.update { it.copy(isPlaying = false) }
                mediaPlayer?.release()
                mediaPlayer = null
                tempFile.delete()
            }
            
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    error = "Failed to play audio: ${e.message}",
                    isPlaying = false
                )
            }
        }
    }
    
    fun stopAudio() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
        }
        mediaPlayer = null
        _uiState.update { it.copy(isPlaying = false) }
    }
    
    fun downloadAudio(context: Context) {
        val audioData = _uiState.value.generatedAudio ?: return
        
        viewModelScope.launch {
            try {
                // Create a file in the downloads directory
                val downloadsDir = context.getExternalFilesDir(null) ?: return@launch
                val fileName = "panther_ai_tts_${System.currentTimeMillis()}.mp3"
                val file = File(downloadsDir, fileName)
                
                file.writeBytes(audioData)
                
                _uiState.update { 
                    it.copy(
                        downloadSuccess = "Audio saved to ${file.absolutePath}"
                    )
                }
                
                // Clear success message after 3 seconds
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(downloadSuccess = null) }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to download audio: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }
}

data class TTSUiState(
    val inputText: String = "",
    val currentModel: String = "provider-3/tts-1",
    val selectedVoice: String = "alloy",
    val generatedAudio: ByteArray? = null,
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val error: String? = null,
    val downloadSuccess: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TTSUiState

        if (inputText != other.inputText) return false
        if (currentModel != other.currentModel) return false
        if (selectedVoice != other.selectedVoice) return false
        if (generatedAudio != null) {
            if (other.generatedAudio == null) return false
            if (!generatedAudio.contentEquals(other.generatedAudio)) return false
        } else if (other.generatedAudio != null) return false
        if (isLoading != other.isLoading) return false
        if (isPlaying != other.isPlaying) return false
        if (error != other.error) return false
        if (downloadSuccess != other.downloadSuccess) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inputText.hashCode()
        result = 31 * result + currentModel.hashCode()
        result = 31 * result + selectedVoice.hashCode()
        result = 31 * result + (generatedAudio?.contentHashCode() ?: 0)
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + isPlaying.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + (downloadSuccess?.hashCode() ?: 0)
        return result
    }
} 