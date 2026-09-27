package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CrewMember
import com.example.data.Lobby
import com.example.data.StaffContact
import com.example.data.StaffRepository
import com.example.data.StationContact
import com.example.ui.components.KharsiaLobbyEmblem
import com.example.ui.theme.DarkBackgroundNavy
import com.example.ui.theme.DarkBorderBlue
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.DarkSurfaceNavy
import com.example.ui.theme.RailwayAmber
import com.example.ui.theme.RailwayGold
import com.example.ui.theme.RailwayGreen
import com.example.ui.theme.RailwayNavy
import com.example.ui.theme.RailwayRed

private fun dialPhoneNumber(context: Context, number: String) {
    val clean = number.replace(Regex("[^0-9+]"), "")
    if (clean.isNotBlank()) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean"))
        context.startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDirectoryScreen(
    staffRepository: StaffRepository,
    onLobbyClick: (Lobby) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val emergencyContacts = remember { staffRepository.getEmergencyQuickContacts() }
    val stations = remember { staffRepository.getStations() }
    val kharsiaLobby = remember { staffRepository.getKharsiaLobby() }
    val otherLobbies = remember { staffRepository.getOtherLobbies() }
    val controlLobby = remember { staffRepository.getControlCenterLobby() }
    val crewMaster = remember { staffRepository.getCrewMaster() }

    // Tabs: 0: Stations CUG (PDF), 1: Kharsia Lobby (369 Crew), 2: Other 12 Lobbies, 3: Control Center & Landlines
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    // Station section filter
    var stationSectionFilter by remember { mutableStateOf("ALL") }
    // Kharsia crew category filter
    var crewCategoryFilter by remember { mutableIntStateOf(0) } // 0: ALL, 1: LP, 2: ALP

    // Emergency strip expansion state
    var showEmergencyStrip by remember { mutableStateOf(true) }

    // Filtered Stations (141)
    val filteredStations = remember(searchQuery, stationSectionFilter, stations) {
        stations.filter { st ->
            val matchesSection = when (stationSectionFilter) {
                "SEC1" -> st.section.contains("Bilaspur – Jharsuguda", ignoreCase = true)
                "SEC2" -> st.section.contains("Champa – Gevra", ignoreCase = true)
                "SEC3" -> st.section.contains("Bilaspur – Jhalwara", ignoreCase = true)
                "SEC4" -> st.section.contains("Durg – Raipur", ignoreCase = true)
                "SEC5" -> st.section.contains("Itwari", ignoreCase = true)
                else -> true
            }
            val matchesQuery = if (searchQuery.isBlank()) true else {
                val q = searchQuery.trim().uppercase()
                st.code.uppercase().contains(q) ||
                st.name.uppercase().contains(q) ||
                st.cugMobile.contains(q) ||
                st.landline.contains(q)
            }
            matchesSection && matchesQuery
        }
    }

    // Filtered Kharsia Crew (369)
    val filteredKharsiaCrew = remember(searchQuery, crewCategoryFilter, crewMaster) {
        crewMaster.filter { crew ->
            val matchesCat = when (crewCategoryFilter) {
                1 -> crew.category.equals("LP", ignoreCase = true) || crew.designation.contains("LP", ignoreCase = true)
                2 -> crew.category.equals("ALP", ignoreCase = true) || crew.designation.contains("ALP", ignoreCase = true)
                else -> true
            }
            val matchesQuery = if (searchQuery.isBlank()) true else {
                val q = searchQuery.trim().uppercase()
                crew.crewId.uppercase().contains(q) ||
                crew.name.uppercase().contains(q) ||
                crew.designation.uppercase().contains(q) ||
                crew.mobile.contains(q)
            }
            matchesCat && matchesQuery
        }
    }

    // Filtered Other Lobbies
    val filteredOtherLobbies = remember(searchQuery, otherLobbies) {
        if (searchQuery.isBlank()) otherLobbies
        else otherLobbies.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.code.contains(searchQuery, ignoreCase = true)
        }
    }

    // Filtered Control Contacts
    val filteredControlContacts = remember(searchQuery, controlLobby) {
        val allContacts = mutableListOf<Pair<String, StaffContact>>()
        controlLobby?.categories?.forEach { cat ->
            cat.contacts.forEach { c ->
                allContacts.add(Pair(cat.category, c))
            }
        }
        if (searchQuery.isBlank()) allContacts
        else {
            val q = searchQuery.trim().uppercase()
            allContacts.filter { (cat, c) ->
                cat.uppercase().contains(q) ||
                c.name.uppercase().contains(q) ||
                c.designation.uppercase().contains(q) ||
                c.mobile.contains(q)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KharsiaLobbyEmblem(size = 38.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SECR Operational Directory",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "141 Stations • Kharsia Lobby • Control Center",
                                style = MaterialTheme.typography.labelSmall,
                                color = RailwayGold
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_directory_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEmergencyStrip = !showEmergencyStrip }) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = "Toggle Emergency",
                            tint = if (showEmergencyStrip) RailwayRed else Color.LightGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackgroundNavy)
            )
        },
        containerColor = DarkCanvasBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // =========================================================================
            // 1. TOP EMERGENCY & CONTROL CENTER QUICK DIAL SECTION (ALWAYS AT THE TOP)
            // =========================================================================
            AnimatedVisibility(visible = showEmergencyStrip) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF140D1B)),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFDC2626).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(RailwayRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CONTROL CENTER & EMERGENCY HELPLINES (आपातकालीन नंबर)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCA5A5),
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = "1-TAP CALL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = RailwayGold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Fast scroll horizontal cards
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            emergencyContacts.forEach { contact ->
                                EmergencyQuickChip(
                                    title = contact.name,
                                    phone = contact.mobile,
                                    role = contact.designation,
                                    onCall = { dialPhoneNumber(context, contact.mobile) }
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 2. SEARCH BAR
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        val placeholder = when (selectedTab) {
                            0 -> "Search Station Code (KHS, RIG, BSP) or Name..."
                            1 -> "Search Crew Name or CMS ID (e.g. KHS1001)..."
                            2 -> "Search Lobby Name or Code (BSP, KRBA)..."
                            else -> "Search TLC, TPC, Power Controller, DPC..."
                        }
                        Text(placeholder, color = Color(0xFF7E8EA6), fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = RailwayGold
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_directory"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = RailwayGold,
                        unfocusedBorderColor = DarkBorderBlue,
                        focusedContainerColor = DarkSurfaceNavy,
                        unfocusedContainerColor = DarkSurfaceNavy
                    )
                )
            }

            // =========================================================================
            // 3. MAIN SECTION TABS
            // =========================================================================
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurfaceNavy,
                contentColor = RailwayGold,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = RailwayGold
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        searchQuery = ""
                    },
                    text = {
                        Text(
                            "Stations CUG (141)",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) RailwayGold else Color.White,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        searchQuery = ""
                    },
                    text = {
                        Text(
                            "Kharsia Lobby (369)",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) RailwayGold else Color.White,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        searchQuery = ""
                    },
                    text = {
                        Text(
                            "Other Lobbies (${otherLobbies.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 2) RailwayGold else Color.White,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        searchQuery = ""
                    },
                    text = {
                        Text(
                            "Control Center",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 3) RailwayGold else Color.White,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            // =========================================================================
            // 4. TAB CONTENTS
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                when (selectedTab) {
                    0 -> StationsTabView(
                        stations = filteredStations,
                        currentFilter = stationSectionFilter,
                        onFilterChange = { stationSectionFilter = it },
                        onCall = { phone -> dialPhoneNumber(context, phone) }
                    )
                    1 -> KharsiaLobbyTabView(
                        crewList = filteredKharsiaCrew,
                        kharsiaLobby = kharsiaLobby,
                        categoryFilter = crewCategoryFilter,
                        onCategoryFilterChange = { crewCategoryFilter = it },
                        onCall = { phone -> dialPhoneNumber(context, phone) }
                    )
                    2 -> OtherLobbiesTabView(
                        lobbies = filteredOtherLobbies,
                        onLobbyClick = onLobbyClick
                    )
                    3 -> ControlCenterTabView(
                        contacts = filteredControlContacts,
                        onCall = { phone -> dialPhoneNumber(context, phone) }
                    )
                }
            }
        }
    }
}

