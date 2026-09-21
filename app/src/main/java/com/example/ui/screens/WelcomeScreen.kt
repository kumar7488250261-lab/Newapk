package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KharsiaLobbyEmblem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Step 2: Welcome Screen (Matching Screenshot 2).
 * Displays "Welcome to Kharsia Lobby", multi-color loading progress bar,
 * "Enter Portal Now ->" pill button, and division footer.
 */
@Composable
fun WelcomeScreen(
    onEnterPortal: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
            )
        }
        delay(2500)
        onEnterPortal()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060D18))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 44.dp, horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Official Circular Kharsia Lobby Emblem
                KharsiaLobbyEmblem(size = 140.dp)

                Spacer(modifier = Modifier.height(26.dp))

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
                        color = Color(0xFFFFB74D), // Golden Amber
                        fontSize = 32.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SOUTH EAST CENTRAL RAILWAY",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF90A4AE),
                        letterSpacing = 1.8.sp,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "CREW MANAGEMENT & OPERATIONAL PORTAL",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF607D8B),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Custom Gradient Progress Bar matching Screenshot 2
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF132030))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.value)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF00E5FF), // Cyan
                                        Color(0xFF00B0FF),
                                        Color(0xFFFFD54F)  // Amber / Gold
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // "Enter Portal Now ->" pill button
                Button(
                    onClick = onEnterPortal,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0C243B).copy(alpha = 0.85f)
                    ),
                    border = BorderStroke(1.2.dp, Color(0xFF1E4D7A)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                    modifier = Modifier.testTag("btn_enter_portal_now")
                ) {
                    Text(
                        text = "Enter Portal Now",
                        color = Color(0xFF90CAF9),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF90CAF9),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Bottom Division Label
            Text(
                text = "KHS  •  BILASPUR DIVISION  •  SECR",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF546E7A),
                    letterSpacing = 1.5.sp,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}
