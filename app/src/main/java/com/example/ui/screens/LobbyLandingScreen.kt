package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KharsiaLobbyEmblem
import com.example.ui.theme.*

@Composable
fun LobbyLandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToGuestDirectory: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.bg_kharsia_lobby_building),
            contentDescription = "Kharsia Lobby Building",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // High visibility gradient overlay: clear at top so building and Hindi signboard are prominently visible
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF040E1B).copy(alpha = 0.30f),
                            Color(0xFF040E1B).copy(alpha = 0.82f),
                            Color(0xFF040E1B).copy(alpha = 0.96f)
                        )
                    )
                )
        )

        // Top Badge: "INDIAN RAILWAYS • SECR BILASPUR"
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 18.dp)
            ) {
                // Central Circular Emblem
                KharsiaLobbyEmblem(
                    modifier = Modifier.padding(bottom = 12.dp),
                    size = 96.dp
                )

                // Railway Division Capsule Tag
                Surface(
                    color = Color(0xFF0C243B).copy(alpha = 0.92f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E4D7A))
                ) {
                    Text(
                        text = "INDIAN RAILWAYS • SECR BILASPUR",
                        color = Color(0xFF64B5F6),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Central Card matching Screenshot 1
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1E33).copy(alpha = 0.94f)),
                border = BorderStroke(1.5.dp, Color(0xFFE5A93C).copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WELCOME TO KHARSIA LOBBY",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "संयुक्त चालक एवं परिचालक लॉबी",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF1B748) // Golden yellow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "खरसिया",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "दक्षिण पूर्व मध्य रेलवे",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF90CAF9)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFF1E3A5F), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "COMBINED CREW & TM LOBBY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Running Staff Management Portal",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF78909C)
                        )
                    )
                }
            }

            // Bottom Login Action Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bright Blue LOGIN Button with Lock icon and Arrow
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_staff_login"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "LOGIN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Authorized Running Staff Portal • Bilaspur Division",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF78909C),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Procedural station facade background matching the red and beige Kharsia lobby building.
 */
@Composable
fun StationBuildingIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Dark red building pillars and facade
        val redColor = Color(0xFF8B1E1E)
        val paleWallColor = Color(0xFFCFD8DC)
        val awningRed = Color(0xFFB71C1C)
        val glassColor = Color(0xFF1A334B)

        // Center building block
        drawRect(
            color = redColor,
            topLeft = Offset(w * 0.15f, h * 0.12f),
            size = Size(w * 0.70f, h * 0.75f)
        )

        // Upper pale wall
        drawRect(
            color = paleWallColor,
            topLeft = Offset(w * 0.28f, h * 0.10f),
            size = Size(w * 0.44f, h * 0.25f)
        )

        // Center upper board with yellow sign
        drawRect(
            color = Color(0xFFF9A825),
            topLeft = Offset(w * 0.22f, h * 0.38f),
            size = Size(w * 0.56f, h * 0.12f)
        )
        drawRect(
            color = Color(0xFF263238),
            topLeft = Offset(w * 0.22f, h * 0.38f),
            size = Size(w * 0.56f, h * 0.12f),
            style = Stroke(width = 3.dp.toPx())
        )

        // Awning stripes
        val stripeCount = 10
        val stripeW = (w * 0.60f) / stripeCount
        for (i in 0 until stripeCount) {
            val color = if (i % 2 == 0) awningRed else Color.White
            drawRect(
                color = color,
                topLeft = Offset(w * 0.20f + (i * stripeW), h * 0.50f),
                size = Size(stripeW, h * 0.06f)
            )
        }

        // Two large station doors / windows
        drawRect(
            color = glassColor,
            topLeft = Offset(w * 0.22f, h * 0.58f),
            size = Size(w * 0.24f, h * 0.35f)
        )
        drawRect(
            color = glassColor,
            topLeft = Offset(w * 0.54f, h * 0.58f),
            size = Size(w * 0.24f, h * 0.35f)
        )
    }
}