// =============================================================================
// SUB-COMPONENTS & TAB VIEWS
// =============================================================================

@Composable
private fun EmergencyQuickChip(
    title: String,
    phone: String,
    role: String,
    onCall: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .clickable { onCall() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDC2626)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = phone,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = RailwayGold
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 0: STATIONS CUG (141 STATIONS FROM PDF)
// -----------------------------------------------------------------------------
@Composable
private fun StationsTabView(
    stations: List<StationContact>,
    currentFilter: String,
    onFilterChange: (String) -> Unit,
    onCall: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Section Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val chips = listOf(
                "ALL" to "All (141)",
                "SEC1" to "BSP-IB (27)",
                "SEC2" to "Champa-Korba (13)",
                "SEC3" to "Katni Route (38)",
                "SEC4" to "Durg-Raipur (32)",
                "SEC5" to "Nagpur-Gondia (31)"
            )
            chips.forEach { (key, label) ->
                val isSelected = currentFilter == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) RailwayGold else DarkSurfaceNavy)
                        .border(1.dp, if (isSelected) RailwayGold else DarkBorderBlue, RoundedCornerShape(8.dp))
                        .clickable { onFilterChange(key) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color(0xFF0F1E36) else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Official SECR Operational CUG & Landline Directory (${stations.size} stations shown):",
            fontSize = 11.5.sp,
            color = Color(0xFFA0B4D0),
            modifier = Modifier.padding(vertical = 2.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 6.dp)
        ) {
            items(stations, key = { "${it.section}_${it.code}_${it.sNo}" }) { st ->
                StationCardItem(station = st, onCall = onCall)
            }
        }
    }
}

