package com.pantherai.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pantherai.app.data.model.AIModel
import com.pantherai.app.data.model.ModelType

@Composable
fun HomeScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToImage: (String) -> Unit,
    onNavigateToVideo: (String) -> Unit,
    onNavigateToTTS: (String) -> Unit,
    onNavigateToTranscription: (String) -> Unit,
    onNavigateToSupport: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val aiModels = remember {
        listOf(
            // Chat Models
            AIModelCategory(
                title = "Chat Models",
                description = "Conversational AI assistants",
                icon = Icons.Default.Chat,
                color = Color(0xFF1E3A8A),
                models = listOf(
                    AIModel("provider-1/chatgpt-4o-latest", "ChatGPT-4o Latest", "OpenAI", ModelType.CHAT, "Latest GPT-4 model with enhanced capabilities"),
                    AIModel("provider-3/gpt-4", "GPT-4", "OpenAI", ModelType.CHAT, "Powerful language model for complex tasks"),
                    AIModel("provider-3/gpt-4.1-mini", "GPT-4.1 Mini", "OpenAI", ModelType.CHAT, "Fast and efficient GPT-4 variant"),
                    AIModel("provider-6/gpt-4.1-mini", "GPT-4.1 Mini", "OpenAI", ModelType.CHAT, "Optimized mini model"),
                    AIModel("provider-6/gpt-4.1-nano", "GPT-4.1 Nano", "OpenAI", ModelType.CHAT, "Ultra-fast nano model"),
                    AIModel("provider-3/gpt-4.1-nano", "GPT-4.1 Nano", "OpenAI", ModelType.CHAT, "Compact nano variant"),
                    AIModel("provider-6/gpt-4o-mini-search-preview", "GPT-4o Mini Search", "OpenAI", ModelType.CHAT, "Search-enabled mini model"),
                    AIModel("provider-3/gpt-4o-mini-search-preview", "GPT-4o Mini Search", "OpenAI", ModelType.CHAT, "Preview search model"),
                    AIModel("provider-6/gpt-4o", "GPT-4o", "OpenAI", ModelType.CHAT, "Latest GPT-4o model"),
                    AIModel("provider-6/o3-medium", "O3 Medium", "OpenAI", ModelType.CHAT, "Medium-sized O3 model"),
                    AIModel("provider-6/o3-high", "O3 High", "OpenAI", ModelType.CHAT, "High-performance O3 model"),
                    AIModel("provider-6/o3-low", "O3 Low", "OpenAI", ModelType.CHAT, "Fast O3 model"),
                    AIModel("provider-6/gpt-4.1", "GPT-4.1", "OpenAI", ModelType.CHAT, "Advanced GPT-4.1 model"),
                    AIModel("provider-6/o4-mini-medium", "O4 Mini Medium", "OpenAI", ModelType.CHAT, "Medium O4 mini model"),
                    AIModel("provider-6/o4-mini-high", "O4 Mini High", "OpenAI", ModelType.CHAT, "High-performance O4 mini"),
                    AIModel("provider-6/o4-mini-low", "O4 Mini Low", "OpenAI", ModelType.CHAT, "Fast O4 mini model"),
                    AIModel("provider-1/gemini-2.5-pro", "Gemini 2.5 Pro", "Google", ModelType.CHAT, "Advanced multimodal AI model"),
                    AIModel("provider-3/deepseek-v3", "DeepSeek V3", "DeepSeek", ModelType.CHAT, "Advanced reasoning model"),
                    AIModel("provider-1/deepseek-v3-0324", "DeepSeek V3 0324", "DeepSeek", ModelType.CHAT, "March 2024 DeepSeek model"),
                    AIModel("provider-1/sonar", "Sonar", "Sonar", ModelType.CHAT, "Fast conversational AI"),
                    AIModel("provider-1/sonar-deep-research", "Sonar Deep Research", "Sonar", ModelType.CHAT, "Research-focused Sonar model"),
                    AIModel("provider-2/mistral-small", "Mistral Small", "Mistral", ModelType.CHAT, "Efficient small model"),
                    AIModel("provider-6/minimax-m1-40k", "MiniMax M1 40K", "MiniMax", ModelType.CHAT, "40K context window model"),
                    AIModel("provider-6/kimi-k2", "Kimi K2", "Kimi", ModelType.CHAT, "Advanced Kimi model"),
                    AIModel("provider-3/kimi-k2", "Kimi K2", "Kimi", ModelType.CHAT, "Kimi K2 variant"),
                    AIModel("provider-6/qwen3-coder-480b-a35b", "Qwen3 Coder 480B", "Alibaba", ModelType.CHAT, "Large coding model"),
                    AIModel("provider-3/llama-3.1-405b", "Llama 3.1 405B", "Meta", ModelType.CHAT, "Massive 405B parameter model"),
                    AIModel("provider-3/qwen-3-235b-a22b-2507", "Qwen 3 235B", "Alibaba", ModelType.CHAT, "Large Qwen model"),
                    AIModel("provider-1/mistral-large", "Mistral Large", "Mistral", ModelType.CHAT, "High-performance language model"),
                    AIModel("provider-2/llama-4-scout", "Llama 4 Scout", "Meta", ModelType.CHAT, "Scout variant of Llama 4"),
                    AIModel("provider-2/llama-4-maverick", "Llama 4 Maverick", "Meta", ModelType.CHAT, "Maverick variant of Llama 4"),
                    AIModel("provider-6/gemini-2.5-flash-thinking", "Gemini 2.5 Flash Thinking", "Google", ModelType.CHAT, "Thinking-enabled flash model"),
                    AIModel("provider-6/gemini-2.5-flash", "Gemini 2.5 Flash", "Google", ModelType.CHAT, "Fast Gemini flash model"),
                    AIModel("provider-1/gemma-3-12b-it", "Gemma 3 12B IT", "Google", ModelType.CHAT, "Instruction-tuned Gemma model"),
                    AIModel("provider-1/llama-3.3-70b-instruct-turbo", "Llama 3.3 70B Turbo", "Meta", ModelType.CHAT, "Turbo variant of Llama 3.3")
                )
            ),
            // Image Models
            AIModelCategory(
                title = "Image Generation",
                description = "Create stunning visuals from text",
                icon = Icons.Default.Image,
                color = Color(0xFF7C3AED),
                models = listOf(
                    AIModel("provider-4/imagen-3", "Imagen 3", "Google", ModelType.IMAGE, "High-quality image synthesis"),
                    AIModel("provider-4/imagen-4", "Imagen 4", "Google", ModelType.IMAGE, "Latest Imagen model"),
                    AIModel("provider-6/sana-1.5-flash", "Sana 1.5 Flash", "Sana", ModelType.IMAGE, "Quick image generation"),
                    AIModel("provider-1/FLUX.1-schnell", "FLUX.1 Schnell", "Stability AI", ModelType.IMAGE, "Fast FLUX image generation"),
                    AIModel("provider-2/FLUX.1-schnell", "FLUX.1 Schnell", "Stability AI", ModelType.IMAGE, "Provider 2 FLUX model"),
                    AIModel("provider-3/FLUX.1-schnell", "FLUX.1 Schnell", "Stability AI", ModelType.IMAGE, "Provider 3 FLUX model"),
                    AIModel("provider-6/sana-1.5", "Sana 1.5", "Sana", ModelType.IMAGE, "Standard Sana model"),
                    AIModel("provider-3/FLUX.1-dev", "FLUX.1 Dev", "Stability AI", ModelType.IMAGE, "Development FLUX model"),
                    AIModel("provider-6/FLUX.1-dev", "FLUX.1 Dev", "Stability AI", ModelType.IMAGE, "Provider 6 FLUX dev"),
                    AIModel("provider-1/FLUX.1.1-pro", "FLUX.1.1 Pro", "Stability AI", ModelType.IMAGE, "Professional FLUX model"),
                    AIModel("provider-6/FLUX.1-pro", "FLUX.1 Pro", "Stability AI", ModelType.IMAGE, "Provider 6 FLUX pro"),
                    AIModel("provider-1/FLUX.1-kontext-pro", "FLUX.1 Kontext Pro", "Stability AI", ModelType.IMAGE, "Context-aware FLUX pro"),
                    AIModel("provider-6/FLUX.1-kontext-pro", "FLUX.1 Kontext Pro", "Stability AI", ModelType.IMAGE, "Provider 6 Kontext pro"),
                    AIModel("provider-6/FLUX.1-1-pro", "FLUX.1.1 Pro", "Stability AI", ModelType.IMAGE, "Provider 6 FLUX 1.1 pro"),
                    AIModel("provider-6/FLUX.1-kontext-dev", "FLUX.1 Kontext Dev", "Stability AI", ModelType.IMAGE, "Development Kontext model"),
                    AIModel("provider-2/FLUX.1-schnell-v2", "FLUX.1 Schnell V2", "Stability AI", ModelType.IMAGE, "Version 2 Schnell model"),
                    AIModel("provider-6/FLUX.1-kontext-max", "FLUX.1 Kontext Max", "Stability AI", ModelType.IMAGE, "Maximum Kontext model")
                )
            ),
            // Video Models
            AIModelCategory(
                title = "Video Generation",
                description = "Generate videos from text prompts",
                icon = Icons.Default.VideoLibrary,
                color = Color(0xFF059669),
                models = listOf(
                    AIModel("provider-6/wan-2.1", "Wan 2.1", "Wan", ModelType.VIDEO, "Advanced video generation model")
                )
            ),
            // Audio TTS Models
            AIModelCategory(
                title = "Text-to-Speech",
                description = "Convert text to natural speech",
                icon = Icons.Default.VolumeUp,
                color = Color(0xFFDC2626),
                models = listOf(
                    AIModel("provider-3/tts-1", "TTS 1", "OpenAI", ModelType.AUDIO_TTS, "High-quality text-to-speech"),
                    AIModel("provider-6/sonic-2", "Sonic 2", "Sonic", ModelType.AUDIO_TTS, "Fast speech synthesis"),
                    AIModel("provider-6/sonic", "Sonic", "Sonic", ModelType.AUDIO_TTS, "Natural voice generation")
                )
            ),
            // Audio Transcription Models
            AIModelCategory(
                title = "Audio Transcription",
                description = "Convert speech to text",
                icon = Icons.Default.Mic,
                color = Color(0xFFEA580C),
                models = listOf(
                    AIModel("provider-2/whisper-1", "Whisper 1", "OpenAI", ModelType.AUDIO_TRANSCRIBE, "Fast transcription"),
                    AIModel("provider-3/whisper-1", "Whisper 1", "OpenAI", ModelType.AUDIO_TRANSCRIBE, "Accurate speech recognition"),
                    AIModel("provider-6/distil-whisper-large-v3-en", "Distil Whisper", "Hugging Face", ModelType.AUDIO_TRANSCRIBE, "Optimized for English"),
                    AIModel("provider-3/gpt-4o-mini-transcribe", "GPT-4o Mini Transcribe", "OpenAI", ModelType.AUDIO_TRANSCRIBE, "Mini transcription model")
                )
            )
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panther AI",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onNavigateToSupport) {
                        Text(
                            text = "☕️",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Welcome Section
                WelcomeSection()
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            items(aiModels) { category ->
                ModelCategoryCard(
                    category = category,
                    onModelClick = { model ->
                        when (model.type) {
                            ModelType.CHAT -> onNavigateToChat(model.id)
                            ModelType.IMAGE -> onNavigateToImage(model.id)
                            ModelType.VIDEO -> onNavigateToVideo(model.id)
                            ModelType.AUDIO_TTS -> onNavigateToTTS(model.id)
                            ModelType.AUDIO_TRANSCRIBE -> onNavigateToTranscription(model.id)
                        }
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun WelcomeSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Welcome to Panther AI",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Explore the latest AI models for chat, image generation, video creation, and more. Choose a model to get started!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ModelCategoryCard(
    category: AIModelCategory,
    onModelClick: (AIModel) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Category Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    category.color,
                                    category.color.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Models Grid
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(category.models) { model ->
                    ModelCard(
                        model = model,
                        onClick = { onModelClick(model) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelCard(
    model: AIModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = model.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = model.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.provider,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class AIModelCategory(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val models: List<AIModel>
) 