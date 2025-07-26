package com.pantherai.app.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class AIModel(
    val id: String,
    val name: String,
    val provider: String,
    val type: ModelType,
    val description: String,
    val isAvailable: Boolean = true
)

enum class ModelType {
    CHAT,
    IMAGE,
    VIDEO,
    AUDIO_TTS,
    AUDIO_TRANSCRIBE
}

@Immutable
data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long,
    val model: String? = null
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

@Immutable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7,
    val maxTokens: Int = 150
)

@Immutable
data class ChatResponse(
    val id: String,
    val choices: List<ChatChoice>,
    val usage: Usage?
)

@Immutable
data class ChatChoice(
    val index: Int,
    val message: ChatMessage,
    val finishReason: String?
)

@Immutable
data class Usage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

@Immutable
data class ImageGenerationRequest(
    val model: String,
    val prompt: String,
    val n: Int = 1,
    val size: String = "1024x1024"
)

@Immutable
data class ImageGenerationResponse(
    val created: Long,
    val data: List<ImageData>
)

@Immutable
data class ImageData(
    val url: String,
    val revisedPrompt: String?
)

@Immutable
data class VideoGenerationRequest(
    val model: String,
    val prompt: String,
    val ratio: String = "16:9",
    val quality: String = "480p",
    val duration: Int = 4
)

@Immutable
data class VideoGenerationResponse(
    val created: Long,
    val data: List<VideoData>
)

@Immutable
data class VideoData(
    val url: String
)

@Immutable
data class TTSRequest(
    val model: String,
    val input: String,
    val voice: String = "alloy"
)

@Immutable
data class TranscriptionRequest(
    val model: String,
    val file: String // Base64 encoded audio file
)

@Immutable
data class TranscriptionResponse(
    val text: String
)

@Immutable
data class HistoryItem(
    val id: String,
    val userId: String,
    val model: String,
    val type: ModelType,
    val input: Map<String, Any>,
    val output: Map<String, Any>?,
    val createdAt: Long,
    val updatedAt: Long
)

@Immutable
data class UserPreferences(
    val userId: String,
    val defaultChatModel: String = "provider-1/chatgpt-4o-latest",
    val defaultImageModel: String = "provider-2/flux.1-schnell",
    val defaultVideoModel: String = "provider-6/wan-2.1",
    val defaultTTSModel: String = "provider-3/tts-1",
    val defaultTranscribeModel: String = "provider-3/whisper-1",
    val theme: String = "system"
) 