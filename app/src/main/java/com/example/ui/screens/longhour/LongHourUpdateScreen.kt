package com.example.ui.screens.longhour

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.equipment.LongHourDutyRecord
import com.example.ui.components.KharsiaLobbyEmblem
import com.example.ui.screens.equipment.EquipmentViewModel
import com.example.ui.screens.equipment.InChargePasswordDialog
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongHourUpdateScreen(
    viewModel: EquipmentViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Portals: 0 -> "Crew Portal", 1 -> "Admin Portal (Password Protected)"
    var selectedPortal by remember { mutableStateOf(0) }

    // Admin Auth State
    var showAdminPasswordDialog by remember { mutableStateOf(false) }
    var isAdminVerified by remember { mutableStateOf(viewModel.isAdminSessionActive) }

    // Ticker to recompute live duty hours duration dynamically every 30 seconds
    var ticker by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30000)
            ticker = System.currentTimeMillis()
        }
    }

    // Snackbar Host State
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
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
                                text = "Long Hour Update",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "SECR Kharsia Lobby • Duty Hours & Overtime Monitoring",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackgroundNavy
                )
            )
        },
        containerColor = DarkCanvasBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Portal Selector Tabs
            TabRow(
                selectedTabIndex = selectedPortal,
                containerColor = DarkSurfaceNavy,
                contentColor = RailwayAmber,
                divider = {}
            ) {
                Tab(
                    selected = selectedPortal == 0,
                    onClick = { selectedPortal = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Crew Portal",
                                fontWeight = if (selectedPortal == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    selectedContentColor = RailwayAmber,
                    unselectedContentColor = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.testTag("tab_crew_portal")
                )

                Tab(
                    selected = selectedPortal == 1,
                    onClick = {
                        if (isAdminVerified) {
                            selectedPortal = 1
                        } else {
                            showAdminPasswordDialog = true
                        }
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isAdminVerified) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Admin Portal",
                                fontWeight = if (selectedPortal == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    selectedContentColor = RailwayAmber,
                    unselectedContentColor = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.testTag("tab_admin_portal")
                )
            }

            // Portal Content
            when (selectedPortal) {
                0 -> {
                    CrewPortalView(
                        viewModel = viewModel,
                        ticker = ticker
                    )
                }
                1 -> {
                    AdminPortalView(
                        viewModel = viewModel,
                        ticker = ticker,
                        onLockAdmin = {
                            viewModel.endAdminSession()
                            isAdminVerified = false
                            selectedPortal = 0
                        }
                    )
                }
            }
        }
    }

    // Admin Password Dialog
    if (showAdminPasswordDialog) {
        InChargePasswordDialog(
            title = "Admin Portal Verification",
            onDismiss = { showAdminPasswordDialog = false },
            onConfirm = { enteredPin ->
                if (viewModel.verifyAdminPin(enteredPin)) {
                    isAdminVerified = true
                    showAdminPasswordDialog = false
                    selectedPortal = 1
                } else {
                    // Password incorrect
                }
            }
        )
    }
}

