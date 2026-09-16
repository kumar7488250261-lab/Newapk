package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
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
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateToStaffDirectory: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var showWelcomeDialog by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        MenuItemData(
            title = "Staff Directory",
            subtitle = "Call Book & Contact details of 14 lobbies",
            icon = Icons.Filled.ContactPhone,
            isEnabled = true,
            statusText = "Available",
            tag = "menu_staff_directory"
        ),
        MenuItemData(
            title = "Long hour update",
            subtitle = "Crew duty hours & overtime monitoring",
            icon = Icons.Outlined.HourglassEmpty,
            isEnabled = false,
            statusText = "Coming Soon",
            tag = "menu_long_hour"
        ),
        MenuItemData(
            title = "Store register",
            subtitle = "Safety equipment & lobby inventory ledger",
            icon = Icons.Outlined.Inventory2,
            isEnabled = false,
            statusText = "Coming Soon",
            tag = "menu_store_register"
        ),
        MenuItemData(
            title = "Roaster &TLC update",
            subtitle = "Traction Loco Controller updates & duty rosters",
            icon = Icons.Outlined.EventNote,
            isEnabled = false,
            statusText = "Coming Soon",
            tag = "menu_roaster_tlc"
        ),
        MenuItemData(
            title = "Jeep movement",
            subtitle = "Crew transport & road vehicle dispatch log",
            icon = Icons.Outlined.DirectionsCar,
            isEnabled = false,
            statusText = "Coming Soon",
            tag = "menu_jeep_movement"
        ),
        MenuItemData(
            title = "Pdd &pad",
            subtitle = "Pre-departure detention & arrival logs",
            icon = Icons.Outlined.Train,
            isEnabled = false,
            statusText = "Coming Soon",
            tag = "menu_pdd_pad"
        )
    )

    // Welcome Dialog on first opening as requested
    if (showWelcomeDialog) {
        AlertDialog(
            onDismissRequest = { showWelcomeDialog = false },
            confirmButton = {
                Button(
                    onClick = { showWelcomeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RailwayBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("welcome_continue_btn")
                ) {
                    Text("Continue to Portal", fontWeight = FontWeight.Bold)
                }
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_kharsia_logo),
                    contentDescription = "Kharsia Lobby Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            },
            title = {
                Text(
                    text = "Welcome to KHS LOBBY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = RailwayNavy,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Kharsia Lobby • SECR Bilaspur Division",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = RailwayGold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Welcome to Kharsia Lobby Crew Management & Staff Directory Portal. Only the Staff Directory is currently active for all crew calling & contact lookup.",
                        fontSize = 13.sp,
                        color = RailwayTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

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
                                .size(38.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "KHARSIA LOBBY",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "SECR • Bilaspur Division",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RailwayBlue,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = RailwaySurface
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
                // Welcome Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("welcome_banner_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(RailwayNavy, RailwayBlue)
                                )
                            )
                            .padding(20.dp)
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
                                        .size(52.dp)
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
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Surface(
                                            color = RailwayAmber,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "KHS",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp,
                                                color = RailwayNavy,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Crew Management & Operational Portal. Access staff directory, call book numbers and department contacts.",
                                        fontSize = 12.sp,
                                        color = Color(0xFFE2E8F0),
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
                    text = "MAIN MENU",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RailwayTextSecondary,
                    letterSpacing = 1.sp,
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
                Spacer(modifier = Modifier.height(16.dp))
                // Footer notice
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Notice",
                            tint = RailwayGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "As per lobby notice, only Staff Directory is functional. All other modules are coming soon.",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
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
            containerColor = if (isClickable) Color.White else RailwayDisabledBg
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isClickable) 3.dp else 0.dp
        ),
        border = if (isClickable) {
            CardDefaults.outlinedCardBorder().copy(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(listOf(RailwayBlue, RailwayLightBlue))
            )
        } else {
            CardDefaults.outlinedCardBorder().copy(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(RailwayDivider, RailwayDivider))
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isClickable) Color(0xFFEFF6FF) else Color(0xFFE2E8F0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (isClickable) RailwayBlue else RailwayDisabled,
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
                    color = if (isClickable) RailwayNavy else RailwayDisabled
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = if (isClickable) RailwayTextSecondary else RailwayDisabled,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status Badge
            if (isClickable) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Open",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RailwaySuccess
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = RailwaySuccess,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = "Coming Soon",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RailwayDanger,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
