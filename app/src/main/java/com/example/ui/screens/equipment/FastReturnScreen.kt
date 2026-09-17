package com.example.ui.screens.equipment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.LightSurface
import com.example.ui.theme.RailwayNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastReturnScreen(
    viewModel: EquipmentViewModel,
    onBack: () -> Unit
) {
    val activeRecords by viewModel.activeStoreRecords.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Store Register - समान वापसी (CHO)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RailwayNavy)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightSurface)
        ) {
            StoreReturnTab(
                viewModel = viewModel,
                activeRecords = activeRecords
            )
        }
    }
}
