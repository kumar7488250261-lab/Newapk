package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LobbyInfo
import com.example.data.repository.CrewRepository
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LightBackground
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyDetailScreen(
    lobbyCode: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val repository = remember { CrewRepository(context) }

    var lobbyInfo by remember { mutableStateOf<LobbyInfo?>(null) }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(lobbyCode) {
        isLoading = true
        lobbyInfo = repository.getLobby(lobbyCode)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = lobbyInfo?.name ?: "$lobbyCode Lobby",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = lobbyInfo?.division ?: "SECR Bilaspur Division",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("lobby_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary
                )
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NavyPrimary)
            }
        } else if (lobbyInfo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Lobby details not found.")
            }
        } else {
            val lobby = lobbyInfo!!
            val totalLobbyCrew = lobby.categories.sumOf { it.contacts.size }

            val categoriesList = remember(lobby) {
                listOf("ALL") + lobby.categories.map { it.name }
            }

            val displayedCategories = remember(lobby, selectedCategory) {
                if (selectedCategory == "ALL") {
                    lobby.categories
                } else {
                    lobby.categories.filter { it.name == selectedCategory }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Header Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${lobby.name} (${lobby.code})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Badge(containerColor = GoldAccent, contentColor = NavyDark) {
                                Text("$totalLobbyCrew Staff", fontWeight = FontWeight.Bold)
                            }
                        }
                        if (lobby.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lobby.description,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Category Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoriesList.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(if (category == "ALL") "All Categories ($totalLobbyCrew)" else category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_cat_${category.take(4)}")
                        )
                    }
                }

                // Contact list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                ) {
                    displayedCategories.forEach { category ->
                        item(key = "hdr_${category.name}") {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = NavyPrimary.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = category.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NavyDark
                                    )
                                    Badge(containerColor = NavyPrimary, contentColor = Color.White) {
                                        Text("${category.contacts.size}", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        itemsIndexed(
                            items = category.contacts,
                            key = { index, item ->
                                "lobby_crew_${lobby.code}_${category.name}_${item.name}_${item.mobile}_$index"
                            }
                        ) { _, crew ->
                            CrewContactCard(
                                crew = crew.copy(lobbyCode = lobby.code, category = category.name),
                                onCall = { mobile ->
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$mobile"))
                                    context.startActivity(intent)
                                },
                                onCopy = { mobile ->
                                    clipboardManager.setText(AnnotatedString(mobile))
                                    Toast.makeText(context, "Copied $mobile to clipboard", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
