package com.example.ui.screens.equipment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentRegisterHomeScreen(
    viewModel: EquipmentViewModel,
    onNavigateToFastIssue: () -> Unit,
    onNavigateToFastReturn: () -> Unit,
    onBack: () -> Unit
) {
    val activeCount by viewModel.activeIssuedCount.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Store Register",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Kharsia Combined Lobby • Equipment Tracking",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.syncWithGoogleSheets() },
                        enabled = !isSyncing
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Sync,
                                contentDescription = "Sync",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RailwayNavy)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live status banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "CURRENTLY ISSUED ITEMS",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF94A3B8),
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$activeCount",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    fontSize = 36.sp
                                )
                            )
                            Text(
                                text = "Active equipment with Loco Pilots / Guards",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8).copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
            }

            // Fast Issue Action Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToFastIssue() }
                        .testTag("card_fast_issue"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF132F20)),
                    border = BorderStroke(1.5.dp, Color(0xFF2E7D32)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2E7D32)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Assignment,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "फास्ट इशू / Fast Issue",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFF2E7D32).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "DISPATCH",
                                        color = Color(0xFF81C784),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Issue walkie-talkie, torches, keys & equipment with auto crew lookup",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA5D6A7))
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF81C784)
                        )
                    }
                }
            }

            // Fast Return Action Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToFastReturn() }
                        .testTag("card_fast_return"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2638)),
                    border = BorderStroke(1.5.dp, Color(0xFF1976D2)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1976D2)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.AssignmentReturn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "फास्ट रिटर्न / Fast Return",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFF1976D2).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "RECEIVE",
                                        color = Color(0xFF90CAF9),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Receive back issued equipment, verify condition & close register",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF90CAF9))
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF90CAF9)
                        )
                    }
                }
            }

            // Rules & SOP Note
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = RailwayNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lobby Store SOP",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• All equipment must be verified and acknowledged by staff before departure.\n• On sign-off, equipment must be deposited promptly in working condition.\n• In case of damage or missing equipment, report immediately to Lobby In-charge.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
