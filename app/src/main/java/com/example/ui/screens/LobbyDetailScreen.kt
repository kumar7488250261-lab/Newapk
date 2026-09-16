package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
fun LobbyDetailScreen(
    lobby: Lobby,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    val context = LocalContext.current

    // All categories list including "ALL"
    val categoryList = remember(lobby) {
        listOf("ALL") + lobby.categories.map { it.name }
    }

    // Filter contacts based on selectedCategory and searchQuery
    val filteredContacts = remember(searchQuery, selectedCategory, lobby) {
        val q = searchQuery.trim().lowercase()
        val allContacts = if (selectedCategory == "ALL") {
            lobby.categories.flatMap { it.contacts }
        } else {
            lobby.categories.find { it.name.equals(selectedCategory, ignoreCase = true) }?.contacts ?: emptyList()
        }

        if (q.isEmpty()) {
            allContacts
        } else {
            allContacts.filter {
                it.name.lowercase().contains(q) ||
                it.mobile.contains(q) ||
                it.category.lowercase().contains(q)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = lobby.name,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = RailwayAmber
                            ) {
                                Text(
                                    text = lobby.code,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF070E1B),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${lobby.totalContacts} Staff Registered",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RailwayAmber
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("lobby_detail_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (lobby.id == "kharsia" || lobby.code == "KHS") {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kharsia_logo),
                            contentDescription = "Kharsia Logo",
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
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
            // Search Input inside this lobby
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("lobby_search_input"),
                placeholder = { Text("Search by name or number in ${lobby.code}...", fontSize = 14.sp, color = DarkTextMuted) },
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

            // Category Filter Tabs (e.g. ALL, TLC, DPC, CLI, LPG, ALP, TM, etc.)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryList) { catName ->
                    val isSelected = selectedCategory == catName
                    val count = if (catName == "ALL") {
                        lobby.totalContacts
                    } else {
                        lobby.categories.find { it.name == catName }?.contacts?.size ?: 0
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFF1E3A8A) else DarkSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF38BDF8) else DarkCardBorder
                        ),
                        modifier = Modifier
                            .clickable { selectedCategory = catName }
                            .testTag("cat_chip_$catName")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = catName,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else DarkTextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) Color(0xFF38BDF8).copy(alpha = 0.25f) else DarkSurfaceElevated
                            ) {
                                Text(
                                    text = count.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF38BDF8) else DarkTextMuted,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Showing total in current filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${filteredContacts.size} staff",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkTextSecondary
                )
                if (selectedCategory != "ALL") {
                    Text(
                        text = "Category: $selectedCategory",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailwayLightBlue
                    )
                }
            }

            // Staff Contacts List
            if (filteredContacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = "No staff",
                            modifier = Modifier.size(54.dp),
                            tint = DarkTextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No staff found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredContacts) { contact ->
                        StaffContactCard(
                            contact = contact,
                            onCall = { ContactActions.dialPhoneNumber(context, contact.mobile) },
                            onCopy = { ContactActions.copyToClipboard(context, contact.mobile, contact.name) },
                            onWhatsApp = { ContactActions.openWhatsApp(context, contact.mobile) }
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
