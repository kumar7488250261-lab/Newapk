package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KharsiaLobbyEmblem
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateToStaffDirectory: () -> Unit,
    onNavigateToEquipmentRegister: () -> Unit,
    onNavigateToPeriodicalRest: () -> Unit,
    onNavigateToLongHour: () -> Unit,
    snackbarHostState: SnackbarHostState,
    loggedInUserId: String = "KHS1234",
    onLogout: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()

    val menuItems = remember {
        listOf(
            MenuItemData(
                title = "Staff Directory",
                subtitle = "Call Book & Contact details of 14 lobbies",
                icon = Icons.Default.ContactPhone,
                isEnabled = true,
                statusBadge = "ACTIVE >",
                testTag = "menu_staff_directory",
                iconTint = Color(0xFF00E676),
                iconBgColor = Color(0xFF003820)
            ),
            MenuItemData(
                title = "PR remark",
                subtitle = "Mark PR, Auto-fetch Crew Details, Sign-off & Admin Approval",
                icon = Icons.Default.EventAvailable,
                isEnabled = true,
                statusBadge = "ACTIVE >",
                testTag = "menu_periodical_rest",
                iconTint = Color(0xFF2979FF),
                iconBgColor = Color(0xFF0D2550)
            ),
            MenuItemData(
                title = "Store register",
                subtitle = "Fast Issue & Fast Return / CHO Equipment Register",
                icon = Icons.Default.Inventory2,
                isEnabled = true,
                statusBadge = "ACTIVE >",
                testTag = "menu_store_register",
                iconTint = Color(0xFFBA68C8),
                iconBgColor = Color(0xFF331640)
            ),
            MenuItemData(
                title = "Long hour update",
                subtitle = "Crew duty hours & overtime monitoring",
                icon = Icons.Default.HourglassEmpty,
                isEnabled = true,
                statusBadge = "ACTIVE >",
                testTag = "menu_long_hour",
                iconTint = Color(0xFFFFB74D),
                iconBgColor = Color(0xFF3D260D)
            ),
            MenuItemData(
                title = "Roaster &TLC update",
                subtitle = "Traction Loco Controller updates & duty rosters",
                icon = Icons.Default.CalendarMonth,
                isEnabled = false,
                statusBadge = "Coming Soon",
                testTag = "menu_roaster_tlc",
                iconTint = Color(0xFF4FC3F7),
                iconBgColor = Color(0xFF0E324A)
            ),
            MenuItemData(
                title = "Jeep movement",
                subtitle = "Crew transport & road vehicle dispatch log",
                icon = Icons.Default.DirectionsCar,
                isEnabled = false,
                statusBadge = "Coming Soon",
                testTag = "menu_jeep_movement",
                iconTint = Color(0xFFFF4081),
                iconBgColor = Color(0xFF421024)
            ),
            MenuItemData(
                title = "Pdd &pad",
                subtitle = "Pre-departure detention & arrival logs",
                icon = Icons.Default.Train,
                isEnabled = false,
                statusBadge = "Coming Soon",
                testTag = "menu_pdd_pad",
                iconTint = Color(0xFF4DB6AC),
                iconBgColor = Color(0xFF0C312E)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KharsiaLobbyEmblem(size = 40.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "WELCOME TO KHARSIA LOBBY",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 0.8.sp
                                )
                            )
                            Text(
                                text = "SECR • Bilaspur Division • Indian Railways",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFF1B748))
                            )
                        }
                    }
                },
                actions = {
                    // Crew ID Tag
                    Surface(
                        color = Color(0xFF102844),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFF1E4D7A))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF64B5F6),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = loggedInUserId,
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color(0xFFFF5252)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070E17))
            )
        },
        containerColor = Color(0xFF070E17),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF070E17))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "OPERATIONAL MODULES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF78909C),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                )
            }

            items(menuItems, key = { it.testTag }) { item ->
                DarkMenuCard(
                    item = item,
                    onClick = {
                        when (item.testTag) {
                            "menu_staff_directory" -> onNavigateToStaffDirectory()
                            "menu_periodical_rest" -> onNavigateToPeriodicalRest()
                            "menu_store_register" -> onNavigateToEquipmentRegister()
                            "menu_long_hour" -> onNavigateToLongHour()
                            else -> {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("${item.title} सुविधा शीघ्र उपलब्ध होगी")
                                }
                            }
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DarkMenuCard(
    item: MenuItemData,
    onClick: () -> Unit
) {
    val borderColor = if (item.isEnabled) item.iconTint.copy(alpha = 0.5f) else Color(0xFF1B2E46)
    val cardBg = Color(0xFF0C1929)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(item.testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon in colored container
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (item.isEnabled) Color.White else Color(0xFF90A4AE)
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF78909C),
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status Badge
            Surface(
                color = if (item.isEnabled) Color(0xFF003820) else Color(0xFF1D2833),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    1.dp,
                    if (item.isEnabled) Color(0xFF00E676).copy(alpha = 0.6f) else Color(0xFF37474F)
                )
            ) {
                Text(
                    text = item.statusBadge,
                    color = if (item.isEnabled) Color(0xFF00E676) else Color(0xFFB0BEC5),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
