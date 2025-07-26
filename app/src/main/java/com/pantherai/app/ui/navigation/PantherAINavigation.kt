package com.pantherai.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pantherai.app.ui.screens.auth.LoginScreen
import com.pantherai.app.ui.screens.auth.SignUpScreen
import com.pantherai.app.ui.screens.chat.ChatScreen
import com.pantherai.app.ui.screens.home.HomeScreen
import com.pantherai.app.ui.screens.image.ImageGenerationScreen
import com.pantherai.app.ui.screens.settings.SettingsScreen
import com.pantherai.app.ui.screens.video.VideoGenerationScreen
import com.pantherai.app.ui.screens.audio.TTSScreen
import com.pantherai.app.ui.screens.audio.TranscriptionScreen
import com.pantherai.app.ui.screens.history.HistoryScreen
import com.pantherai.app.ui.screens.support.SupportScreen
import com.pantherai.app.ui.components.DrawerContent
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Login : Screen("login", "Login")
    object SignUp : Screen("signup", "Sign Up")
    object Chat : Screen("chat/{modelId}", "Chat")
    object ImageGeneration : Screen("image/{modelId}", "Image Generation")
    object VideoGeneration : Screen("video/{modelId}", "Video Generation")
    object TTS : Screen("tts/{modelId}", "Text-to-Speech")
    object Transcription : Screen("transcription/{modelId}", "Audio Transcription")
    object History : Screen("history", "History")
    object Settings : Screen("settings", "Settings")
    object Support : Screen("support", "Support")
}

@Composable
fun PantherAINavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf(0) }
    
    val items = listOf(
        Screen.Home,
        Screen.History,
        Screen.Settings
    )
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    selectedItem = selectedItem,
                    onItemSelected = { index ->
                        selectedItem = index
                        scope.launch {
                            drawerState.close()
                        }
                        when (index) {
                            0 -> navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                            1 -> navController.navigate(Screen.History.route)
                            2 -> navController.navigate(Screen.Settings.route)
                        }
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToChat = { modelId ->
                        navController.navigate(Screen.Chat.route.replace("{modelId}", modelId))
                    },
                    onNavigateToImage = { modelId ->
                        navController.navigate(Screen.ImageGeneration.route.replace("{modelId}", modelId))
                    },
                    onNavigateToVideo = { modelId ->
                        navController.navigate(Screen.VideoGeneration.route.replace("{modelId}", modelId))
                    },
                    onNavigateToTTS = { modelId ->
                        navController.navigate(Screen.TTS.route.replace("{modelId}", modelId))
                    },
                    onNavigateToTranscription = { modelId ->
                        navController.navigate(Screen.Transcription.route.replace("{modelId}", modelId))
                    },
                    onNavigateToSupport = {
                        navController.navigate(Screen.Support.route)
                    },
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
            
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToSignUp = {
                        navController.navigate(Screen.SignUp.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    },
                    onSignUpSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Chat.route) { backStackEntry ->
                val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
                ChatScreen(
                    modelId = modelId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSupport = {
                        navController.navigate(Screen.Support.route)
                    }
                )
            }
            
            composable(Screen.ImageGeneration.route) { backStackEntry ->
                val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
                ImageGenerationScreen(
                    modelId = modelId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSupport = {
                        navController.navigate(Screen.Support.route)
                    }
                )
            }
            
            composable(Screen.VideoGeneration.route) { backStackEntry ->
                val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
                VideoGenerationScreen(
                    modelId = modelId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSupport = {
                        navController.navigate(Screen.Support.route)
                    }
                )
            }
            
            composable(Screen.TTS.route) { backStackEntry ->
                val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
                TTSScreen(
                    modelId = modelId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSupport = {
                        navController.navigate(Screen.Support.route)
                    }
                )
            }
            
            composable(Screen.Transcription.route) { backStackEntry ->
                val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
                TranscriptionScreen(
                    modelId = modelId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSupport = {
                        navController.navigate(Screen.Support.route)
                    }
                )
            }
            
            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToChat = { modelId ->
                        navController.navigate(Screen.Chat.route.replace("{modelId}", modelId))
                    },
                    onNavigateToSupport = {
                        navController.navigate(Screen.Support.route)
                    }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        // Navigate to login and clear back stack
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToSupport = {
                        navController.navigate(Screen.Support.route)
                    }
                )
            }
            
            composable(Screen.Support.route) {
                SupportScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
} 