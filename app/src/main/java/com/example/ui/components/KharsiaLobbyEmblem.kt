package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Beautiful vector emblem matching the circular Indian Railways SECR Kharsia Lobby logo
 * seen in the official user screenshots.
 */
@Composable
fun KharsiaLobbyEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)
        val radius = w / 2f

        // Outer Dark Navy Circle
        drawCircle(
            color = Color(0xFF0D1B2A),
            radius = radius,
            center = center
        )

        // Outer Gold/Amber Ring
        drawCircle(
            color = Color(0xFFF39C12),
            radius = radius * 0.94f,
            center = center,
            style = Stroke(width = w * 0.05f)
        )

        // Inner Light Blue Sky
        drawCircle(
            color = Color(0xFF5BA4E6),
            radius = radius * 0.85f,
            center = center
        )

        // Ground / Platform Track Base (Grey)
        val groundPath = Path().apply {
            moveTo(w * 0.15f, h * 0.70f)
            lineTo(w * 0.85f, h * 0.70f)
            lineTo(w * 0.85f, h * 0.92f)
            lineTo(w * 0.15f, h * 0.92f)
            close()
        }
        drawPath(groundPath, color = Color(0xFF37474F))

        // Railway tracks (perspective rails)
        val railColor = Color(0xFFB0BEC5)
        drawLine(
            color = railColor,
            start = Offset(w * 0.38f, h * 0.65f),
            end = Offset(w * 0.22f, h * 0.90f),
            strokeWidth = w * 0.035f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = railColor,
            start = Offset(w * 0.62f, h * 0.65f),
            end = Offset(w * 0.78f, h * 0.90f),
            strokeWidth = w * 0.035f,
            cap = StrokeCap.Round
        )

        // Track Sleepers (crossbars)
        drawLine(
            color = Color(0xFF78909C),
            start = Offset(w * 0.34f, h * 0.72f),
            end = Offset(w * 0.66f, h * 0.72f),
            strokeWidth = w * 0.025f
        )
        drawLine(
            color = Color(0xFF78909C),
            start = Offset(w * 0.30f, h * 0.79f),
            end = Offset(w * 0.70f, h * 0.79f),
            strokeWidth = w * 0.03f
        )
        drawLine(
            color = Color(0xFF78909C),
            start = Offset(w * 0.25f, h * 0.87f),
            end = Offset(w * 0.75f, h * 0.87f),
            strokeWidth = w * 0.035f
        )

        // Electric Locomotive / WAP-7 front cabin (White & Blue with Red stripe)
        val locoWidth = w * 0.28f
        val locoHeight = h * 0.24f
        val locoLeft = w * 0.22f
        val locoTop = h * 0.44f

        // Locomotive Body (White)
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(locoLeft, locoTop),
            size = Size(locoWidth, locoHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f)
        )

        // Locomotive Top Roof (Navy Blue)
        drawRect(
            color = Color(0xFF0D47A1),
            topLeft = Offset(locoLeft, locoTop),
            size = Size(locoWidth, locoHeight * 0.28f)
        )

        // Windshield glasses (Cyan/Dark Blue)
        drawRect(
            color = Color(0xFF1565C0),
            topLeft = Offset(locoLeft + locoWidth * 0.12f, locoTop + locoHeight * 0.32f),
            size = Size(locoWidth * 0.34f, locoHeight * 0.25f)
        )
        drawRect(
            color = Color(0xFF1565C0),
            topLeft = Offset(locoLeft + locoWidth * 0.54f, locoTop + locoHeight * 0.32f),
            size = Size(locoWidth * 0.34f, locoHeight * 0.25f)
        )

        // Red band on train
        drawRect(
            color = Color(0xFFD32F2F),
            topLeft = Offset(locoLeft, locoTop + locoHeight * 0.62f),
            size = Size(locoWidth, locoHeight * 0.14f)
        )

        // Headlight (Yellow)
        drawCircle(
            color = Color(0xFFFFEB3B),
            radius = w * 0.022f,
            center = Offset(locoLeft + locoWidth / 2f, locoTop + locoHeight * 0.83f)
        )

        // Yellow Railway Station Board "KHARSIA"
        val boardWidth = w * 0.36f
        val boardHeight = h * 0.20f
        val boardLeft = w * 0.54f
        val boardTop = h * 0.46f

        // Board posts (poles)
        drawLine(
            color = Color(0xFF263238),
            start = Offset(boardLeft + boardWidth * 0.2f, boardTop + boardHeight),
            end = Offset(boardLeft + boardWidth * 0.2f, h * 0.74f),
            strokeWidth = w * 0.02f
        )
        drawLine(
            color = Color(0xFF263238),
            start = Offset(boardLeft + boardWidth * 0.8f, boardTop + boardHeight),
            end = Offset(boardLeft + boardWidth * 0.8f, h * 0.74f),
            strokeWidth = w * 0.02f
        )

        // Yellow Board
        drawRoundRect(
            color = Color(0xFFFFD54F),
            topLeft = Offset(boardLeft, boardTop),
            size = Size(boardWidth, boardHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f)
        )
        // Board Border
        drawRoundRect(
            color = Color(0xFF263238),
            topLeft = Offset(boardLeft, boardTop),
            size = Size(boardWidth, boardHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f),
            style = Stroke(width = w * 0.015f)
        )

        // Text representation lines on board
        drawLine(
            color = Color(0xFF0D1B2A),
            start = Offset(boardLeft + boardWidth * 0.2f, boardTop + boardHeight * 0.35f),
            end = Offset(boardLeft + boardWidth * 0.8f, boardTop + boardHeight * 0.35f),
            strokeWidth = w * 0.02f
        )
        drawLine(
            color = Color(0xFF0D1B2A),
            start = Offset(boardLeft + boardWidth * 0.25f, boardTop + boardHeight * 0.65f),
            end = Offset(boardLeft + boardWidth * 0.75f, boardTop + boardHeight * 0.65f),
            strokeWidth = w * 0.015f
        )

        // Bottom SECR Wheel / Wings Emblem
        drawCircle(
            color = Color.White,
            radius = w * 0.08f,
            center = Offset(w / 2f, h * 0.84f)
        )
        drawCircle(
            color = Color(0xFF0D1B2A),
            radius = w * 0.05f,
            center = Offset(w / 2f, h * 0.84f)
        )
    }
}
