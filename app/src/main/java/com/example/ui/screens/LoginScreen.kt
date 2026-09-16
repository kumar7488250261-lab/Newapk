package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

/**
 * Modern High-Fidelity Login Screen.
 * User ID Rule: Must be 'KHS' followed by EXACTLY 4 digits (e.g. KHS1234).
 * Password Rule: Default is '1234'.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (userId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var userIdError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Validation helper
    fun validateAndSubmit() {
        val trimmedId = userId.trim().uppercase()
        val trimmedPass = password.trim()

        var hasError = false

        // ID Validation: KHS + exactly 4 digits
        // Regex: starts with KHS, followed by 4 digits, nothing else
        val idRegex = Regex("^KHS[0-9]{4}$")
        if (trimmedId.isEmpty()) {
            userIdError = "Please enter User ID"
            hasError = true
        } else if (!idRegex.matches(trimmedId)) {
            userIdError = "User ID must be KHS followed by exactly 4 digits (e.g. KHS1234)"
            hasError = true
        } else {
            userIdError = null
        }

        // Password Validation: default is 1234
        if (trimmedPass.isEmpty()) {
            passwordError = "Please enter Password"
            hasError = true
        } else if (trimmedPass != "1234") {
            passwordError = "Incorrect password! Please check and try again"
            hasError = true
        } else {
            passwordError = null
        }

        if (!hasError) {
            onLoginSuccess(trimmedId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Staff Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("login_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF081326),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
                .testTag("login_screen")
        ) {
            // Subtle ambient railway glow circles
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF38BDF8).copy(alpha = 0.08f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFF59E0B).copy(alpha = 0.06f), Color.Transparent)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo and "WELCOME TO KHARSIA LOBBY"
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0C1D36))
                        .border(
                            BorderStroke(
                                2.dp,
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF38BDF8),
                                        Color(0xFF10B981),
                                        Color(0xFFF59E0B),
                                        Color(0xFF38BDF8)
                                    )
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_kharsia_logo),
                        contentDescription = "Kharsia Lobby Logo",
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Banner: WELCOME TO KHARSIA LOBBY
                Text(
                    text = "WELCOME TO KHARSIA LOBBY",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.8.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "संयुक्त चालक एवं परिचालक लॉबी खरसिया",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFBBF24),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "SECR • Bilaspur Division • Indian Railways",
                    fontSize = 11.sp,
                    color = DarkTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Modern Login Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_form_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF38BDF8).copy(alpha = 0.4f),
                                DarkCardBorder,
                                Color(0xFF10B981).copy(alpha = 0.3f)
                            )
                        )
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "SECURE LOGIN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextMuted,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // USER ID FIELD
                        Text(
                            text = "USER ID",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = userId,
                            onValueChange = { input ->
                                val cleaned = input.filter { it.isLetterOrDigit() }.take(7)
                                userId = cleaned.uppercase()
                                if (userIdError != null) userIdError = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_id_input"),
                            placeholder = {
                                Text(
                                    text = "Enter User ID",
                                    color = DarkTextMuted,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = "User ID",
                                    tint = if (userIdError != null) MaterialTheme.colorScheme.error else RailwayLightBlue
                                )
                            },
                            trailingIcon = {
                                if (userId.isNotEmpty()) {
                                    IconButton(onClick = { userId = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = DarkTextSecondary
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            isError = userIdError != null,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                keyboardType = KeyboardType.Ascii,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RailwayLightBlue,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedTextColor = DarkTextPrimary,
                                unfocusedTextColor = DarkTextPrimary,
                                errorBorderColor = MaterialTheme.colorScheme.error
                            )
                        )

                        // ID Error if any
                        if (userIdError != null) {
                            Text(
                                text = userIdError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // PASSWORD FIELD
                        Text(
                            text = "PASSWORD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { input ->
                                password = input
                                if (passwordError != null) passwordError = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            placeholder = {
                                Text(
                                    text = "Enter Password",
                                    color = DarkTextMuted,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = if (passwordError != null) MaterialTheme.colorScheme.error else RailwayLightBlue
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = DarkTextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            isError = passwordError != null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    validateAndSubmit()
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RailwayLightBlue,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedTextColor = DarkTextPrimary,
                                unfocusedTextColor = DarkTextPrimary,
                                errorBorderColor = MaterialTheme.colorScheme.error
                            )
                        )

                        // Password Error if any
                        if (passwordError != null) {
                            Text(
                                text = passwordError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // SUBMIT LOGIN BUTTON
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                validateAndSubmit()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Login,
                                    contentDescription = "Login",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LOGIN TO LOBBY",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Security footer note
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Running Staff Authentication",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary
                            )
                            Text(
                                text = "Only authorized crew of Bilaspur Division may log in.",
                                fontSize = 11.sp,
                                color = DarkTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
