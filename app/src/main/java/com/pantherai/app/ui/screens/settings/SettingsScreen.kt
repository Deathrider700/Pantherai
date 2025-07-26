package com.pantherai.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                
                // User Profile Section
                UserProfileSection(uiState.userEmail)
            }
            
            item {
                // AI Model Preferences
                ModelPreferencesSection(
                    defaultChatModel = uiState.defaultChatModel,
                    defaultImageModel = uiState.defaultImageModel,
                    defaultVideoModel = uiState.defaultVideoModel,
                    defaultTTSModel = uiState.defaultTTSModel,
                    defaultTranscribeModel = uiState.defaultTranscribeModel,
                    onModelChange = { type, model -> viewModel.updateDefaultModel(type, model) }
                )
            }
            
            item {
                // TTS Voice Settings
                TTSVoiceSettingsSection(
                    selectedVoice = uiState.selectedVoice,
                    onVoiceChange = { viewModel.updateVoice(it) }
                )
            }
            
            item {
                // App Settings
                AppSettingsSection(
                    isDarkMode = uiState.isDarkMode,
                    onDarkModeChange = { viewModel.updateDarkMode(it) },
                    autoSaveHistory = uiState.autoSaveHistory,
                    onAutoSaveHistoryChange = { viewModel.updateAutoSaveHistory(it) }
                )
            }
            
            item {
                // Account Actions
                AccountActionsSection(
                    onLogout = onLogout,
                    onDeleteAccount = { viewModel.deleteAccount() }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun UserProfileSection(userEmail: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "Panther AI User",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = userEmail ?: "user@pantherai.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ModelPreferencesSection(
    defaultChatModel: String,
    defaultImageModel: String,
    defaultVideoModel: String,
    defaultTTSModel: String,
    defaultTranscribeModel: String,
    onModelChange: (String, String) -> Unit
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
            Text(
                text = "Default AI Models",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chat Model
            ModelPreferenceItem(
                title = "Chat Model",
                currentModel = defaultChatModel,
                options = listOf(
                    "provider-1/chatgpt-4o-latest",
                    "provider-3/gpt-4",
                    "provider-1/gemini-2.5-pro",
                    "provider-1/mistral-large"
                ),
                onModelChange = { onModelChange("chat", it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Image Model
            ModelPreferenceItem(
                title = "Image Model",
                currentModel = defaultImageModel,
                options = listOf(
                    "provider-2/flux.1-schnell",
                    "provider-4/imagen-3",
                    "provider-4/imagen-4",
                    "provider-6/sana-1.5-flash"
                ),
                onModelChange = { onModelChange("image", it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Video Model
            ModelPreferenceItem(
                title = "Video Model",
                currentModel = defaultVideoModel,
                options = listOf("provider-6/wan-2.1"),
                onModelChange = { onModelChange("video", it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // TTS Model
            ModelPreferenceItem(
                title = "TTS Model",
                currentModel = defaultTTSModel,
                options = listOf(
                    "provider-3/tts-1",
                    "provider-6/sonic-2",
                    "provider-6/sonic"
                ),
                onModelChange = { onModelChange("tts", it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Transcription Model
            ModelPreferenceItem(
                title = "Transcription Model",
                currentModel = defaultTranscribeModel,
                options = listOf(
                    "provider-3/whisper-1",
                    "provider-2/whisper-1",
                    "provider-6/distil-whisper-large-v3-en"
                ),
                onModelChange = { onModelChange("transcribe", it) }
            )
        }
    }
}

@Composable
private fun ModelPreferenceItem(
    title: String,
    currentModel: String,
    options: List<String>,
    onModelChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            TextButton(
                onClick = { expanded = !expanded }
            ) {
                Text(
                    text = currentModel.split("/").lastOrNull() ?: currentModel,
                    style = MaterialTheme.typography.bodySmall
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        if (expanded) {
            Column {
                options.forEach { option ->
                    TextButton(
                        onClick = {
                            onModelChange(option)
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                            color = if (option == currentModel) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TTSVoiceSettingsSection(
    selectedVoice: String,
    onVoiceChange: (String) -> Unit
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
            Text(
                text = "TTS Voice Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val voices = listOf("alloy", "echo", "fable", "onyx", "nova", "shimmer")
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(voices) { voice ->
                    FilterChip(
                        onClick = { onVoiceChange(voice) },
                        label = { Text(voice.capitalize()) },
                        selected = selectedVoice == voice,
                        leadingIcon = if (selectedVoice == voice) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun AppSettingsSection(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    autoSaveHistory: Boolean,
    onAutoSaveHistoryChange: (Boolean) -> Unit
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
            Text(
                text = "App Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dark Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Dark Mode",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeChange
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Auto Save History Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Auto Save History",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Switch(
                    checked = autoSaveHistory,
                    onCheckedChange = onAutoSaveHistoryChange
                )
            }
        }
    }
}

@Composable
private fun AccountActionsSection(
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
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
            Text(
                text = "Account Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Log Out Button
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Delete Account Button
            OutlinedButton(
                onClick = onDeleteAccount,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Account")
            }
        }
    }
} 