@Composable
private fun StationCardItem(
    station: StationContact,
    onCall: (String) -> Unit
) {
    val isKharsia = station.code.equals("KHS", ignoreCase = true)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isKharsia) Color(0xFF1E2E48) else DarkSurfaceNavy
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.2.dp,
                if (isKharsia) RailwayGold else DarkBorderBlue,
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Station Code Badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isKharsia) Brush.verticalGradient(listOf(Color(0xFFB45309), Color(0xFFD97706)))
                        else Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF2563EB)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = station.code,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = if (station.code.length > 5) 10.sp else 12.5.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isKharsia) RailwayGold else Color.White,
                        fontSize = 14.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isKharsia) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(RailwayGold)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "HOME LOBBY",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F1E36)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = station.section,
                    fontSize = 10.5.sp,
                    color = Color(0xFF93C5FD),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CUG: ${station.cugMobile}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailwayGreen
                    )
                    if (station.landline.isNotBlank()) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "STD: ${station.landline}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF67E8F9)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Action Call Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Call CUG
                IconButton(
                    onClick = { onCall(station.cugMobile) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(RailwayGreen.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call CUG",
                        tint = RailwayGreen,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Call Landline if available
                if (station.landline.isNotBlank()) {
                    IconButton(
                        onClick = { onCall(station.landline) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0891B2).copy(alpha = 0.25f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneInTalk,
                            contentDescription = "Call Landline",
                            tint = Color(0xFF22D3EE),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 1: KHARSIA LOBBY DEDICATED VIEW (369 RUNNING STAFF & SUPERVISORS)
// -----------------------------------------------------------------------------
@Composable
private fun KharsiaLobbyTabView(
    crewList: List<CrewMember>,
    kharsiaLobby: Lobby?,
    categoryFilter: Int,
    onCategoryFilterChange: (Int) -> Unit,
    onCall: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Kharsia Lobby Header Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2E48)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, RailwayGold, RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                KharsiaLobbyEmblem(size = 48.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "खरसिया कंबाइंड लॉबी • KHARSIA LOBBY",
                        fontWeight = FontWeight.Bold,
                        color = RailwayGold,
                        fontSize = 13.5.sp
                    )
                    Text(
                        text = "369 Active Running Staff (Loco Pilots & ALPs) • 24x7 Operations",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Lobby Master Landline: 07766-276100 • Station CUG: 9752090650",
                        fontSize = 10.sp,
                        color = Color(0xFF93C5FD)
                    )
                }
                IconButton(
                    onClick = { onCall("07766276100") },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RailwayGold)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call Kharsia Lobby",
                        tint = Color(0xFF0F1E36),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALL (369)", "LP Goods", "ALP / SALP").forEachIndexed { idx, label ->
                val isSelected = categoryFilter == idx
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) RailwayGold else DarkSurfaceNavy)
                        .border(1.dp, if (isSelected) RailwayGold else DarkBorderBlue, RoundedCornerShape(8.dp))
                        .clickable { onCategoryFilterChange(idx) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color(0xFF0F1E36) else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Showing ${crewList.size} Kharsia Crew Members:",
            fontSize = 11.5.sp,
            color = Color(0xFFA0B4D0)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 6.dp)
        ) {
            items(crewList, key = { it.crewId }) { crew ->
                KharsiaCrewCard(crew = crew, onCall = onCall)
            }
        }
    }
}

