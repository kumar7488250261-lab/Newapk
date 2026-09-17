package com.example.ui.screens.equipment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
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
import com.example.data.equipment.EquipmentRecord
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastReturnScreen(
    viewModel: EquipmentViewModel,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRecordForReturn by remember { mutableStateOf<EquipmentRecord?>(null) }
    var returnRemarks by remember { mutableStateOf("") }

    val activeRecords by viewModel.activeIssuedRecords.collectAsStateWithLifecycle()

    val filteredRecords = remember(activeRecords, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) activeRecords
        else {
            activeRecords.filter {
                it.issuedToCrewName.lowercase().contains(q) ||
                        it.issuedToCrewId.lowercase().contains(q) ||
                        it.equipmentName.lowercase().contains(q) ||
                        it.equipmentSerialNo.lowercase().contains(q)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("फास्ट रिटर्न / Fast Return", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("${activeRecords.size} Currently Issued Items", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RailwayNavy)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightSurface)
                .padding(16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Crew ID, Name, Equipment, Serial No") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_return_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = RailwayGreen,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "कोई रिकॉर्ड नहीं मिला" else "कोई उपकरण वर्तमान में जारी नहीं है",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredRecords) { record ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = record.equipmentName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = RailwayNavy
                                        )
                                    )
                                    Surface(
                                        color = RailwayAmber.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Tag: ${record.equipmentSerialNo}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFB7791F)
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Issued To: ${record.issuedToCrewName} (${record.issuedToCrewId})",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Designation: ${record.designation}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                                Text(
                                    text = "Issue Time: ${record.issueTime}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { selectedRecordForReturn = record },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_return_${record.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = RailwayNavy),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.AssignmentReturn, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("वापस लें / Return Equipment")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Return confirmation dialog
    if (selectedRecordForReturn != null) {
        val record = selectedRecordForReturn!!
        AlertDialog(
            onDismissRequest = { selectedRecordForReturn = null },
            title = {
                Text(
                    text = "उपकरण वापसी पुष्टि / Confirm Return",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Equipment: ${record.equipmentName} (${record.equipmentSerialNo})")
                    Text("Crew: ${record.issuedToCrewName} (${record.issuedToCrewId})")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = returnRemarks,
                        onValueChange = { returnRemarks = it },
                        label = { Text("Condition / Remarks (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitReturn(record, returnRemarks)
                        selectedRecordForReturn = null
                        returnRemarks = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("पुष्टि करें / Confirm Return")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRecordForReturn = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
