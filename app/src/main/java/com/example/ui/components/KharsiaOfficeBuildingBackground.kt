package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Authentic architectural representation of Kharsia Combined Crew & TM Lobby building.
 * Features the distinctive red building pillars, upper floor windows,
 * and the iconic yellow & black bilingual station signboard.
 */
@Composable
fun KharsiaOfficeBuildingBackground(
    modifier: Modifier = Modifier,
    dimOverlayAlpha: Float = 0.65f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Sky & twilight atmospheric gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF061426),
                    Color(0xFF0F2642),
                    Color(0xFF142030)
                ),
                startY = 0f,
                endY = h
            ),
            size = size
        )

        // Building base silhouette
        val brickRed = Color(0xFF7A1B1B)
        val columnRed = Color(0xFF9E2424)
        val paleWall = Color(0xFFD4D8DD)
        val yellowBoard = Color(0xFFFFC107)
        val darkBoardBorder = Color(0xFF212121)
        val windowGlass = Color(0xFF1C344D)
        val awningDark = Color(0xFF4A1010)

        // Two-story main lobby building facade
        val bldgLeft = w * 0.08f
        val bldgWidth = w * 0.84f
        val bldgTop = h * 0.08f
        val bldgHeight = h * 0.70f

        // Lower & upper structure
        drawRect(
            color = brickRed,
            topLeft = Offset(bldgLeft, bldgTop),
            size = Size(bldgWidth, bldgHeight)
        )

        // Pale plaster band for second floor
        drawRect(
            color = paleWall,
            topLeft = Offset(bldgLeft + w * 0.04f, bldgTop + h * 0.03f),
            size = Size(bldgWidth - w * 0.08f, h * 0.22f)
        )

        // Upper windows
        val winW = w * 0.12f
        val winH = h * 0.09f
        val winY = bldgTop + h * 0.07f
        for (i in 0..4) {
            val winX = bldgLeft + w * 0.08f + (i * w * 0.14f)
            drawRect(
                color = windowGlass,
                topLeft = Offset(winX, winY),
                size = Size(winW, winH)
            )
            drawRect(
                color = Color(0xFFECEFF1),
                topLeft = Offset(winX, winY),
                size = Size(winW, winH),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Distinctive Yellow Lobby Signboard in the middle
        val signTop = bldgTop + h * 0.28f
        val signH = h * 0.11f
        val signW = bldgWidth - w * 0.04f
        val signLeft = bldgLeft + w * 0.02f

        drawRect(
            color = yellowBoard,
            topLeft = Offset(signLeft, signTop),
            size = Size(signW, signH)
        )
        drawRect(
            color = darkBoardBorder,
            topLeft = Offset(signLeft, signTop),
            size = Size(signW, signH),
            style = Stroke(width = 3.dp.toPx())
        )

        // Lower floor pillars and porch canopy
        val porchTop = signTop + signH + h * 0.02f
        val porchH = h * 0.05f
        drawRect(
            color = awningDark,
            topLeft = Offset(bldgLeft, porchTop),
            size = Size(bldgWidth, porchH)
        )

        // Pillars
        val pillarCount = 5
        val pillarW = w * 0.05f
        val groundY = bldgTop + bldgHeight
        val pillarH = groundY - (porchTop + porchH)
        for (i in 0 until pillarCount) {
            val px = bldgLeft + w * 0.05f + (i * (bldgWidth - w * 0.15f) / (pillarCount - 1))
            drawRect(
                color = columnRed,
                topLeft = Offset(px, porchTop + porchH),
                size = Size(pillarW, pillarH)
            )
        }

        // Entrance glass doors between pillars
        drawRect(
            color = windowGlass,
            topLeft = Offset(w * 0.35f, porchTop + porchH + h * 0.04f),
            size = Size(w * 0.30f, pillarH - h * 0.04f)
        )

        // Platform ground line
        drawRect(
            color = Color(0xFF1E2833),
            topLeft = Offset(0f, groundY),
            size = Size(w, h - groundY)
        )

        // Dark dim overlay so foreground text/cards are completely legible
        drawRect(
            color = Color(0xFF07111E).copy(alpha = dimOverlayAlpha),
            topLeft = Offset.Zero,
            size = size
        )
    }
}
