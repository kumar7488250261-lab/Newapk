package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
fun HomeScreen(
    onNavigateToDirectory: (lobbyCode: String) -> Unit,
    onNavigateToLobbyDetail: (lobbyCode: String) -> Unit,
    onNavigateToRunningRoom: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { CrewRepository(context) }
    var lobbies by remember { mutableStateOf<List<LobbyInfo>>(emptyList()) }
    var totalCrewCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        val loaded = repository.getLobbies()
        lobbies = loaded
        totalCrewCount = loaded.sumOf { it.categories.sumOf { c -> c.contacts.size } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Kharsia Lobby & Crew Management",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "SECR Bilaspur Division",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Bilaspur Division Crew Directory",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Loco Pilots, Assistant Loco Pilots, Train Managers & Supervisory Staff",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            HomeStatItem(title = "Total Crew", value = if (totalCrewCount > 0) "$totalCrewCount" else "1,950+")
                            HomeStatItem(title = "Lobbies", value = "${lobbies.size.coerceAtLeast(14)}")
                            HomeStatItem(title = "Division", value = "SECR")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Quick Services",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeActionCard(
                        title = "Staff Directory",
                        subtitle = "All Lobbies & Categories",
                        iconRes = R.drawable.ic_people,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_staff_directory"),
                        onClick = { onNavigateToDirectory("ALL") }
                    )
                    HomeActionCard(
                        title = "Kharsia Lobby",
                        subtitle = "KHS Crew Details",
                        iconRes = R.drawable.ic_train,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_kharsia_lobby"),
                        onClick = { onNavigateToLobbyDetail("KHS") }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeActionCard(
                        title = "Running Room",
                        subtitle = "Occupancy & Facilities",
                        iconRes = R.drawable.ic_hotel,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_running_room"),
                        onClick = onNavigateToRunningRoom
                    )
                    HomeActionCard(
                        title = "Control & Officers",
                        subtitle = "TRSO & Emergency",
                        iconRes = R.drawable.ic_phone,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_control_officers"),
                        onClick = { onNavigateToLobbyDetail("CTRL") }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Division Lobbies",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    TextButton(onClick = { onNavigateToDirectory("ALL") }) {
                        Text("View All", color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    lobbies.chunked(2).forEach { rowLobbies ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowLobbies.forEach { lobby ->
                                val staffCount = lobby.categories.sumOf { it.contacts.size }
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToLobbyDetail(lobby.code) }
                                        .testTag("lobby_card_${lobby.code}"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = lobby.code,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = NavyPrimary
                                            )
                                            Badge(containerColor = NavyDark.copy(alpha = 0.1f), contentColor = NavyDark) {
                                                Text("$staffCount", fontSize = 11.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = lobby.name,
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                            if (rowLobbies.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeStatItem(title: String, value: String) {
    Column {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = GoldAccent)
        Text(text = title, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
fun HomeActionCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(NavyPrimary.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
        }
    }
}
