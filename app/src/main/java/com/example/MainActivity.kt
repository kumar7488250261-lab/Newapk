package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.AuthManager
import com.example.data.StaffRepository
import com.example.data.equipment.InChargeAuthManager
import com.example.data.jeep.JeepRepository
import com.example.data.lr.LrDeclarationRepository
import com.example.ui.screens.LobbyDetailScreen
import com.example.ui.screens.LobbyLandingScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StaffDirectoryScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.screens.equipment.StoreRegisterScreen
import com.example.ui.screens.jeep.JeepAvailabilityScreen
import com.example.ui.screens.jeep.JeepMovementEntryScreen
import com.example.ui.screens.jeep.JeepSubMenuScreen
import com.example.ui.screens.longhour.LongHourUpdateScreen
import com.example.ui.screens.lr.LrDeclarationScreen
import com.example.ui.screens.pr.PeriodicalRestScreen
import com.example.ui.screens.roster.RosterAdminPortalScreen
import com.example.ui.screens.roster.RosterTlcSubMenuScreen
import com.example.ui.screens.roster.ShiftWiseRosterScreen
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.KharsiaLobbyTheme

class MainActivity : ComponentActivity() {

    private val authManager by lazy { AuthManager(this) }
    private val staffRepository by lazy { StaffRepository(this) }
    private val inChargeAuthManager by lazy { InChargeAuthManager(this) }
    private val lrDeclarationRepository by lazy { LrDeclarationRepository(this) }
    private val jeepRepository by lazy { JeepRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KharsiaLobbyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkCanvasBg
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

                    when (val screen = currentScreen) {
                        is Screen.Splash -> {
                            SplashScreen(
                                onTimeout = {
                                    currentScreen = if (authManager.isLoggedIn) {
                                        Screen.MainMenu
                                    } else {
                                        Screen.Welcome
                                    }
                                }
                            )
                        }

                        is Screen.Welcome -> {
                            WelcomeScreen(
                                onContinue = { currentScreen = Screen.Landing }
                            )
                        }

                        is Screen.Landing -> {
                            BackHandler {
                                finish()
                            }
                            LobbyLandingScreen(
                                onNavigateToLogin = { currentScreen = Screen.Login }
                            )
                        }

                        is Screen.Login -> {
                            BackHandler {
                                currentScreen = Screen.Landing
                            }
                            LoginScreen(
                                authManager = authManager,
                                onLoginSuccess = {
                                    currentScreen = Screen.MainMenu
                                },
                                onBack = { currentScreen = Screen.Landing }
                            )
                        }

                        is Screen.MainMenu -> {
                            BackHandler {
                                finish()
                            }
                            MainMenuScreen(
                                currentUserId = authManager.currentUserId,
                                onNavigateToStaffDirectory = { currentScreen = Screen.StaffDirectory },
                                onNavigateToEquipmentRegister = { currentScreen = Screen.EquipmentRegisterHome },
                                onNavigateToPeriodicalRest = { currentScreen = Screen.PeriodicalRest },
                                onNavigateToLongHour = { currentScreen = Screen.LongHour },
                                onNavigateToJeepSubMenu = { currentScreen = Screen.JeepSubMenu },
                                onNavigateToRosterTlc = { currentScreen = Screen.RosterTlcSubMenu },
                                onNavigateToLrDeclaration = { currentScreen = Screen.LrDeclaration },
                                onLogout = {
                                    authManager.logout()
                                    currentScreen = Screen.Landing
                                }
                            )
                        }

                        is Screen.StaffDirectory -> {
                            BackHandler {
                                currentScreen = if (authManager.isLoggedIn) Screen.MainMenu else Screen.Landing
                            }
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
                            BackHandler {
                                currentScreen = Screen.StaffDirectory
                            }
                            LobbyDetailScreen(
                                lobby = screen.lobby,
                                onBack = { currentScreen = Screen.StaffDirectory }
                            )
                        }

                        is Screen.EquipmentRegisterHome, is Screen.FastIssue, is Screen.FastReturn, is Screen.SupervisorDashboard -> {
                            BackHandler {
                                currentScreen = Screen.MainMenu
                            }
                            StoreRegisterScreen(
                                inChargeAuthManager = inChargeAuthManager,
                                staffRepository = staffRepository,
                                onBack = { currentScreen = Screen.MainMenu }
                            )
                        }

                        is Screen.PeriodicalRest -> {
                            BackHandler {
                                currentScreen = Screen.MainMenu
                            }
                            PeriodicalRestScreen(
                                inChargeAuthManager = inChargeAuthManager,
                                staffRepository = staffRepository,
                                onBack = { currentScreen = Screen.MainMenu }
                            )
                        }

                        is Screen.LongHour -> {
                            BackHandler {
                                currentScreen = Screen.MainMenu
                            }
                            LongHourUpdateScreen(
                                inChargeAuthManager = inChargeAuthManager,
                                staffRepository = staffRepository,
                                onBack = { currentScreen = Screen.MainMenu }
                            )
                        }

                        is Screen.JeepSubMenu -> {
                            BackHandler {
                                currentScreen = Screen.MainMenu
                            }
                            JeepSubMenuScreen(
                                jeepRepository = jeepRepository,
                                staffRepository = staffRepository,
                                onBack = { currentScreen = Screen.MainMenu }
                            )
                        }

                        is Screen.JeepAvailability -> {
                            BackHandler {
                                currentScreen = Screen.JeepSubMenu
                            }
                            JeepAvailabilityScreen(
                                jeepRepository = jeepRepository,
                                staffRepository = staffRepository,
                                onNavigateToEntry = { currentScreen = Screen.JeepMovementEntry },
                                onBack = { currentScreen = Screen.JeepSubMenu }
                            )
                        }

                        is Screen.JeepMovementEntry -> {
                            BackHandler {
                                currentScreen = Screen.JeepSubMenu
                            }
                            JeepMovementEntryScreen(
                                jeepRepository = jeepRepository,
                                staffRepository = staffRepository,
                                onBack = { currentScreen = Screen.JeepSubMenu }
                            )
                        }

                        is Screen.RosterTlcSubMenu -> {
                            BackHandler {
                                currentScreen = Screen.MainMenu
                            }
                            RosterTlcSubMenuScreen(
                                inChargeAuthManager = inChargeAuthManager,
                                onNavigateToShiftRoster = { currentScreen = Screen.ShiftWiseRoster },
                                onNavigateToAdminPortal = { currentScreen = Screen.RosterAdminPortal },
                                onBack = { currentScreen = Screen.MainMenu }
                            )
                        }

                        is Screen.ShiftWiseRoster -> {
                            BackHandler {
                                currentScreen = Screen.RosterTlcSubMenu
                            }
                            ShiftWiseRosterScreen(
                                onBack = { currentScreen = Screen.RosterTlcSubMenu }
                            )
                        }

                        is Screen.RosterAdminPortal -> {
                            BackHandler {
                                currentScreen = Screen.RosterTlcSubMenu
                            }
                            RosterAdminPortalScreen(
                                staffRepository = staffRepository,
                                onBack = { currentScreen = Screen.RosterTlcSubMenu }
                            )
                        }

                        is Screen.LrDeclaration -> {
                            BackHandler {
                                currentScreen = Screen.MainMenu
                            }
                            LrDeclarationScreen(
                                lrRepository = lrDeclarationRepository,
                                staffRepository = staffRepository,
                                currentUserId = authManager.currentUserId,
                                onBack = { currentScreen = Screen.MainMenu }
                            )
                        }

                    }
                }
            }
        }
    }
}
