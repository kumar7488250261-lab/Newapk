package com.example.ui.screens.roster

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.KharsiaLobbyEmblem
import com.example.data.StaffContact
import com.example.data.equipment.CrewMember
import com.example.data.equipment.RosterTlcRecord
import com.example.ui.screens.equipment.EquipmentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RosterAdminPortalScreen(
    viewModel: EquipmentViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val contactsList by viewModel.allDirectoryContacts.collectAsStateWithLifecycle()
    val crewList by viewModel.allCrewMembers.collectAsStateWithLifecycle()

    val sdfDate = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }
    val today = remember { sdfDate.format(Date()) }

    var rosterDate by remember { mutableStateOf(today) }
    var shiftTiming by remember { mutableStateOf("06-14") } // "06-14", "14-22", "22-06"

    // Roles with Auto Fetch (name + mobile)
    var tfrCrewName by remember { mutableStateOf("") }
    var tfrMobile by remember { mutableStateOf("") }

    var lhCrewName by remember { mutableStateOf("") }
    var lhMobile by remember { mutableStateOf("") }

    var diCrewName by remember { mutableStateOf("") }
    var diMobile by remember { mutableStateOf("") }

    var wdCrewName by remember { mutableStateOf("") }
    var wdMobile by remember { mutableStateOf("") }

    // Manual Fill
    var cmsName by remember { mutableStateOf("") }

    // Lobby CLI fetch with name and mobile
    var lobbyCliShift by remember { mutableStateOf("00-08") } // "00-08", "08-16", "16-00"
    var lobbyCliName by remember { mutableStateOf("") }
    var lobbyCliMobile by remember { mutableStateOf("") }

    // Sander manual fill: 00-08, 08-16, 16-00
    var sanderBoyShift by remember { mutableStateOf("00-08") } // "00-08", "08-16", "16-00"
    var sanderBoyName by remember { mutableStateOf("") }

    // TLC details: 17-01, 01-09, 09-17
    var tlcShift by remember { mutableStateOf("17-01") } // "17-01", "01-09", "09-17"
    var tlcMlName by remember { mutableStateOf("") }
    var tlcMlMobile by remember { mutableStateOf("") }
    var tlcLhName by remember { mutableStateOf("") }
    var tlcLhMobile by remember { mutableStateOf("") }

    var remarks by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dialog state for crew search
    var activeSearchRole by remember { mutableStateOf<String?>(null) }

    if (activeSearchRole != null) {
        CrewSearchDialog(
            title = "Search Staff for $activeSearchRole",
            contacts = contactsList,
            crews = crewList,
            onDismiss = { activeSearchRole = null },
            onSelect = { name, mobile ->
                when (activeSearchRole) {
                    "TFR" -> { tfrCrewName = name; tfrMobile = mobile }
                    "LH" -> { lhCrewName = name; lhMobile = mobile }
                    "DI" -> { diCrewName = name; diMobile = mobile }
                    "WD" -> { wdCrewName = name; wdMobile = mobile }
                    "LOBBY CLI" -> { lobbyCliName = name; lobbyCliMobile = mobile }
                    "TLC ML" -> { tlcMlName = name; tlcMlMobile = mobile }
                    "TLC LH" -> { tlcLhName = name; tlcLhMobile = mobile }
                }
                activeSearchRole = null
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onBack()
            },
            title = { Text("सफलतापूर्वक दर्ज!", fontWeight = FontWeight.Bold) },
            text = { Text("रोस्टर एवं TLC अपडेट डेटाबेस एवं Google Sheets में सुरक्षित हो गया है।") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text("ठीक है (OK)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    val cal = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance()
                newCal.set(year, month, dayOfMonth)
                rosterDate = sdfDate.format(newCal.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
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
                                text = "Admin Portal - Roaster & TLC Entry",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Shift Wise Roaster & TLC Update Entry Form",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCE93D8))
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_admin_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF160E21)
                )
            )
        },
        containerColor = Color(0xFF0C0712)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Date & Shift Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B112B)),
                    border = BorderStroke(1.dp, Color(0xFF7B1FA2))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Shift Wise Roaster Date & Timing",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Date Picker Field
                        Text(
                            text = "ROASTER DATE (दिनांक)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCE93D8), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = rosterDate,
                            onValueChange = { rosterDate = it },
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = Color(0xFFBA68C8))
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0E0717),
                                unfocusedContainerColor = Color(0xFF0E0717),
                                focusedBorderColor = Color(0xFFBA68C8),
                                unfocusedBorderColor = Color(0xFF4A148C)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("input_roster_date"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Shift Selection: 06-14, 14-22, 22-06
                        Text(
                            text = "SHIFT TIMING (शिफ्ट चुनें: 06-14, 14-22, 22-06)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCE93D8), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("06-14", "14-22", "22-06").forEach { shift ->
                                val isSelected = shiftTiming == shift
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { shiftTiming = shift },
                                    label = { Text(shift, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color(0xFF0E0717),
                                        labelColor = Color(0xFFCE93D8),
                                        selectedContainerColor = Color(0xFF7B1FA2),
                                        selectedLabelColor = Color.White
                                    ),
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFFBA68C8) else Color(0xFF4A148C))
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Roles (TFR, LH, DI, WD, CMS)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141926)),
                    border = BorderStroke(1.dp, Color(0xFF1976D2))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2. Roaster Duty Staff (Auto-Fetch with Mobile)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF00E676).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "AUTO FETCH",
                                    color = Color(0xFF00E676),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // TFR
                        RoleEntryField(
                            roleName = "TFR",
                            name = tfrCrewName,
                            mobile = tfrMobile,
                            onSearchClick = { activeSearchRole = "TFR" },
                            onNameChange = { tfrCrewName = it },
                            onMobileChange = { tfrMobile = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // LH
                        RoleEntryField(
                            roleName = "LH",
                            name = lhCrewName,
                            mobile = lhMobile,
                            onSearchClick = { activeSearchRole = "LH" },
                            onNameChange = { lhCrewName = it },
                            onMobileChange = { lhMobile = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // DI
                        RoleEntryField(
                            roleName = "DI",
                            name = diCrewName,
                            mobile = diMobile,
                            onSearchClick = { activeSearchRole = "DI" },
                            onNameChange = { diCrewName = it },
                            onMobileChange = { diMobile = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // WD
                        RoleEntryField(
                            roleName = "WD",
                            name = wdCrewName,
                            mobile = wdMobile,
                            onSearchClick = { activeSearchRole = "WD" },
                            onNameChange = { wdCrewName = it },
                            onMobileChange = { wdMobile = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // CMS (Manually fill)
                        Text(
                            text = "CMS (Manually fill / मैनुअल भरें)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = cmsName,
                            onValueChange = { cmsName = it },
                            placeholder = { Text("Enter CMS staff name...", color = Color(0xFF546E7A)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0B141E),
                                unfocusedContainerColor = Color(0xFF0B141E),
                                focusedBorderColor = Color(0xFF2979FF),
                                unfocusedBorderColor = Color(0xFF1B324D)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("input_cms_name"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Section 3: Lobby CLI
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF102127)),
                    border = BorderStroke(1.dp, Color(0xFF00897B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "3. LOBBY CLI (Fetch with name & show mobile no)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Shift Wise: 00-08, 08-16, 16-00
                        Text(
                            text = "LOBBY CLI SHIFT (शिफ्ट: 00-08, 08-16, 16-00)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF80CBC4), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("00-08", "08-16", "16-00").forEach { shift ->
                                val isSelected = lobbyCliShift == shift
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { lobbyCliShift = shift },
                                    label = { Text(shift, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color(0xFF07171C),
                                        labelColor = Color(0xFF80CBC4),
                                        selectedContainerColor = Color(0xFF00897B),
                                        selectedLabelColor = Color.White
                                    ),
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF80CBC4) else Color(0xFF004D40))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        RoleEntryField(
                            roleName = "LOBBY CLI",
                            name = lobbyCliName,
                            mobile = lobbyCliMobile,
                            onSearchClick = { activeSearchRole = "LOBBY CLI" },
                            onNameChange = { lobbyCliName = it },
                            onMobileChange = { lobbyCliMobile = it }
                        )
                    }
                }
            }

            // Section 4: Sander Boy
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF261D15)),
                    border = BorderStroke(1.dp, Color(0xFFF57C00))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "4. Sander (Manual Fill)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Shift Wise: 00-08, 08-16, 16-00
                        Text(
                            text = "SANDER SHIFT (शिफ्ट: 00-08, 08-16, 16-00)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("00-08", "08-16", "16-00").forEach { shift ->
                                val isSelected = sanderBoyShift == shift
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { sanderBoyShift = shift },
                                    label = { Text(shift, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color(0xFF140C06),
                                        labelColor = Color(0xFFFFB74D),
                                        selectedContainerColor = Color(0xFFF57C00),
                                        selectedLabelColor = Color.White
                                    ),
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFFFFB74D) else Color(0xFFE65100))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "SANDER STAFF NAME (नाम दर्ज करें)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = sanderBoyName,
                            onValueChange = { sanderBoyName = it },
                            placeholder = { Text("Enter Sander name...", color = Color(0xFF6D4C41)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF140C06),
                                unfocusedContainerColor = Color(0xFF140C06),
                                focusedBorderColor = Color(0xFFF57C00),
                                unfocusedBorderColor = Color(0xFFE65100)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("input_sander_boy_name"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Section 5: TLC Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF221727)),
                    border = BorderStroke(1.dp, Color(0xFF8E24AA))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "5. TLC Update (Traction Loco Controller)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // TLC Shift: 17-01, 01-09, 09-17
                        Text(
                            text = "TLC SHIFT (शिफ्ट: 17-01, 01-09, 09-17)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCE93D8), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("17-01", "01-09", "09-17").forEach { shift ->
                                val isSelected = tlcShift == shift
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { tlcShift = shift },
                                    label = { Text(shift, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color(0xFF190D24),
                                        labelColor = Color(0xFFCE93D8),
                                        selectedContainerColor = Color(0xFF8E24AA),
                                        selectedLabelColor = Color.White
                                    ),
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFFCE93D8) else Color(0xFF4A148C))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        RoleEntryField(
                            roleName = "TLC ML",
                            name = tlcMlName,
                            mobile = tlcMlMobile,
                            onSearchClick = { activeSearchRole = "TLC ML" },
                            onNameChange = { tlcMlName = it },
                            onMobileChange = { tlcMlMobile = it }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        RoleEntryField(
                            roleName = "TLC LH",
                            name = tlcLhName,
                            mobile = tlcLhMobile,
                            onSearchClick = { activeSearchRole = "TLC LH" },
                            onNameChange = { tlcLhName = it },
                            onMobileChange = { tlcLhMobile = it }
                        )
                    }
                }
            }

            // Remarks & Error Message
            item {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFFF5252),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (rosterDate.isBlank()) {
                            errorMessage = "कृपया रोस्टर की तारीख चुनें"
                            return@Button
                        }
                        errorMessage = null
                        isSubmitting = true

                        val record = RosterTlcRecord(
                            rosterDate = rosterDate.trim(),
                            shiftTiming = shiftTiming,
                            tfrCrewName = tfrCrewName.trim(),
                            tfrMobile = tfrMobile.trim(),
                            lhCrewName = lhCrewName.trim(),
                            lhMobile = lhMobile.trim(),
                            diCrewName = diCrewName.trim(),
                            diMobile = diMobile.trim(),
                            wdCrewName = wdCrewName.trim(),
                            wdMobile = wdMobile.trim(),
                            cmsName = cmsName.trim(),
                            lobbyCliShift = lobbyCliShift,
                            lobbyCliName = lobbyCliName.trim(),
                            lobbyCliMobile = lobbyCliMobile.trim(),
                            sanderBoyShift = sanderBoyShift,
                            sanderBoyName = sanderBoyName.trim(),
                            tlcShift = tlcShift,
                            tlcMlName = tlcMlName.trim(),
                            tlcMlMobile = tlcMlMobile.trim(),
                            tlcLhName = tlcLhName.trim(),
                            tlcLhMobile = tlcLhMobile.trim(),
                            remarks = remarks.trim(),
                            updatedByAdmin = viewModel.currentAdminUser
                        )

                        viewModel.submitRosterTlc(record) { success ->
                            isSubmitting = false
                            if (success) {
                                showSuccessDialog = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_submit_roster"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "सुरक्षित करें (Save & Auto-Sync to Google Sheets)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RoleEntryField(
    roleName: String,
    name: String,
    mobile: String,
    onSearchClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onMobileChange: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$roleName (Auto-fetch with name & mobile)",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold)
            )

            TextButton(
                onClick = onSearchClick,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF00E676))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Search $roleName", fontSize = 12.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = { Text("Name", color = Color(0xFF546E7A)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF0B141E),
                    unfocusedContainerColor = Color(0xFF0B141E),
                    focusedBorderColor = Color(0xFF2979FF),
                    unfocusedBorderColor = Color(0xFF1B324D)
                ),
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = mobile,
                onValueChange = onMobileChange,
                placeholder = { Text("Mobile No", color = Color(0xFF546E7A)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF00E676),
                    unfocusedTextColor = Color(0xFF00E676),
                    focusedContainerColor = Color(0xFF0B141E),
                    unfocusedContainerColor = Color(0xFF0B141E),
                    focusedBorderColor = Color(0xFF00E676),
                    unfocusedBorderColor = Color(0xFF1B324D)
                ),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
fun CrewSearchDialog(
    title: String,
    contacts: List<StaffContact>,
    crews: List<CrewMember>,
    onDismiss: () -> Unit,
    onSelect: (name: String, mobile: String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filteredContacts = remember(contacts, query) {
        if (query.isBlank()) contacts.take(30) else {
            contacts.filter { it.name.contains(query, ignoreCase = true) || it.mobile.contains(query) }.take(40)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1C2E)),
            border = BorderStroke(1.dp, Color(0xFF2979FF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Type name or mobile number...", color = Color(0xFF78909C), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF2979FF)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF070F18),
                        unfocusedContainerColor = Color(0xFF070F18),
                        focusedBorderColor = Color(0xFF2979FF),
                        unfocusedBorderColor = Color(0xFF1B324D)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredContacts.size) { index ->
                        val contact = filteredContacts[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(contact.name, contact.mobile.ifBlank { contact.cug }) },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF081422)),
                            border = BorderStroke(1.dp, Color(0xFF1B324D))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = contact.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = contact.designation, color = Color(0xFF90A4AE), fontSize = 11.sp)
                                }
                                val mob = contact.mobile.ifBlank { contact.cug }
                                if (mob.isNotBlank()) {
                                    Text(text = "📞 $mob", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
