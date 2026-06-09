package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Temporary values inside screen UI
    var emailInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    // Sync input fields when auth flow resets
    LaunchedEffect(authStep) {
        if (authStep == AuthStep.EMAIL_INPUT) {
            emailInput = ""
            otpInput = ""
            nameInput = ""
            passwordInput = ""
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                )
            )
            .verticalScroll(scrollState)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Pulse Blood Ring Graphic Accent
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(8.dp, RoundedCornerShape(45.dp), clip = false)
                    .clip(RoundedCornerShape(45.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF8A80),
                                MaterialTheme.colorScheme.primary
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
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
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
                            Text(
                                text = "প্রবেশ করুন অথবা নিবন্ধন করুন",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "ক্রিয়াশীল ডাটাবেজ সেশনের জন্য জিমেইল ব্যবহার করুন",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        AuthStep.OTP_VERIFICATION -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { viewModel.resetAuthFlow() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "জিমেইল যাচাইকরণ কোড",
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
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "নতুন অ্যাকাউন্ট তৈরি করুন",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "নিরাপদ জিমেইল পাসওয়ার্ড দিন",
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
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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

                    // Display errors beautifully
                    authErrorMsg?.let { msg ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
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
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
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
                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = { 
                                        emailInput = it
                                        viewModel.setAuthError(null)
                                    },
                                    label = { Text("জিমেইল আইডি দিন") },
                                    placeholder = { Text("উদাহরণ: correct@gmail.com") },
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
                                                viewModel.setAuthStep(AuthStep.OTP_VERIFICATION)
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

                            AuthStep.OTP_VERIFICATION -> {
                                // Real simulated Gmail inbox popup simulator
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFFFF9C4)
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
                                                contentDescription = "Simulated notification",
                                                tint = Color(0xFFF57F17),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "জিমেইল ইনবক্স সিমুলেটর",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFF57F17)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "রক্তবন্ধু থেকে আপনার ইনবক্সে কোড পাঠানো হয়েছে।",
                                            fontSize = 11.sp,
                                            color = Color.Black.copy(alpha = 0.7f)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "ভেরিফিকেশন কোড:  $generatedOtp",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFFE65100)
                                            )
                                            
                                            // Auto-fill trigger button
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(0xFFFFCC80)
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .clickable { triggerMagicAutoFill() }
                                            ) {
                                                Text(
                                                    text = "অটো-ফিল কোড",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFE65100),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
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
                                        if (otpInput.trim() == generatedOtp) {
                                            // Code matches! Now check if registered
                                            viewModel.checkAndSendVerificationCode(authEmail) { isRegistered ->
                                                if (isRegistered) {
                                                    viewModel.setAuthStep(AuthStep.PASSWORD_LOGIN)
                                                } else {
                                                    viewModel.setAuthStep(AuthStep.PASSWORD_SETUP)
                                                }
                                            }
                                        } else {
                                            viewModel.setAuthError("ভুল ভেরিফিকেশন কোড! দয়া করে সঠিক কোডটি দিন।")
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

                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { 
                                        passwordInput = it
                                        viewModel.setAuthError(null)
                                    },
                                    label = { Text("৬+ অক্ষরের একটি পাসওয়ার্ড দিন") },
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
                                        if (nameTrim.length < 3) {
                                            viewModel.setAuthError("দয়া করে আপনার সঠিক সম্পূর্ণ নামটি লিখুন।")
                                        } else if (passTrim.length < 6) {
                                            viewModel.setAuthError("পাসওয়ার্ড কমপক্ষে ৬ সংখ্যার বা অক্ষরের হতে হবে।")
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
                                        text = "নিবন্ধন সম্পন্ন করুন (Firestore)",
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
                                        text = "লগইন করুন (Firestore)",
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
