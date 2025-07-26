package com.pantherai.app.data.repository

import com.pantherai.app.data.model.*
import com.pantherai.app.data.remote.A4FService
import com.pantherai.app.data.remote.SupabaseService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepository @Inject constructor(
    private val a4FService: A4FService,
    private val supabaseService: SupabaseService
) {
    
    companion object {
        const val A4F_API_KEY = "ddc-a4f-4c0658a7764c432c9aa8e4a6d409afb3"
    }
    
    // Chat
    suspend fun sendChatMessage(
        model: String,
        messages: List<ChatMessage>,
        temperature: Double = 0.7,
        maxTokens: Int = 150
    ): Result<ChatResponse> {
        return try {
            val request = ChatRequest(
                model = model,
                messages = messages,
                temperature = temperature,
                maxTokens = maxTokens
            )
            
            val response = a4FService.chatCompletion(
                apiKey = "Bearer $A4F_API_KEY",
                request = request
            )
            
            if (response.isSuccessful) {
                response.body()?.let { chatResponse ->
                    // Save to history
                    val currentUser = supabaseService.getCurrentUser()
                    currentUser?.let { user ->
                        val historyItem = HistoryItem(
                            id = java.util.UUID.randomUUID().toString(),
                            userId = user.id,
                            model = model,
                            type = ModelType.CHAT,
                            input = mapOf(
                                "messages" to messages,
                                "temperature" to temperature,
                                "maxTokens" to maxTokens
                            ),
                            output = mapOf(
                                "response" to chatResponse
                            ),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        supabaseService.saveHistory(historyItem)
                    }
                    Result.success(chatResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Image Generation
    suspend fun generateImage(
        model: String,
        prompt: String,
        n: Int = 1,
        size: String = "1024x1024"
    ): Result<ImageGenerationResponse> {
        return try {
            val request = ImageGenerationRequest(
                model = model,
                prompt = prompt,
                n = n,
                size = size
            )
            
            val response = a4FService.imageGeneration(
                apiKey = "Bearer $A4F_API_KEY",
                request = request
            )
            
            if (response.isSuccessful) {
                response.body()?.let { imageResponse ->
                    // Save to history
                    val currentUser = supabaseService.getCurrentUser()
                    currentUser?.let { user ->
                        val historyItem = HistoryItem(
                            id = java.util.UUID.randomUUID().toString(),
                            userId = user.id,
                            model = model,
                            type = ModelType.IMAGE,
                            input = mapOf(
                                "prompt" to prompt,
                                "n" to n,
                                "size" to size
                            ),
                            output = mapOf(
                                "response" to imageResponse
                            ),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        supabaseService.saveHistory(historyItem)
                    }
                    Result.success(imageResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Video Generation
    suspend fun generateVideo(
        model: String,
        prompt: String,
        ratio: String = "16:9",
        quality: String = "480p",
        duration: Int = 4
    ): Result<VideoGenerationResponse> {
        return try {
            val request = VideoGenerationRequest(
                model = model,
                prompt = prompt,
                ratio = ratio,
                quality = quality,
                duration = duration
            )
            
            val response = a4FService.videoGeneration(
                apiKey = "Bearer $A4F_API_KEY",
                request = request
            )
            
            if (response.isSuccessful) {
                response.body()?.let { videoResponse ->
                    // Save to history
                    val currentUser = supabaseService.getCurrentUser()
                    currentUser?.let { user ->
                        val historyItem = HistoryItem(
                            id = java.util.UUID.randomUUID().toString(),
                            userId = user.id,
                            model = model,
                            type = ModelType.VIDEO,
                            input = mapOf(
                                "prompt" to prompt,
                                "ratio" to ratio,
                                "quality" to quality,
                                "duration" to duration
                            ),
                            output = mapOf(
                                "response" to videoResponse
                            ),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        supabaseService.saveHistory(historyItem)
                    }
                    Result.success(videoResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Text-to-Speech
    suspend fun textToSpeech(
        model: String,
        input: String,
        voice: String = "alloy"
    ): Result<ByteArray> {
        return try {
            val request = TTSRequest(
                model = model,
                input = input,
                voice = voice
            )
            
            val response = a4FService.textToSpeech(
                apiKey = "Bearer $A4F_API_KEY",
                request = request
            )
            
            if (response.isSuccessful) {
                response.body()?.let { audioData ->
                    // Save to history
                    val currentUser = supabaseService.getCurrentUser()
                    currentUser?.let { user ->
                        val historyItem = HistoryItem(
                            id = java.util.UUID.randomUUID().toString(),
                            userId = user.id,
                            model = model,
                            type = ModelType.AUDIO_TTS,
                            input = mapOf(
                                "input" to input,
                                "voice" to voice
                            ),
                            output = mapOf(
                                "audioSize" to audioData.size
                            ),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        supabaseService.saveHistory(historyItem)
                    }
                    Result.success(audioData)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Audio Transcription
    suspend fun transcribeAudio(
        model: String,
        audioData: ByteArray
    ): Result<TranscriptionResponse> {
        return try {
            // First upload to Supabase storage
            val fileName = "audio_${System.currentTimeMillis()}.mp3"
            val uploadResult = supabaseService.uploadFile("audio", fileName, audioData)
            
            if (uploadResult.isSuccess) {
                // For now, we'll use a mock response since the actual transcription
                // would require the file to be accessible via URL
                val mockResponse = TranscriptionResponse("Transcribed audio content")
                
                // Save to history
                val currentUser = supabaseService.getCurrentUser()
                currentUser?.let { user ->
                    val historyItem = HistoryItem(
                        id = java.util.UUID.randomUUID().toString(),
                        userId = user.id,
                        model = model,
                        type = ModelType.AUDIO_TRANSCRIBE,
                        input = mapOf(
                            "fileName" to fileName,
                            "audioSize" to audioData.size
                        ),
                        output = mapOf(
                            "transcription" to mockResponse.text
                        ),
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    supabaseService.saveHistory(historyItem)
                }
                
                Result.success(mockResponse)
            } else {
                Result.failure(Exception("Failed to upload audio file"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // History
    suspend fun getHistory(
        type: String? = null,
        limit: Int = 50
    ): Result<List<HistoryItem>> {
        val currentUser = supabaseService.getCurrentUser()
        return if (currentUser != null) {
            supabaseService.getHistory(currentUser.id, type, limit)
        } else {
            Result.failure(Exception("User not authenticated"))
        }
    }
    
    suspend fun deleteHistoryItem(historyId: String): Result<Unit> {
        return supabaseService.deleteHistory(historyId)
    }
    
    // User Preferences
    suspend fun getUserPreferences(): Result<UserPreferences?> {
        val currentUser = supabaseService.getCurrentUser()
        return if (currentUser != null) {
            supabaseService.getUserPreferences(currentUser.id)
        } else {
            Result.failure(Exception("User not authenticated"))
        }
    }
    
    suspend fun updateUserPreferences(preferences: UserPreferences): Result<Unit> {
        return supabaseService.updateUserPreferences(preferences)
    }
    
    // Authentication
    suspend fun signUp(email: String, password: String): Result<Unit> {
        return supabaseService.signUp(email, password)
    }
    
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return supabaseService.signIn(email, password)
    }
    
    suspend fun signOut(): Result<Unit> {
        return supabaseService.signOut()
    }
    
    fun getCurrentUser() = supabaseService.getCurrentUser()
    
    fun getAuthStateFlow() = supabaseService.getAuthStateFlow()
} 