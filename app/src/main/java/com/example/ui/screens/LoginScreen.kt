package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun attemptLogin() {
        val trimmedId = userId.trim().uppercase()
        val trimmedPass = password.trim()

        if (trimmedId.isEmpty()) {
            errorMessage = "कृपया Crew ID / Staff User ID दर्ज करें"
            return
        }
        if (trimmedPass.isEmpty()) {
            errorMessage = "कृपया पासवर्ड दर्ज करें"
            return
        }

        // Validate user ID format: KHS followed by digits, or demo KHS1234
        val validIdPattern = Regex("^KHS\\d{4}$", RegexOption.IGNORE_CASE)
        if (!validIdPattern.matches(trimmedId) && trimmedId != "KHS1234") {
            errorMessage = "अमान्य Crew ID! प्रारूप KHS + 4 अंक होना चाहिए (उदा. KHS1001, KHS1234)"
            return
        }

        if (trimmedPass != "1234") {
            errorMessage = "गलत पासवर्ड! डिफ़ॉल्ट पासवर्ड 1234 है।"
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
                        text = "Staff Login",
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
                    containerColor = Color(0xFF070E17) // Darkest navy
                )
            )
        },
        containerColor = Color(0xFF070E17) // Dark theme background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF070E17))
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular Railway Emblem
            KharsiaLobbyEmblem(
                modifier = Modifier.padding(bottom = 16.dp),
                size = 90.dp
            )

            // Prominent Heading exactly as requested: "WELCOME TO KHARSIA LOBBY"
            Text(
                text = "WELCOME TO KHARSIA LOBBY",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Hindi Subheading
            Text(
                text = "संयुक्त चालक एवं परिचालक लॉबी खरसिया",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE5A93C) // Golden Amber
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "SECR • Bilaspur Division • Indian Railways",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Dark Blue Card for Login Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2236)),
                border = BorderStroke(1.dp, Color(0xFF1E3A5F).copy(alpha = 0.8f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "SECURE LOGIN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF7E9BB8),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "USER ID",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF90A4AE),
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = userId,
                        onValueChange = {
                            userId = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text("Enter User ID (e.g. KHS1001)", color = Color(0xFF546E7A))
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
                            focusedContainerColor = Color(0xFF091624),
                            unfocusedContainerColor = Color(0xFF091624),
                            focusedBorderColor = Color(0xFF2979FF),
                            unfocusedBorderColor = Color(0xFF1E3A5F)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_userid"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "PASSWORD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF90A4AE),
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text("Enter Password", color = Color(0xFF546E7A))
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
                                    tint = Color(0xFF78909C)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { attemptLogin() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF091624),
                            unfocusedContainerColor = Color(0xFF091624),
                            focusedBorderColor = Color(0xFF2979FF),
                            unfocusedBorderColor = Color(0xFF1E3A5F)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_password"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Color(0xFF421015),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFD32F2F).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = Color(0xFFFF8A80),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Vibrant Blue Action Button
                    Button(
                        onClick = { attemptLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_submit_login"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LOGIN TO LOBBY",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Info Card in dark theme
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B2C)),
                border = BorderStroke(1.dp, Color(0xFF162D4A))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Running Staff Authentication",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Only authorized crew of Bilaspur Division may log in. Default password: 1234",
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
}
