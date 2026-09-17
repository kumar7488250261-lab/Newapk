package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.equipment.CrewMember
import com.example.data.equipment.InChargeAuthManager
import com.example.data.equipment.PrRequest
import com.example.ui.screens.equipment.EquipmentViewModel
import com.example.ui.screens.equipment.InChargePasswordDialog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodicalRestScreen(
    viewModel: EquipmentViewModel,
    crewDirectory: List<CrewMember>,
    authManager: InChargeAuthManager,
    onBack: () -> Unit
) {
    var crewIdInput by remember { mutableStateOf("") }
    var crewName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val todayFormatted = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time) }
    val timeFormatted = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time) }

    var signOffDate by remember { mutableStateOf(todayFormatted) }
    var signOffTime by remember { mutableStateOf(timeFormatted) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Admin Mode & Dialogs
    var isAdminMode by remember { mutableStateOf(false) }
    var showAdminPasswordDialog by remember { mutableStateOf(false) }
    var selectedRequestForAction by remember { mutableStateOf<PrRequest?>(null) }
    var showActionDialog by remember { mutableStateOf(false) }

    // Filter tab: "ALL", "PENDING", "CONFIRMED", "NOT_DUE"
    var selectedFilterTab by remember { mutableStateOf("ALL") }

    val allRequests by viewModel.allPrRequests.collectAsState()
    val pendingRequests by viewModel.pendingPrRequests.collectAsState()
    val pendingCount by viewModel.pendingPrCount.collectAsState()

    val filteredList = remember(allRequests, selectedFilterTab) {
        when (selectedFilterTab) {
            "PENDING" -> allRequests.filter { it.status == "PENDING" }
            "CONFIRMED" -> allRequests.filter { it.status == "CONFIRMED" }
            "NOT_DUE" -> allRequests.filter { it.status == "NOT_DUE" }
            else -> allRequests
        }
    }

    // Auto-fetch staff details when Crew ID is typed
    LaunchedEffect(crewIdInput) {
        val query = crewIdInput.trim().uppercase()
        val found = crewDirectory.find { it.crewId.equals(query, ignoreCase = true) }
        if (found != null) {
            crewName = found.name
            designation = found.designation
        } else {
            if (query.isEmpty()) {
                crewName = ""
                designation = ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Periodical Rest (PR) Portal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (isAdminMode) "Supervisor / Admin Mode 🔓" else "Staff Submission & Status",
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
                    if (!isAdminMode) {
                        Button(
                            onClick = { showAdminPasswordDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = RailwayAmber),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Admin",
                                tint = RailwayDarkNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Admin Login",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = RailwayDarkNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = { isAdminMode = false },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Logout Admin", fontSize = 12.sp)
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
            // Staff PR Submission Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EventAvailable,
                                contentDescription = null,
                                tint = RailwayNavy,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Apply / Request Periodical Rest (PR)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RailwayNavy
                                )
                            )
                        }

                        Text(
                            text = "Enter your Crew ID to auto-fetch your name & designation, then select your sign-off date and time.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )

                        // Crew ID Input
                        OutlinedTextField(
                            value = crewIdInput,
                            onValueChange = { crewIdInput = it },
                            label = { Text("Enter Crew ID / T.E. No. (e.g. KHS1001)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pr_crew_id_input"),
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = RailwayNavy)
                            },
                            trailingIcon = {
                                if (crewName.isNotEmpty()) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Found",
                                        tint = RailwayGreen
                                    )
                                }
                            }
                        )

                        // Auto-filled details row
                        if (crewName.isNotEmpty()) {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = RailwayNavy.copy(alpha = 0.05f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Staff Name", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                                        Text(crewName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = RailwayNavy))
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Designation", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                                        Badge(containerColor = RailwayNavy, contentColor = Color.White) {
                                            Text(designation, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }

                        // Sign-Off Date & Time Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = signOffDate,
                                onValueChange = { signOffDate = it },
                                label = { Text("Sign-off Date") },
                                modifier = Modifier.weight(1f),
                                leadingIcon = {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = RailwayNavy, modifier = Modifier.size(18.dp))
                                }
                            )

                            OutlinedTextField(
                                value = signOffTime,
                                onValueChange = { signOffTime = it },
                                label = { Text("Sign-off Time") },
                                modifier = Modifier.weight(1f),
                                leadingIcon = {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = RailwayNavy, modifier = Modifier.size(18.dp))
                                }
                            )
                        }

                        // Submit Button
                        Button(
                            onClick = {
                                viewModel.submitPrRequest(
                                    crewId = crewIdInput,
                                    crewName = crewName,
                                    designation = designation,
                                    signOffDate = signOffDate,
                                    signOffTime = signOffTime,
                                    onSuccess = {
                                        crewIdInput = ""
                                        crewName = ""
                                        designation = ""
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("pr_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = RailwayNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "PR Request Submit करें",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                    }
                }
            }

            // Summary & Filter Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Periodical Rest Requests List",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RailwayNavy)
                        )
                        Text(
                            text = "Total ${allRequests.size} requests ($pendingCount Pending)",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            // Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "PENDING", "CONFIRMED", "NOT_DUE").forEach { tab ->
                        val isSelected = selectedFilterTab == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilterTab = tab },
                            label = {
                                Text(
                                    when (tab) {
                                        "ALL" -> "सभी (${allRequests.size})"
                                        "PENDING" -> "Pending (${pendingRequests.size})"
                                        "CONFIRMED" -> "PR Updated"
                                        "NOT_DUE" -> "PR not Due"
                                        else -> tab
                                    }
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RailwayNavy,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Requests List
            if (filteredList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "कोई PR अनुरोध नहीं मिला",
                                style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                            )
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { req ->
                    PrRequestCard(
                        request = req,
                        isAdminMode = isAdminMode,
                        onActionClick = {
                            selectedRequestForAction = req
                            showActionDialog = true
                        }
                    )
                }
            }
        }
    }

    // Admin Password Dialog
    if (showAdminPasswordDialog) {
        InChargePasswordDialog(
            onDismiss = { showAdminPasswordDialog = false },
            onSuccess = { _ ->
                isAdminMode = true
                showAdminPasswordDialog = false
            },
            onVerifyPin = { pin ->
                authManager.verifyPin(pin)
            }
        )
    }

    // Admin Action Dialog for Reviewing PR
    if (showActionDialog && selectedRequestForAction != null) {
        val req = selectedRequestForAction!!
        PrAdminReviewDialog(
            request = req,
            onDismiss = { showActionDialog = false },
            onUpdateStatus = { newStatus, remarks ->
                viewModel.updatePrStatus(
                    requestId = req.id,
                    newStatus = newStatus,
                    remarks = remarks,
                    adminId = authManager.getInchargeId()
                )
                showActionDialog = false
            }
        )
    }
}

