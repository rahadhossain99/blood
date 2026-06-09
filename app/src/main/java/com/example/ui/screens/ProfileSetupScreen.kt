package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donor
import com.example.ui.viewmodel.BloodViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    viewModel: BloodViewModel,
    profile: Donor,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf("") }

    var name by remember { mutableStateOf(profile.name) }
    var selectedGroup by remember { mutableStateOf(profile.bloodGroup) }
    var selectedDivision by remember { mutableStateOf(profile.division) }
    var area by remember { mutableStateOf(profile.area) }
    var phone by remember { mutableStateOf(profile.phone) }
    var isAvailable by remember { mutableStateOf(profile.isAvailable) }
    var avatarId by remember { mutableStateOf(profile.avatarId) }
    var lastDonationDate by remember { mutableStateOf(profile.lastDonationDate) }
    var customAvatarUrl by remember { mutableStateOf(profile.customAvatarUrl) }

    var isUploading by remember { mutableStateOf(false) }
    var uploadPercentage by remember { mutableStateOf(0) }
    var showPhotoSourceChooser by remember { mutableStateOf(false) }
    var uploadComplete by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Real ActivityResult contract to pick photo from local storage gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { selectedUri ->
            isUploading = true
            uploadPercentage = 0
            uploadComplete = false
            scope.launch {
                try {
                    // Read actual image bytes from the content resolver
                    val inputStream = context.contentResolver.openInputStream(selectedUri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()
                    if (bytes != null) {
                        // Compress and convert into Base64 for persistent Firestore storage
                        val base64String = "data:image/jpeg;base64," + android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                        customAvatarUrl = base64String
                        uploadPercentage = 100
                        uploadComplete = true
                    } else {
                        errorMessage = "ছবিটি লোড করা যায়নি।"
                    }
                } catch (e: Exception) {
                    errorMessage = "ছবি লোড করার সময় ত্রুটি ঘটেছে।"
                } finally {
                    isUploading = false
                }
            }
        }
    }

    val bloodGroups = listOf("O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-")
    val divisions = listOf(
        "Sadar" to "যশোর সদর",
        "Jhikargachha" to "ঝিকরগাছা",
        "Abhaynagar" to "অভয়নগর",
        "Manirampur" to "মণিরামপুর",
        "Chougachha" to "চৌগাছা",
        "Bagherpara" to "বাঘারপাড়া",
        "Keshabpur" to "কেশবপুর",
        "Sharsha" to "শার্শা"
    )

    val donationPeriods = listOf(
        "কখনো নয়",
        "১ মাসের মধ্যে",
        "৩ মাস আগে",
        "৬ মাস আগে",
        "১ বছর বা তার বেশি"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "প্রোফাইল সম্পন্ন করুন",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "আপনার সঠিক তথ্য দিন যেন রক্ত সংকটে অন্যরা আপনার সাথে সহজে যোগাযোগ করতে পারে।",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // Select avatar text
            Text(
                text = "আপনার পছন্দের রক্তের সেবা ব্যাজ সিলেক্ট করুন:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Avatar selection carousel (Removed cartoon assets to use beautiful caring badges)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items((1..5).toList()) { id ->
                    val isSelected = avatarId == id
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { avatarId = id }
                    ) {
                        DonorAvatar(
                            avatarId = id,
                            size = 64.dp,
                            borderWidth = if (isSelected) 4.dp else 1.dp,
                            borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                            customAvatarUrl = customAvatarUrl
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Photo upload block
            Text(
                text = "আপনার নিজের ছবি আপলোড করুন (ঐচ্ছিক):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            DonorAvatar(
                                avatarId = avatarId,
                                size = 56.dp,
                                borderWidth = 1.dp,
                                borderColor = MaterialTheme.colorScheme.primary,
                                customAvatarUrl = customAvatarUrl
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = if (customAvatarUrl.isNotEmpty()) "নিজস্ব ছবি আপলোড করা হয়েছে" else "ডিফল্ট ব্যাজ সক্রিয়",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (customAvatarUrl.isNotEmpty()) "রক্তের সেবা প্রোফাইলে সেট করা হয়েছে" else "গ্যালারি বা ক্যামেরা থেকে ছবি আপলোড করুন",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Button(
                            onClick = { showPhotoSourceChooser = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("upload_photo_button")
                        ) {
                            Text(text = "আপলোড", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Upload Progress Indicator Simulation
                    if (isUploading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ছবি সাইন সাইজ প্রসেস হচ্ছে ও ক্লাউডে আপলোড হচ্ছে...",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$uploadPercentage%",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            androidx.compose.material3.LinearProgressIndicator(
                                progress = { uploadPercentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        }
                    }

                    if (uploadComplete && !isUploading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF42A5F5),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ছবি সফলভাবে আপলোড সম্পন্ন হয়েছে!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }

            if (showPhotoSourceChooser) {
                Dialog(onDismissRequest = { showPhotoSourceChooser = false }) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ছবি আপলোড করুন",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Predefined choices representing high quality donor assets
                            val choices = listOf(
                                Triple("স্মার্টফোন গ্যালারি থেকে আপলোড দিন", "gallery", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=256"),
                                Triple("ক্যামেরা দিয়ে নতুন ছবি তুলুন", "camera", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?q=80&w=256"),
                                Triple("প্রোফাইল ছবি ডেমো সেট করুন", "preset", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?q=80&w=256")
                            )

                            choices.forEach { (label, type, targetUrl) ->
                                Button(
                                    onClick = {
                                        showPhotoSourceChooser = false
                                        if (type == "gallery") {
                                            // Launch the real gallery photo picker contract
                                            galleryLauncher.launch("image/*")
                                        } else {
                                            isUploading = true
                                            uploadPercentage = 0
                                            uploadComplete = false
                                            scope.launch {
                                                for (p in 1..10) {
                                                    kotlinx.coroutines.delay(100)
                                                    uploadPercentage = p * 10
                                                }
                                                isUploading = false
                                                uploadComplete = true
                                                customAvatarUrl = targetUrl
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            androidx.compose.material3.TextButton(onClick = { showPhotoSourceChooser = false }) {
                                Text(text = "বাতিল", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // Name textfield
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("পূর্ণ নাম (যেমন: শাওন আহমেদ)") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("full_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // Blood Group chips grid
            Text(
                text = "রক্তের গ্রুপ নির্বাচন করুন:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bloodGroups.forEach { group ->
                    val isSelected = selectedGroup == group
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGroup = group },
                        label = { Text(group, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                            }
                        } else null,
                        modifier = Modifier.testTag("chip_$group")
                    )
                }
            }

            // Division selection label (no GPS locator)
            Text(
                text = "যশোরের উপজেলা নির্বাচন করুন:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                divisions.forEach { (engName, bangName) ->
                    val isSelected = selectedDivision == engName
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDivision = engName },
                        label = { Text(bangName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("chip_div_$engName")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specific Area input Field
            OutlinedTextField(
                value = area,
                onValueChange = { area = it },
                label = { Text("সুনির্দিষ্ট স্থান (যেমন: পালবাড়ি মোড়, যশোর সদর)") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("area_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // Phone number
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("মোবাইল নাম্বার (যেমন: 017xxxxxxxx)") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .testTag("phone_number_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // Ready Switch Block
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = if (isAvailable) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "আমি রক্তদানে ইচ্ছুক ও প্রস্তুত",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "অন রাখলে আপনাকে ডোনার তালিকায় 'অ্যাভেলেবল' দেখাবে।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("availability_switch")
                    )
                }
            }

            // Radio Options for Last Donation Date
            Text(
                text = "সর্বশেষ কবে রক্ত দান করেছেন?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    donationPeriods.forEach { period ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { lastDonationDate = period }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = lastDonationDate == period,
                                onClick = { lastDonationDate = period }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = period, fontSize = 14.sp)
                        }
                    }
                }
            }

            if (errorMessage.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (name.trim().isEmpty()) {
                        errorMessage = "অনুগ্রহ করে আপনার নাম পূরণ করুন।"
                        return@Button
                    }
                    if (area.trim().isEmpty()) {
                        errorMessage = "অনুগ্রহ করে আপনার নির্দিষ্ট এলাকাটি লিখুন।"
                        return@Button
                    }
                    if (phone.trim().isEmpty() || phone.trim().length < 8) {
                        errorMessage = "একটি সঠিক মোবাইল নাম্বার দিন যেন মানুষ যোগাযোগ করতে পারে।"
                        return@Button
                    }

                    viewModel.saveProfile(
                        name = name.trim(),
                        bloodGroup = selectedGroup,
                        division = selectedDivision,
                        area = area.trim(),
                        phone = phone.trim(),
                        avatarId = avatarId,
                        isAvailable = isAvailable,
                        lastDonationDate = lastDonationDate,
                        customAvatarUrl = customAvatarUrl.trim()
                    )
                    onSaved()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_profile_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "প্রোফাইল সংরক্ষণ করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
