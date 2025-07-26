package com.pantherai.app.ui.screens.audio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaRecorder
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
class TranscriptionViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TranscriptionUiState())
    val uiState: StateFlow<TranscriptionUiState> = _uiState.asStateFlow()
    
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    
    fun setModel(modelId: String) {
        _uiState.update { it.copy(currentModel = modelId) }
    }
    
    fun updateModel(model: String) {
        _uiState.update { it.copy(currentModel = model) }
    }
    
    fun startRecording(context: Context) {
        try {
            // Create audio file
            val audioDir = context.getExternalFilesDir(null) ?: return
            audioFile = File.createTempFile("audio_recording", ".mp3", audioDir)
            
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
            
            _uiState.update { it.copy(isRecording = true) }
            
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    error = "Failed to start recording: ${e.message}",
                    isRecording = false
                )
            }
        }
    }
    
    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            
            _uiState.update { it.copy(isRecording = false) }
            
            // Start transcription automatically
            audioFile?.let { file ->
                transcribeAudio(file)
            }
            
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    error = "Failed to stop recording: ${e.message}",
                    isRecording = false
                )
            }
        }
    }
    
    private fun transcribeAudio(audioFile: File) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isTranscribing = true, error = null) }
                
                val audioBytes = audioFile.readBytes()
                val model = _uiState.value.currentModel
                
                val result = aiRepository.transcribeAudio(
                    model = model,
                    audioData = audioBytes
                )
                
                result.fold(
                    onSuccess = { transcription ->
                        _uiState.update { 
                            it.copy(
                                transcription = transcription.text,
                                isTranscribing = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                error = "Failed to transcribe audio: ${error.message}",
                                isTranscribing = false
                            )
                        }
                    }
                )
                
                // Clean up audio file
                audioFile.delete()
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Error transcribing audio: ${e.message}",
                        isTranscribing = false
                    )
                }
            }
        }
    }
    
    fun copyToClipboard(context: Context, text: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Transcription", text)
            clipboard.setPrimaryClip(clip)
            
            _uiState.update { 
                it.copy(
                    copySuccess = "Transcription copied to clipboard"
                )
            }
            
            // Clear success message after 3 seconds
            viewModelScope.launch {
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(copySuccess = null) }
            }
            
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    error = "Failed to copy to clipboard: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
        mediaRecorder = null
        audioFile?.delete()
    }
}

data class TranscriptionUiState(
    val currentModel: String = "provider-3/whisper-1",
    val isRecording: Boolean = false,
    val isTranscribing: Boolean = false,
    val transcription: String? = null,
    val error: String? = null,
    val copySuccess: String? = null
) 