@Composable
private fun KharsiaCrewCard(
    crew: CrewMember,
    onCall: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceNavy),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorderBlue, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(RailwayNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = RailwayGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = crew.name,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(RailwayGold.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = crew.crewId,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RailwayGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${crew.designation} • ${crew.cadre}",
                    fontSize = 11.5.sp,
                    color = Color(0xFFA0B4D0)
                )

                if (crew.mobile.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "CUG / Mobile: ${crew.mobile}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RailwayGreen
                    )
                }
            }

            if (crew.mobile.isNotBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onCall(crew.mobile) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RailwayGreen.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call",
                        tint = RailwayGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 2: OTHER 12 LOBBIES (BSP, RIG, KRBA, SDL, etc.)
// -----------------------------------------------------------------------------
@Composable
private fun OtherLobbiesTabView(
    lobbies: List<Lobby>,
    onLobbyClick: (Lobby) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "SECR Bilaspur Division Lobbies (${lobbies.size}):",
            fontSize = 11.5.sp,
            color = Color(0xFFA0B4D0),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 6.dp)
        ) {
            items(lobbies, key = { it.code }) { lobby ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceNavy),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkBorderBlue, RoundedCornerShape(12.dp))
                        .clickable { onLobbyClick(lobby) }
                        .testTag("lobby_item_${lobby.code}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RailwayNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lobby.code,
                                color = RailwayGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lobby.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${lobby.totalContacts} Contacts • ${lobby.categories.size} Sections",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA0B4D0)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF7E8EA6)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 3: CONTROL CENTER (FULL DIRECTORY: TLC, TPC, CCC, SSE, DPC)
// -----------------------------------------------------------------------------
@Composable
private fun ControlCenterTabView(
    contacts: List<Pair<String, StaffContact>>,
    onCall: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "SECR Divisional Control Center & Departmental Helplines (${contacts.size}):",
            fontSize = 11.5.sp,
            color = Color(0xFFA0B4D0),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 6.dp)
        ) {
            items(contacts, key = { "${it.first}_${it.second.name}_${it.second.mobile}" }) { (dept, contact) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceNavy),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkBorderBlue, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF7F1D1D)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color(0xFFFCA5A5),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.name,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$dept • ${contact.designation}",
                                fontSize = 11.sp,
                                color = Color(0xFF93C5FD)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Number: ${contact.mobile}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RailwayGreen
                            )
                        }

                        IconButton(
                            onClick = { onCall(contact.mobile) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(RailwayGreen.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call",
                                tint = RailwayGreen,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
