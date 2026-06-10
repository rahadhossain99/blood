package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AuthStep
import com.example.ui.viewmodel.BloodViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    val authStep by viewModel.authStep.collectAsState()
    val authEmail by viewModel.authEmail.collectAsState()
    val generatedOtp by viewModel.generatedOtp.collectAsState()
    val authIsLoading by viewModel.authIsLoading.collectAsState()
    val authErrorMsg by viewModel.authErrorMsg.collectAsState()
    val isPasswordResetFlow by viewModel.isPasswordResetFlow.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Temporary values inside screen UI
    var emailInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var activeAuthTab by remember { androidx.compose.runtime.mutableIntStateOf(0) } // 0 = login, 1 = register
    var isForgotPasswordMode by remember { mutableStateOf(false) }

    // Sync input fields when auth flow resets
    LaunchedEffect(authStep) {
        if (authStep == AuthStep.EMAIL_INPUT) {
            emailInput = ""
            otpInput = ""
            nameInput = ""
            passwordInput = ""
            isForgotPasswordMode = false
        }
    }

    // Interactive simulated Auto-Fill logic for OTP
    var isAutoFilling by remember { mutableStateOf(false) }
    fun triggerMagicAutoFill() {
        if (generatedOtp.isEmpty() || isAutoFilling) return
        isAutoFilling = true
        scope.launch {
            otpInput = ""
            for (char in generatedOtp) {
                otpInput += char
                delay(150) // Beautiful typing effect delay
            }
            isAutoFilling = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // 1. Warm organic blood-red premium gradient cover
                val bgGradient = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFFAFA), Color(0xFFFFF0F2), Color(0xFFFFECEF))
                )
                drawRect(brush = bgGradient)

                // 2. Faint professional medical/science grid lines
                val spacingPx = 40.dp.toPx()
                val lineCol = Color(0xFFFF1744).copy(alpha = 0.05f)
                val lineStroke = 1.dp.toPx()

                var xVal = 0f
                while (xVal < size.width) {
                    drawLine(
                        color = lineCol,
                        start = androidx.compose.ui.geometry.Offset(xVal, 0f),
                        end = androidx.compose.ui.geometry.Offset(xVal, size.height),
                        strokeWidth = lineStroke
                    )
                    xVal += spacingPx
                }

                var yVal = 0f
                while (yVal < size.height) {
                    drawLine(
                        color = lineCol,
                        start = androidx.compose.ui.geometry.Offset(0f, yVal),
                        end = androidx.compose.ui.geometry.Offset(size.width, yVal),
                        strokeWidth = lineStroke
                    )
                    yVal += spacingPx
                }

                // 3. Faint atmospheric organic blood rings
                drawCircle(color = Color(0xFFFF1744).copy(alpha = 0.02f), radius = 150.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.15f))
                drawCircle(color = Color(0xFFFF1744).copy(alpha = 0.03f), radius = 220.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.95f, size.height * 0.75f))
                drawCircle(color = Color(0xFFFF1744).copy(alpha = 0.015f), radius = 90.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.9f))
            }
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Pulse Blood Ring Graphic Accent
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                    .shadow(8.dp, RoundedCornerShape(45.dp), clip = false)
                    .clip(RoundedCornerShape(45.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF1744), // Neon Red
                                Color(0xFFD50000)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Opacity,
                    contentDescription = "Blood Drop Logo",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "রক্তবন্ধু",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Text(
                text = "রক্ত দিয়ে জীবন বাঁচান",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Impact Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    icon = Icons.Default.Group,
                    count = "৫,০০০+",
                    label = "দাতা সংযুক্ত",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.VolunteerActivism,
                    count = "১২,৫০০+",
                    label = "ব্যাগ দান সম্পন্ন",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Interactive Dynamic Auth Card Widget
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Status Info based on Auth Step
                    when (authStep) {
                        AuthStep.EMAIL_INPUT -> {
                            if (isForgotPasswordMode) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { isForgotPasswordMode = false }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "পাসওয়ার্ড ভুলে গেছেন?",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "জিমেইলে পাসওয়ার্ড রিসেট কোড পাঠানো হবে",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            } else {
                                // Sliding visual tabs selection
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Login Tab Button
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (activeAuthTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent)
                                            .clickable { 
                                                activeAuthTab = 0 
                                                viewModel.setAuthError(null)
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = if (activeAuthTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "লগইন করুন",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (activeAuthTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Register Tab Button
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (activeAuthTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent)
                                            .clickable { 
                                                activeAuthTab = 1 
                                                viewModel.setAuthError(null)
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = if (activeAuthTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "রেজিস্ট্রেশন",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (activeAuthTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = if (activeAuthTab == 0) "আপনার জিমেইল ও পাসওয়ার্ড দিয়ে সরাসরি লগইন করুন" else "নতুন রক্তদাতা একাউন্ট তৈরি করতে জিমেইল ভেরিফাই করুন",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        AuthStep.OTP_VERIFICATION -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { viewModel.resetAuthFlow() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isPasswordResetFlow) "পাসওয়ার্ড রিসেট কোড" else "জিমেইল যাচাইকরণ কোড",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = authEmail,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        AuthStep.PASSWORD_SETUP -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { viewModel.setAuthStep(AuthStep.OTP_VERIFICATION) }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isPasswordResetFlow) "নতুন পাসওয়ার্ড সেট করুন" else "নতুন অ্যাকাউন্ট তৈরি করুন",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = if (isPasswordResetFlow) "আপনার শক্তিশালী নতুন পাসওয়ার্ড লিখুন" else "নিরাপদ জিমেইল পাসওয়ার্ড দিন",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        AuthStep.PASSWORD_LOGIN -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { viewModel.setAuthStep(AuthStep.EMAIL_INPUT) }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "অ্যাকাউন্টে লগইন করুন",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = authEmail,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Display errors beautifully with Neon Red
                    authErrorMsg?.let { msg ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0x22FF1744) // Transparent Neon Red Background
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Error info",
                                    tint = Color(0xFFFF1744), // Neon Red Icon
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    color = Color(0xFFFF1744), // Neon Red Text
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Show loader or the actual input forms
                    if (authIsLoading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 24.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(44.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "সার্ভার যাচাইকরণ চলছে...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        // RENDER FORMS DEPENDING ON THE ACTIVE STEP
                        when (authStep) {
                            AuthStep.EMAIL_INPUT -> {
                                if (isForgotPasswordMode) {
                                    // Password Reset Mode Email Input
                                    OutlinedTextField(
                                        value = emailInput,
                                        onValueChange = { 
                                            emailInput = it
                                            viewModel.setAuthError(null)
                                        },
                                        label = { Text("জিমেইল আইডি দিন") },
                                        placeholder = { Text("username@gmail.com") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("gmail_input_field"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            val trimmed = emailInput.trim()
                                            if (trimmed.isBlank() || !trimmed.contains("@")) {
                                                viewModel.setAuthError("অনুগ্রহ করে একটি সঠিক জিমেইল এড্রেস লিখুন।")
                                            } else {
                                                viewModel.checkAndSendVerificationCode(trimmed) { exists ->
                                                    if (!exists) {
                                                        viewModel.setAuthError("এই জিমেইল আইডিটি দিয়ে কোনো রক্তবন্ধু অ্যাকাউন্ট খোঁজে পাওয়া যায়নি।")
                                                    } else {
                                                        viewModel.setPasswordResetFlow(true)
                                                        viewModel.setAuthStep(AuthStep.OTP_VERIFICATION)
                                                    }
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("send_otp_button")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "রিসেন্ট কোড পাঠান",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                } else {
                                    // Normal login or registration modes
                                    if (activeAuthTab == 0) {
                                        // Login Form: direct email & password, bypassing OTP!
                                        OutlinedTextField(
                                            value = emailInput,
                                            onValueChange = { 
                                                emailInput = it
                                                viewModel.setAuthError(null)
                                            },
                                            label = { Text("জিমেইল আইডি") },
                                            placeholder = { Text("correct@gmail.com") },
                                            leadingIcon = {
                                                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                            },
                                            singleLine = true,
                                            shape = RoundedCornerShape(16.dp),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("gmail_input_field"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        OutlinedTextField(
                                            value = passwordInput,
                                            onValueChange = { 
                                                passwordInput = it
                                                viewModel.setAuthError(null)
                                            },
                                            label = { Text("পাসওয়ার্ড") },
                                            placeholder = { Text("আপনার পাসওয়ার্ড লিখুন") },
                                            leadingIcon = {
                                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                            },
                                            singleLine = true,
                                            shape = RoundedCornerShape(16.dp),
                                            visualTransformation = PasswordVisualTransformation(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("login_password_field"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                            )
                                        )

                                        // Clickable Forgot Password Link
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    isForgotPasswordMode = true
                                                    viewModel.setAuthError(null)
                                                    viewModel.setPasswordResetFlow(true)
                                                }
                                            ) {
                                                Text(
                                                    text = "পাসওয়ার্ড ভুলে গেছেন? (Forgot Password)",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Button(
                                            onClick = {
                                                val trimmedEmail = emailInput.trim()
                                                val trimmedPass = passwordInput.trim()
                                                if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
                                                    viewModel.setAuthError("অনুগ্রহ করে একটি সঠিক জিমেইল এড্রেস লিখুন।")
                                                } else if (trimmedPass.isEmpty()) {
                                                    viewModel.setAuthError("অনুগ্রহ করে পাসওয়ার্ড দিন।")
                                                } else {
                                                    viewModel.signInWithEmailPassword(
                                                        email = trimmedEmail,
                                                        password = trimmedPass,
                                                        onComplete = {
                                                            // Auto transition to main dashboard on success
                                                        },
                                                        onError = { err ->
                                                            // Handled in authErrorMsg
                                                        }
                                                    )
                                                }
                                            },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(52.dp)
                                                .testTag("complete_login_button")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "লগইন করুন",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    } else {
                                        // Registration Form: email only, setup with OTP verification!
                                        OutlinedTextField(
                                            value = emailInput,
                                            onValueChange = { 
                                                emailInput = it
                                                viewModel.setAuthError(null)
                                            },
                                            label = { Text("জিমেইল আইডি দিন") },
                                            placeholder = { Text("correct@gmail.com") },
                                            leadingIcon = {
                                                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                            },
                                            singleLine = true,
                                            shape = RoundedCornerShape(16.dp),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("gmail_input_field"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = {
                                                val trimmed = emailInput.trim()
                                                if (trimmed.isBlank() || !trimmed.contains("@")) {
                                                    viewModel.setAuthError("অনুগ্রহ করে একটি সঠিক জিমেইল এড্রেস লিখুন।")
                                                } else {
                                                    viewModel.checkAndSendVerificationCode(trimmed) { exists ->
                                                        if (exists) {
                                                            viewModel.setAuthError("এই জিমেইল আইডি দিয়ে ইতিমধ্যেই একটি অ্যাকাউন্ট খোলা রয়েছে। দয়া করে লগইন করুন।")
                                                            viewModel.resetAuthFlow()
                                                        } else {
                                                            viewModel.setPasswordResetFlow(false)
                                                            viewModel.setAuthStep(AuthStep.OTP_VERIFICATION)
                                                        }
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(52.dp)
                                                .testTag("send_otp_button")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "ভেরিফিকেশন কোড পাঠান",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            AuthStep.OTP_VERIFICATION -> {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFE8F5E9)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                        .shadow(2.dp, RoundedCornerShape(16.dp))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MarkEmailRead,
                                                contentDescription = "Email sent",
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "আপনার ইমেইলে কোড পাঠানো হয়েছে!",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "রক্তবন্ধু সিস্টেম সরাসরি $authEmail ঠিকানায় ৬ সংখ্যার অথেনটিকেশন কোডটি পাঠিয়েছে। অনুগ্রহ করে আপনার ইনবক্স বা স্প্যাম ফোল্ডার চেক করুন।",
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            color = Color.Black.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                // Interactive nicely formatted OTP Input
                                OutlinedTextField(
                                    value = otpInput,
                                    onValueChange = { 
                                        if (it.length <= 6) {
                                            otpInput = it
                                            viewModel.setAuthError(null)
                                        }
                                    },
                                    label = { Text("৬-ডিজিটের ভেরিফিকেশন কোড") },
                                    placeholder = { Text("১ ২ ৩ ৪ ৫ ৬") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("otp_input_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 16.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (viewModel.verifyOtp(otpInput.trim())) {
                                            if (isPasswordResetFlow) {
                                                viewModel.setAuthStep(AuthStep.PASSWORD_SETUP)
                                            } else {
                                                // Code matches! Now check if registered
                                                viewModel.checkUserRegistrationOnly(authEmail) { isRegistered ->
                                                    if (isRegistered) {
                                                        viewModel.setAuthStep(AuthStep.PASSWORD_LOGIN)
                                                     } else {
                                                        viewModel.setAuthStep(AuthStep.PASSWORD_SETUP)
                                                     }
                                                 }
                                             }
                                        } else {
                                            // Error handled inside verifyOtp
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("confirm_otp_button")
                                ) {
                                    Text(
                                        text = "কোড নিশ্চিত করুন",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            AuthStep.PASSWORD_SETUP -> {
                                if (!isPasswordResetFlow) {
                                    OutlinedTextField(
                                        value = nameInput,
                                        onValueChange = { 
                                            nameInput = it
                                            viewModel.setAuthError(null)
                                        },
                                        label = { Text("আপনার সম্পূর্ণ নাম লিখুন") },
                                        placeholder = { Text("যেমন: রাহাদ হোসাইন") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("register_name_field"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { 
                                        passwordInput = it
                                        viewModel.setAuthError(null)
                                    },
                                    label = { Text(if (isPasswordResetFlow) "৬+ অক্ষরের একটি নতুন পাসওয়ার্ড দিন" else "৬+ অক্ষরের একটি পাসওয়ার্ড দিন") },
                                    placeholder = { Text("পাসওয়ার্ড") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("register_password_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        val nameTrim = nameInput.trim()
                                        val passTrim = passwordInput.trim()
                                        if (!isPasswordResetFlow && nameTrim.length < 3) {
                                            viewModel.setAuthError("দয়া করে আপনার সঠিক সম্পূর্ণ নামটি লিখুন।")
                                        } else if (passTrim.length < 6) {
                                            viewModel.setAuthError("পাসওয়ার্ড কমপক্ষে ৬ সংখ্যার বা অক্ষরের হতে হবে।")
                                        } else {
                                            if (isPasswordResetFlow) {
                                                viewModel.resetUserPassword(
                                                    email = authEmail,
                                                    passwordInput = passTrim,
                                                    onComplete = {
                                                        // Complete reset
                                                    },
                                                    onError = { err ->
                                                        // Handled in VM error
                                                    }
                                                )
                                            } else {
                                                viewModel.signUpWithEmailPassword(
                                                    email = authEmail,
                                                    name = nameTrim,
                                                    password = passTrim,
                                                    onComplete = {
                                                        // Sign up complete! MainAppHost automatically transitions
                                                    },
                                                    onError = { err ->
                                                        // Handled in viewmodel error stream
                                                    }
                                                )
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("complete_register_button")
                                ) {
                                    Text(
                                        text = if (isPasswordResetFlow) "পাসওয়ার্ড সংশোধন করুন" else "নিবন্ধন সম্পন্ন করুন",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            AuthStep.PASSWORD_LOGIN -> {
                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { 
                                        passwordInput = it
                                        viewModel.setAuthError(null)
                                    },
                                    label = { Text("লগইন পাসওয়ার্ড") },
                                    placeholder = { Text("আপনার পাসওয়ার্ড দিন") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_password_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        val passTrim = passwordInput.trim()
                                        if (passTrim.isEmpty()) {
                                            viewModel.setAuthError("অনুগ্রহ করে আপনার অ্যাকাউন্ট পাসওয়ার্ড দিন।")
                                        } else {
                                            viewModel.signInWithEmailPassword(
                                                email = authEmail,
                                                password = passTrim,
                                                onComplete = {
                                                    // Auth complete! Transition happens automatically
                                                },
                                                onError = { err ->
                                                    // Error is populated in stream
                                                }
                                            )
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("complete_login_button")
                                ) {
                                    Text(
                                        text = "লগইন করুন",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Safety trust label
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "আমরা আপনার গোপনীয়তা শতভাগ রক্ষা করি",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
    }
}

// Stats display card
@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
         Column(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(14.dp),
             horizontalAlignment = Alignment.CenterHorizontally,
             verticalArrangement = Arrangement.Center
         ) {
             Icon(
                 imageVector = icon,
                 contentDescription = null,
                 tint = MaterialTheme.colorScheme.primary,
                 modifier = Modifier.size(22.dp)
             )
             Spacer(modifier = Modifier.height(4.dp))
             Text(
                 text = count,
                 fontSize = 16.sp,
                 fontWeight = FontWeight.Black,
                 color = MaterialTheme.colorScheme.primary
             )
             Text(
                 text = label,
                 fontSize = 10.sp,
                 fontWeight = FontWeight.Medium,
                 color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                 textAlign = TextAlign.Center,
                 lineHeight = 12.sp
             )
         }
    }
}

// Using standard Jetpack Compose animateContentSize
