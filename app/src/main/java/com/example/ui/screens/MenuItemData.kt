package com.example.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItemData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val isEnabled: Boolean,
    val statusBadge: String,
    val testTag: String,
    val iconTint: Color,
    val iconBgColor: Color
)
