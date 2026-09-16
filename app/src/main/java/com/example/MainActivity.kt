package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.AuthManager
import com.example.data.Lobby
import com.example.data.StaffRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

sealed interface Screen {
    object Splash : Screen
    object LobbyLanding : Screen
    object Login : Screen
    object MainMenu : Screen
    object StaffDirectory : Screen
    data class LobbyDetail(val lobby: Lobby) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val repository = remember { StaffRepository(context) }
                val lobbies = remember { repository.getLobbies() }
                val authManager = remember { AuthManager(context) }

                var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
                var currentUserId by remember { mutableStateOf(authManager.userId) }
                val snackbarHostState = remember { SnackbarHostState() }

                BackHandler(
                    enabled = currentScreen != Screen.MainMenu &&
                            currentScreen != Screen.Splash &&
                            currentScreen != Screen.LobbyLanding
                ) {
                    currentScreen = when (currentScreen) {
                        is Screen.LobbyDetail -> Screen.StaffDirectory
                        is Screen.StaffDirectory -> Screen.MainMenu
                        Screen.Login -> Screen.LobbyLanding
                        Screen.MainMenu -> Screen.MainMenu
                        Screen.LobbyLanding -> Screen.LobbyLanding
                        Screen.Splash -> Screen.Splash
                    }
                }

                when (val screen = currentScreen) {
                    Screen.Splash -> {
                        SplashScreen(
                            onTimeout = {
                                currentScreen = if (authManager.isLoggedIn) {
                                    currentUserId = authManager.userId
                                    Screen.MainMenu
                                } else {
                                    Screen.LobbyLanding
                                }
                            }
                        )
                    }
                    Screen.LobbyLanding -> {
                        LobbyLandingScreen(
                            onNavigateToLogin = {
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    Screen.Login -> {
                        LoginScreen(
                            onLoginSuccess = { userId ->
                                authManager.login(userId)
                                currentUserId = userId
                                currentScreen = Screen.MainMenu
                            },
                            onBack = {
                                currentScreen = Screen.LobbyLanding
                            }
                        )
                    }
                    Screen.MainMenu -> {
                        MainMenuScreen(
                            onNavigateToStaffDirectory = {
                                currentScreen = Screen.StaffDirectory
                            },
                            snackbarHostState = snackbarHostState,
                            loggedInUserId = currentUserId,
                            onLogout = {
                                authManager.logout()
                                currentScreen = Screen.LobbyLanding
                            }
                        )
                    }
                    Screen.StaffDirectory -> {
                        StaffDirectoryScreen(
                            lobbies = lobbies,
                            onSelectLobby = { selectedLobby ->
                                currentScreen = Screen.LobbyDetail(selectedLobby)
                            },
                            onBack = {
                                currentScreen = Screen.MainMenu
                            }
                        )
                    }
                    is Screen.LobbyDetail -> {
                        LobbyDetailScreen(
                            lobby = screen.lobby,
                            onBack = {
                                currentScreen = Screen.StaffDirectory
                            }
                        )
                    }
                }
            }
        }
    }
}