/**
 * CREW PORTAL:
 * Data entry form for:
 * LPG ID (auto detect name), ALP ID (auto detect name),
 * Train no, Loco no, Sign on time, Sign on date,
 * Select direction (UP / DN / CIC dropdown),
 * Current Station Code, Arrival time of current station,
 * Current position of your train (1-7 dropdown) & Timing.
 *
 * Upar me sign on time se uska total duty hour duration dikhna chahiye
 * Us crew ka naam sign on aur duty hour dikhte rahega jab tak admin usko close naa kar de.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrewPortalView(
    viewModel: EquipmentViewModel,
    ticker: Long
) {
    val context = LocalContext.current
    val activeRecords by viewModel.activeLongHourRecords.collectAsStateWithLifecycle()

    // Form fields
    var lpgId by remember { mutableStateOf("") }
    var lpgName by remember { mutableStateOf("") }
    var alpId by remember { mutableStateOf("") }
    var alpName by remember { mutableStateOf("") }

    var trainNo by remember { mutableStateOf("") }
    var locoNo by remember { mutableStateOf("") }

    val cal = Calendar.getInstance()
    val defaultDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(cal.time)
    val defaultTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)

    var signOnDate by remember { mutableStateOf(defaultDate) }
    var signOnTime by remember { mutableStateOf(defaultTime) }

    // Direction Dropdown: UP / DN / CIC
    var direction by remember { mutableStateOf("UP") }
    var directionExpanded by remember { mutableStateOf(false) }
    val directionOptions = listOf("UP", "DN", "CIC")

    // Current Station
    var currentStationCode by remember { mutableStateOf("") }
    var arrivalTimeCurrentStation by remember { mutableStateOf(defaultTime) }

    // Current train position (1 to 7) dropdown:
    val positionOptions = listOf(
        "1. Train Placed",
        "2. Train Release",
        "3. Train Drawn Out",
        "4. Train Ready",
        "5. GDR Start",
        "6. GDR Complete",
        "7. Train Departure"
    )
    var currentPosition by remember { mutableStateOf(positionOptions[0]) }
    var positionExpanded by remember { mutableStateOf(false) }
    var positionTiming by remember { mutableStateOf(defaultTime) }

    var formError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Auto-detect LPG name
    val lpgLookup by viewModel.lpgLookupResult.collectAsStateWithLifecycle()
    LaunchedEffect(lpgLookup) {
        lpgLookup?.let {
            lpgName = it.name
        }
    }

    // Auto-detect ALP name
    val alpLookup by viewModel.alpLookupResult.collectAsStateWithLifecycle()
    LaunchedEffect(alpLookup) {
        alpLookup?.let {
            alpName = it.name
        }
    }

    // Date Picker for Sign-On Date
    val signOnDatePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val c = Calendar.getInstance()
                c.set(year, month, dayOfMonth)
                signOnDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Time Picker for Sign-On Time
    val signOnTimePicker = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                signOnTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        )
    }

    // Time Picker for Current Station Arrival Time
    val arrivalTimePicker = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                arrivalTimeCurrentStation = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        )
    }

    // Time Picker for Position Timing
    val positionTimePicker = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                positionTiming = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- TOP BANNER: LIVE ACTIVE DUTIES WITH DUTY HOUR DURATION ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceNavy),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorderBlue))
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
                                    .size(12.dp)
                                    .background(Color(0xFF00E676), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LIVE DUTY HOURS TRACKER",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF00E676)
                                )
                            )
                        }

                        Text(
                            text = "${activeRecords.size} Active",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = RailwayAmber
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (activeRecords.isEmpty()) {
                        Text(
                            text = "No active long hour duties currently running. Fill the form below to initiate duty tracking.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.6f))
                        )
                    } else {
                        Text(
                            text = "Duty status remains live until closed by Lobby Admin:",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        activeRecords.forEach { record ->
                            val liveDuration = viewModel.calculateDutyDuration(record.signOnDate, record.signOnTime)
                            ActiveDutyMiniCard(record = record, liveDuration = liveDuration)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // --- CREW DATA ENTRY FORM ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceNavy),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorderBlue))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = RailwayAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Crew Duty Hour Entry Form",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    Text(
                        text = "Enter Crew IDs to auto-detect names. All fields will auto-save.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. LPG ID & Auto Name
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = lpgId,
                            onValueChange = {
                                lpgId = it.uppercase()
                                viewModel.lookupLpg(it)
                            },
                            label = { Text("LPG Crew ID") },
                            placeholder = { Text("e.g. KHS001") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_lpg_id"),
                            colors = darkTextFieldColors(),
                            trailingIcon = {
                                if (lpgLookup != null) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Matched", tint = Color(0xFF00E676))
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = lpgName,
                            onValueChange = { lpgName = it },
                            label = { Text("LPG Name") },
                            placeholder = { Text("Auto detected") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("input_lpg_name"),
                            colors = darkTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. ALP ID & Auto Name
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = alpId,
                            onValueChange = {
                                alpId = it.uppercase()
                                viewModel.lookupAlp(it)
                            },
                            label = { Text("ALP Crew ID") },
                            placeholder = { Text("e.g. KHS002") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_alp_id"),
                            colors = darkTextFieldColors(),
                            trailingIcon = {
                                if (alpLookup != null) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Matched", tint = Color(0xFF00E676))
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = alpName,
                            onValueChange = { alpName = it },
                            label = { Text("ALP Name") },
                            placeholder = { Text("Auto detected") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("input_alp_name"),
                            colors = darkTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Train No & Loco No
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = trainNo,
                            onValueChange = { trainNo = it },
                            label = { Text("Train No.") },
                            placeholder = { Text("e.g. 12834 / BOXN") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_train_no"),
                            colors = darkTextFieldColors()
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = locoNo,
                            onValueChange = { locoNo = it },
                            label = { Text("Loco No.") },
                            placeholder = { Text("e.g. 31245 WAG-9") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_loco_no"),
                            colors = darkTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Sign-on Date & Sign-on Time (with pickers)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = signOnDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sign On Date") },
                            trailingIcon = {
                                IconButton(onClick = { signOnDatePicker.show() }) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = "Pick date", tint = RailwayAmber)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clickable { signOnDatePicker.show() }
                                .testTag("input_sign_on_date"),
                            colors = darkTextFieldColors()
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = signOnTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sign On Time") },
                            trailingIcon = {
                                IconButton(onClick = { signOnTimePicker.show() }) {
                                    Icon(Icons.Default.AccessTime, contentDescription = "Pick time", tint = RailwayAmber)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clickable { signOnTimePicker.show() }
                                .testTag("input_sign_on_time"),
                            colors = darkTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. Select Direction Dropdown: UP / DN / CIC
                    ExposedDropdownMenuBox(
                        expanded = directionExpanded,
                        onExpandedChange = { directionExpanded = !directionExpanded }
                    ) {
                        OutlinedTextField(
                            value = direction,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Direction (UP / DN / CIC)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = directionExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("select_direction"),
                            colors = darkTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = directionExpanded,
                            onDismissRequest = { directionExpanded = false },
                            modifier = Modifier.background(DarkSurfaceNavy)
                        ) {
                            directionOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt, color = Color.White, fontWeight = FontWeight.SemiBold) },
                                    onClick = {
                                        direction = opt
                                        directionExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6. Current Station Code & Arrival Time
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = currentStationCode,
                            onValueChange = { currentStationCode = it.uppercase() },
                            label = { Text("Station Code (Khada / Passed)") },
                            placeholder = { Text("e.g. RIG, ROB, KHS") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("input_current_station_code"),
                            colors = darkTextFieldColors()
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = arrivalTimeCurrentStation,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Arrival Time") },
                            trailingIcon = {
                                IconButton(onClick = { arrivalTimePicker.show() }) {
                                    Icon(Icons.Default.Schedule, contentDescription = "Pick arrival time", tint = RailwayAmber)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clickable { arrivalTimePicker.show() }
                                .testTag("input_arrival_time"),
                            colors = darkTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 7. Current Position of Train Dropdown (1 to 7)
                    ExposedDropdownMenuBox(
                        expanded = positionExpanded,
                        onExpandedChange = { positionExpanded = !positionExpanded }
                    ) {
                        OutlinedTextField(
                            value = currentPosition,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Current Position of Your Train") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = positionExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("select_current_position"),
                            colors = darkTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = positionExpanded,
                            onDismissRequest = { positionExpanded = false },
                            modifier = Modifier.background(DarkSurfaceNavy)
                        ) {
                            positionOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt, color = Color.White) },
                                    onClick = {
                                        currentPosition = opt
                                        positionExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 8. Their Timing (Position Timing)
                    OutlinedTextField(
                        value = positionTiming,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Position Event Timing") },
                        trailingIcon = {
                            IconButton(onClick = { positionTimePicker.show() }) {
                                Icon(Icons.Default.Timelapse, contentDescription = "Pick timing", tint = RailwayAmber)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { positionTimePicker.show() }
                            .testTag("input_position_timing"),
                        colors = darkTextFieldColors()
                    )

                    // Error display if any
                    if (formError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = formError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (lpgId.isBlank()) {
                                formError = "Please enter LPG Crew ID"
                                return@Button
                            }
                            if (trainNo.isBlank()) {
                                formError = "Please enter Train No."
                                return@Button
                            }
                            if (currentStationCode.isBlank()) {
                                formError = "Please enter Current Station Code"
                                return@Button
                            }

                            formError = null
                            isSubmitting = true
                            viewModel.submitLongHourDuty(
                                lpgId = lpgId,
                                lpgName = lpgName.ifBlank { "Crew $lpgId" },
                                alpId = alpId,
                                alpName = alpName.ifBlank { "ALP $alpId" },
                                trainNo = trainNo,
                                locoNo = locoNo,
                                signOnDate = signOnDate,
                                signOnTime = signOnTime,
                                direction = direction,
                                currentStationCode = currentStationCode,
                                arrivalTimeCurrentStation = arrivalTimeCurrentStation,
                                currentTrainPosition = currentPosition,
                                positionTiming = positionTiming
                            )
                            isSubmitting = false

                            // Reset entry fields
                            trainNo = ""
                            locoNo = ""
                            currentStationCode = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RailwayAmber),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_submit_long_hour")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SUBMIT LONG HOUR DUTY",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * ADMIN PORTAL:
 * Password protected.
 * Shows all long hour duty records with:
 * LPG Name, ALP Name, Train No, Sign on date & time, Direction, Current station, Position,
 * and LIVE DUTY HOURS DURATION.
 * Admin can close duty by entering:
 * Relief Time, Relief Date, and Relief Station Code.
 * Also configures Google Sheet auto-save endpoint.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminPortalView(
    viewModel: EquipmentViewModel,
    ticker: Long,
    onLockAdmin: () -> Unit
) {
    val allRecords by viewModel.allLongHourRecords.collectAsStateWithLifecycle()
    val webhookUrl by viewModel.sheetsWebhookUrl.collectAsStateWithLifecycle()

    var showWebhookDialog by remember { mutableStateOf(false) }
    var closingRecord by remember { mutableStateOf<LongHourDutyRecord?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Admin Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceNavy),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorderBlue))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "LOBBY ADMIN CONSOLE",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Active Supervisor: ${viewModel.currentAdminUser}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                                )
                            }
                        }

                        IconButton(
                            onClick = onLockAdmin,
                            modifier = Modifier.testTag("btn_lock_admin")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock Console", tint = Color.Red)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Google Sheet Auto-Save Status / Settings Button
                    OutlinedButton(
                        onClick = { showWebhookDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_google_sheet_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = if (webhookUrl.isNotBlank()) Color(0xFF00E676) else RailwayAmber
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (webhookUrl.isNotBlank()) "Google Sheet Sync: Connected ✓" else "Setup Google Sheet Auto-Save Webhook",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }

        // Summary Counts
        item {
            val activeCount = allRecords.count { !it.isClosed }
            val closedCount = allRecords.count { it.isClosed }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricPill(
                    title = "Active Duties",
                    value = "$activeCount",
                    color = Color(0xFF00E676),
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    title = "Relieved / Closed",
                    value = "$closedCount",
                    color = RailwayAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Records List
        if (allRecords.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No duty records available.",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            items(allRecords, key = { it.id }) { record ->
                AdminDutyCard(
                    record = record,
                    liveDuration = if (record.isClosed) {
                        viewModel.calculateDutyDurationBetween(
                            record.signOnDate,
                            record.signOnTime,
                            record.reliefDate ?: record.signOnDate,
                            record.reliefTime ?: record.signOnTime
                        )
                    } else {
                        viewModel.calculateDutyDuration(record.signOnDate, record.signOnTime)
                    },
                    onRelieveDuty = { closingRecord = record },
                    onDelete = { viewModel.deleteLongHourRecord(record.id) }
                )
            }
        }
    }

    // Dialog for Admin to enter Relief details & Close Duty
    closingRecord?.let { record ->
        ReliefDutyDialog(
            record = record,
            onDismiss = { closingRecord = null },
            onConfirm = { date, time, station ->
                viewModel.closeLongHourDutyByAdmin(
                    id = record.id,
                    reliefDate = date,
                    reliefTime = time,
                    reliefStationCode = station,
                    record = record
                )
                closingRecord = null
            }
        )
    }

    // Dialog for Google Sheet Webhook setup
    if (showWebhookDialog) {
        GoogleSheetWebhookDialog(
            currentUrl = webhookUrl,
            onDismiss = { showWebhookDialog = false },
            onSave = { newUrl ->
                viewModel.setSheetsWebhookUrl(newUrl)
                showWebhookDialog = false
            }
        )
    }
}

/**
 * Compact mini-card showing active crew and dynamic live duty hours
 */
