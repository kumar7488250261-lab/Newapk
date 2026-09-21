package com.example.ui.screens.jeep

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.equipment.JeepAvailabilityItem
import com.example.data.equipment.JeepMovementRecord
import com.example.ui.screens.equipment.EquipmentViewModel

/**
 * Jeep Availability Screen (जीप उपलब्धता):
 * Shows all 6 jeeps:
 * 1. Available jeeps sequenced by FIFO last arrival time (Earliest arrived = Turn #1, then serial-wise 2, 3...)
 * 2. On-movement jeeps showing who went where, at what time, and driver name.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeepAvailabilityScreen(
    viewModel: EquipmentViewModel,
    onNavigateToEntry: () -> Unit,
    onBack: () -> Unit
) {
    val availabilityList by viewModel.jeepAvailabilityList.collectAsStateWithLifecycle()
    val movementsHistory by viewModel.allJeepMovements.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Available (Turn), 1: On Movement, 2: All 6 Jeeps, 3: History Log

    val availableJeeps = availabilityList.filter { it.isAvailable }
    val onMovementJeeps = availabilityList.filter { !it.isAvailable }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "जीप उपलब्धता व टर्न स्थिति",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Jeep Availability • Kharsia Lobby",
                            color = Color(0xFFF1B748),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .testTag("btn_jeep_avail_back")
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = onNavigateToEntry,
                        modifier = Modifier
                            .testTag("btn_quick_new_entry")
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF7B1FA2),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("नई एंट्री", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                            Color(0xFF0A192A),
                            Color(0xFF070F18)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Summary Stat Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2B1C)),
                        border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${availableJeeps.size}",
                                color = Color(0xFF00E676),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            )
                            Text(
                                text = "उपलब्ध जीपें (KHS)",
                                color = Color(0xFFA5D6A7),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF331E06)),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${onMovementJeeps.size}",
                                color = Color(0xFFFFB74D),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            )
                            Text(
                                text = "मार्ग में (On Duty)",
                                color = Color(0xFFFFE082),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Filter
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF081524),
                    contentColor = Color(0xFFF1B748),
                    edgePadding = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "उपलब्ध टर्न (${availableJeeps.size})",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "मार्ग में (${onMovementJeeps.size})",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "सभी 6 जीपें",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Text(
                                text = "मूवमेंट इतिहास (${movementsHistory.size})",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rule explanation banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0C243B),
                    border = BorderStroke(1.dp, Color(0xFF1B4E78)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "टर्न नियम: जो जीप पहले आई (Last Arrival Time), वह टर्न 1 पर रहेगी।",
                            color = Color(0xFFB0BEC5),
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List content
                when (selectedTab) {
                    0 -> {
                        // Available Jeeps (Turn-wise sorted FIFO)
                        if (availableJeeps.isEmpty()) {
                            EmptyJeepState(message = "वर्तमान में कोई जीप लॉबी पर उपलब्ध नहीं है। सभी मार्ग में हैं।")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(availableJeeps) { jeep ->
                                    AvailableJeepCard(jeep = jeep)
                                }
                            }
                        }
                    }
                    1 -> {
                        // On Movement Jeeps (Kha gya, kitne baje)
                        if (onMovementJeeps.isEmpty()) {
                            EmptyJeepState(message = "कोई भी जीप अभी मार्ग में नहीं है। सभी जीपें लॉबी पर उपलब्ध हैं।")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(onMovementJeeps) { jeep ->
                                    OnMovementJeepCard(jeep = jeep)
                                }
                            }
                        }
                    }
                    2 -> {
                        // All 6 Jeeps combined overview
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(availabilityList) { jeep ->
                                if (jeep.isAvailable) {
                                    AvailableJeepCard(jeep = jeep)
                                } else {
                                    OnMovementJeepCard(jeep = jeep)
                                }
                            }
                        }
                    }
                    3 -> {
                        // Movement History Log
                        if (movementsHistory.isEmpty()) {
                            EmptyJeepState(message = "अभी तक कोई मूवमेंट रिकॉर्ड दर्ज नहीं किया गया है।")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(movementsHistory) { record ->
                                    MovementHistoryCard(
                                        record = record,
                                        onDelete = { viewModel.deleteJeepMovement(record.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvailableJeepCard(jeep: JeepAvailabilityItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("jeep_card_${jeep.jeepNo}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1D2F)),
        border = BorderStroke(
            1.2.dp,
            if (jeep.turnNumber == 1) Color(0xFFF1B748) else Color(0xFF1E3A5F)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Turn Rank Badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (jeep.turnNumber == 1) Brush.linearGradient(
                                listOf(Color(0xFFFFB300), Color(0xFFE65100))
                            ) else Brush.linearGradient(
                                listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TURN",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "#${jeep.turnNumber}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Jeep No: ${jeep.jeepNo}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (jeep.turnNumber == 1) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFF1B748).copy(alpha = 0.2f),
                                border = BorderStroke(0.8.dp, Color(0xFFF1B748))
                            ) {
                                Text(
                                    text = "1st PRIORITY",
                                    color = Color(0xFFF1B748),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "उपलब्ध (Available at KHS Lobby)",
                            color = Color(0xFF00E676),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF1E3A5F), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Last Arrival Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF90CAF9),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Last Arrival: ",
                        color = Color(0xFF90A4AE),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${jeep.lastArrivalTime} hrs (${jeep.lastArrivalDate})",
                        color = Color(0xFFE0E0E0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF90CAF9),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = jeep.driverName,
                        color = Color(0xFFB0BEC5),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OnMovementJeepCard(jeep: JeepAvailabilityItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("jeep_on_duty_${jeep.jeepNo}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF241510)),
        border = BorderStroke(1.2.dp, Color(0xFFFF7043)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFBF360C)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Jeep No: ${jeep.jeepNo}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5722))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "मार्ग में (On Movement)",
                            color = Color(0xFFFF8A65),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE65100),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D))
                ) {
                    Text(
                        text = "ON DUTY",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF3E2723), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Where it went and at what time ("kon jeep kha gya bas aur kitne baje")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color(0xFFFF8A65),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "कहाँ गया: ",
                    color = Color(0xFFFFCCBC),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${jeep.currentLocation} (${jeep.movementDestination})",
                    color = Color(0xFFFFD54F),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFFFF8A65),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "कितने बजे: ",
                    color = Color(0xFFFFCCBC),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${jeep.departureTime} hrs (${jeep.departureDate})",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = null,
                    tint = Color(0xFFFF8A65),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ड्राइवर: ",
                    color = Color(0xFFFFCCBC),
                    fontSize = 13.sp
                )
                Text(
                    text = jeep.driverName.ifBlank { "Lobby Driver" },
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (jeep.crewSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "क्रू: ${jeep.crewSummary}",
                    color = Color(0xFFB0BEC5),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun MovementHistoryCard(
    record: JeepMovementRecord,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1929)),
        border = BorderStroke(1.dp, Color(0xFF1E3958))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Jeep ${record.jeepNo}",
                        color = Color(0xFFF1B748),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Driver: ${record.driverName}",
                        color = Color(0xFF90A4AE),
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete record",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Journey Route
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TripOrigin,
                    contentDescription = null,
                    tint = Color(0xFF4FC3F7),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "From: ${record.fromStation} (${record.departureTime})",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color(0xFFFF8A65),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "To: ${record.toStation} (${record.arrivalTime.ifBlank { "Arr: --" }})",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            if (record.returningFromStation.isNotBlank() || record.returningToStation.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "वापसी: ${record.returningFromStation} (${record.returningDepartureTime}) → ${record.returningToStation} (${record.returningArrivalTime})",
                    color = Color(0xFF81C784),
                    fontSize = 11.sp
                )
            }

            if (record.outwardCrews.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "जाने वाले क्रू: ${record.outwardCrews}",
                    color = Color(0xFFB0BEC5),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }

            if (record.returningCrews.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "वापसी क्रू: ${record.returningCrews}",
                    color = Color(0xFFB0BEC5),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun EmptyJeepState(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF091724)),
        border = BorderStroke(1.dp, Color(0xFF1E3958))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = Color(0xFF546E7A),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = Color(0xFF90A4AE),
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
