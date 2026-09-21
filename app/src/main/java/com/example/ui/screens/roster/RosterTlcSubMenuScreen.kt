package com.example.ui.screens.roster

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KharsiaLobbyEmblem
import com.example.ui.screens.equipment.EquipmentViewModel
import com.example.ui.screens.equipment.InChargePasswordDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RosterTlcSubMenuScreen(
    viewModel: EquipmentViewModel,
    onNavigateToShiftView: () -> Unit,
    onNavigateToAdminEntry: () -> Unit,
    onBack: () -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }

    if (showPasswordDialog) {
        InChargePasswordDialog(
            title = "Admin Portal Verification (एडमिन पोर्टल)",
            onDismiss = { showPasswordDialog = false },
            onConfirm = { pin ->
                if (viewModel.verifyAdminPin(pin)) {
                    showPasswordDialog = false
                    onNavigateToAdminEntry()
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KharsiaLobbyEmblem(size = 36.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Roaster & TLC Update",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "SECR Kharsia Lobby • Shift Wise Roster & Admin Entry",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_roster_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF071426)
                )
            )
        },
        containerColor = Color(0xFF060E18)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "SELECT MODULE (सुविधा चुनें)",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF78909C),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            )

            // Menu Option 1: Shift Wise Roster & TLC Update View
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToShiftView() }
                    .testTag("menu_roster_shift_view"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2235)),
                border = BorderStroke(1.2.dp, Color(0xFF0288D1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF01579B), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewTimeline,
                            contentDescription = null,
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "1. Shift Wise Roaster & TLC",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "शिफ्ट अनुसार सारा रोस्टर व TLC अपडेट देखें (06-14, 14-22, 22-06, Lobby CLI, Sander Boy)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFB0BEC5),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF4FC3F7)
                    )
                }
            }

            // Menu Option 2: Admin Portal (Password Protected)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (viewModel.isAdminSessionActive) {
                            onNavigateToAdminEntry()
                        } else {
                            showPasswordDialog = true
                        }
                    }
                    .testTag("menu_roster_admin_portal"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1624)),
                border = BorderStroke(1.2.dp, Color(0xFFAB47BC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF4A148C), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color(0xFFE1BEE7),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "2. Admin Portal (डेटा एंट्री)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFF5252).copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "PASSWORD",
                                    color = Color(0xFFFF8A80),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "पासवर्ड प्रोटेक्टेड एडमिन पोर्टल: TFR, LH, DI, WD (ऑटो फेच + मोबाइल), CMS, Lobby CLI, Sander Boy व TLC शिफ्ट अपडेट",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCE93D8),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFFE1BEE7)
                    )
                }
            }
        }
    }
}
