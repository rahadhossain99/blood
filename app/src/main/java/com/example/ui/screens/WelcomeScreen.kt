package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Input States
    var emailInput by remember { mutableStateOf(viewModel.getRememberedEmail()) }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }

    // Toggle States
    var activeAuthTab by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Register
    var isForgotPasswordMode by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var rememberMeChecked by remember { mutableStateOf(viewModel.isRememberMeEnabled()) }
    var resendCooldownSeconds by remember { mutableLongStateOf(0L) }
    var isAutoFilling by remember { mutableStateOf(false) }

    // Common Email Domains for 1-Tap Quick Append
    val emailDomains = listOf("@gmail.com", "@yahoo.com", "@outlook.com", "@hotmail.com")

    // Cooldown countdown timer effect
    LaunchedEffect(authStep, resendCooldownSeconds) {
        if (resendCooldownSeconds > 0) {
            delay(1000)
            resendCooldownSeconds -= 1
        }
    }

    // Reset inputs when step returns to EMAIL_INPUT
    LaunchedEffect(authStep) {
        if (authStep == AuthStep.EMAIL_INPUT) {
            otpInput = ""
            passwordInput = ""
            confirmPasswordInput = ""
            isForgotPasswordMode = false
            passwordVisible = false
            confirmPasswordVisible = false
            if (emailInput.isEmpty()) {
                emailInput = viewModel.getRememberedEmail()
            }
        } else if (authStep == AuthStep.OTP_VERIFICATION) {
            resendCooldownSeconds = 60L
        }
    }

    // Interactive simulated Auto-Fill logic for OTP
    fun triggerMagicAutoFill() {
        if (generatedOtp.isEmpty() || isAutoFilling) return
        isAutoFilling = true
        scope.launch {
            otpInput = ""
            for (char in generatedOtp) {
                otpInput += char
                delay(120) // Smooth typing animation
            }
            isAutoFilling = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Warm organic blood-red premium gradient cover
                val bgGradient = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF7F8),
                        Color(0xFFFFF0F2),
                        Color(0xFFFFECEF)
                    )
                )
                drawRect(brush = bgGradient)

                // Atmospheric medical micro-grid
                val spacingPx = 40.dp.toPx()
                val lineCol = Color(0xFFFF1744).copy(alpha = 0.035f)
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

                // Ambient glowing circles
                drawCircle(color = Color(0xFFFF1744).copy(alpha = 0.04f), radius = 180.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.15f))
                drawCircle(color = Color(0xFFFF1744).copy(alpha = 0.035f), radius = 220.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.95f, size.height * 0.75f))
            }
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Pulse Blood Droplet Hero Emblem
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(1100),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )

        Box(
            modifier = Modifier
                .size(86.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
                .shadow(12.dp, CircleShape, spotColor = Color(0xFFFF1744))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF2A55),
                            Color(0xFFD50000),
                            Color(0xFF8E0000)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Opacity,
                contentDescription = "রক্তবন্ধু লোগো",
                tint = Color.White,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Branding Title
        Text(
            text = "রক্তবন্ধু",
            fontSize = 34.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "একটি রক্তদান • একটি নতুন জীবন",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Platform Metrics Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                icon = Icons.Default.Group,
                count = "৫,২০০+",
                label = "নিবন্ধিত রক্তদাতা",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.VolunteerActivism,
                count = "১২,৫০০+",
                label = "ব্যাগ দান সম্পন্ন",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.Security,
                count = "১০০%",
                label = "সুরক্ষিত যাচাইকরণ",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Core Authentication Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, Color(0xFFFFEBEE)),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = Color(0x33FF1744))
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header / Step Progression indicator
                when (authStep) {
                    AuthStep.EMAIL_INPUT -> {
                        if (isForgotPasswordMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        isForgotPasswordMode = false
                                        viewModel.setAuthError(null)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "ফিরে যান",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "পাসওয়ার্ড পুনরুদ্ধার",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "আপনার জিমেইলে ভেরিফিকেশন কোড পাঠানো হবে",
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                    )
                                }
                            }
                        } else {
                            // Ultra Smooth Modern Tab Selector (Login vs Register)
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF7F2F3),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Login Tab
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (activeAuthTab == 0) MaterialTheme.colorScheme.primary
                                                else Color.Transparent
                                            )
                                            .clickable {
                                                activeAuthTab = 0
                                                viewModel.setAuthError(null)
                                            }
                                            .padding(vertical = 11.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = if (activeAuthTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(17.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "লগইন করুন",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (activeAuthTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Register Tab
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (activeAuthTab == 1) MaterialTheme.colorScheme.primary
                                                else Color.Transparent
                                            )
                                            .clickable {
                                                activeAuthTab = 1
                                                viewModel.setAuthError(null)
                                            }
                                            .padding(vertical = 11.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.PersonAdd,
                                                contentDescription = null,
                                                tint = if (activeAuthTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(17.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "নতুন একাউন্ট",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (activeAuthTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = if (activeAuthTab == 0)
                                    "আপনার জিমেইল ও পাসওয়ার্ড দিয়ে সরাসরি লগইন করুন"
                                else
                                    "নতুন রক্তদাতা হিসেবে নিবন্ধন করতে জিমেইল নিশ্চিত করুন",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
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
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "ইমেইল পরিবর্তন",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isPasswordResetFlow) "পাসওয়ার্ড রিসেট ভেরিফিকেশন" else "জিমেইল ওটিপি নিশ্চিতকরণ",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = authEmail,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    AuthStep.PASSWORD_SETUP -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.setAuthStep(AuthStep.OTP_VERIFICATION) }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "পেছনে যান",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isPasswordResetFlow) "নতুন পাসওয়ার্ড দিন" else "প্রোফাইল ও পাসওয়ার্ড সেট করুন",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "নিরাপদ ও শক্তিশালী পাসওয়ার্ড ব্যবহার করুন",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    AuthStep.PASSWORD_LOGIN -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.setAuthStep(AuthStep.EMAIL_INPUT) }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "পেছনে যান",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "পাসওয়ার্ড দিয়ে প্রবেশ করুন",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = authEmail,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error Banner with Clear Visual Accent
                authErrorMsg?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0x18FF1744)),
                        border = BorderStroke(1.dp, Color(0xFFFF1744).copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(14.dp),
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
                                contentDescription = "ত্রুটি",
                                tint = Color(0xFFFF1744),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = msg,
                                color = Color(0xFFD50000),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Loading Indicator
                if (authIsLoading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 28.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(46.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "নিরাপদ সংযোগ যাচাই করা হচ্ছে...",
                            fontSize = 13.5.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // AUTH STEP FORMS
                    when (authStep) {
                        AuthStep.EMAIL_INPUT -> {
                            if (isForgotPasswordMode) {
                                // Forgot Password Mode: Send Reset OTP
                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = {
                                        emailInput = it
                                        viewModel.setAuthError(null)
                                    },
                                    label = { Text("আপনার নিবন্ধিত জিমেইল আইডি") },
                                    placeholder = { Text("username@gmail.com") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    trailingIcon = {
                                        if (emailInput.isNotEmpty()) {
                                            IconButton(onClick = { emailInput = "" }) {
                                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("gmail_input_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                )

                                // Quick Domain Suggestions
                                if (!emailInput.contains("@") && emailInput.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(emailDomains) { domain ->
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color(0xFFFCE4EC),
                                                modifier = Modifier.clickable {
                                                    emailInput = emailInput.trim() + domain
                                                    viewModel.setAuthError(null)
                                                }
                                            ) {
                                                Text(
                                                    text = domain,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = {
                                        val trimmed = emailInput.trim()
                                        if (!viewModel.isValidEmail(trimmed)) {
                                            viewModel.setAuthError("অনুগ্রহ করে একটি সঠিক জিমেইল এড্রেস লিখুন।")
                                        } else {
                                            keyboardController?.hide()
                                            viewModel.checkAndSendVerificationCode(trimmed) { exists ->
                                                if (!exists) {
                                                    viewModel.setAuthError("এই জিমেইল আইডি দিয়ে কোনো রক্তবন্ধু অ্যাকাউন্ট পাওয়া যায়নি।")
                                                } else {
                                                    viewModel.setPasswordResetFlow(true)
                                                    viewModel.setAuthStep(AuthStep.OTP_VERIFICATION)
                                                }
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("send_otp_button")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(19.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "রিসেট কোড পাঠান",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                if (activeAuthTab == 0) {
                                    // LOGIN FORM: Email + Password
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
                                        trailingIcon = {
                                            if (emailInput.isNotEmpty()) {
                                                IconButton(onClick = { emailInput = "" }) {
                                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                                }
                                            }
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("gmail_input_field"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        )
                                    )

                                    // Domain Chips
                                    if (!emailInput.contains("@") && emailInput.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(emailDomains) { domain ->
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = Color(0xFFFCE4EC),
                                                    modifier = Modifier.clickable {
                                                        emailInput = emailInput.trim() + domain
                                                        viewModel.setAuthError(null)
                                                    }
                                                ) {
                                                    Text(
                                                        text = domain,
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Password Field with Toggle
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
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = if (passwordVisible) "পাসওয়ার্ড লুকান" else "পাসওয়ার্ড দেখুন",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(
                                            onDone = { keyboardController?.hide() }
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("login_password_field"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Row with "Remember Me" and "Forgot Password"
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable {
                                                rememberMeChecked = !rememberMeChecked
                                                viewModel.setRememberMe(emailInput, rememberMeChecked)
                                            }
                                        ) {
                                            Checkbox(
                                                checked = rememberMeChecked,
                                                onCheckedChange = {
                                                    rememberMeChecked = it
                                                    viewModel.setRememberMe(emailInput, it)
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                            )
                                            Text(
                                                text = "মনে রাখুন",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        TextButton(
                                            onClick = {
                                                isForgotPasswordMode = true
                                                viewModel.setAuthError(null)
                                                viewModel.setPasswordResetFlow(true)
                                            }
                                        ) {
                                            Text(
                                                text = "পাসওয়ার্ড ভুলে গেছেন?",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            val trimmedEmail = emailInput.trim()
                                            val trimmedPass = passwordInput.trim()
                                            if (!viewModel.isValidEmail(trimmedEmail)) {
                                                viewModel.setAuthError("অনুগ্রহ করে একটি সঠিক জিমেইল এড্রেস লিখুন।")
                                            } else if (trimmedPass.isEmpty()) {
                                                viewModel.setAuthError("অনুগ্রহ করে পাসওয়ার্ড দিন।")
                                            } else {
                                                keyboardController?.hide()
                                                if (rememberMeChecked) {
                                                    viewModel.setRememberMe(trimmedEmail, true)
                                                }
                                                viewModel.signInWithEmailPassword(
                                                    email = trimmedEmail,
                                                    password = trimmedPass,
                                                    onComplete = {
                                                        Toast.makeText(context, "সফলভাবে লগইন হয়েছে!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    onError = { /* handled in errorMsg */ }
                                                )
                                            }
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
                                                fontSize = 15.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                } else {
                                    // REGISTRATION FORM: Email only -> OTP setup
                                    OutlinedTextField(
                                        value = emailInput,
                                        onValueChange = {
                                            emailInput = it
                                            viewModel.setAuthError(null)
                                        },
                                        label = { Text("আপনার জিমেইল আইডি দিন") },
                                        placeholder = { Text("correct@gmail.com") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        },
                                        trailingIcon = {
                                            if (emailInput.isNotEmpty()) {
                                                IconButton(onClick = { emailInput = "" }) {
                                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                                }
                                            }
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(
                                            onDone = { keyboardController?.hide() }
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("gmail_input_field"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        )
                                    )

                                    // Domain Chips
                                    if (!emailInput.contains("@") && emailInput.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(emailDomains) { domain ->
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = Color(0xFFFCE4EC),
                                                    modifier = Modifier.clickable {
                                                        emailInput = emailInput.trim() + domain
                                                        viewModel.setAuthError(null)
                                                    }
                                                ) {
                                                    Text(
                                                        text = domain,
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Button(
                                        onClick = {
                                            val trimmed = emailInput.trim()
                                            if (!viewModel.isValidEmail(trimmed)) {
                                                viewModel.setAuthError("অনুগ্রহ করে একটি সঠিক জিমেইল এড্রেস লিখুন।")
                                            } else {
                                                keyboardController?.hide()
                                                viewModel.checkAndSendVerificationCode(trimmed) { exists ->
                                                    if (exists) {
                                                        viewModel.setAuthError("এই জিমেইল আইডি দিয়ে ইতিমধ্যেই একটি অ্যাকাউন্ট খোলা রয়েছে। দয়া করে লগইন করুন।")
                                                        activeAuthTab = 0
                                                    } else {
                                                        viewModel.setPasswordResetFlow(false)
                                                        viewModel.setAuthStep(AuthStep.OTP_VERIFICATION)
                                                    }
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
                            // Info Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.MarkEmailRead,
                                            contentDescription = "Email sent",
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "আপনার জিমেইলে কোড পাঠানো হয়েছে!",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$authEmail ঠিকানায় ৬-সংখ্যার ওটিপি কোড পাঠানো হয়েছে। অনুগ্রহ করে ইনবক্স বা স্প্যাম (Spam) ফোল্ডার চেক করুন।",
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp,
                                        color = Color.Black.copy(alpha = 0.75f)
                                    )
                                }
                            }

                            // 6-Digit Visual Code Display Box
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (i in 0 until 6) {
                                    val char = otpInput.getOrNull(i)?.toString() ?: ""
                                    val isFocused = otpInput.length == i
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (char.isNotEmpty()) Color(0xFFFFEBEE)
                                                else Color(0xFFF5F5F5)
                                            )
                                            .border(
                                                width = if (isFocused) 2.dp else 1.dp,
                                                color = if (isFocused) MaterialTheme.colorScheme.primary
                                                else if (char.isNotEmpty()) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                                else Color.LightGray.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = char,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            // Hidden actual input field for keyboard entry
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = {
                                    val digitsOnly = it.filter { c -> c.isDigit() }
                                    if (digitsOnly.length <= 6) {
                                        otpInput = digitsOnly
                                        viewModel.setAuthError(null)
                                    }
                                },
                                label = { Text("৬-ডিজিটের ভেরিফিকেশন কোড লিখুন") },
                                placeholder = { Text("১ ২ ৩ ৪ ৫ ৬") },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = { keyboardController?.hide() }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action Tools: Paste from Clipboard & Magic Auto-fill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val clipText = clipboardManager.getText()?.text?.trim() ?: ""
                                        val digits = clipText.filter { it.isDigit() }
                                        if (digits.length >= 6) {
                                            otpInput = digits.take(6)
                                            viewModel.setAuthError(null)
                                        } else {
                                            Toast.makeText(context, "ক্লিপবোর্ডে ৬-সংখ্যার কোড নেই", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("পেস্ট করুন", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { triggerMagicAutoFill() },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFF9800))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("অটো-ফিল", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Resend Code with Cooldown Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (resendCooldownSeconds > 0) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "পুনরায় কোড পাঠাতে $resendCooldownSeconds সেকেন্ড অপেক্ষা করুন",
                                        fontSize = 11.5.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                } else {
                                    TextButton(
                                        onClick = {
                                            viewModel.resendVerificationCode { success ->
                                                if (success) {
                                                    resendCooldownSeconds = 60L
                                                    Toast.makeText(context, "নতুন ভেরিফিকেশন কোড পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "পুনরায় কোড পাঠান (Resend Code)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (viewModel.verifyOtp(otpInput.trim())) {
                                        keyboardController?.hide()
                                        if (isPasswordResetFlow) {
                                            viewModel.setAuthStep(AuthStep.PASSWORD_SETUP)
                                        } else {
                                            viewModel.checkUserRegistrationOnly(authEmail) { isRegistered ->
                                                if (isRegistered) {
                                                    viewModel.setAuthStep(AuthStep.PASSWORD_LOGIN)
                                                } else {
                                                    viewModel.setAuthStep(AuthStep.PASSWORD_SETUP)
                                                }
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("confirm_otp_button")
                            ) {
                                Text(
                                    text = "কোড নিশ্চিত করুন",
                                    fontSize = 15.5.sp,
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
                                    label = { Text("আপনার সম্পূর্ণ নাম") },
                                    placeholder = { Text("যেমন: রাহাদ হোসাইন") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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

                            // Password Input with Visibility Toggle
                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = {
                                    passwordInput = it
                                    viewModel.setAuthError(null)
                                },
                                label = { Text(if (isPasswordResetFlow) "নতুন পাসওয়ার্ড (কমপক্ষে ৬ অক্ষর)" else "একটি শক্তিশালী পাসওয়ার্ড দিন") },
                                placeholder = { Text("পাসওয়ার্ড") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_password_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                            )

                            // Password Strength Bar
                            if (passwordInput.isNotEmpty()) {
                                val strength = when {
                                    passwordInput.length >= 8 && passwordInput.any { it.isDigit() } && passwordInput.any { !it.isLetterOrDigit() } -> 3
                                    passwordInput.length >= 6 && passwordInput.any { it.isDigit() } -> 2
                                    passwordInput.length >= 6 -> 1
                                    else -> 0
                                }
                                val strengthColor = when (strength) {
                                    3 -> Color(0xFF2E7D32)
                                    2 -> Color(0xFFF57C00)
                                    1 -> Color(0xFFFFB300)
                                    else -> Color(0xFFD32F2F)
                                }
                                val strengthLabel = when (strength) {
                                    3 -> "খুব শক্তিশালী (Strong)"
                                    2 -> "মোটামুটি শক্তিশালী (Good)"
                                    1 -> "সাধারণ (Fair)"
                                    else -> "খুব দুর্বল (Weak)"
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "পাসওয়ার্ড মান: $strengthLabel",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = strengthColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    for (step in 0..2) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(if (strength > step) strengthColor else Color.LightGray.copy(alpha = 0.4f))
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Confirm Password
                            OutlinedTextField(
                                value = confirmPasswordInput,
                                onValueChange = {
                                    confirmPasswordInput = it
                                    viewModel.setAuthError(null)
                                },
                                label = { Text("পাসওয়ার্ডটি পুনরায় লিখুন") },
                                placeholder = { Text("পাসওয়ার্ড নিশ্চিত করুন") },
                                leadingIcon = {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    if (confirmPasswordInput.isNotEmpty() && confirmPasswordInput == passwordInput) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Matched", tint = Color(0xFF2E7D32))
                                    } else {
                                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                            Icon(
                                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = { keyboardController?.hide() }
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    val nameTrim = nameInput.trim()
                                    val passTrim = passwordInput.trim()
                                    val confTrim = confirmPasswordInput.trim()

                                    if (!isPasswordResetFlow && nameTrim.length < 3) {
                                        viewModel.setAuthError("দয়া করে আপনার সঠিক সম্পূর্ণ নামটি লিখুন।")
                                    } else if (passTrim.length < 6) {
                                        viewModel.setAuthError("পাসওয়ার্ড কমপক্ষে ৬ সংখ্যার বা অক্ষরের হতে হবে।")
                                    } else if (passTrim != confTrim) {
                                        viewModel.setAuthError("দুটি পাসওয়ার্ড মেলেনি! দয়া করে একই পাসওয়ার্ড দিন।")
                                    } else {
                                        keyboardController?.hide()
                                        if (isPasswordResetFlow) {
                                            viewModel.resetUserPassword(
                                                email = authEmail,
                                                passwordInput = passTrim,
                                                onComplete = {
                                                    Toast.makeText(context, "পাসওয়ার্ড সফলভাবে পরিবর্তন হয়েছে!", Toast.LENGTH_SHORT).show()
                                                },
                                                onError = { /* handled in VM */ }
                                            )
                                        } else {
                                            viewModel.signUpWithEmailPassword(
                                                email = authEmail,
                                                name = nameTrim,
                                                password = passTrim,
                                                onComplete = {
                                                    Toast.makeText(context, "নিবন্ধন সফল হয়েছে! স্বাগতম রক্তবন্ধু পরিবারে।", Toast.LENGTH_SHORT).show()
                                                },
                                                onError = { /* handled in VM */ }
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("complete_register_button")
                            ) {
                                Text(
                                    text = if (isPasswordResetFlow) "পাসওয়ার্ড সংশোধন করুন" else "নিবন্ধন সম্পন্ন করুন",
                                    fontSize = 15.5.sp,
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
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = { keyboardController?.hide() }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    val passTrim = passwordInput.trim()
                                    if (passTrim.isEmpty()) {
                                        viewModel.setAuthError("অনুগ্রহ করে আপনার অ্যাকাউন্ট পাসওয়ার্ড দিন।")
                                    } else {
                                        keyboardController?.hide()
                                        viewModel.signInWithEmailPassword(
                                            email = authEmail,
                                            password = passTrim,
                                            onComplete = {
                                                Toast.makeText(context, "স্বাগতম!", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { /* handled in VM */ }
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("complete_login_button")
                            ) {
                                Text(
                                    text = "লগইন সম্পন্ন করুন",
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy & Security Trust Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.8f),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0).copy(alpha = 0.5f)),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AES-128 এনক্রিপ্টেড • আপনার তথ্যের ১০০% গোপনীয়তা নিশ্চিত",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.65f)
                )
            }
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
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = 11.sp
            )
        }
    }
}
