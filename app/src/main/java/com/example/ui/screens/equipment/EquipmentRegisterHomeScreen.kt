package com.example.ui.screens.equipment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.equipment.StoreIssueRecord
import com.example.ui.components.KharsiaLobbyEmblem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentRegisterHomeScreen(
    viewModel: EquipmentViewModel,
    onNavigateToFastIssue: () -> Unit,
    onNavigateToFastReturn: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Admin Auth State for Store Register
    var isAdminUnlocked by remember { mutableStateOf(viewModel.isAdminSessionActive) }
    var adminPinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var showPinDialog by remember { mutableStateOf(false) }

    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val allStoreRecords by viewModel.allStoreRecords.collectAsStateWithLifecycle()
    val activeStoreRecords by viewModel.activeStoreRecords.collectAsStateWithLifecycle()
    val pendingApprovalRecords by viewModel.pendingApprovalRecords.collectAsStateWithLifecycle()
    val pendingReturnRecords by viewModel.pendingReturnRecords.collectAsStateWithLifecycle()
    val allShiftRecords by viewModel.allShiftRecords.collectAsStateWithLifecycle()
    val storeAdminMember by viewModel.storeAdminMember.collectAsStateWithLifecycle()
    val storeShiftSlot by viewModel.storeShiftSlot.collectAsStateWithLifecycle()
    val storeShiftDate by viewModel.storeShiftDate.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KharsiaLobbyEmblem(size = 36.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Store Register (स्टोर रजिस्टर)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Kharsia Lobby • LPG / TM / ALP Equipment",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                            )
                        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightSurface)
        ) {
            // Three Primary Tabs as requested:
            // 1. Issue Form
            // 2. Return / CHO
            // 3. Admin Portal (Password protected)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0F2236),
                contentColor = Color.White
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "समान जारी (Issue)",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) Color(0xFFE5A93C) else Color(0xFFB0BEC5)
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = if (selectedTab == 0) Color(0xFFE5A93C) else Color(0xFFB0BEC5)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "समान वापसी (CHO)",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) Color(0xFFE5A93C) else Color(0xFFB0BEC5)
                        )
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (activeStoreRecords.isNotEmpty()) {
                                    Badge(containerColor = Color(0xFFE65100)) {
                                        Text("${activeStoreRecords.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.AssignmentReturn,
                                contentDescription = null,
                                tint = if (selectedTab == 1) Color(0xFFE5A93C) else Color(0xFFB0BEC5)
                            )
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = {
                        if (!isAdminUnlocked) {
                            showPinDialog = true
                        } else {
                            selectedTab = 2
                        }
                    },
                    text = {
                        Text(
                            "एडमिन पोर्टल",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 2) Color(0xFFE5A93C) else Color(0xFFB0BEC5)
                        )
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                val totalPending = pendingApprovalRecords.size + pendingReturnRecords.size
                                if (totalPending > 0) {
                                    Badge(containerColor = Color(0xFFD32F2F)) {
                                        Text("$totalPending")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isAdminUnlocked) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (selectedTab == 2) Color(0xFFE5A93C) else Color(0xFFB0BEC5)
                            )
                        }
                    }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> StoreIssueTab(
                    viewModel = viewModel,
                    onSwitchToReturn = { selectedTab = 1 }
                )
                1 -> StoreReturnTab(
                    viewModel = viewModel,
                    activeRecords = activeStoreRecords
                )
                2 -> StoreAdminPortalTab(
                    viewModel = viewModel,
                    pendingApprovals = pendingApprovalRecords,
                    pendingReturns = pendingReturnRecords,
                    allRecords = allStoreRecords,
                    shiftRecords = allShiftRecords,
                    storeAdmin = storeAdminMember,
                    currentShift = storeShiftSlot,
                    shiftDate = storeShiftDate,
                    onLockAdmin = {
                        isAdminUnlocked = false
                        viewModel.endAdminSession()
                    }
                )
            }
        }
    }

    // Admin PIN Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF2979FF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Store In-Charge Login")
                }
            },
            text = {
                Column {
                    Text(
                        text = "स्टोर एडमिन पोर्टल खोलने के लिए 4 अंकों का इन-चार्ज पिन (1234) दर्ज करें:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF455A64)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                adminPinInput = it
                                pinError = null
                            }
                        },
                        label = { Text("Admin PIN") },
                        placeholder = { Text("1234") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        isError = pinError != null,
                        supportingText = pinError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (viewModel.verifyAdminPin(adminPinInput)) {
                            isAdminUnlocked = true
                            showPinDialog = false
                            adminPinInput = ""
                            selectedTab = 2
                        } else {
                            pinError = "गलत पिन! डिफ़ॉल्ट इन-चार्ज पिन 1234 है।"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RailwayNavy)
                ) {
                    Text("लॉगिन करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

/**
 * Tab 0: Issue Equipment Form
 * Adheres strictly to the user requirements:
 * - Issue date
 * - Crew ID (auto fetch crew name, designation)
 * - Crew name
 * - Designation
 * - TO time
 * - TO booked (manually fill)
 * - Conditional items:
 *   - LPG: Walkie Talkie dropdown (Motorola, Convey, Other), Walkie Talkie No, Spare Battery No, Detonator No
 *   - Train Manager: Walkie Talkie dropdown, Walkie Talkie No, Spare Battery No
 *   - ALP: FSD dropdown (Actech, APAUL, Other), FSD No
 * - Notes / Tippani
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreIssueTab(
    viewModel: EquipmentViewModel,
    onSwitchToReturn: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var issueDate by remember { mutableStateOf(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())) }
    var crewId by remember { mutableStateOf("") }
    var crewName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("Loco Pilot (Goods)") }
    var roleType by remember { mutableStateOf("LPG") } // "LPG", "TRAIN_MANAGER", "ALP"

    var toTime by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    var toBooked by remember { mutableStateOf("") }

    // Conditional Fields
    val wtBrands = listOf("Motorola", "Convey", "Other")
    var selectedWtBrand by remember { mutableStateOf(wtBrands[0]) }
    var walkieTalkieNo by remember { mutableStateOf("") }
    var spareBatteryNo by remember { mutableStateOf("") }
    var detonatorNo by remember { mutableStateOf("") }

    val fsdBrands = listOf("Actech", "APAUL", "Other")
    var selectedFsdBrand by remember { mutableStateOf(fsdBrands[0]) }
    var fsdNo by remember { mutableStateOf("") }

    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Dropdown expanded states
    var wtBrandExpanded by remember { mutableStateOf(false) }
    var fsdBrandExpanded by remember { mutableStateOf(false) }
    var roleExpanded by remember { mutableStateOf(false) }

    val crewLookup by viewModel.crewLookupResult.collectAsStateWithLifecycle()

    // Auto detect role and name when crew lookup returns
    LaunchedEffect(crewLookup) {
        crewLookup?.let { found ->
            crewName = found.name
            designation = found.designation
            val desUpper = found.designation.uppercase()
            val catUpper = found.category.uppercase()
            roleType = when {
                desUpper.contains("ALP") || catUpper == "ALP" -> "ALP"
                desUpper.contains("GUARD") || desUpper.contains("TRAIN MANAGER") || desUpper.contains("GD") -> "TRAIN_MANAGER"
                else -> "LPG"
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Official Guidelines Notice
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, Color(0xFF81C784)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "खरसिया लॉबी स्टोर नियम (SECR Rules):",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                        )
                        Text(
                            text = "• LPG: Walkie Talkie + Battery + Detonator\n• Train Manager: Walkie Talkie + Battery\n• ALP: Fog Signal Device (FSD)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF2E7D32),
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }
        }

        // Section 1: Issue Date & Crew Info
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCFD8DC)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. बुनियादी विवरण (Basic Details)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = RailwayNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Issue Date Picker Field
                    OutlinedTextField(
                        value = issueDate,
                        onValueChange = { issueDate = it },
                        label = { Text("Issue Date (दिनांक)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = RailwayNavy
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                val d = calendar.get(Calendar.DAY_OF_MONTH)
                                val m = calendar.get(Calendar.MONTH)
                                val y = calendar.get(Calendar.YEAR)
                                DatePickerDialog(context, { _, year, month, day ->
                                    issueDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month + 1, year)
                                }, y, m, d).show()
                            }) {
                                Icon(Icons.Default.EditCalendar, contentDescription = "Pick Date")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("issue_date_field"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Crew ID with auto fetch
                    OutlinedTextField(
                        value = crewId,
                        onValueChange = {
                            crewId = it.uppercase()
                            viewModel.lookupCrew(it)
                            formError = null
                        },
                        label = { Text("Crew ID (क्रू आईडी)") },
                        placeholder = { Text("उदा. KHS1001, KHS1024") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = RailwayNavy
                            )
                        },
                        trailingIcon = {
                            if (crewLookup != null) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Found",
                                    tint = Color(0xFF00C853)
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("store_crew_id_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Crew Name (Auto Fetched)
                    OutlinedTextField(
                        value = crewName,
                        onValueChange = { crewName = it },
                        label = { Text("Crew Name (नाम - ऑटो डिटेक्ट)") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = RailwayNavy)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("store_crew_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Designation & Role Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = designation,
                            onValueChange = { designation = it },
                            label = { Text("Designation") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        // Role Selector Box (LPG / Train Manager / ALP)
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = when (roleType) {
                                    "LPG" -> "LPG (Goods)"
                                    "TRAIN_MANAGER" -> "Train Manager"
                                    "ALP" -> "ALP"
                                    else -> "Other"
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("श्रेणी (Role)") },
                                trailingIcon = {
                                    IconButton(onClick = { roleExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { roleExpanded = true }
                            )

                            DropdownMenu(
                                expanded = roleExpanded,
                                onDismissRequest = { roleExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("LPG (Loco Pilot Goods)") },
                                    onClick = {
                                        roleType = "LPG"
                                        roleExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Train Manager (Guard)") },
                                    onClick = {
                                        roleType = "TRAIN_MANAGER"
                                        roleExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("ALP (Assistant Loco Pilot)") },
                                    onClick = {
                                        roleType = "ALP"
                                        roleExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // TO Time & TO Booked
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = toTime,
                            onValueChange = { toTime = it },
                            label = { Text("TO Time (समय)") },
                            leadingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = RailwayNavy)
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val h = calendar.get(Calendar.HOUR_OF_DAY)
                                    val m = calendar.get(Calendar.MINUTE)
                                    TimePickerDialog(context, { _, hour, minute ->
                                        toTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                                    }, h, m, true).show()
                                }) {
                                    Icon(Icons.Default.AccessTime, contentDescription = "Pick Time")
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = toBooked,
                            onValueChange = { toBooked = it },
                            label = { Text("TO Booked (गंतव्य/ट्रेन)") },
                            placeholder = { Text("उदा. BoxN/Bhilai") },
                            leadingIcon = {
                                Icon(Icons.Default.Train, contentDescription = null, tint = RailwayNavy)
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }
            }
        }

        // Section 2: Conditional Role Equipment Inputs
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, if (roleType == "LPG") Color(0xFF1565C0) else if (roleType == "TRAIN_MANAGER") Color(0xFF7B1FA2) else Color(0xFF2E7D32)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = when (roleType) {
                                "LPG" -> Color(0xFF1565C0)
                                "TRAIN_MANAGER" -> Color(0xFF7B1FA2)
                                else -> Color(0xFF2E7D32)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = when (roleType) {
                                    "LPG" -> "LPG ALLOTTED ITEMS"
                                    "TRAIN_MANAGER" -> "TRAIN MANAGER ALLOTTED ITEMS"
                                    else -> "ALP ALLOTTED ITEMS"
                                },
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // LPG or Train Manager -> Walkie Talkie & Battery
                    if (roleType == "LPG" || roleType == "TRAIN_MANAGER") {
                        // Walkie Talkie Dropdown + Number
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1.2f)) {
                                OutlinedTextField(
                                    value = selectedWtBrand,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Walkie Talkie Make") },
                                    trailingIcon = {
                                        IconButton(onClick = { wtBrandExpanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { wtBrandExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = wtBrandExpanded,
                                    onDismissRequest = { wtBrandExpanded = false }
                                ) {
                                    wtBrands.forEach { brand ->
                                        DropdownMenuItem(
                                            text = { Text(brand) },
                                            onClick = {
                                                selectedWtBrand = brand
                                                wtBrandExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = walkieTalkieNo,
                                onValueChange = { walkieTalkieNo = it },
                                label = { Text("Walkie Talkie No.") },
                                placeholder = { Text("उदा. WT-402") },
                                leadingIcon = {
                                    Icon(Icons.Default.Radio, contentDescription = null, tint = RailwayNavy)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("walkie_talkie_no_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Spare Battery No
                        OutlinedTextField(
                            value = spareBatteryNo,
                            onValueChange = { spareBatteryNo = it },
                            label = { Text("Spare Battery No. (स्पेयर बैटरी नं.)") },
                            placeholder = { Text("उदा. BAT-812") },
                            leadingIcon = {
                                Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = Color(0xFF2E7D32))
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("spare_battery_no_input")
                        )

                        // LPG ONLY: Detonator No
                        if (roleType == "LPG") {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = detonatorNo,
                                onValueChange = { detonatorNo = it },
                                label = { Text("Detonator No. (डिटोनेटर बॉक्स नं.)") },
                                placeholder = { Text("उदा. DET-09") },
                                leadingIcon = {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE65100))
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("detonator_no_input")
                            )
                        }
                    }

                    // ALP ONLY: FSD
                    if (roleType == "ALP") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1.2f)) {
                                OutlinedTextField(
                                    value = selectedFsdBrand,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("FSD Make / Type") },
                                    trailingIcon = {
                                        IconButton(onClick = { fsdBrandExpanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { fsdBrandExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = fsdBrandExpanded,
                                    onDismissRequest = { fsdBrandExpanded = false }
                                ) {
                                    fsdBrands.forEach { brand ->
                                        DropdownMenuItem(
                                            text = { Text(brand) },
                                            onClick = {
                                                selectedFsdBrand = brand
                                                fsdBrandExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = fsdNo,
                                onValueChange = { fsdNo = it },
                                label = { Text("FSD No. (डिवाइस नं.)") },
                                placeholder = { Text("उदा. FSD-104") },
                                leadingIcon = {
                                    Icon(Icons.Default.Devices, contentDescription = null, tint = Color(0xFF2E7D32))
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("fsd_no_input")
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Tippani / Notes
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCFD8DC)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. टिप्पणी या नोट्स (Remarks / Notes)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = RailwayNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("सामान की स्थिति या अन्य विशेष टिप्पणी दर्ज करें...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp)
                            .testTag("store_notes_input")
                    )
                }
            }
        }

        // Validation Error Message
        if (formError != null) {
            item {
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF5350))
                ) {
                    Text(
                        text = formError ?: "",
                        color = Color(0xFFC62828),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    if (crewId.isBlank()) {
                        formError = "कृपया Crew ID दर्ज करें"
                        return@Button
                    }
                    if (crewName.isBlank()) {
                        formError = "कृपया Crew Name दर्ज करें"
                        return@Button
                    }

                    // Role validation
                    when (roleType) {
                        "LPG" -> {
                            if (walkieTalkieNo.isBlank()) {
                                formError = "LPG के लिए Walkie Talkie No. दर्ज करना अनिवार्य है"
                                return@Button
                            }
                        }
                        "TRAIN_MANAGER" -> {
                            if (walkieTalkieNo.isBlank()) {
                                formError = "Train Manager के लिए Walkie Talkie No. दर्ज करना अनिवार्य है"
                                return@Button
                            }
                        }
                        "ALP" -> {
                            if (fsdNo.isBlank()) {
                                formError = "ALP के लिए FSD No. दर्ज करना अनिवार्य है"
                                return@Button
                            }
                        }
                    }

                    formError = null
                    isSubmitting = true

                    viewModel.submitStoreIssue(
                        issueDate = issueDate,
                        crewId = crewId.trim(),
                        crewName = crewName.trim(),
                        designation = designation.trim(),
                        roleType = roleType,
                        toTime = toTime.trim(),
                        toBooked = toBooked.trim(),
                        walkieTalkieBrand = if (roleType != "ALP") selectedWtBrand else null,
                        walkieTalkieNo = if (roleType != "ALP") walkieTalkieNo.trim() else null,
                        spareBatteryNo = if (roleType != "ALP") spareBatteryNo.trim() else null,
                        detonatorNo = if (roleType == "LPG") detonatorNo.trim() else null,
                        fsdBrand = if (roleType == "ALP") selectedFsdBrand else null,
                        fsdNo = if (roleType == "ALP") fsdNo.trim() else null,
                        notes = notes.trim()
                    )

                    // Reset item fields for next entry
                    walkieTalkieNo = ""
                    spareBatteryNo = ""
                    detonatorNo = ""
                    fsdNo = ""
                    notes = ""
                    toBooked = ""
                    isSubmitting = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_store_issue_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = RailwayNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "सामान जारी करें (Issue Equipment)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Tab 1: Return / CHO (Charge Hand Over)
 * Handles equipment return by crew:
 * - CHO Date
 * - CHO Time
 * - Return Condition Notes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreReturnTab(
    viewModel: EquipmentViewModel,
    activeRecords: List<StoreIssueRecord>
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var searchQuery by remember { mutableStateOf("") }
    var selectedRecordForReturn by remember { mutableStateOf<StoreIssueRecord?>(null) }
    var choDate by remember { mutableStateOf(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())) }
    var choTime by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    var returnNotes by remember { mutableStateOf("") }

    val filteredRecords = remember(searchQuery, activeRecords) {
        if (searchQuery.isBlank()) activeRecords
        else {
            val q = searchQuery.trim().lowercase()
            activeRecords.filter {
                it.crewId.lowercase().contains(q) ||
                        it.crewName.lowercase().contains(q) ||
                        (it.walkieTalkieNo?.lowercase()?.contains(q) == true) ||
                        (it.fsdNo?.lowercase()?.contains(q) == true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "वर्तमान में जारी उपकरण (${activeRecords.size})",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = RailwayNavy
                )
            )
            Surface(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFFB74D))
            ) {
                Text(
                    text = "CHO बाकी: ${activeRecords.size}",
                    color = Color(0xFFE65100),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Crew ID, Name या Equipment No से खोजें...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_return_records")
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (activeRecords.isEmpty()) "कोई सक्रिय जारी उपकरण नहीं है। सभी वापस हो चुके हैं।" else "कोई रिकॉर्ड नहीं मिला।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF78909C)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRecords, key = { it.id }) { record ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFCFD8DC)),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = when (record.roleType) {
                                            "LPG" -> Color(0xFF1565C0)
                                            "TRAIN_MANAGER" -> Color(0xFF7B1FA2)
                                            else -> Color(0xFF2E7D32)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = record.roleType,
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${record.crewName} (${record.crewId})",
                                        fontWeight = FontWeight.Bold,
                                        color = RailwayNavy,
                                        fontSize = 14.5.sp
                                    )
                                }

                                Surface(
                                    color = if (record.status == "APPROVED") Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (record.status == "APPROVED") "Approved" else "Pending Appr",
                                        color = if (record.status == "APPROVED") Color(0xFF2E7D32) else Color(0xFFF57F17),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Issue: ${record.issueDate} • TO Time: ${record.toTime} • TO Booked: ${record.toBooked.ifBlank { "N/A" }}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF546E7A))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Equipment Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (!record.walkieTalkieNo.isNullOrBlank()) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("WT (${record.walkieTalkieBrand ?: "WT"}): ${record.walkieTalkieNo}", fontSize = 11.sp) },
                                        leadingIcon = { Icon(Icons.Default.Radio, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    )
                                }
                                if (!record.spareBatteryNo.isNullOrBlank()) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("Bat: ${record.spareBatteryNo}", fontSize = 11.sp) },
                                        leadingIcon = { Icon(Icons.Default.BatteryChargingFull, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    )
                                }
                                if (!record.detonatorNo.isNullOrBlank()) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("Det: ${record.detonatorNo}", fontSize = 11.sp) },
                                        leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    )
                                }
                                if (!record.fsdNo.isNullOrBlank()) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("FSD (${record.fsdBrand ?: "FSD"}): ${record.fsdNo}", fontSize = 11.sp) },
                                        leadingIcon = { Icon(Icons.Default.Devices, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    )
                                }
                            }

                            if (record.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "टिप्पणी: ${record.notes}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78909C), fontSize = 11.5.sp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    selectedRecordForReturn = record
                                    choDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                                    choTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                                    returnNotes = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_cho_return_${record.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.AssignmentReturn, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("समान वापसी (CHO Return) दर्ज करें")
                            }
                        }
                    }
                }
            }
        }
    }

    // Return (CHO) Dialog
    selectedRecordForReturn?.let { record ->
        AlertDialog(
            onDismissRequest = { selectedRecordForReturn = null },
            title = {
                Text(
                    text = "CHO (Charge Hand Over) Return",
                    fontWeight = FontWeight.Bold,
                    color = RailwayNavy
                )
            },
            text = {
                Column {
                    Text(
                        text = "Crew: ${record.crewName} (${record.crewId})",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                    Text(
                        text = "Designation: ${record.designation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF546E7A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // CHO Date
                    OutlinedTextField(
                        value = choDate,
                        onValueChange = { choDate = it },
                        label = { Text("CHO Date (वापसी दिनांक)") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                val d = calendar.get(Calendar.DAY_OF_MONTH)
                                val m = calendar.get(Calendar.MONTH)
                                val y = calendar.get(Calendar.YEAR)
                                DatePickerDialog(context, { _, year, month, day ->
                                    choDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month + 1, year)
                                }, y, m, d).show()
                            }) {
                                Icon(Icons.Default.EditCalendar, contentDescription = null)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // CHO Time
                    OutlinedTextField(
                        value = choTime,
                        onValueChange = { choTime = it },
                        label = { Text("CHO Time (वापसी समय)") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                val h = calendar.get(Calendar.HOUR_OF_DAY)
                                val m = calendar.get(Calendar.MINUTE)
                                TimePickerDialog(context, { _, hour, minute ->
                                    choTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                                }, h, m, true).show()
                            }) {
                                Icon(Icons.Default.AccessTime, contentDescription = null)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Notes
                    OutlinedTextField(
                        value = returnNotes,
                        onValueChange = { returnNotes = it },
                        label = { Text("Tippani / Notes (वापसी स्थिति)") },
                        placeholder = { Text("उदा. उपकरण सही सलामत वापस मिला") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitStoreReturn(
                            recordId = record.id,
                            choDate = choDate.trim(),
                            choTime = choTime.trim(),
                            returnNotes = returnNotes.trim()
                        )
                        selectedRecordForReturn = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) {
                    Text("CHO जमा करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRecordForReturn = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

/**
 * Tab 2: Store Admin Portal
 * Shift-wise tracking (06-14, 14-22, 22-06):
 * - Admin crew ID entry -> Auto fetch name & designation
 * - Live shift statistics: Walkie Talkie, Battery, Detonator, FSD issued
 * - Approve option for equipment taken
 * - Approve option for return
 * - Google Sheets sync
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreAdminPortalTab(
    viewModel: EquipmentViewModel,
    pendingApprovals: List<StoreIssueRecord>,
    pendingReturns: List<StoreIssueRecord>,
    allRecords: List<StoreIssueRecord>,
    shiftRecords: List<com.example.data.equipment.StoreShiftRecord>,
    storeAdmin: com.example.data.equipment.CrewMember?,
    currentShift: String,
    shiftDate: String,
    onLockAdmin: () -> Unit
) {
    var adminCrewIdInput by remember { mutableStateOf(storeAdmin?.crewId ?: "") }
    val shiftOptions = listOf("06:00 - 14:00", "14:00 - 22:00", "22:00 - 06:00")
    var shiftExpanded by remember { mutableStateOf(false) }

    // Sub tabs for Admin Portal: Approvals vs Today's Shift Logs
    var adminSubTab by remember { mutableIntStateOf(0) }

    // Calculate current shift counts
    val currentShiftIssues = remember(allRecords, currentShift, shiftDate) {
        allRecords.filter { it.issueDate == shiftDate && (it.issueShift == currentShift || it.issueShift.isEmpty()) }
    }
    val countWt = currentShiftIssues.count { !it.walkieTalkieNo.isNullOrBlank() }
    val countBat = currentShiftIssues.count { !it.spareBatteryNo.isNullOrBlank() }
    val countDet = currentShiftIssues.count { !it.detonatorNo.isNullOrBlank() }
    val countFsd = currentShiftIssues.count { !it.fsdNo.isNullOrBlank() }
    val countReturns = currentShiftIssues.count { it.isReturned }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin On Duty Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2236)),
                border = BorderStroke(1.5.dp, Color(0xFFE5A93C)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color(0xFFE5A93C),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Store In-Charge Shift Login",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        IconButton(onClick = onLockAdmin) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color(0xFFEF5350))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Crew ID input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = adminCrewIdInput,
                            onValueChange = {
                                adminCrewIdInput = it.uppercase()
                                viewModel.lookupStoreAdmin(it)
                            },
                            label = { Text("Admin Crew ID", color = Color(0xFF90A4AE)) },
                            placeholder = { Text("उदा. KHS1001", color = Color(0xFF546E7A)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFE5A93C),
                                unfocusedBorderColor = Color(0xFF37474F)
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("admin_crew_id_input")
                        )

                        // Shift Selector
                        Box(modifier = Modifier.weight(1.4f)) {
                            OutlinedTextField(
                                value = currentShift,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Shift", color = Color(0xFF90A4AE)) },
                                trailingIcon = {
                                    IconButton(onClick = { shiftExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFFE5A93C),
                                    unfocusedBorderColor = Color(0xFF37474F)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { shiftExpanded = true }
                            )

                            DropdownMenu(
                                expanded = shiftExpanded,
                                onDismissRequest = { shiftExpanded = false }
                            ) {
                                shiftOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            viewModel.setStoreShiftSlot(opt)
                                            shiftExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (storeAdmin != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFF16385C),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ऑन ड्यूटी: ${storeAdmin.name} • ${storeAdmin.designation}",
                                    color = Color(0xFFE0E0E0),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Shift Statistics Card (Walkie Talkie, Battery, Detonator, FSD)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCFD8DC)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "आज का शिफ्ट वितरण ($currentShift)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RailwayNavy
                            )
                        )
                        Text(
                            text = shiftDate,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78909C))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Metric Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCounterBox(
                            title = "Walkie Talkie",
                            count = countWt,
                            color = Color(0xFF1565C0),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCounterBox(
                            title = "Spare Battery",
                            count = countBat,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCounterBox(
                            title = "Detonators",
                            count = countDet,
                            color = Color(0xFFE65100),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCounterBox(
                            title = "FSD Devices",
                            count = countFsd,
                            color = Color(0xFF6A1B9A),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Google Sheets Sync Button for shift
                    Button(
                        onClick = { viewModel.saveAndSyncShiftSummary() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_sync_shift_sheets"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Outlined.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Google Sheets में शिफ्ट समरी सिंक करें")
                    }
                }
            }
        }

        // Sub-Tab Switcher: Pending Approvals (Issue & Return) vs Shift Records
        item {
            TabRow(
                selectedTabIndex = adminSubTab,
                containerColor = Color(0xFFECEFF1),
                contentColor = RailwayNavy
            ) {
                Tab(
                    selected = adminSubTab == 0,
                    onClick = { adminSubTab = 0 },
                    text = {
                        Text(
                            "स्वीकृति पेंडिंग (${pendingApprovals.size + pendingReturns.size})",
                            fontWeight = if (adminSubTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = adminSubTab == 1,
                    onClick = { adminSubTab = 1 },
                    text = {
                        Text(
                            "सभी जारी रिकॉर्ड (${allRecords.size})",
                            fontWeight = if (adminSubTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        if (adminSubTab == 0) {
            // Pending Issue Approvals
            item {
                Text(
                    text = "सामान इशू स्वीकृति पेंडिंग (${pendingApprovals.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = RailwayNavy)
                )
            }

            if (pendingApprovals.isEmpty()) {
                item {
                    Text(
                        text = "कोई नया इशू अनुमोदन बाकी नहीं है।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF78909C)
                    )
                }
            } else {
                items(pendingApprovals, key = { "issue_${it.id}" }) { record ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${record.crewName} (${record.crewId}) - ${record.roleType}",
                                    fontWeight = FontWeight.Bold,
                                    color = RailwayNavy
                                )
                                Text(
                                    text = "${record.issueDate} ${record.toTime}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF78909C)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "TO Booked: ${record.toBooked.ifBlank { "N/A" }}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            // Items info
                            Text(
                                text = buildString {
                                    if (!record.walkieTalkieNo.isNullOrBlank()) append("WT: ${record.walkieTalkieNo} (${record.walkieTalkieBrand})  ")
                                    if (!record.spareBatteryNo.isNullOrBlank()) append("Bat: ${record.spareBatteryNo}  ")
                                    if (!record.detonatorNo.isNullOrBlank()) append("Det: ${record.detonatorNo}  ")
                                    if (!record.fsdNo.isNullOrBlank()) append("FSD: ${record.fsdNo} (${record.fsdBrand})")
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF1565C0))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.approveStoreIssue(record) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("✓ सामान स्वीकृति दें (Approve Issue)")
                            }
                        }
                    }
                }
            }

            // Pending Return Approvals
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "सामान वापसी (CHO) स्वीकृति पेंडिंग (${pendingReturns.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
                )
            }

            if (pendingReturns.isEmpty()) {
                item {
                    Text(
                        text = "कोई वापसी अनुमोदन बाकी नहीं है।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF78909C)
                    )
                }
            } else {
                items(pendingReturns, key = { "return_${it.id}" }) { record ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF80CBC4)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${record.crewName} (${record.crewId}) - CHO Returned",
                                fontWeight = FontWeight.Bold,
                                color = RailwayNavy
                            )
                            Text(
                                text = "CHO Date: ${record.choDate} ${record.choTime} • Tippani: ${record.returnNotes ?: "None"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF546E7A)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.approveStoreReturn(record) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("✓ वापसी स्वीकृत करें (Approve Return)")
                            }
                        }
                    }
                }
            }
        } else {
            // All Store Records Tab
            items(allRecords, key = { "all_${it.id}" }) { record ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${record.crewName} (${record.crewId})",
                                fontWeight = FontWeight.Bold,
                                color = RailwayNavy
                            )
                            Text(
                                text = if (record.isReturned) "Returned" else "Issued",
                                color = if (record.isReturned) Color(0xFF2E7D32) else Color(0xFFE65100),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "${record.issueDate} • TO: ${record.toTime} • Role: ${record.roleType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF78909C)
                        )
                        Text(
                            text = buildString {
                                if (!record.walkieTalkieNo.isNullOrBlank()) append("WT: ${record.walkieTalkieNo} ")
                                if (!record.spareBatteryNo.isNullOrBlank()) append("Bat: ${record.spareBatteryNo} ")
                                if (!record.detonatorNo.isNullOrBlank()) append("Det: ${record.detonatorNo} ")
                                if (!record.fsdNo.isNullOrBlank()) append("FSD: ${record.fsdNo}")
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF37474F))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCounterBox(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF455A64)
                )
            )
        }
    }
}
