package com.example.ui.screens.pr

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Tabs: 0 -> "Apply PR", 1 -> "PR Status & Requests", 2 -> "Admin Panel"
    var selectedTab by remember { mutableStateOf(0) }

    // State for Apply PR Form
    var crewId by remember { mutableStateOf("") }
    var crewName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }

    val cal = Calendar.getInstance()
    val initialDateStr = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(cal.time)
    val initialTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)

    var signOffDate by remember { mutableStateOf(initialDateStr) }
    var signOffTime by remember { mutableStateOf(initialTimeStr) }
    var formError by remember { mutableStateOf<String?>(null) }
    var formSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Admin Auth State
    var showAdminPasswordDialog by remember { mutableStateOf(false) }
    var isAdminAuthenticated by remember { mutableStateOf(viewModel.isAdminSessionActive) }

    // Admin Review Dialog State
    var selectedPrForReview by remember { mutableStateOf<PrRequest?>(null) }
    var adminRemarkOption by remember { mutableStateOf("PR updated") } // "PR updated", "PR not Due", "Custom"
    var customRemarks by remember { mutableStateOf("") }

    // Auto lookup observation
    val crewLookup by viewModel.crewLookupResult.collectAsStateWithLifecycle()

    LaunchedEffect(crewLookup) {
        val found = crewLookup
        if (found != null) {
            crewName = found.name
            designation = found.designation
        }
    }

    val allRequests by viewModel.allPrRequests.collectAsStateWithLifecycle()

    // Listen to UI messages
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Date Picker Dialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val c = Calendar.getInstance()
                c.set(year, month, dayOfMonth)
                signOffDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Time Picker Dialog
    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                signOffTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PR Remark (Periodical Rest)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "SECR Kharsia Lobby • Mark PR & Admin Panel",
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
                    if (isAdminAuthenticated) {
                        TextButton(
                            onClick = {
                                viewModel.endAdminSession()
                                isAdminAuthenticated = false
                            }
                        ) {
                            Text("Logout Admin", color = RailwayAmber, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        IconButton(onClick = { showAdminPasswordDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Login",
                                tint = RailwayAmber
                            )
                        }
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
                .consumeWindowInsets(paddingValues)
                .imePadding()
                .background(LightSurface)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = RailwayNavy
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Mark PR (स्टाफ)", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.EditCalendar, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "PR List (${allRequests.size})",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    icon = { Icon(Icons.Default.FormatListBulleted, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = {
                        if (isAdminAuthenticated || viewModel.isAdminSessionActive) {
                            isAdminAuthenticated = true
                            selectedTab = 2
                        } else {
                            showAdminPasswordDialog = true
                        }
                    },
                    text = { Text("Admin Panel", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Shield, contentDescription = null) }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // APPLY PR TAB
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = RailwayNavy.copy(alpha = 0.1f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.EventAvailable,
                                                    contentDescription = null,
                                                    tint = RailwayNavy
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Periodical Rest (PR) Application",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = RailwayNavy
                                                )
                                            )
                                            Text(
                                                text = "स्टाफ अपनी Crew ID डालकर PR लगा सकते हैं",
                                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Crew ID input with auto fetch
                                    OutlinedTextField(
                                        value = crewId,
                                        onValueChange = {
                                            crewId = it
                                            formError = null
                                            viewModel.lookupCrew(it)
                                        },
                                        label = { Text("Crew ID (e.g. KHS1001, KHS1002)") },
                                        placeholder = { Text("Enter Crew ID to auto-fetch details") },
                                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                        trailingIcon = {
                                            if (crewLookup != null) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = "Matched", tint = RailwayGreen)
                                            }
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("pr_crew_id_input"),
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Name field (auto filled or manual)
                                    OutlinedTextField(
                                        value = crewName,
                                        onValueChange = { crewName = it },
                                        label = { Text("Staff Name (नाम)") },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("pr_crew_name_input"),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Designation field (auto filled or manual)
                                    OutlinedTextField(
                                        value = designation,
                                        onValueChange = { designation = it },
                                        label = { Text("Designation (पद - LP / ALP / Guard)") },
                                        leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("pr_designation_input"),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Text(
                                        text = "Sign-Off Details (साइन-ऑफ दिनांक एवं समय):",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = RailwayNavy
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Date picker row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        OutlinedCard(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { datePickerDialog.show() }
                                                .testTag("pr_date_picker_btn"),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = RailwayNavy)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text("Sign-Off Date", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                                                    Text(signOffDate, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                                }
                                            }
                                        }

                                        // Time picker row
                                        OutlinedCard(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { timePickerDialog.show() }
                                                .testTag("pr_time_picker_btn"),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = RailwayNavy)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text("Sign-Off Time", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                                                    Text(signOffTime, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                                }
                                            }
                                        }
                                    }

                                    if (formError != null) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = formError!!,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                    }

                                    if (formSuccessMessage != null) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Surface(
                                            color = Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RailwayGreen)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = formSuccessMessage!!,
                                                    color = Color(0xFF2E7D32),
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = {
                                            val trimmedId = crewId.trim()
                                            val trimmedName = crewName.trim()
                                            val trimmedDesig = designation.trim()

                                            if (trimmedId.isEmpty()) {
                                                formError = "कृपया Crew ID दर्ज करें"
                                                return@Button
                                            }
                                            if (trimmedName.isEmpty()) {
                                                formError = "कृपया कर्मचारी का नाम दर्ज करें"
                                                return@Button
                                            }
                                            if (trimmedDesig.isEmpty()) {
                                                formError = "कृपया पद (Designation) दर्ज करें"
                                                return@Button
                                            }

                                            formError = null
                                            viewModel.submitPrRequest(
                                                crewId = trimmedId,
                                                crewName = trimmedName,
                                                designation = trimmedDesig,
                                                signOffDate = signOffDate,
                                                signOffTime = signOffTime
                                            )

                                            formSuccessMessage = "PR आवेदन सफलतापूर्वक दर्ज किया गया!"
                                            crewId = ""
                                            crewName = ""
                                            designation = ""
                                            viewModel.clearCrewLookup()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("submit_pr_request_btn"),
                                        colors = ButtonDefaults.buttonColors(containerColor = RailwayNavy),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "PR दर्ज करें / Submit PR Request",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Sample helper card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = RailwayNavy.copy(alpha = 0.05f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "💡 त्वरित सुझाव (Quick Tips):",
                                        fontWeight = FontWeight.Bold,
                                        color = RailwayNavy
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "• KHS1001, KHS1002, KHS1003, KHS1004 आदि डालने पर नाम व पद अपने आप आ जाएगा।\n• अपना अंतिम साइन-ऑफ दिनांक और समय सही चुनें।\n• आवेदन के बाद 'PR List' टैब में स्टेटस देख सकते हैं।",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 20.sp)
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // PR LIST TAB (Pending + Approved + All requests)
                    PrListView(
                        requests = allRequests,
                        isAdmin = isAdminAuthenticated,
                        onReview = { pr ->
                            selectedPrForReview = pr
                        },
                        onDelete = { prId ->
                            viewModel.deletePrRequest(prId)
                        }
                    )
                }

                2 -> {
                    // ADMIN PANEL TAB
                    AdminPanelView(
                        requests = allRequests,
                        onReview = { pr ->
                            selectedPrForReview = pr
                        },
                        onDelete = { prId ->
                            viewModel.deletePrRequest(prId)
                        },
                        onLogout = {
                            viewModel.endAdminSession()
                            isAdminAuthenticated = false
                            selectedTab = 0
                        }
                    )
                }
            }
        }
    }

    // Admin Verification Password Dialog
    if (showAdminPasswordDialog) {
        InChargePasswordDialog(
            title = "Admin Panel Access (PIN: 1234)",
            onDismiss = { showAdminPasswordDialog = false },
            onConfirm = { pin ->
                if (viewModel.verifyAdminPin(pin)) {
                    isAdminAuthenticated = true
                    showAdminPasswordDialog = false
                    selectedTab = 2
                } else {
                    // Show error or keep dialog
                }
            }
        )
    }

    // Admin Review Dialog (Confirm or Not with Remarks)
    if (selectedPrForReview != null) {
        val pr = selectedPrForReview!!
        AlertDialog(
            onDismissRequest = { selectedPrForReview = null },
            title = {
                Text(
                    text = "PR Request Review / पुष्टि",
                    fontWeight = FontWeight.Bold,
                    color = RailwayNavy
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Staff: ${pr.crewName} (${pr.crewId})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Designation: ${pr.designation}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Text(
                        text = "Sign-Off: ${pr.signOffDate} at ${pr.signOffTime}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Text(
                        text = "Requested: ${pr.requestDate}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Select Remarks / टिप्पणी चुनें:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = adminRemarkOption == "PR lag gya" || adminRemarkOption == "PR updated",
                            onClick = { adminRemarkOption = "PR lag gya" },
                            label = { Text("PR lag gya") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RailwayGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = adminRemarkOption == "PR not due" || adminRemarkOption == "PR not Due",
                            onClick = { adminRemarkOption = "PR not due" },
                            label = { Text("PR not due") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RailwayRed,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = adminRemarkOption == "Custom",
                            onClick = { adminRemarkOption = "Custom" },
                            label = { Text("Custom") }
                        )
                    }

                    if (adminRemarkOption == "Custom") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customRemarks,
                            onValueChange = { customRemarks = it },
                            label = { Text("Enter Custom Remarks") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Confirm button -> status: Confirmed
                    Button(
                        onClick = {
                            val finalRemarks = if (adminRemarkOption == "Custom") customRemarks else if (adminRemarkOption == "PR updated") "PR lag gya" else adminRemarkOption
                            viewModel.reviewPrRequest(
                                requestId = pr.id,
                                status = "Confirmed",
                                remarks = finalRemarks
                            )
                            selectedPrForReview = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RailwayGreen),
                        modifier = Modifier.testTag("admin_confirm_pr_btn")
                    ) {
                        Text("Approve (PR lag gya)")
                    }

                    // Reject / Not Due button -> status: Not Due
                    Button(
                        onClick = {
                            val finalRemarks = if (adminRemarkOption == "Custom") customRemarks else if (adminRemarkOption == "PR updated" || adminRemarkOption == "PR lag gya") "PR not due" else adminRemarkOption
                            viewModel.reviewPrRequest(
                                requestId = pr.id,
                                status = "Not Due",
                                remarks = finalRemarks
                            )
                            selectedPrForReview = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RailwayRed),
                        modifier = Modifier.testTag("admin_reject_pr_btn")
                    ) {
                        Text("Reject (PR not due)")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPrForReview = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun PrListView(
    requests: List<PrRequest>,
    isAdmin: Boolean,
    onReview: (PrRequest) -> Unit,
    onDelete: (Long) -> Unit
) {
    var statusFilter by remember { mutableStateOf("All") }

    val filteredList = remember(requests, statusFilter) {
        when (statusFilter) {
            "Pending" -> requests.filter { it.status == "Pending" }
            "Confirmed" -> requests.filter { it.status == "Confirmed" }
            "Not Due" -> requests.filter { it.status == "Not Due" }
            else -> requests
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Pending", "Confirmed", "Not Due").forEach { filter ->
                FilterChip(
                    selected = statusFilter == filter,
                    onClick = { statusFilter = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RailwayNavy,
                        selectedLabelColor = Color.White
                    )
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "कोई PR अनुरोध नहीं मिला ($statusFilter)",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { req ->
                    PrRequestCard(
                        request = req,
                        isAdmin = isAdmin,
                        onReview = { onReview(req) },
                        onDelete = { onDelete(req.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PrRequestCard(
    request: PrRequest,
    isAdmin: Boolean,
    onReview: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (request.status) {
        "Confirmed" -> RailwayGreen
        "Not Due" -> RailwayRed
        else -> RailwayAmber
    }

    val statusBg = when (request.status) {
        "Confirmed" -> Color(0xFFE8F5E9)
        "Not Due" -> Color(0xFFFFEBEE)
        else -> Color(0xFFFFF8E1)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
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
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        color = RailwayNavy.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = RailwayNavy
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = request.crewName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "${request.crewId} • ${request.designation}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = request.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = statusColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Sign-Off Date & Time:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                    Text(
                        text = "${request.signOffDate} at ${request.signOffTime}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = RailwayNavy)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Requested On:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                    Text(
                        text = request.requestDate,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }

            if (request.remarks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = LightSurface,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Notes, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Admin Remarks: ${request.remarks}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = if (request.status == "Confirmed") RailwayGreen else if (request.status == "Not Due") RailwayRed else TextPrimary
                            )
                        )
                    }
                }
            }

            if (isAdmin) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onReview,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = RailwayNavy),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.RateReview, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Review / Action")
                    }

                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RailwayRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPanelView(
    requests: List<PrRequest>,
    onReview: (PrRequest) -> Unit,
    onDelete: (Long) -> Unit,
    onLogout: () -> Unit
) {
    val pendingRequests = requests.filter { it.status == "Pending" }
    val confirmedRequests = requests.filter { it.status == "Confirmed" }
    val notDueRequests = requests.filter { it.status == "Not Due" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin header card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RailwayNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = RailwayAmber,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LOBBY ADMIN MONITORING",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminStatCard(
                            modifier = Modifier.weight(1f),
                            title = "PENDING",
                            count = "${pendingRequests.size}",
                            color = RailwayAmber
                        )
                        AdminStatCard(
                            modifier = Modifier.weight(1f),
                            title = "CONFIRMED",
                            count = "${confirmedRequests.size}",
                            color = RailwayGreen
                        )
                        AdminStatCard(
                            modifier = Modifier.weight(1f),
                            title = "NOT DUE",
                            count = "${notDueRequests.size}",
                            color = RailwayRed
                        )
                    }
                }
            }
        }

        // Section title
        item {
            Text(
                text = "Pending PR Action Queue (${pendingRequests.size}):",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = RailwayNavy
                )
            )
        }

        if (pendingRequests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No pending PR requests at the moment. All up-to-date!",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }
                }
            }
        } else {
            items(pendingRequests) { req ->
                PrRequestCard(
                    request = req,
                    isAdmin = true,
                    onReview = { onReview(req) },
                    onDelete = { onDelete(req.id) }
                )
            }
        }

        // All Reviewed section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Recently Processed PR History:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = RailwayNavy
                )
            )
        }

        val processed = requests.filter { it.status != "Pending" }
        if (processed.isEmpty()) {
            item {
                Text(
                    text = "No history yet",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            }
        } else {
            items(processed) { req ->
                PrRequestCard(
                    request = req,
                    isAdmin = true,
                    onReview = { onReview(req) },
                    onDelete = { onDelete(req.id) }
                )
            }
        }
    }
}

@Composable
fun AdminStatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                count,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    }
}
