package com.pantherai.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantherai.app.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadUserPreferences()
    }
    
    private fun loadUserPreferences() {
        viewModelScope.launch {
            try {
                val preferences = aiRepository.getUserPreferences()
                preferences.fold(
                    onSuccess = { prefs ->
                        prefs?.let { userPrefs ->
                            _uiState.update { 
                                it.copy(
                                    userEmail = aiRepository.getCurrentUser()?.email,
                                    defaultChatModel = userPrefs.defaultChatModel,
                                    defaultImageModel = userPrefs.defaultImageModel,
                                    defaultVideoModel = userPrefs.defaultVideoModel,
                                    defaultTTSModel = userPrefs.defaultTTSModel,
                                    defaultTranscribeModel = userPrefs.defaultTranscribeModel,
                                    selectedVoice = "alloy", // Default voice
                                    isDarkMode = userPrefs.theme == "dark",
                                    autoSaveHistory = true // Default to true
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                error = "Failed to load preferences: ${error.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Error loading preferences: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun updateDefaultModel(type: String, model: String) {
        viewModelScope.launch {
            try {
                val currentPrefs = aiRepository.getUserPreferences().getOrNull()
                val updatedPrefs = currentPrefs?.copy(
                    defaultChatModel = if (type == "chat") model else currentPrefs.defaultChatModel,
                    defaultImageModel = if (type == "image") model else currentPrefs.defaultImageModel,
                    defaultVideoModel = if (type == "video") model else currentPrefs.defaultVideoModel,
                    defaultTTSModel = if (type == "tts") model else currentPrefs.defaultTTSModel,
                    defaultTranscribeModel = if (type == "transcribe") model else currentPrefs.defaultTranscribeModel
                )
                
                updatedPrefs?.let { prefs ->
                    val result = aiRepository.updateUserPreferences(prefs)
                    result.fold(
                        onSuccess = {
                            _uiState.update { 
                                it.copy(
                                    defaultChatModel = prefs.defaultChatModel,
                                    defaultImageModel = prefs.defaultImageModel,
                                    defaultVideoModel = prefs.defaultVideoModel,
                                    defaultTTSModel = prefs.defaultTTSModel,
                                    defaultTranscribeModel = prefs.defaultTranscribeModel
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update { 
                                it.copy(
                                    error = "Failed to update model preference: ${error.message}"
                                )
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Error updating model preference: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun updateVoice(voice: String) {
        _uiState.update { it.copy(selectedVoice = voice) }
        // TODO: Save voice preference to local storage or Supabase
    }
    
    fun updateDarkMode(isDark: Boolean) {
        _uiState.update { it.copy(isDarkMode = isDark) }
        // TODO: Save theme preference to local storage or Supabase
    }
    
    fun updateAutoSaveHistory(autoSave: Boolean) {
        _uiState.update { it.copy(autoSaveHistory = autoSave) }
        // TODO: Save auto-save preference to local storage or Supabase
    }
    
    fun deleteAccount() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isDeletingAccount = true) }
                
                // TODO: Implement account deletion logic
                // This would typically involve:
                // 1. Delete user data from Supabase
                // 2. Delete local data
                // 3. Sign out user
                
                _uiState.update { it.copy(isDeletingAccount = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to delete account: ${e.message}",
                        isDeletingAccount = false
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class SettingsUiState(
    val userEmail: String? = null,
    val defaultChatModel: String = "provider-1/chatgpt-4o-latest",
    val defaultImageModel: String = "provider-2/flux.1-schnell",
    val defaultVideoModel: String = "provider-6/wan-2.1",
    val defaultTTSModel: String = "provider-3/tts-1",
    val defaultTranscribeModel: String = "provider-3/whisper-1",
    val selectedVoice: String = "alloy",
    val isDarkMode: Boolean = false,
    val autoSaveHistory: Boolean = true,
    val error: String? = null,
    val isDeletingAccount: Boolean = false
) 