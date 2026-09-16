package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animate progress smoothly across 2 seconds (2000 ms)
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
        // Auto-navigate after 2 seconds
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030814),
                        Color(0xFF0A182F),
                        Color(0xFF040A17)
                    )
                )
            )
            .testTag("kharsia_splash_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1.2f))

            // Official Kharsia Lobby Circular Seal Logo with glowing rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(170.dp)
                    .border(
                        width = 3.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFF59E0B),
                                Color(0xFFFBBF24),
                                Color(0xFFF59E0B)
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_kharsia_logo),
                    contentDescription = "Kharsia Lobby Seal Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Welcome Text Header
            Text(
                text = "Welcome to",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Kharsia Lobby",
                color = Color(0xFFFBBF24),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SOUTH EAST CENTRAL RAILWAY",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "CREW MANAGEMENT & OPERATIONAL PORTAL",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 2-Second Animated Progress Bar
            Box(
                modifier = Modifier
                    .width(260.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF1E293B))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressAnim.value)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF06B6D4),
                                    Color(0xFF10B981),
                                    Color(0xFFF59E0B)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Fast-forward / Enter Portal button
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onTimeout)
                    .testTag("enter_portal_now_btn"),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF132238).copy(alpha = 0.8f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF38BDF8).copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enter Portal Now",
                        color = Color(0xFF93C5FD),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Enter",
                        tint = Color(0xFF93C5FD),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer division info
            Text(
                text = "KHS  •  BILASPUR DIVISION  •  SECR",
                color = Color(0xFF475569),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}
