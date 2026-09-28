package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LightBackground
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class RoomStatus(
    val roomNumber: String,
    val totalBeds: Int,
    val occupiedBeds: Int,
    val acStatus: String,
    val cleanliness: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunningRoomScreen(
    onBack: () -> Unit
) {
    val rooms = remember {
        listOf(
            RoomStatus("Room 101", 4, 3, "AC Working", "Cleaned"),
            RoomStatus("Room 102", 4, 2, "AC Working", "Cleaned"),
            RoomStatus("Room 103", 4, 4, "AC Working", "Occupied"),
            RoomStatus("Room 104", 4, 1, "AC Working", "Cleaned"),
            RoomStatus("Room 201", 6, 4, "AC Working", "Cleaned"),
            RoomStatus("Room 202", 6, 5, "AC Working", "Cleaned"),
            RoomStatus("Room 203", 6, 2, "AC Working", "Cleaned"),
            RoomStatus("Room 204", 6, 0, "AC Working", "Available"),
            RoomStatus("Room 301 (VIP)", 2, 1, "AC Working", "Cleaned"),
            RoomStatus("Room 302 (VIP)", 2, 2, "AC Working", "Occupied")
        )
    }

    val totalBeds = rooms.sumOf { it.totalBeds }
    val occupiedBeds = rooms.sumOf { it.occupiedBeds }
    val availableBeds = totalBeds - occupiedBeds

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kharsia Running Room",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("running_room_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Running Room Bed Occupancy",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OccupancyStat(label = "Total Beds", value = "$totalBeds", color = Color.White)
                        OccupancyStat(label = "Occupied", value = "$occupiedBeds", color = GoldAccent)
                        OccupancyStat(label = "Available", value = "$availableBeds", color = Color(0xFF10B981))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Facilities & Amenities",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AmenityChip(iconRes = R.drawable.ic_restaurant, text = "Subsidized Kitchen")
                AmenityChip(iconRes = R.drawable.ic_bed, text = "Clean Linen")
                AmenityChip(iconRes = R.drawable.ic_hotel, text = "RO Water")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Room-wise Status",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(rooms) { room ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(NavyPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_hotel),
                                        contentDescription = null,
                                        tint = NavyPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = room.roomNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${room.acStatus} • ${room.cleanliness}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Badge(
                                containerColor = if (room.occupiedBeds >= room.totalBeds) Color(0xFFEF4444) else Color(0xFF10B981),
                                contentColor = Color.White
                            ) {
                                Text("${room.occupiedBeds}/${room.totalBeds} Beds", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OccupancyStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = color)
        Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
fun RowScope.AmenityChip(iconRes: Int, text: String) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}
