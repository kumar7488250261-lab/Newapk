package com.example.ui.screens.equipment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.equipment.DesignationCategory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastIssueScreen(
    viewModel: EquipmentViewModel,
    onBack: () -> Unit
) {
    var equipmentName by remember { mutableStateOf("Walkie Talkie") }
    var serialNo by remember { mutableStateOf("") }
    var crewId by remember { mutableStateOf("") }
    var crewName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("Loco Pilot (Goods)") }
    var category by remember { mutableStateOf(DesignationCategory.LP) }
    var trainNo by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    var isManualStaff by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val crewLookup by viewModel.crewLookupResult.collectAsStateWithLifecycle()

    LaunchedEffect(crewLookup) {
        val found = crewLookup
        if (found != null) {
            crewName = found.name
            designation = found.designation
            category = when (found.category) {
                "ALP" -> DesignationCategory.ALP
                "GUARD" -> DesignationCategory.GUARD
                else -> DesignationCategory.LP
            }
        }
    }

    val quickEquipmentList = listOf(
        "Walkie Talkie (5W VHF)",
        "HS Torch / Tri-Colour Torch",
        "Detonators Box (10 Nos)",
        "Loco Reverser Key",
        "Tail Lamp (Flashing Red)",
        "First Aid Box",
        "CBC Uncoupling Tool"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("फास्ट इशू / Fast Issue", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Equip Staff with Fast Auto-Lookup", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RailwayNavy)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Equipment details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. उपकरण विवरण / Equipment Details",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = RailwayNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = equipmentName,
                            onValueChange = { equipmentName = it },
                            label = { Text("Equipment Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = serialNo,
                            onValueChange = { serialNo = it },
                            label = { Text("Serial No / Asset Tag (e.g. WT-1024)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Quick Select:", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Walkie Talkie", "HS Torch", "Tail Lamp").forEach { eq ->
                                SuggestionChip(
                                    onClick = { equipmentName = eq },
                                    label = { Text(eq, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Crew details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "2. क्रू विवरण / Crew Details",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = RailwayNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = crewId,
                            onValueChange = {
                                crewId = it
                                viewModel.lookupCrew(it)
                            },
                            label = { Text("Crew ID (Auto Lookup, e.g. KHS1001)") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_crew_id"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = crewName,
                            onValueChange = { crewName = it },
                            label = { Text("Crew Member Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            enabled = isManualStaff || crewLookup == null,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = designation,
                            onValueChange = { designation = it },
                            label = { Text("Designation") },
                            enabled = isManualStaff || crewLookup == null,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = trainNo,
                            onValueChange = { trainNo = it },
                            label = { Text("Train No / Load (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Remarks (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            if (formError != null) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = formError!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (equipmentName.trim().isEmpty()) {
                            formError = "कृपया उपकरण का नाम दर्ज करें"
                            return@Button
                        }
                        if (serialNo.trim().isEmpty()) {
                            formError = "कृपया उपकरण सीरियल नंबर दर्ज करें"
                            return@Button
                        }
                        if (crewId.trim().isEmpty() || crewName.trim().isEmpty()) {
                            formError = "कृपया मान्य क्रू आईडी या नाम दर्ज करें"
                            return@Button
                        }

                        formError = null
                        viewModel.submitIssue(
                            equipmentName = equipmentName.trim(),
                            serialNo = serialNo.trim(),
                            crewId = crewId.trim().uppercase(),
                            crewName = crewName.trim(),
                            designation = designation.trim(),
                            category = category,
                            trainNo = trainNo.trim(),
                            remarks = remarks.trim()
                        )
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_submit_issue"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "जारी करें / Confirm Issue",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
