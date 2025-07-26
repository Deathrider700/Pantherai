package com.pantherai.app.data.remote

import com.pantherai.app.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface A4FService {
    
    // Chat completions
    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>
    
    // Image generation
    @POST("v1/images/generations")
    suspend fun imageGeneration(
        @Header("Authorization") apiKey: String,
        @Body request: ImageGenerationRequest
    ): Response<ImageGenerationResponse>
    
    // Video generation
    @POST("v1/video/generations")
    suspend fun videoGeneration(
        @Header("Authorization") apiKey: String,
        @Body request: VideoGenerationRequest
    ): Response<VideoGenerationResponse>
    
    // Text-to-Speech
    @POST("v1/audio/speech")
    suspend fun textToSpeech(
        @Header("Authorization") apiKey: String,
        @Body request: TTSRequest
    ): Response<ByteArray>
    
    // Audio transcription
    @Multipart
    @POST("v1/audio/transcriptions")
    suspend fun audioTranscription(
        @Header("Authorization") apiKey: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody
    ): Response<TranscriptionResponse>
} 