@Composable
private fun ActiveDutyMiniCard(
    record: LongHourDutyRecord,
    liveDuration: String
) {
    Surface(
        color = DarkBackgroundNavy,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderBlue),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Train: ${record.trainNo} • ${record.direction}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = RailwayAmber
                    )
                )
                Text(
                    text = "LPG: ${record.lpgName} (${record.lpgId})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
                if (record.alpId.isNotBlank()) {
                    Text(
                        text = "ALP: ${record.alpName} (${record.alpId})",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
                    )
                }
                Text(
                    text = "Sign On: ${record.signOnDate} at ${record.signOnTime} | Stn: ${record.currentStationCode}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f))
                )
            }

            // Live Duty Hours Badge
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = Color(0xFFE53935).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935))
                ) {
                    Text(
                        text = "⏱ $liveDuration",
                        color = Color(0xFFFF8A80),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "RUNNING",
                    color = Color(0xFF00E676),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/**
 * Detailed Admin card for long hour records with relief actions
 */
@Composable
private fun AdminDutyCard(
    record: LongHourDutyRecord,
    liveDuration: String,
    onRelieveDuty: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceNavy),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (record.isClosed) DarkBorderBlue else Color(0xFFE53935).copy(alpha = 0.6f)
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Train, Loco, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Train ${record.trainNo}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFF1E88E5).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E88E5))
                    ) {
                        Text(
                            text = record.direction,
                            color = Color(0xFF90CAF9),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (record.locoNo.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Loco: ${record.locoNo}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                        )
                    }
                }

                // Status pill
                if (record.isClosed) {
                    Surface(
                        color = Color(0xFF00E676).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676))
                    ) {
                        Text(
                            text = "DUTY CLOSED ✓",
                            color = Color(0xFF00E676),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        color = Color(0xFFE53935).copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935))
                    ) {
                        Text(
                            text = "● LIVE ACTIVE",
                            color = Color(0xFFFF8A80),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Crew details
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LPG: ${record.lpgName} (${record.lpgId})",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    if (record.alpId.isNotBlank()) {
                        Text(
                            text = "ALP: ${record.alpName} (${record.alpId})",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                        )
                    }
                }

                // Total duty duration
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Duty Duration",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f))
                    )
                    Text(
                        text = liveDuration,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (record.isClosed) RailwayAmber else Color(0xFFFF5252)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DarkBorderBlue.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Journey Tracking Info
            Text(
                text = "Sign On: ${record.signOnDate} at ${record.signOnTime}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
            )
            Text(
                text = "Current Station: ${record.currentStationCode} (Arrival: ${record.arrivalTimeCurrentStation})",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
            )
            Text(
                text = "Position: ${record.currentTrainPosition} @ ${record.positionTiming}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
            )

            // If closed, display relief information
            if (record.isClosed) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = DarkBackgroundNavy,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Relieved at: ${record.reliefStationCode} | Date: ${record.reliefDate} ${record.reliefTime}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E676)
                            )
                        )
                        Text(
                            text = "Closed By: ${record.closedBy ?: "Admin"} at ${record.closedAt}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Record",
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                }

                if (!record.isClosed) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onRelieveDuty,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_relieve_crew_${record.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Relieve Crew / ड्यूटी हटाए",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Relief Dialog for admin to input relief date, time, and station code
 */
@Composable
private fun ReliefDutyDialog(
    record: LongHourDutyRecord,
    onDismiss: () -> Unit,
    onConfirm: (date: String, time: String, stationCode: String) -> Unit
) {
    val context = LocalContext.current
    val cal = Calendar.getInstance()
    val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(cal.time)
    val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)

    var reliefDate by remember { mutableStateOf(today) }
    var reliefTime by remember { mutableStateOf(now) }
    var reliefStationCode by remember { mutableStateOf(record.currentStationCode) }
    var error by remember { mutableStateOf<String?>(null) }

    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val c = Calendar.getInstance()
                c.set(year, month, dayOfMonth)
                reliefDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePicker = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                reliefTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Relieve Crew & Close Duty",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Train ${record.trainNo} • Crew: ${record.lpgName}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Sign-on: ${record.signOnDate} ${record.signOnTime}. Enter relief details to close live duty and sync with Google Sheet.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedTextField(
                    value = reliefStationCode,
                    onValueChange = { reliefStationCode = it.uppercase() },
                    label = { Text("Relief Station Code") },
                    placeholder = { Text("e.g. KHS, RIG, BSP") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_relief_station")
                )

                OutlinedTextField(
                    value = reliefDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Relief Date") },
                    trailingIcon = {
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick date")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePicker.show() }
                )

                OutlinedTextField(
                    value = reliefTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Relief Time") },
                    trailingIcon = {
                        IconButton(onClick = { timePicker.show() }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Pick time")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { timePicker.show() }
                )

                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reliefStationCode.isBlank()) {
                        error = "Please enter Relief Station Code"
                        return@Button
                    }
                    onConfirm(reliefDate, reliefTime, reliefStationCode)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("Confirm & Close Duty")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Dialog to configure Google Sheets Webhook App Script Endpoint
 */
@Composable
private fun GoogleSheetWebhookDialog(
    currentUrl: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var url by remember { mutableStateOf(currentUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudSync, contentDescription = null, tint = RailwayAmber)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Google Sheet Auto-Save Setup")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "All crew entries and admin relief closures will automatically sync to your Google Sheet.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Google Apps Script Webhook URL") },
                    placeholder = { Text("https://script.google.com/macros/s/.../exec") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "If left empty, all records remain safely preserved in the local database with full real-time calculation.",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(url) },
                colors = ButtonDefaults.buttonColors(containerColor = RailwayAmber)
            ) {
                Text("Save / सहेजें", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun MetricPill(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurfaceNavy,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderBlue),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
        }
    }
}

@Composable
private fun darkTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = DarkBackgroundNavy,
    unfocusedContainerColor = DarkBackgroundNavy,
    focusedBorderColor = RailwayAmber,
    unfocusedBorderColor = DarkBorderBlue,
    focusedLabelColor = RailwayAmber,
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    cursorColor = RailwayAmber
)
