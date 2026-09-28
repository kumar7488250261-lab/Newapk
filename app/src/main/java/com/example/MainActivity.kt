package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LobbyDetailScreen
import com.example.ui.screens.RunningRoomScreen
import com.example.ui.screens.StaffDirectoryScreen
import com.example.ui.theme.KharsiaLobbyTheme

sealed class Screen {
    data object Home : Screen()
    data class Directory(val lobbyCode: String = "ALL") : Screen()
    data class LobbyDetail(val lobbyCode: String) : Screen()
    data object RunningRoom : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KharsiaLobbyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                    when (val screen = currentScreen) {
                        is Screen.Home -> {
                            HomeScreen(
                                onNavigateToDirectory = { lobbyCode ->
                                    currentScreen = Screen.Directory(lobbyCode)
                                },
                                onNavigateToLobbyDetail = { lobbyCode ->
                                    currentScreen = Screen.LobbyDetail(lobbyCode)
                                },
                                onNavigateToRunningRoom = {
                                    currentScreen = Screen.RunningRoom
                                }
                            )
                        }
                        is Screen.Directory -> {
                            BackHandler {
                                currentScreen = Screen.Home
                            }
                            StaffDirectoryScreen(
                                initialLobbyCode = screen.lobbyCode,
                                onBack = { currentScreen = Screen.Home }
                            )
                        }
                        is Screen.LobbyDetail -> {
                            BackHandler {
                                currentScreen = Screen.Home
                            }
                            LobbyDetailScreen(
                                lobbyCode = screen.lobbyCode,
                                onBack = { currentScreen = Screen.Home }
                            )
                        }
                        is Screen.RunningRoom -> {
                            BackHandler {
                                currentScreen = Screen.Home
                            }
                            RunningRoomScreen(
                                onBack = { currentScreen = Screen.Home }
                            )
                        }
                    }
                }
            }
        }
    }
}
