package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

data class MenuItemData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val isEnabled: Boolean,
    val statusText: String,
    val tag: String,
    val accentColor: Color,
    val darkContainerColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateToStaffDirectory: () -> Unit,
    snackbarHostState: SnackbarHostState,
    loggedInUserId: String = "KHS1234",
    onLogout: () -> Unit = {}
) {
    val menuItems = listOf(
        MenuItemData(
            title = "Staff Directory",
            subtitle = "Call Book & Contact details of 14 lobbies",
            icon = Icons.Filled.ContactPhone,
            isEnabled = true,
            statusText = "Available",
            tag = "menu_staff_directory",
            accentColor = ModuleStaffGreen,
            darkContainerColor = ModuleStaffGreenDark
        ),
        MenuItemData(
            title = "Long hour update",
            subtitle = "Crew duty hours & overtime monitoring",
            icon = Icons.Outlined.HourglassEmpty,
            isEnabled = false,
            statusText = "Coming Soon",
            tag = "menu_long_hour",
            accentColor = ModuleLongHourAmber,
            darkContainerColor = ModuleLongHourDark
        ),
        MenuItemData(
            title = "Store register",
            subtitle = "Safety equipment & lobby inventory ledger",
            icon = Icons.Outlined.Inventory2,
            isEnabled = false,
            statusText = "Coming Soon",
            tag = "menu_store_register",
            accentColor = ModuleStorePurple,
            darkContainerColor = ModuleStoreDark
        ),
        MenuItemData(
            title = "Roaster &TLC update",
            subtitle = "Traction Loco Controller updates & duty rosters",
            icon = Icons.Outlined.EventNote,
            isEnabled = false,
            statusText = "Coming Soon",
            tag = "menu_roaster_tlc",
            accentColor = ModuleRosterBlue,
            darkContainerColor = ModuleRosterDark
        ),
        MenuItemData(
            title = "Jeep movement",
            subtitle = "Crew transport & road vehicle dispatch log",
            icon = Icons.Outlined.DirectionsCar,
            isEnabled = false,
            statusText = "Coming Soon",
            tag = "menu_jeep_movement",
            accentColor = ModuleJeepRose,
            darkContainerColor = ModuleJeepDark
        ),
        MenuItemData(
            title = "Pdd &pad",
            subtitle = "Pre-departure detention & arrival logs",
            icon = Icons.Outlined.Train,
            isEnabled = false,
            statusText = "Coming Soon",
            tag = "menu_pdd_pad",
            accentColor = ModulePddTeal,
            darkContainerColor = ModulePddDark
        )
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kharsia_logo),
                            contentDescription = "Kharsia Lobby Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "KHARSIA LOBBY",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "SECR • Bilaspur Division",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RailwayAmber
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF0F2642),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = loggedInUserId,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("menu_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFF87171)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF081326),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Welcome Banner Card in vibrant dark style
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("welcome_banner_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(Color(0xFF38BDF8).copy(alpha = 0.4f), Color(0xFFF59E0B).copy(alpha = 0.4f))
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF0A182F), Color(0xFF132B4F))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_kharsia_logo),
                                    contentDescription = "Kharsia Lobby Logo",
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Welcome to KHS LOBBY",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Surface(
                                            color = RailwayAmber,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "KHS",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp,
                                                color = Color(0xFF070E1B),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Crew Management & Operational Portal. Access staff directory, call book numbers and department contacts.",
                                        fontSize = 12.sp,
                                        color = Color(0xFFCBD5E1),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "OPERATIONAL MODULES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            // Exactly the 6 requested menu items
            items(menuItems.size) { index ->
                val item = menuItems[index]
                MenuCard(
                    item = item,
                    onClick = {
                        if (item.isEnabled) {
                            onNavigateToStaffDirectory()
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun MenuCard(
    item: MenuItemData,
    onClick: () -> Unit
) {
    val isClickable = item.isEnabled

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(item.tag)
            .then(
                if (isClickable) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isClickable) Color(0xFF0F1E33) else Color(0xFF0C1626)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isClickable) 4.dp else 0.dp
        ),
        border = if (isClickable) {
            BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(
                    listOf(Color(0xFF10B981), Color(0xFF06B6D4))
                )
            )
        } else {
            BorderStroke(
                1.dp,
                item.accentColor.copy(alpha = 0.22f)
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colorful Icon container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isClickable) {
                            Color(0xFF064E3B)
                        } else {
                            item.darkContainerColor.copy(alpha = 0.6f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (isClickable) Color(0xFF34D399) else item.accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title and description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isClickable) Color(0xFFF8FAFC) else Color(0xFFE2E8F0)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = if (isClickable) Color(0xFF94A3B8) else Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status Badge
            if (isClickable) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF064E3B),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF34D399)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = item.accentColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, item.accentColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Coming Soon",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = item.accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
