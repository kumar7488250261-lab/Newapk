package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
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
import com.example.data.Lobby
import com.example.data.StaffContact
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyDetailScreen(
    lobby: Lobby,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = remember(lobby) {
        listOf("All") + lobby.categories.map { it.category }
    }

    val contacts = remember(selectedCategory, searchQuery, lobby) {
        val baseList = if (selectedCategory == "All") {
            lobby.categories.flatMap { it.contacts }
        } else {
            lobby.categories.firstOrNull { it.category == selectedCategory }?.contacts ?: emptyList()
        }

        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) baseList
        else {
            baseList.filter {
                it.name.lowercase().contains(q) ||
                        it.designation.lowercase().contains(q) ||
                        it.mobile.contains(q)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = lobby.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Station Code: ${lobby.code} • ${lobby.totalContacts} Staff Members",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search by name, designation, mobile...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Category Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = (selectedCategory == cat),
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RailwayNavy,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Contacts List
            items(contacts) { contact ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(46.dp)
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

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = contact.designation,
                                style = MaterialTheme.typography.bodySmall.copy(color = RailwayAmber, fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ph: ${contact.mobile}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }

                        // Call Action Button
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${contact.mobile}")
                                }
                                context.startActivity(intent)
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }
    }
}
