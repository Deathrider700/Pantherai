package com.pantherai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pantherai.app.data.model.ModelType
import com.pantherai.app.data.repository.AIRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Composable
fun DrawerContent(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // User Profile Section
        UserProfileSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Navigation Items
        NavigationItems(
            selectedItem = selectedItem,
            onItemSelected = onItemSelected
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Model Selector
        ModelSelector()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick Actions
        QuickActions()
    }
}

@Composable
private fun UserProfileSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // User Name
            Text(
                text = "Panther AI User",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            // User Email
            Text(
                text = "user@pantherai.com",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun NavigationItems(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        NavigationItem(
            icon = Icons.Default.Home,
            title = "Home",
            description = "AI Models Dashboard"
        ),
        NavigationItem(
            icon = Icons.Default.History,
            title = "History",
            description = "Your AI Interactions"
        ),
        NavigationItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            description = "App Configuration"
        )
    )
    
    Column {
        Text(
            text = "Navigation",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        items.forEachIndexed { index, item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { 
                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ModelSelector() {
    var selectedModelType by remember { mutableStateOf(ModelType.CHAT) }
    
    Column {
        Text(
            text = "Quick Model Access",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Model Type Tabs
        TabRow(
            selectedTabIndex = selectedModelType.ordinal,
            modifier = Modifier.fillMaxWidth()
        ) {
            ModelType.values().forEach { modelType ->
                Tab(
                    selected = selectedModelType == modelType,
                    onClick = { selectedModelType = modelType },
                    text = {
                        Text(
                            text = modelType.name.replace("_", " "),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Model List
        when (selectedModelType) {
            ModelType.CHAT -> ChatModels()
            ModelType.IMAGE -> ImageModels()
            ModelType.VIDEO -> VideoModels()
            ModelType.AUDIO_TTS -> TTSModels()
            ModelType.AUDIO_TRANSCRIBE -> TranscriptionModels()
        }
    }
}

@Composable
private fun ChatModels() {
    val models = listOf(
        "provider-1/chatgpt-4o-latest",
        "provider-3/gpt-4",
        "provider-1/gemini-2.5-pro",
        "provider-1/mistral-large"
    )
    
    ModelList(models = models)
}

@Composable
private fun ImageModels() {
    val models = listOf(
        "provider-2/flux.1-schnell",
        "provider-4/imagen-3",
        "provider-4/imagen-4",
        "provider-6/sana-1.5-flash"
    )
    
    ModelList(models = models)
}

@Composable
private fun VideoModels() {
    val models = listOf(
        "provider-6/wan-2.1"
    )
    
    ModelList(models = models)
}

@Composable
private fun TTSModels() {
    val models = listOf(
        "provider-3/tts-1",
        "provider-6/sonic-2",
        "provider-6/sonic"
    )
    
    ModelList(models = models)
}

@Composable
private fun TranscriptionModels() {
    val models = listOf(
        "provider-3/whisper-1",
        "provider-2/whisper-1",
        "provider-6/distil-whisper-large-v3-en"
    )
    
    ModelList(models = models)
}

@Composable
private fun ModelList(models: List<String>) {
    Column {
        models.forEach { model ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = model,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActions() {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(
                icon = Icons.Default.Chat,
                label = "New Chat",
                onClick = { /* Navigate to chat */ }
            )
            
            QuickActionButton(
                icon = Icons.Default.Image,
                label = "Generate Image",
                onClick = { /* Navigate to image generation */ }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(
                icon = Icons.Default.VideoLibrary,
                label = "Create Video",
                onClick = { /* Navigate to video generation */ }
            )
            
            QuickActionButton(
                icon = Icons.Default.Mic,
                label = "Transcribe",
                onClick = { /* Navigate to transcription */ }
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

data class NavigationItem(
    val icon: ImageVector,
    val title: String,
    val description: String
) 