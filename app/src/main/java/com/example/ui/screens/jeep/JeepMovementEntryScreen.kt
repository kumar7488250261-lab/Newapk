package com.example.ui.screens.jeep

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.equipment.*
import com.example.ui.screens.equipment.EquipmentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeepMovementEntryScreen(
    viewModel: EquipmentViewModel,
    onBack: () -> Unit
) {
    val crewList by viewModel.allCrewMembers.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    val sdfDate = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }
    val sdfTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val today = remember { sdfDate.format(Date()) }
    val currentTime = remember { sdfTime.format(Date()) }

    // Jeep No & Driver
    var selectedJeepNo by remember { mutableStateOf("89") }
    var jeepDropdownExpanded by remember { mutableStateOf(false) }
    var driverName by remember { mutableStateOf("") }

    // Outward Journey Fields
    var toTime by remember { mutableStateOf(currentTime) }
    var fromStation by remember { mutableStateOf("KHS") }
    var departureDate by remember { mutableStateOf(today) }
    var departureTime by remember { mutableStateOf(currentTime) }
    var toStation by remember { mutableStateOf("RIG") }
    var arrivalDate by remember { mutableStateOf(today) }
    var arrivalTime by remember { mutableStateOf("") }

    // Outward Crew: Dynamic Slots (Starts with 1, up to 7, with Add Crew button)
    val outwardCrewSlots = remember {
        mutableStateListOf<CrewSlotItem>().apply {
            add(CrewSlotItem(slotIndex = 0, type = "CREW"))
        }
    }

    // Returning Journey Fields
    var returningFromStation by remember { mutableStateOf("RIG") }
    var returningDepartureDate by remember { mutableStateOf(today) }
    var returningDepartureTime by remember { mutableStateOf("") }
    var returningToStation by remember { mutableStateOf("KHS") }
    var returningArrivalDate by remember { mutableStateOf(today) }
    var returningArrivalTime by remember { mutableStateOf("") }
    var reliefTime by remember { mutableStateOf("") }

    // Returning Crew: Dynamic Slots (Starts with 1, up to 7, with Add Crew button)
    val returningCrewSlots = remember {
        mutableStateListOf<CrewSlotItem>().apply {
            add(CrewSlotItem(slotIndex = 0, type = "EMPTY"))
        }
    }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "जीप मूवमेंट एंट्री फॉर्म",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Jeep Movement Dispatch & Return Log",
                            color = Color(0xFFF1B748),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .testTag("btn_jeep_entry_back")
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF070F18)
                )
            )
        },
        containerColor = Color(0xFF070F18)
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF070F18),
                            Color(0xFF0C1929),
                            Color(0xFF070F18)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 650.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Section 1: Jeep Number & Driver Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B2C)),
                    border = BorderStroke(1.2.dp, Color(0xFF1E3A5F))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = Color(0xFFF1B748),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1. जीप एवं ड्राइवर विवरण",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Jeep No Dropdown
                        Text(
                            text = "JEEP NO (जीप नंबर)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF90CAF9),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { jeepDropdownExpanded = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dropdown_jeep_no"),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF06121E)),
                                border = BorderStroke(1.dp, Color(0xFF2979FF))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Jeep No: $selectedJeepNo",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Jeep",
                                        tint = Color(0xFF2979FF)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = jeepDropdownExpanded,
                                onDismissRequest = { jeepDropdownExpanded = false },
                                modifier = Modifier
                                    .background(Color(0xFF08192B))
                                    .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(8.dp))
                            ) {
                                JeepConstants.JEEP_LIST.forEach { jeepItem ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.DirectionsCar,
                                                    contentDescription = null,
                                                    tint = if (jeepItem == "Breakdown") Color(0xFFFF5252) else Color(0xFF64B5F6),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = jeepItem,
                                                    color = if (jeepItem == selectedJeepNo) Color(0xFFF1B748) else Color.White,
                                                    fontWeight = if (jeepItem == selectedJeepNo) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedJeepNo = jeepItem
                                            jeepDropdownExpanded = false
                                        },
                                        modifier = Modifier.testTag("jeep_option_$jeepItem")
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Jeep Driver Name (manually fill)
                        Text(
                            text = "JEEP DRIVER NAME (ड्राइवर का नाम)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF90CAF9),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = driverName,
                            onValueChange = { driverName = it },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2979FF))
                            },
                            placeholder = { Text("ड्राइवर का नाम लिखें (जैसे: Ramesh Yadav)", color = Color(0xFF546E7A), fontSize = 13.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF06121E),
                                unfocusedContainerColor = Color(0xFF06121E),
                                focusedBorderColor = Color(0xFF2979FF),
                                unfocusedBorderColor = Color(0xFF1B324D)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_driver_name"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Outward Journey (जाने वाले 7 क्रू स्लॉट्स)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B2C)),
                    border = BorderStroke(1.2.dp, Color(0xFF1E3A5F))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "2. जाने वाले क्रू (${outwardCrewSlots.size}/7)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF00E676).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "AUTO-FETCH",
                                    color = Color(0xFF00E676),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "नाम लिखते ही खरसिया लॉबी डायरेक्टरी (369 स्टाफ) से नाम, पद व पदनाम ऑटो-फेच होगा। खाली जाने पर 'Empty' चुनें। 'Add Crew' बटन से 7 क्रू तक जोड़ें।",
                            color = Color(0xFF90A4AE),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        outwardCrewSlots.forEachIndexed { index, slot ->
                            CrewSlotRow(
                                slotIndex = index + 1,
                                slotItem = slot,
                                crewList = crewList,
                                onSlotChanged = { updated ->
                                    outwardCrewSlots[index] = updated
                                },
                                onRemove = if (outwardCrewSlots.size > 1) {
                                    { outwardCrewSlots.removeAt(index) }
                                } else null
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (outwardCrewSlots.size < 7) {
                            OutlinedButton(
                                onClick = {
                                    outwardCrewSlots.add(
                                        CrewSlotItem(
                                            slotIndex = outwardCrewSlots.size,
                                            type = "CREW"
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_add_outward_crew"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF00E676)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF00E676)
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+ Add Crew (क्रू जोड़ें - कुल 7 तक)", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Outward Journey Route & Timing
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B2C)),
                    border = BorderStroke(1.2.dp, Color(0xFF1E3A5F))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF29B6F6),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "3. प्रस्थान विवरण एवं समय",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // TO Time
                        Text(
                            text = "TO TIME (टी.ओ. समय)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = toTime,
                            onValueChange = { toTime = it },
                            placeholder = { Text("HH:mm", color = Color(0xFF546E7A)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF06121E),
                                unfocusedContainerColor = Color(0xFF06121E),
                                focusedBorderColor = Color(0xFF2979FF),
                                unfocusedBorderColor = Color(0xFF1B324D)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("input_to_time"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // FROM Station Dropdown with search
                        Text(
                            text = "FROM (कहाँ से - स्टेशन कोड)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StationDropdownSelector(
                            selectedCode = fromStation,
                            onCodeSelected = { fromStation = it },
                            testTagPrefix = "from_station"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Departure Time and Date
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DEPARTURE DATE (प्रस्थान दिनांक)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = departureDate,
                                    onValueChange = { departureDate = it },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF06121E),
                                        unfocusedContainerColor = Color(0xFF06121E),
                                        focusedBorderColor = Color(0xFF2979FF),
                                        unfocusedBorderColor = Color(0xFF1B324D)
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("input_departure_date"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DEPARTURE TIME (प्रस्थान समय)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = departureTime,
                                    onValueChange = { departureTime = it },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF06121E),
                                        unfocusedContainerColor = Color(0xFF06121E),
                                        focusedBorderColor = Color(0xFF2979FF),
                                        unfocusedBorderColor = Color(0xFF1B324D)
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("input_departure_time"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // TO Station Dropdown with search
                        Text(
                            text = "TO (कहाँ तक - स्टेशन कोड)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StationDropdownSelector(
                            selectedCode = toStation,
                            onCodeSelected = { toStation = it },
                            testTagPrefix = "to_station"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Arrival Time & Date
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ARRIVAL DATE (आगमन दिनांक)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = arrivalDate,
                                    onValueChange = { arrivalDate = it },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF06121E),
                                        unfocusedContainerColor = Color(0xFF06121E),
                                        focusedBorderColor = Color(0xFF2979FF),
                                        unfocusedBorderColor = Color(0xFF1B324D)
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("input_arrival_date"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ARRIVAL TIME (आगमन समय)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = arrivalTime,
                                    onValueChange = { arrivalTime = it },
                                    placeholder = { Text("HH:mm", color = Color(0xFF546E7A)) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF06121E),
                                        unfocusedContainerColor = Color(0xFF06121E),
                                        focusedBorderColor = Color(0xFF2979FF),
                                        unfocusedBorderColor = Color(0xFF1B324D)
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("input_arrival_time"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: Returning Journey (वापसी क्रू - 7 स्लॉट्स)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162124)),
                    border = BorderStroke(1.2.dp, Color(0xFF00695C))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardReturn,
                                    contentDescription = null,
                                    tint = Color(0xFF80CBC4),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "4. वापसी क्रू (${returningCrewSlots.size}/7)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF00897B).copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "RETURNING CREW",
                                    color = Color(0xFF80CBC4),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "लॉबी वापस आने वाले क्रू का नाम सर्च या Empty/Other दर्ज करें। 'Add Crew' बटन से 7 क्रू तक जोड़ें।",
                            color = Color(0xFFB2DFDB),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        returningCrewSlots.forEachIndexed { index, slot ->
                            CrewSlotRow(
                                slotIndex = index + 1,
                                slotItem = slot,
                                crewList = crewList,
                                onSlotChanged = { updated ->
                                    returningCrewSlots[index] = updated
                                },
                                onRemove = if (returningCrewSlots.size > 1) {
                                    { returningCrewSlots.removeAt(index) }
                                } else null
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (returningCrewSlots.size < 7) {
                            OutlinedButton(
                                onClick = {
                                    returningCrewSlots.add(
                                        CrewSlotItem(
                                            slotIndex = returningCrewSlots.size,
                                            type = "EMPTY"
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_add_returning_crew"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF80CBC4)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF80CBC4)
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+ Add Crew (वापसी क्रू जोड़ें - कुल 7 तक)", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 5: Returning Journey Route & Timing
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162124)),
                    border = BorderStroke(1.2.dp, Color(0xFF00695C))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AltRoute,
                                contentDescription = null,
                                tint = Color(0xFF80CBC4),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "5. वापसी प्रस्थान, आगमन एवं रिलीफ समय",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // RELIEF TIME in Returning section
                        Text(
                            text = "RELIEF TIME (रिलीफ समय)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF80CBC4), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = reliefTime,
                            onValueChange = { reliefTime = it },
                            placeholder = { Text("HH:mm", color = Color(0xFF546E7A)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF06121E),
                                unfocusedContainerColor = Color(0xFF06121E),
                                focusedBorderColor = Color(0xFF00897B),
                                unfocusedBorderColor = Color(0xFF004D40)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("input_relief_time"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Returning From Station
                        Text(
                            text = "RETURNING FROM (वापसी कहाँ से - स्टेशन कोड)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF80CBC4), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StationDropdownSelector(
                            selectedCode = returningFromStation,
                            onCodeSelected = { returningFromStation = it },
                            testTagPrefix = "returning_from_station"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Returning Departure Date and Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DEPARTURE DATE (प्रस्थान दिनांक)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF80CBC4), fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = returningDepartureDate,
                                    onValueChange = { returningDepartureDate = it },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF06121E),
                                        unfocusedContainerColor = Color(0xFF06121E),
                                        focusedBorderColor = Color(0xFF00897B),
                                        unfocusedBorderColor = Color(0xFF004D40)
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("input_ret_departure_date"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DEPARTURE TIME (प्रस्थान समय)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF80CBC4), fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = returningDepartureTime,
                                    onValueChange = { returningDepartureTime = it },
                                    placeholder = { Text("HH:mm", color = Color(0xFF546E7A)) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF06121E),
                                        unfocusedContainerColor = Color(0xFF06121E),
                                        focusedBorderColor = Color(0xFF00897B),
                                        unfocusedBorderColor = Color(0xFF004D40)
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("input_ret_departure_time"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Returning To Station
                        Text(
                            text = "RETURNING TO (वापसी कहाँ तक - स्टेशन कोड)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF80CBC4), fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StationDropdownSelector(
                            selectedCode = returningToStation,
                            onCodeSelected = { returningToStation = it },
                            testTagPrefix = "returning_to_station"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Returning Arrival Date & Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ARRIVAL DATE (आगमन दिनांक)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF80CBC4), fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = returningArrivalDate,
                                    onValueChange = { returningArrivalDate = it },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF06121E),
                                        unfocusedContainerColor = Color(0xFF06121E),
                                        focusedBorderColor = Color(0xFF00897B),
                                        unfocusedBorderColor = Color(0xFF004D40)
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("input_ret_arrival_date"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ARRIVAL TIME (आगमन समय)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF80CBC4), fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = returningArrivalTime,
                                    onValueChange = { returningArrivalTime = it },
                                    placeholder = { Text("HH:mm", color = Color(0xFF546E7A)) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF06121E),
                                        unfocusedContainerColor = Color(0xFF06121E),
                                        focusedBorderColor = Color(0xFF00897B),
                                        unfocusedBorderColor = Color(0xFF004D40)
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("input_ret_arrival_time"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3B151E))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFF5252))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFFF8A80),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (driverName.isBlank()) {
                            errorMessage = "कृपया जीप ड्राइवर का नाम भरें"
                            return@Button
                        }
                        if (fromStation.isBlank() || toStation.isBlank()) {
                            errorMessage = "कृपया प्रस्थान और गंतव्य स्टेशन कोड चुनें"
                            return@Button
                        }

                        errorMessage = null
                        isSubmitting = true

                        val outwardSummary = outwardCrewSlots
                            .filter { it.type != "EMPTY" || outwardCrewSlots.all { s -> s.type == "EMPTY" } }
                            .joinToString(", ") { it.getDisplaySummary() }

                        val returningSummary = returningCrewSlots
                            .filter { it.type != "EMPTY" }
                            .joinToString(", ") { it.getDisplaySummary() }

                        val isCompleted = returningArrivalTime.isNotBlank() || arrivalTime.isNotBlank()

                        val record = JeepMovementRecord(
                            jeepNo = selectedJeepNo,
                            driverName = driverName.trim(),
                            toTime = toTime.trim(),
                            fromStation = fromStation.trim(),
                            departureDate = departureDate.trim(),
                            departureTime = departureTime.trim(),
                            toStation = toStation.trim(),
                            arrivalDate = arrivalDate.trim(),
                            arrivalTime = arrivalTime.trim(),
                            reliefTime = reliefTime.trim(),
                            outwardCrews = outwardSummary,
                            returningCrews = returningSummary,
                            returningFromStation = returningFromStation.trim(),
                            returningDepartureDate = returningDepartureDate.trim(),
                            returningDepartureTime = returningDepartureTime.trim(),
                            returningToStation = returningToStation.trim(),
                            returningArrivalDate = returningArrivalDate.trim(),
                            returningArrivalTime = returningArrivalTime.trim(),
                            isCompleted = isCompleted
                        )

                        viewModel.submitJeepMovement(record) { success ->
                            isSubmitting = false
                            if (success) {
                                showSuccessDialog = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_submit_jeep_movement"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "मूवमेंट दर्ज करें (SUBMIT ENTRY)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onBack()
            },
            title = {
                Text("मूवमेंट सफलतापूर्वक दर्ज हुआ!", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("जीप $selectedJeepNo का मूवमेंट और क्रू रिकॉर्ड सफलतापूर्वक सुरक्षित कर लिया गया है। जीप उपलब्धता सूची में इसका टर्न अपडेट हो गया है।")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
                ) {
                    Text("ठीक है (OK)")
                }
            }
        )
    }
}

/**
 * Single Crew Slot Row (1 to 7):
 * - Search by name / ID with auto-fetch post & designation
 * - Quick 'Empty' button
 * - Quick 'Other' button
 * - Quick 'Clear' button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrewSlotRow(
    slotIndex: Int,
    slotItem: CrewSlotItem,
    crewList: List<CrewMember>,
    onSlotChanged: (CrewSlotItem) -> Unit,
    onRemove: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var customOtherText by remember { mutableStateOf(slotItem.otherText) }

    val filteredList = remember(searchQuery, crewList) {
        if (searchQuery.trim().length >= 2) {
            val q = searchQuery.trim().lowercase()
            crewList.filter {
                it.name.lowercase().contains(q) ||
                it.crewId.lowercase().contains(q) ||
                it.designation.lowercase().contains(q)
            }.take(8)
        } else {
            emptyList()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF071422)),
        border = BorderStroke(1.dp, Color(0xFF1B334D))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "क्रू #$slotIndex",
                        color = Color(0xFF90CAF9),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    if (onRemove != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Remove Crew",
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Type toggles: Crew, Empty, Other
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = slotItem.type == "CREW",
                        onClick = {
                            onSlotChanged(slotItem.copy(type = "CREW"))
                        },
                        label = { Text("क्रू सर्च", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1565C0),
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = slotItem.type == "EMPTY",
                        onClick = {
                            onSlotChanged(slotItem.copy(type = "EMPTY", crewName = "", crewId = "", designation = ""))
                            searchQuery = ""
                        },
                        label = { Text("खाली (Empty)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF37474F),
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = slotItem.type == "OTHER",
                        onClick = {
                            onSlotChanged(slotItem.copy(type = "OTHER", crewName = "", crewId = "", designation = ""))
                        },
                        label = { Text("Other", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF4A148C),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (slotItem.type) {
                "CREW" -> {
                    if (slotItem.crewName.isNotBlank()) {
                        // Auto-fetched Crew Profile Card
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0C243B),
                            border = BorderStroke(1.dp, Color(0xFF2979FF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = slotItem.crewName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${slotItem.crewId} • ${slotItem.designation}",
                                        color = Color(0xFF64B5F6),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onSlotChanged(slotItem.copy(crewName = "", crewId = "", designation = ""))
                                        searchQuery = ""
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color(0xFF90A4AE),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Search textfield
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                isSearching = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64B5F6))
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF90A4AE))
                                    }
                                }
                            },
                            placeholder = { Text("Search by name or Crew ID...", color = Color(0xFF546E7A), fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF040A12),
                                unfocusedContainerColor = Color(0xFF040A12),
                                focusedBorderColor = Color(0xFF2979FF),
                                unfocusedBorderColor = Color(0xFF1B324D)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_crew_slot_$slotIndex"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // Dropdown of matching results
                        if (filteredList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1C2E)),
                                border = BorderStroke(1.dp, Color(0xFF1E3D5F))
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    filteredList.forEach { member ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onSlotChanged(
                                                        slotItem.copy(
                                                            crewId = member.crewId,
                                                            crewName = member.name,
                                                            designation = member.designation
                                                        )
                                                    )
                                                    searchQuery = ""
                                                    isSearching = false
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = Color(0xFF64B5F6),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = member.name,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                                Text(
                                                    text = "${member.crewId} • ${member.designation}",
                                                    color = Color(0xFF90CAF9),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                        HorizontalDivider(color = Color(0xFF142B42), thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
                "EMPTY" -> {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF141E28),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "— जीप खाली गई (Empty Slot) —",
                            color = Color(0xFF78909C),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                    }
                }
                "OTHER" -> {
                    OutlinedTextField(
                        value = customOtherText,
                        onValueChange = {
                            customOtherText = it
                            onSlotChanged(slotItem.copy(otherText = it))
                        },
                        placeholder = { Text("अन्य व्यक्ति का नाम व पद लिखें...", color = Color(0xFF546E7A), fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF040A12),
                            unfocusedContainerColor = Color(0xFF040A12),
                            focusedBorderColor = Color(0xFF7B1FA2),
                            unfocusedBorderColor = Color(0xFF4A148C)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Searchable Station Code Selector Dropdown:
 * All stations from user list with instant filter!
 */
@Composable
fun StationDropdownSelector(
    selectedCode: String,
    onCodeSelected: (String) -> Unit,
    testTagPrefix: String
) {
    var expanded by remember { mutableStateOf(false) }
    var stationSearch by remember { mutableStateOf("") }

    val currentStation = JeepConstants.STATIONS.find { it.code == selectedCode }
        ?: JeepConstants.Station(selectedCode, selectedCode)

    val filteredStations = remember(stationSearch) {
        if (stationSearch.isBlank()) {
            JeepConstants.STATIONS
        } else {
            val q = stationSearch.trim().lowercase()
            JeepConstants.STATIONS.filter {
                it.code.lowercase().contains(q) || it.name.lowercase().contains(q)
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("${testTagPrefix}_dropdown"),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF06121E)),
            border = BorderStroke(1.dp, Color(0xFF2979FF))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color(0xFF2979FF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentStation.displayLabel,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFF2979FF)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                stationSearch = ""
            },
            modifier = Modifier
                .width(320.dp)
                .background(Color(0xFF08192B))
                .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(8.dp))
        ) {
            // Search field inside dropdown
            OutlinedTextField(
                value = stationSearch,
                onValueChange = { stationSearch = it },
                placeholder = { Text("स्टेशन सर्च करें...", color = Color(0xFF546E7A), fontSize = 12.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64B5F6), modifier = Modifier.size(16.dp))
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF040A12),
                    unfocusedContainerColor = Color(0xFF040A12),
                    focusedBorderColor = Color(0xFF2979FF),
                    unfocusedBorderColor = Color(0xFF1B324D)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp)
            )

            HorizontalDivider(color = Color(0xFF1E3A5F))

            Box(modifier = Modifier.heightIn(max = 280.dp)) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    filteredStations.forEach { station ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = station.code,
                                        color = if (station.code == selectedCode) Color(0xFFF1B748) else Color(0xFF64B5F6),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.width(60.dp)
                                    )
                                    Text(
                                        text = station.name,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }
                            },
                            onClick = {
                                onCodeSelected(station.code)
                                expanded = false
                                stationSearch = ""
                            }
                        )
                    }
                }
            }
        }
    }
}
