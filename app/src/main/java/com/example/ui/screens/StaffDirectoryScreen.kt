package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Lobby
import com.example.data.StaffContact
import com.example.ui.theme.*
import com.example.ui.util.ContactActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDirectoryScreen(
    lobbies: List<Lobby>,
    onSelectLobby: (Lobby) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val searchResults = remember(searchQuery, lobbies) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val q = searchQuery.trim().lowercase()
            val matches = mutableListOf<StaffContact>()
            for (lobby in lobbies) {
                for (cat in lobby.categories) {
                    for (c in cat.contacts) {
                        if (c.name.lowercase().contains(q) ||
                            c.mobile.contains(q) ||
                            c.category.lowercase().contains(q) ||
                            c.lobbyName.lowercase().contains(q) ||
                            c.lobbyCode.lowercase().contains(q)
                        ) {
                            matches.add(c)
                        }
                    }
                }
            }
            matches
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kharsia_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Staff Directory",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "14 Stations & Operational Lobbies",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("staff_dir_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF081326),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("staff_search_input"),
                placeholder = { Text("Search staff name, number, station...", fontSize = 14.sp, color = DarkTextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = RailwayLightBlue
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = DarkTextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RailwayLightBlue,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = DarkTextPrimary,
                    unfocusedTextColor = DarkTextPrimary
                )
            )

            if (searchQuery.isNotBlank()) {
                // Showing search results
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Results: ${searchResults.size} staff found",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailwayTextSecondary
                    )
                }

                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = "Not found",
                                modifier = Modifier.size(54.dp),
                                tint = RailwayDisabled
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No staff found matching '$searchQuery'",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = RailwayTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Check name spelling, mobile number or lobby",
                                fontSize = 12.sp,
                                color = RailwayDisabled
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(searchResults) { contact ->
                            StaffContactCard(
                                contact = contact,
                                onCall = { ContactActions.dialPhoneNumber(context, contact.mobile) },
                                onCopy = { ContactActions.copyToClipboard(context, contact.mobile, contact.name) },
                                onWhatsApp = { ContactActions.openWhatsApp(context, contact.mobile) }
                            )
                        }
                    }
                }
            } else {
                // Showing lobby tiles list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "SELECT STATION / LOBBY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextMuted,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }

                    items(lobbies) { lobby ->
                        LobbyTileCard(
                            lobby = lobby,
                            onClick = { onSelectLobby(lobby) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LobbyTileCard(
    lobby: Lobby,
    onClick: () -> Unit
) {
    val isKharsia = lobby.id == "kharsia" || lobby.code == "KHS"

    val stationBadgeColor = when (lobby.code.uppercase()) {
        "KHS" -> Color(0xFF10B981)
        "BSP" -> Color(0xFF3B82F6)
        "CPH" -> Color(0xFF8B5CF6)
        "KRBA" -> Color(0xFFF97316)
        "RIG" -> Color(0xFF06B6D4)
        "SDL" -> Color(0xFFEC4899)
        "APR" -> Color(0xFF14B8A6)
        "MDGR" -> Color(0xFFEAB308)
        "BPHB" -> Color(0xFF6366F1)
        "BYT" -> Color(0xFF10B981)
        "USL" -> Color(0xFF38BDF8)
        "PND" -> Color(0xFFA855F7)
        "BRJN" -> Color(0xFFF43F5E)
        "BJRI" -> Color(0xFF22C55E)
        else -> Color(0xFF3B82F6)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("lobby_card_${lobby.code.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isKharsia) Color(0xFF08221B) else DarkSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isKharsia) 4.dp else 1.dp),
        border = if (isKharsia) {
            BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(
                    listOf(Color(0xFF10B981), Color(0xFF06B6D4))
                )
            )
        } else {
            BorderStroke(
                1.dp,
                DarkCardBorder
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Station badge circle
                if (isKharsia) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_kharsia_logo),
                        contentDescription = "Kharsia Logo",
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(stationBadgeColor.copy(alpha = 0.2f))
                            .border(
                                1.5.dp,
                                stationBadgeColor.copy(alpha = 0.8f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (lobby.code.length > 4) lobby.code.take(3) else lobby.code,
                            color = stationBadgeColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = lobby.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkTextPrimary
                        )
                        if (isKharsia) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF064E3B),
                                border = BorderStroke(1.dp, Color(0xFF10B981))
                            ) {
                                Text(
                                    text = "HOME LOBBY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF34D399),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${lobby.totalContacts} Staff Registered",
                        fontSize = 12.sp,
                        color = if (isKharsia) Color(0xFF34D399) else DarkTextSecondary
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = if (isKharsia) Color(0xFF34D399) else DarkTextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category summary preview chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(lobby.categories) { cat ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isKharsia) Color(0xFF0D3328) else DarkSurfaceElevated,
                        border = BorderStroke(0.5.dp, DarkCardBorder)
                    ) {
                        Text(
                            text = "${cat.name} (${cat.contacts.size})",
                            fontSize = 11.sp,
                            color = if (isKharsia) Color(0xFF6EE7B7) else DarkTextSecondary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StaffContactCard(
    contact: StaffContact,
    onCall: () -> Unit,
    onCopy: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("staff_card_${contact.name.replace(" ", "_")}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, DarkCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = contact.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Badge
                    val badgeColor = when (contact.category.uppercase()) {
                        "LPG", "LP" -> Color(0xFF38BDF8)
                        "ALP" -> Color(0xFF2DD4BF)
                        "TM", "GUARD" -> Color(0xFFA855F7)
                        "CLI" -> Color(0xFFFBBF24)
                        "TLC" -> Color(0xFFFB7185)
                        "STATION" -> Color(0xFF34D399)
                        else -> RailwayLightBlue
                    }
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeColor.copy(alpha = 0.15f),
                        border = BorderStroke(0.5.dp, badgeColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = contact.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Lobby Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(0.5.dp, DarkCardBorder)
                    ) {
                        Text(
                            text = contact.lobbyCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = DarkTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = contact.mobile,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Copy Button
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy number",
                        tint = DarkTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // WhatsApp Button
                IconButton(
                    onClick = onWhatsApp,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ActionWhatsAppGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Call Button
                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ActionCallGreen)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Call,
                        contentDescription = "Dial",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
