package com.example.ui.screens.roster

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.equipment.RosterTlcRecord
import com.example.ui.components.KharsiaLobbyEmblem
import com.example.ui.screens.equipment.EquipmentViewModel
import com.example.ui.screens.equipment.InChargePasswordDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftWiseRosterViewScreen(
    viewModel: EquipmentViewModel,
    onNavigateToAdminEntry: () -> Unit,
    onBack: () -> Unit
) {
    val rosterRecords by viewModel.allRosterTlcRecords.collectAsStateWithLifecycle()

    var selectedShiftFilter by remember { mutableStateOf("ALL") } // "ALL", "06-14", "14-22", "22-06"
    var searchQuery by remember { mutableStateOf("") }
    var showPasswordDialogForAdd by remember { mutableStateOf(false) }

    if (showPasswordDialogForAdd) {
        InChargePasswordDialog(
            title = "Admin Verification Required",
            onDismiss = { showPasswordDialogForAdd = false },
            onConfirm = { pin ->
                if (viewModel.verifyAdminPin(pin)) {
                    showPasswordDialogForAdd = false
                    onNavigateToAdminEntry()
                }
            }
        )
    }

    val filteredList = remember(rosterRecords, selectedShiftFilter, searchQuery) {
        rosterRecords.filter { record ->
            val matchesShift = if (selectedShiftFilter == "ALL") true else record.shiftTiming == selectedShiftFilter
            val matchesSearch = if (searchQuery.isBlank()) true else {
                record.tfrCrewName.contains(searchQuery, ignoreCase = true) ||
                record.lhCrewName.contains(searchQuery, ignoreCase = true) ||
                record.diCrewName.contains(searchQuery, ignoreCase = true) ||
                record.wdCrewName.contains(searchQuery, ignoreCase = true) ||
                record.cmsName.contains(searchQuery, ignoreCase = true) ||
                record.lobbyCliName.contains(searchQuery, ignoreCase = true) ||
                record.sanderBoyName.contains(searchQuery, ignoreCase = true) ||
                record.rosterDate.contains(searchQuery, ignoreCase = true) ||
                record.tlcMlName.contains(searchQuery, ignoreCase = true)
            }
            matchesShift && matchesSearch
        }
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
                                text = "Shift Wise Roaster & TLC",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "SECR Kharsia Lobby • Shift-wise Roster & Controller Log",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF81D4FA))
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_shift_view_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (viewModel.isAdminSessionActive) {
                                onNavigateToAdminEntry()
                            } else {
                                showPasswordDialogForAdd = true
                            }
                        },
                        modifier = Modifier.testTag("btn_go_to_admin")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Roster",
                            tint = Color(0xFF4FC3F7)
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, date or role...", color = Color(0xFF546E7A), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF81D4FA)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF0C1929),
                    unfocusedContainerColor = Color(0xFF0C1929),
                    focusedBorderColor = Color(0xFF0288D1),
                    unfocusedBorderColor = Color(0xFF1B324D)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_roster_search")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Shift Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "All Shifts", "06-14" to "06-14", "14-22" to "14-22", "22-06" to "22-06").forEach { (key, label) ->
                    val isSelected = selectedShiftFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedShiftFilter = key },
                        label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF0C1929),
                            labelColor = Color(0xFFB0BEC5),
                            selectedContainerColor = Color(0xFF0288D1),
                            selectedLabelColor = Color.White
                        ),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF4FC3F7) else Color(0xFF1B324D))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF37474F),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (rosterRecords.isEmpty()) "कोई रोस्टर व TLC अपडेट रिकॉर्ड उपलब्ध नहीं है" else "दिए गए फ़िल्टर के अनुसार कोई रिकॉर्ड नहीं मिला",
                            color = Color(0xFF78909C),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (viewModel.isAdminSessionActive) {
                                    onNavigateToAdminEntry()
                                } else {
                                    showPasswordDialogForAdd = true
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("नया रोस्टर अपडेट दर्ज करें (Admin Portal)")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredList, key = { it.id }) { record ->
                        RosterCard(record = record)
                    }
                }
            }
        }
    }
}

@Composable
fun RosterCard(
    record: RosterTlcRecord
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1B2B)),
        border = BorderStroke(1.dp, Color(0xFF1B3859))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Date & Shift
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF0288D1).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "दिनांक: ${record.rosterDate}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "शिफ्ट: ${record.shiftTiming} HRS",
                            color = Color(0xFF4FC3F7),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF00E676).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = record.shiftTiming,
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFF1B3859)
            )

            // Section: Roster Roles (TFR, LH, DI, WD)
            Text(
                text = "रोस्टर स्टाफ (SHIFT: ${record.shiftTiming})",
                color = Color(0xFF90CAF9),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                RoleStaffRow(role = "TFR", name = record.tfrCrewName, mobile = record.tfrMobile)
                RoleStaffRow(role = "LH", name = record.lhCrewName, mobile = record.lhMobile)
                RoleStaffRow(role = "DI", name = record.diCrewName, mobile = record.diMobile)
                RoleStaffRow(role = "WD", name = record.wdCrewName, mobile = record.wdMobile)
                RoleStaffRow(role = "CMS", name = record.cmsName, mobile = "")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Section: Lobby CLI & Sander Boy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Lobby CLI Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF082032), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "LOBBY CLI (${record.lobbyCliShift})",
                        color = Color(0xFF81D4FA),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = record.lobbyCliName.ifBlank { "Not assigned" },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (record.lobbyCliMobile.isNotBlank()) {
                        Text(
                            text = "📞 ${record.lobbyCliMobile}",
                            color = Color(0xFF00E676),
                            fontSize = 11.sp
                        )
                    }
                }

                // Sander Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF082032), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "SANDER (${record.sanderBoyShift})",
                        color = Color(0xFFFFB74D),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = record.sanderBoyName.ifBlank { "Not assigned" },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Section: TLC Updates
            if (record.tlcMlName.isNotBlank() || record.tlcLhName.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF142436), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TLC (TRACTION LOCO CONTROLLER)",
                            color = Color(0xFFCE93D8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF8E24AA).copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "SHIFT ${record.tlcShift}",
                                color = Color(0xFFE1BEE7),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    if (record.tlcMlName.isNotBlank()) {
                        Text(
                            text = "TLC ML: ${record.tlcMlName} ${if (record.tlcMlMobile.isNotBlank()) "• 📞 ${record.tlcMlMobile}" else ""}",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                    if (record.tlcLhName.isNotBlank()) {
                        Text(
                            text = "TLC LH: ${record.tlcLhName} ${if (record.tlcLhMobile.isNotBlank()) "• 📞 ${record.tlcLhMobile}" else ""}",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleStaffRow(
    role: String,
    name: String,
    mobile: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A1929), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF0288D1).copy(alpha = 0.3f)
            ) {
                Text(
                    text = role,
                    color = Color(0xFF81D4FA),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name.ifBlank { "—" },
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (mobile.isNotBlank()) {
            Text(
                text = "📞 $mobile",
                color = Color(0xFF00E676),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
