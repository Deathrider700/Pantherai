package com.pantherai.app.ui.screens.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantherai.app.data.model.ChatMessage
import com.pantherai.app.data.model.MessageRole
import com.pantherai.app.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private var currentModel: String = ""
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFile: File? = null
    private var isRecording = false
    
    fun setModel(modelId: String) {
        currentModel = modelId
    }
    
    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }
    
    // Microphone Button - Voice Input
    fun startVoiceRecording(context: Context) {
        if (isRecording) return
        
        try {
            audioFile = File(context.cacheDir, "voice_input_${System.currentTimeMillis()}.mp3")
            
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
            
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
            
            isRecording = true
            _uiState.update { it.copy(isRecording = true) }
            
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Failed to start recording: ${e.message}") }
        }
    }
    
    fun stopVoiceRecording() {
        if (!isRecording) return
        
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            _uiState.update { it.copy(isRecording = false) }
            
            // Transcribe the audio
            audioFile?.let { file ->
                transcribeAudio(file)
            }
            
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Failed to stop recording: ${e.message}") }
        }
    }
    
    private fun transcribeAudio(audioFile: File) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isTranscribing = true) }
                
                val audioBytes = audioFile.readBytes()
                val result = aiRepository.transcribeAudio(
                    model = "provider-2/whisper-1",
                    audioData = audioBytes
                )
                
                result.fold(
                    onSuccess = { transcription ->
                        _uiState.update { 
                            it.copy(
                                inputText = transcription.text,
                                isTranscribing = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                error = "Transcription failed: ${error.message}",
                                isTranscribing = false
                            )
                        }
                    }
                )
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Transcription error: ${e.message}",
                        isTranscribing = false
                    )
                }
            }
        }
    }
    
    // Speaker Button - Text-to-Speech
    fun playTTS(text: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isPlayingTTS = true) }
                
                val result = aiRepository.textToSpeech(
                    model = "provider-2/tts-1",
                    input = text,
                    voice = "alloy"
                )
                
                result.fold(
                    onSuccess = { audioData ->
                        playAudio(audioData)
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                error = "TTS failed: ${error.message}",
                                isPlayingTTS = false
                            )
                        }
                    }
                )
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "TTS error: ${e.message}",
                        isPlayingTTS = false
                    )
                }
            }
        }
    }
    
    private fun playAudio(audioData: ByteArray) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()
            
            val tempFile = File.createTempFile("tts_audio", ".mp3")
            FileOutputStream(tempFile).use { it.write(audioData) }
            
            mediaPlayer?.apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                setOnCompletionListener {
                    _uiState.update { it.copy(isPlayingTTS = false) }
                    tempFile.delete()
                }
                setOnErrorListener { _, _, _ ->
                    _uiState.update { it.copy(isPlayingTTS = false) }
                    tempFile.delete()
                    true
                }
                start()
            }
            
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    error = "Audio playback failed: ${e.message}",
                    isPlayingTTS = false
                )
            }
        }
    }
    
    // Copy Button
    fun copyToClipboard(context: Context, text: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("AI Message", text)
            clipboard.setPrimaryClip(clip)
            _uiState.update { it.copy(copySuccess = true) }
            
            // Reset copy success after 2 seconds
            viewModelScope.launch {
                kotlinx.coroutines.delay(2000)
                _uiState.update { it.copy(copySuccess = false) }
            }
            
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Failed to copy: ${e.message}") }
        }
    }
    
    // Thumbs Up/Down
    fun sendFeedback(messageId: String, isPositive: Boolean) {
        // TODO: Send feedback to Supabase or analytics
        _uiState.update { it.copy(feedbackSent = true) }
        
        // Reset feedback sent after 2 seconds
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _uiState.update { it.copy(feedbackSent = false) }
        }
    }
    
    // Refresh Button - Regenerate last response
    fun regenerateLastResponse() {
        val messages = _uiState.value.messages
        if (messages.isEmpty()) return
        
        // Find the last user message
        val lastUserMessage = messages.lastOrNull { it.role == MessageRole.USER }
        if (lastUserMessage == null) return
        
        // Remove the last AI response if it exists
        val messagesToKeep = if (messages.last().role == MessageRole.ASSISTANT) {
            messages.dropLast(1)
        } else {
            messages
        }
        
        _uiState.update { 
            it.copy(
                messages = messagesToKeep,
                isLoading = true
            )
        }
        
        // Re-send the last user message
        viewModelScope.launch {
            try {
                val apiMessages = messagesToKeep.map { 
                    com.pantherai.app.data.model.ChatMessage(
                        id = it.id,
                        role = it.role,
                        content = it.content,
                        timestamp = it.timestamp,
                        model = it.model
                    )
                }
                
                val result = aiRepository.sendChatMessage(
                    model = currentModel,
                    messages = apiMessages,
                    temperature = 0.7,
                    maxTokens = 150
                )
                
                result.fold(
                    onSuccess = { response ->
                        val aiMessage = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            role = MessageRole.ASSISTANT,
                            content = response.choices.firstOrNull()?.message?.content ?: "Sorry, I couldn't generate a response.",
                            timestamp = System.currentTimeMillis(),
                            model = currentModel
                        )
                        
                        _uiState.update { 
                            it.copy(
                                messages = it.messages + aiMessage,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        val errorMessage = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            role = MessageRole.ASSISTANT,
                            content = "Sorry, an error occurred: ${error.message}",
                            timestamp = System.currentTimeMillis(),
                            model = currentModel
                        )
                        
                        _uiState.update { 
                            it.copy(
                                messages = it.messages + errorMessage,
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = MessageRole.ASSISTANT,
                    content = "Sorry, an error occurred: ${e.message}",
                    timestamp = System.currentTimeMillis(),
                    model = currentModel
                )
                
                _uiState.update { 
                    it.copy(
                        messages = it.messages + errorMessage,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun sendMessage() {
        val message = _uiState.value.inputText.trim()
        if (message.isBlank() || currentModel.isBlank()) return
        
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = message,
            timestamp = System.currentTimeMillis(),
            model = currentModel
        )
        
        // Add user message to chat
        _uiState.update { 
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true
            )
        }
        
        // Send to AI
        viewModelScope.launch {
            try {
                val messages = _uiState.value.messages.map { 
                    com.pantherai.app.data.model.ChatMessage(
                        id = it.id,
                        role = it.role,
                        content = it.content,
                        timestamp = it.timestamp,
                        model = it.model
                    )
                }
                
                val result = aiRepository.sendChatMessage(
                    model = currentModel,
                    messages = messages,
                    temperature = 0.7,
                    maxTokens = 150
                )
                
                result.fold(
                    onSuccess = { response ->
                        val aiMessage = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            role = MessageRole.ASSISTANT,
                            content = response.choices.firstOrNull()?.message?.content ?: "Sorry, I couldn't generate a response.",
                            timestamp = System.currentTimeMillis(),
                            model = currentModel
                        )
                        
                        _uiState.update { 
                            it.copy(
                                messages = it.messages + aiMessage,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        val errorMessage = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            role = MessageRole.ASSISTANT,
                            content = "Sorry, an error occurred: ${error.message}",
                            timestamp = System.currentTimeMillis(),
                            model = currentModel
                        )
                        
                        _uiState.update { 
                            it.copy(
                                messages = it.messages + errorMessage,
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = MessageRole.ASSISTANT,
                    content = "Sorry, an error occurred: ${e.message}",
                    timestamp = System.currentTimeMillis(),
                    model = currentModel
                )
                
                _uiState.update { 
                    it.copy(
                        messages = it.messages + errorMessage,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun clearChat() {
        _uiState.update { 
            it.copy(
                messages = emptyList(),
                inputText = ""
            )
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
        mediaPlayer?.release()
    }
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isRecording: Boolean = false,
    val isTranscribing: Boolean = false,
    val isPlayingTTS: Boolean = false,
    val error: String? = null,
    val copySuccess: Boolean = false,
    val feedbackSent: Boolean = false
) 