package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.AuthManager
import com.example.data.Lobby
import com.example.data.StaffRepository
import com.example.data.equipment.AppDatabase
import com.example.data.equipment.EquipmentRepository
import com.example.data.equipment.InChargeAuthManager
import com.example.ui.screens.*
import com.example.ui.screens.equipment.*
import com.example.ui.screens.pr.PeriodicalRestScreen
import com.example.ui.theme.KharsiaLobbyTheme

sealed class Screen {
    object Splash : Screen()
    object Landing : Screen()
    object Login : Screen()
    object MainMenu : Screen()
    object StaffDirectory : Screen()
    data class LobbyDetail(val lobby: Lobby) : Screen()
    object EquipmentRegisterHome : Screen()
    object FastIssue : Screen()
    object FastReturn : Screen()
    object SupervisorDashboard : Screen()
    object PeriodicalRest : Screen()
}

class MainActivity : ComponentActivity() {

    private val authManager by lazy { AuthManager(this) }
    private val staffRepository by lazy { StaffRepository(this) }
    private val appDatabase by lazy { AppDatabase.getDatabase(this) }
    private val equipmentRepository by lazy { EquipmentRepository(appDatabase, this) }
    private val inChargeAuthManager by lazy { InChargeAuthManager(this) }

    private val equipmentViewModel: EquipmentViewModel by viewModels {
        EquipmentViewModelFactory(equipmentRepository, inChargeAuthManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KharsiaLobbyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val snackbarHostState = remember { SnackbarHostState() }
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
                    var loggedInUserId by remember { mutableStateOf(authManager.currentUserId) }

                    when (val screen = currentScreen) {
                        is Screen.Splash -> {
                            SplashScreen(
                                onTimeout = {
                                    currentScreen = if (authManager.isLoggedIn) {
                                        Screen.MainMenu
                                    } else {
                                        Screen.Landing
                                    }
                                }
                            )
                        }

                        is Screen.Landing -> {
                            LobbyLandingScreen(
                                onNavigateToLogin = { currentScreen = Screen.Login },
                                onNavigateToGuestDirectory = { currentScreen = Screen.StaffDirectory }
                            )
                        }

                        is Screen.Login -> {
                            LoginScreen(
                                authManager = authManager,
                                onLoginSuccess = { userId ->
                                    loggedInUserId = userId
                                    currentScreen = Screen.MainMenu
                                },
                                onBack = { currentScreen = Screen.Landing }
                            )
                        }

                        is Screen.MainMenu -> {
                            MainMenuScreen(
                                onNavigateToStaffDirectory = { currentScreen = Screen.StaffDirectory },
                                onNavigateToEquipmentRegister = { currentScreen = Screen.EquipmentRegisterHome },
                                onNavigateToPeriodicalRest = { currentScreen = Screen.PeriodicalRest },
                                snackbarHostState = snackbarHostState,
                                loggedInUserId = loggedInUserId,
                                onLogout = {
                                    authManager.logout()
                                    currentScreen = Screen.Landing
                                }
                            )
                        }

                        is Screen.StaffDirectory -> {
                            StaffDirectoryScreen(
                                staffRepository = staffRepository,
                                onLobbyClick = { lobby ->
                                    currentScreen = Screen.LobbyDetail(lobby)
                                },
                                onBack = {
                                    currentScreen = if (authManager.isLoggedIn) Screen.MainMenu else Screen.Landing
                                }
                            )
                        }

                        is Screen.LobbyDetail -> {
                            LobbyDetailScreen(
                                lobby = screen.lobby,
                                onBack = { currentScreen = Screen.StaffDirectory }
                            )
                        }

                        is Screen.EquipmentRegisterHome -> {
                            EquipmentRegisterHomeScreen(
                                viewModel = equipmentViewModel,
                                onNavigateToFastIssue = { currentScreen = Screen.FastIssue },
                                onNavigateToFastReturn = { currentScreen = Screen.FastReturn },
                                onBack = { currentScreen = Screen.MainMenu }
                            )
                        }

                        is Screen.FastIssue -> {
                            FastIssueScreen(
                                viewModel = equipmentViewModel,
                                onBack = { currentScreen = Screen.EquipmentRegisterHome }
                            )
                        }

                        is Screen.FastReturn -> {
                            FastReturnScreen(
                                viewModel = equipmentViewModel,
                                onBack = { currentScreen = Screen.EquipmentRegisterHome }
                            )
                        }

                        is Screen.SupervisorDashboard -> {
                            SupervisorDashboardScreen(
                                viewModel = equipmentViewModel,
                                onBack = { currentScreen = Screen.EquipmentRegisterHome }
                            )
                        }

                        is Screen.PeriodicalRest -> {
                            PeriodicalRestScreen(
                                viewModel = equipmentViewModel,
                                onBack = { currentScreen = Screen.MainMenu }
                            )
                        }
                    }
                }
            }
        }
    }
}