@Composable
fun PrRequestCard(
    request: PrRequest,
    isAdminMode: Boolean,
    onActionClick: () -> Unit
) {
    val statusColor = when (request.status) {
        "CONFIRMED" -> RailwayGreen
        "NOT_DUE" -> RailwayRed
        else -> RailwayAmber
    }

    val statusText = when (request.status) {
        "CONFIRMED" -> "PR Updated / Confirmed"
        "NOT_DUE" -> "PR not Due"
        else -> "Pending Review"
    }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(RailwayNavy.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = request.crewId.takeLast(4),
                            fontWeight = FontWeight.Bold,
                            color = RailwayNavy,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = request.crewName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                        Text(
                            text = "${request.crewId} • ${request.designation}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }

                // Status Badge
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(modifier = Modifier.height(12.dp))

            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Sign-Off Date & Time", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    Text(
                        "${request.signOffDate} at ${request.signOffTime}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = TextPrimary)
                    )
                }

                if (!request.adminRemarks.isNullOrBlank()) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Admin Remarks", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text(
                            request.adminRemarks,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (request.status == "CONFIRMED") RailwayGreen else RailwayRed
                            )
                        )
                    }
                }
            }

            if (request.reviewedBy != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Reviewed by: ${request.reviewedBy} on ${request.reviewedAt ?: ""}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp)
                )
            }

            // Admin Action Button if in Admin Mode
            if (isAdminMode) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = RailwayNavyLight),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Review / Set Status (Admin Panel)")
                }
            }
        }
    }
}

@Composable
fun PrAdminReviewDialog(
    request: PrRequest,
    onDismiss: () -> Unit,
    onUpdateStatus: (status: String, remarks: String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("CONFIRMED") }
    var remarks by remember { mutableStateOf("PR updated") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = RailwayNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Admin PR Review",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = RailwayNavy)
                    )
                }

                Text(
                    "Staff: ${request.crewName} (${request.crewId}) - ${request.designation}\nSign-off: ${request.signOffDate} ${request.signOffTime}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                )

                Text("Select Action / Status:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            selectedStatus = "CONFIRMED"
                            remarks = "PR updated"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedStatus == "CONFIRMED") RailwayGreen else Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm (PR updated)", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            selectedStatus = "NOT_DUE"
                            remarks = "PR not Due"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedStatus == "NOT_DUE") RailwayRed else Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Not Due (PR not Due)", fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onUpdateStatus(selectedStatus, remarks) },
                        colors = ButtonDefaults.buttonColors(containerColor = RailwayNavy)
                    ) {
                        Text("Save Decision")
                    }
                }
            }
        }
    }
}
