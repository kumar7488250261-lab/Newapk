package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.KharsiaLobbyEmblem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash Screen featuring the real Kharsia Lobby building background,
 * displaying "Welcome to Kharsia Lobby" for exactly 2 seconds,
 * then navigating directly to the Login page.
 */
@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }
        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
            )
        }
        // Exactly 2 seconds delay as requested
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Real Kharsia Lobby Building Background
        Image(
            painter = painterResource(id = R.drawable.bg_kharsia_lobby_building),
            contentDescription = "Kharsia Lobby Building",
            contentScale = ContentScale.Crop,
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 44.dp, horizontal = 24.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Official Circular Kharsia Lobby Emblem with Station Signboard
                KharsiaLobbyEmblem(size = 145.dp)

                Spacer(modifier = Modifier.height(26.dp))

                // Welcome to Kharsia Lobby (Aligned Center)
                Text(
                    text = "Welcome to",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Kharsia Lobby",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD54F), // Golden Amber
                        fontSize = 32.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SOUTH EAST CENTRAL RAILWAY",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB0BEC5),
                        letterSpacing = 2.sp,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "CREW MANAGEMENT & OPERATIONAL PORTAL",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.75f),
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(36.dp))

                // 2-Second Progress Indicator
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFFFFD54F),
                    trackColor = Color(0xFF1E354D)
                )
            }

            // Bottom Division Label
            Text(
                text = "KHS  •  BILASPUR DIVISION  •  SECR",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFFB0BEC5),
                    letterSpacing = 1.5.sp,
                    fontSize = 11.sp
                )
            )
        }
    }
}
