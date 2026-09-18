package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AuthManager
import com.example.ui.components.KharsiaLobbyEmblem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authManager: AuthManager,
    onLoginSuccess: (String) -> Unit,
    onBack: () -> Unit
) {
    var crewId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun attemptLogin() {
        val trimmedId = crewId.trim().uppercase()
        val trimmedPass = password.trim()

        if (trimmedId.isEmpty()) {
            errorMessage = "कृपया Crew ID दर्ज करें"
            return
        }
        if (trimmedPass.isEmpty()) {
            errorMessage = "कृपया पासवर्ड दर्ज करें"
            return
        }

        if (trimmedPass != "1234") {
            errorMessage = "अमान्य Crew ID या पासवर्ड"
            return
        }

        errorMessage = null
        authManager.login(trimmedId)
        onLoginSuccess(trimmedId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Login Portal",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF070E17)
                )
            )
        },
        containerColor = Color(0xFF070E17)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Real Kharsia Lobby Building Photo Background
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
                                Color(0xFF05101E).copy(alpha = 0.30f),
                                Color(0xFF05101E).copy(alpha = 0.82f),
                                Color(0xFF05101E).copy(alpha = 0.96f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Circular Railway Emblem with crisp Station Signboard
                KharsiaLobbyEmblem(
                    modifier = Modifier.padding(bottom = 10.dp),
                    size = 96.dp
                )

                // Indian Railways • SECR Bilaspur Capsule Badge (Centered)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF0C2744).copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, Color(0xFF2979FF).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "INDIAN RAILWAYS • SECR BILASPUR",
                        color = Color(0xFF81D4FA),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Welcome to Kharsia Lobby Header (Centered)
                Text(
                    text = "Welcome to",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Kharsia Lobby",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD54F) // Golden Amber
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "SECR BILASPUR DIVISION",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB0BEC5),
                        letterSpacing = 1.5.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Login Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B2C).copy(alpha = 0.94f)),
                    border = BorderStroke(1.2.dp, Color(0xFF1E3A5F)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Hindi and English Title inside card - PERFECTLY CENTERED
                        Text(
                            text = "संयुक्त चालक एवं परिचालक लॉबी",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "खरसिया",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "दक्षिण पूर्व मध्य रेलवे • बिलासपुर मंडल",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFFB0BEC5),
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "COMBINED CREW & TM LOBBY",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF81D4FA),
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.2.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = Color(0xFF1E3A5F)
                        )

                        // Crew ID Input Box (No hint, pure clean input)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "CREW ID",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF90A4AE),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = crewId,
                                onValueChange = {
                                    crewId = it.uppercase()
                                    errorMessage = null
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = Color(0xFF2979FF)
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF071422),
                                    unfocusedContainerColor = Color(0xFF071422),
                                    focusedBorderColor = Color(0xFF2979FF),
                                    unfocusedBorderColor = Color(0xFF1E3A5F)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_crew_id"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Input Box (No hint, pure clean input)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "PASSWORD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF90A4AE),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    errorMessage = null
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF2979FF)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = Color(0xFF546E7A)
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { attemptLogin() }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF071422),
                                    unfocusedContainerColor = Color(0xFF071422),
                                    focusedBorderColor = Color(0xFF2979FF),
                                    unfocusedBorderColor = Color(0xFF1E3A5F)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_password"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Error Message Display
                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFFB71C1C).copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, Color(0xFFEF5350)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = Color(0xFFFF8A80),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large Blue Login Button
                        Button(
                            onClick = { attemptLogin() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("login_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E60E6)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "LOGIN",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.2.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Running Staff Security Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Authorized Running Staff Portal • Bilaspur Division",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF90A4AE),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
