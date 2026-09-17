package com.example.ui.screens.equipment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.equipment.EquipmentStatus
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorDashboardScreen(
    viewModel: EquipmentViewModel,
    onBack: () -> Unit
) {
    val activeCount by viewModel.activeIssuedCount.collectAsStateWithLifecycle()
    val activeRecords by viewModel.activeIssuedRecords.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("सुपरवाइजर डैशबोर्ड", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Supervisor & In-Charge Control", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.syncWithGoogleSheets() }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync Sheets", tint = Color.White)
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
            // Stats card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RailwayNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CURRENT ACTIVE ISSUES",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = RailwayAmber,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$activeCount",
                            style = MaterialTheme.typography.displayMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Text(
                            text = "Lobby Items In Running Staff Possession",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                        )
                    }
                }
            }

            // Sync action button
            item {
                OutlinedButton(
                    onClick = { viewModel.syncWithGoogleSheets() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSyncing
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isSyncing) "Syncing with Cloud / Google Sheets..." else "Sync Records with Google Sheets")
                }
            }

            // Active list
            item {
                Text(
                    text = "Active Issued Items Live Register",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = RailwayNavy
                    )
                )
            }

            if (activeRecords.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No equipment currently issued. All items in Lobby stock.")
                        }
                    }
                }
            } else {
                items(activeRecords) { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = record.equipmentName,
                                    fontWeight = FontWeight.Bold,
                                    color = RailwayNavy
                                )
                                Text(
                                    text = "Tag: ${record.equipmentSerialNo}",
                                    fontWeight = FontWeight.SemiBold,
                                    color = RailwayAmber
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Crew: ${record.issuedToCrewName} (${record.issuedToCrewId}) - ${record.designation}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Issued at: ${record.issueTime}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }
                }
            }
        }
    }
}
