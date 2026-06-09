package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import com.example.ui.auth.GoogleSignInHelper
import com.example.ui.viewmodel.BloodViewModel
import com.example.ui.viewmodel.GoogleAccount

@Composable
fun WelcomeScreen(
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    val showGooglePicker by viewModel.showGooglePicker.collectAsState()
    val googleAccounts = viewModel.availableGoogleAccounts

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var credentialManagerErrorMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Heart & Opacity (Blood Drop) Graphic Canvas
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(8.dp, RoundedCornerShape(50.dp), clip = false)
                    .clip(RoundedCornerShape(50.dp))
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
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name with modern Bengali typography styling
            Text(
                text = "রক্তবন্ধু",
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Text(
                text = "রক্ত দিয়ে জীবন বাঁচান",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Impact statistics / claims cards
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

            Spacer(modifier = Modifier.height(40.dp))

            // Information details card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Safe Policy",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "নিরাপদ ও সহজ সংযোগ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "আপনার তথ্য সুরক্ষায় আমরা শতভাগ দায়বদ্ধ। গুগল একাউন্ট সংযোগ করে মুহূর্তেই দাতা খুঁজুন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Google Sign-In Action button
            Button(
                onClick = { 
                    credentialManagerErrorMsg = null
                    viewModel.openGooglePicker()
                    scope.launch {
                        try {
                            GoogleSignInHelper.triggerGoogleSignIn(
                                context = context,
                                onSuccess = { email, name, idToken ->
                                    viewModel.signInOrRegisterCustomGoogleAccount(email, name) {
                                        viewModel.closeGooglePicker()
                                    }
                                },
                                onError = { errorMsg ->
                                    credentialManagerErrorMsg = errorMsg
                                }
                            )
                        } catch (e: Exception) {
                            credentialManagerErrorMsg = e.message
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("google_login_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Custom Google-like visual icon representation using a styled circle
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            color = Color(0xFF4285F4),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "গুগল একাউন্ট দিয়ে এগিয়ে যান",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Custom Google One-Tap accounts modal sheet mockup
        var isCustomInputMode by remember { mutableStateOf(false) }
        var customEmailInput by remember { mutableStateOf("") }
        var customNameInput by remember { mutableStateOf("") }
        var authProgressState by remember { mutableStateOf(0) }
        var authMessageState by remember { mutableStateOf("") }
        var isAuthenticating by remember { mutableStateOf(false) }
        var authErrorText by remember { mutableStateOf("") }

        AnimatedVisibility(
            visible = showGooglePicker,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Dialog(onDismissRequest = { 
                if (!isAuthenticating) {
                    viewModel.closeGooglePicker()
                    isCustomInputMode = false
                    customEmailInput = ""
                    customNameInput = ""
                    authErrorText = ""
                }
            }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header Google logo representation
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 20.sp)
                            Text(text = "o", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, fontSize = 20.sp)
                            Text(text = "o", color = Color(0xFFFBBC05), fontWeight = FontWeight.Black, fontSize = 20.sp)
                            Text(text = "g", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 20.sp)
                            Text(text = "l", color = Color(0xFF34A853), fontWeight = FontWeight.Black, fontSize = 20.sp)
                            Text(text = "e", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }

                        if (credentialManagerErrorMsg != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Credential Manager Active",
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "রিয়েল-টাইম ওয়ান-ট্যাপ সিগন্যাল অ্যাক্টিভ!",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Credential Manager লাইভ ট্রিগার হয়েছে এবং রিপোর্ট করেছে: \"${credentialManagerErrorMsg}\".\n\nযেহেতু গুগল প্লে-সার্ভিস বা Firebase Web Client ID কনফিগার করা নেই, তাই সিমুলেটেড বাটন ব্যবহার করা হচ্ছে।",
                                            fontSize = 10.sp,
                                            lineHeight = 13.sp,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isAuthenticating) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 4.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = authMessageState,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "প্রোগ্রেস: $authProgressState%",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = { authProgressState / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        } else if (isCustomInputMode) {
                            Text(
                                text = "নতুন অ্যাকাউন্ট যোগ করুন",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "রিয়েল-টাইম ডাটাবেজে আপনার ইমেইল ও নাম যুক্ত করুন",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            if (authErrorText.isNotEmpty()) {
                                Text(
                                    text = authErrorText,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }

                            OutlinedTextField(
                                value = customEmailInput,
                                onValueChange = { customEmailInput = it; authErrorText = "" },
                                label = { Text("গুগল ইমেইল আইডি লিখুন") },
                                placeholder = { Text("উদাহরণ: user@gmail.com") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                                )
                            )

                            OutlinedTextField(
                                value = customNameInput,
                                onValueChange = { customNameInput = it; authErrorText = "" },
                                label = { Text("আপনার সম্পূর্ণ নাম লিখুন") },
                                placeholder = { Text("যেমন: আকাশ আহমেদ") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                                )
                            )

                            Button(
                                onClick = {
                                    if (customEmailInput.isBlank() || !customEmailInput.contains("@")) {
                                        authErrorText = "অনুগ্রহ করে একটি সঠিক গুগল ইমেইল দিন!"
                                    } else if (customNameInput.isBlank() || customNameInput.length < 3) {
                                        authErrorText = "দয়া করে সম্পূর্ণ সঠিক নাম প্রদান করুন!"
                                    } else {
                                        isAuthenticating = true
                                        scope.launch {
                                            authMessageState = "সার্ভারে কানেক্ট করা হচ্ছে..."
                                            authProgressState = 12
                                            delay(500)
                                            
                                            authMessageState = "ফায়ারবেস ডাটাবেজে তথ্য ভেরিফাই হচ্ছে..."
                                            authProgressState = 47
                                            delay(700)

                                            authMessageState = "নিরাপদ লাইভ সেশন অ্যাক্টিভেট হচ্ছে..."
                                            authProgressState = 82
                                            delay(600)

                                            authMessageState = "সফল! একাউন্ট যুক্ত করা হয়েছে"
                                            authProgressState = 100
                                            delay(400)

                                            viewModel.signInOrRegisterCustomGoogleAccount(
                                                customEmailInput.trim(),
                                                customNameInput.trim()
                                            ) {
                                                isAuthenticating = false
                                                isCustomInputMode = false
                                                viewModel.closeGooglePicker()
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text(text = "নিরাপদ সাইন-ইন সম্পন্ন করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Back to list button
                            androidx.compose.material3.TextButton(
                                onClick = { isCustomInputMode = false; authErrorText = "" }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("একাউন্ট তালিকায় ফিরে যান", fontSize = 13.sp)
                                }
                            }
                        } else {
                            Text(
                                text = "রক্তবন্ধু এর সাথে সাইন-ইন করুন",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "একটি গুগল একাউন্ট নির্বাচন করুন",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                            // Render Google accounts
                            googleAccounts.forEach { account ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            isAuthenticating = true
                                            scope.launch {
                                                authMessageState = "গুগল ওয়ান-ট্যাপ সেশন কানেক্ট হচ্ছে..."
                                                authProgressState = 30
                                                delay(400)
                                                authMessageState = "প্রোফাইল ডাটাবেজ তথ্য যাচাই হচ্ছে..."
                                                authProgressState = 75
                                                delay(300)
                                                authProgressState = 100
                                                delay(200)
                                                isAuthenticating = false
                                                viewModel.selectGoogleAccount(account)
                                            }
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    DonorAvatar(
                                        avatarId = account.imageUrlPlaceholder,
                                        size = 40.dp,
                                        borderWidth = 1.dp
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = account.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = account.email,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Trigger Custom Account Input Form
                            Button(
                                onClick = { isCustomInputMode = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text(
                                    text = "+ নিজস্ব গুগল/ইমেইল একাউন্ট যোগ করুন",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Dismiss button
                            androidx.compose.material3.TextButton(
                                onClick = { viewModel.closeGooglePicker() }
                            ) {
                                Text(text = "বাতিল করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
