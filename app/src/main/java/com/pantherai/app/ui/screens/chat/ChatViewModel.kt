package com.pantherai.app.ui.screens.chat

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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private var currentModel: String = ""
    
    fun setModel(modelId: String) {
        currentModel = modelId
    }
    
    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
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
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false
) 