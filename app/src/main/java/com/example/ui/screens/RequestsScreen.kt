package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BloodRequest
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RequestsScreen(
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val requestsList by viewModel.bloodRequests.collectAsState()
    val userProfile by viewModel.currentUserProfile.collectAsState()

    var showPostDialog by remember { mutableStateOf(false) }

    // Form states
    var patientName by remember { mutableStateOf("") }
    var reqBloodGroup by remember { mutableStateOf("O+") }
    var hospitalName by remember { mutableStateOf("") }
    var selectedDivision by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var neededDate by remember { mutableStateOf("") }
    var neededTime by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    var formError by remember { mutableStateOf("") }

    val bloodGroups = listOf("O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-")
    val divisions = listOf(
        "Dhaka" to "ঢাকা",
        "Chittagong" to "চট্টগ্রাম",
        "Sylhet" to "সিলেট",
        "Rajshahi" to "রাজশাহী",
        "Khulna" to "খুলনা",
        "Barishal" to "বরিশাল",
        "Rangpur" to "রংপুর",
        "Mymensingh" to "ময়মনসিংহ"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // pre-load user info into request form where possible
                    if (userProfile != null) {
                        phone = userProfile?.phone ?: ""
                        selectedDivision = userProfile?.division ?: "Dhaka"
                        area = userProfile?.area ?: ""
                    }
                    showPostDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .testTag("create_request_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "পোস্ট যোগ করুন")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "রিকোয়েস্ট করুন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Announcement Banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "জরুরি রক্তের আবেদনসমূহ নিচে প্রদর্শন করা হলো। রক্তদানে এগিয়ে এসে মানুষের জীবন বাঁচাতে সহায়তা করুন।",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 16.sp
                    )
                }
            }

            // List of Requests
            if (requestsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "No Requests",
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "বর্তমানে কোনো জরুরি রক্তের পোস্ট নেই।",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "আপনার এলাকায় রক্তের প্রয়োজন হলে নিচের বাটনটিতে চাপ দিয়ে পোস্ট করে দিতে পারেন।",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(requestsList, key = { it.id }) { request ->
                        RequestCard(
                            request = request,
                            currentUserName = userProfile?.name ?: "",
                            onCallClicked = { callPhone ->
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$callPhone"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Safeguard unit
                                }
                            },
                            onDeleteClicked = { req ->
                                viewModel.removeBloodRequest(req)
                            }
                        )
                    }
                    // Bottom spacing spacer
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Beautiful Request Post Form Dialog
        AnimatedVisibility(
            visible = showPostDialog,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Dialog(onDismissRequest = { showPostDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "রক্তের রিকোয়েস্ট তৈরি করুন",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(onClick = { showPostDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Patient Name input
                        OutlinedTextField(
                            value = patientName,
                            onValueChange = { patientName = it },
                            label = { Text("রোগীর নাম") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("req_patient_name"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Blood group filter chip group selector
                        Text(
                            text = "জরুরি কোন রক্তের গ্রুপ প্রয়োজন?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            bloodGroups.filter { it != "সব" }.forEach { group ->
                                val isSelected = reqBloodGroup == group
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { reqBloodGroup = group },
                                    label = { Text(group, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Hospital Name
                        OutlinedTextField(
                            value = hospitalName,
                            onValueChange = { hospitalName = it },
                            label = { Text("হাসপাতাল/ঠিকানা (যেমন: ঢাকা মেডিকেল কলেজ)") },
                            leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("req_hospital"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Division selector
                        Text(
                            text = "বিভাগ নির্বাচন করুন:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            divisions.forEach { (eng, bng) ->
                                val isSelected = selectedDivision == eng
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedDivision = eng },
                                    label = { Text(bng, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Area
                        OutlinedTextField(
                            value = area,
                            onValueChange = { area = it },
                            label = { Text("সুনির্দিষ্ট এলাকা (যেমন: শাহবাগ, মিরপুর-২)") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("req_area"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Contact phone
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("যোগাযোগের মোবাইল নাম্বার") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("req_phone"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Date and Time
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = neededDate,
                                onValueChange = { neededDate = it },
                                label = { Text("কবে লাগবে (তারিখ)") },
                                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                                placeholder = { Text("যেমন: ১০ জুন") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                                    .testTag("req_date"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            OutlinedTextField(
                                value = neededTime,
                                onValueChange = { neededTime = it },
                                label = { Text("কখন লাগবে (সময়)") },
                                leadingIcon = { Icon(Icons.Default.Alarm, contentDescription = null) },
                                placeholder = { Text("যেমন: সকাল ৯টা") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("req_time"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        // Details notes
                        OutlinedTextField(
                            value = details,
                            onValueChange = { details = it },
                            label = { Text("বিস্তারিত তথ্য বা অন্য কোনো নোট") },
                            placeholder = { Text("যেমন: এক্সচেঞ্জ ডোনার লাগবে কি না ইত্যাদি...") },
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        if (formError.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = formError,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showPostDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "বাতিল করুন")
                            }

                            Button(
                                onClick = {
                                    if (patientName.trim().isEmpty()) {
                                        formError = "রোগীর নাম পূরণ করুন।"
                                        return@Button
                                    }
                                    if (hospitalName.trim().isEmpty()) {
                                        formError = "হাসপাতালের নাম ও ঠিকানা দিন।"
                                        return@Button
                                    }
                                    if (selectedDivision.isEmpty()) {
                                        formError = "একটি বিভাগ নির্বাচন করুন।"
                                        return@Button
                                    }
                                    if (area.trim().isEmpty()) {
                                        formError = "এলাকার নাম পূরণ করুন।"
                                        return@Button
                                    }
                                    if (phone.trim().isEmpty() || phone.trim().length < 8) {
                                        formError = "সঠিক মোবাইল নাম্বার দিন।"
                                        return@Button
                                    }
                                    if (neededDate.trim().isEmpty()) {
                                        formError = "রক্ত লাগবে এমন একটি কাঙ্ক্ষিত তারিখ দিন।"
                                        return@Button
                                    }

                                    viewModel.postBloodRequest(
                                        patientName = patientName.trim(),
                                        bloodGroup = reqBloodGroup,
                                        hospitalName = hospitalName.trim(),
                                        division = selectedDivision,
                                        area = area.trim(),
                                        phone = phone.trim(),
                                        neededDate = neededDate.trim(),
                                        neededTime = neededTime.trim(),
                                        details = details.trim()
                                    )

                                    // clear states
                                    patientName = ""
                                    reqBloodGroup = "O+"
                                    hospitalName = ""
                                    area = ""
                                    phone = ""
                                    neededDate = ""
                                    neededTime = ""
                                    details = ""
                                    formError = ""

                                    showPostDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("submit_request_button")
                            ) {
                                Text(text = "পোস্ট সাবমিট করুন", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestCard(
    request: BloodRequest,
    currentUserName: String,
    onCallClicked: (String) -> Unit,
    onDeleteClicked: (BloodRequest) -> Unit
) {
    val showDelete = currentUserName.isNotEmpty() && request.requestedBy.equals(currentUserName, ignoreCase = true)

    val banglaDivision = when (request.division) {
        "Dhaka" -> "ঢাকা"
        "Chittagong" -> "চট্টগ্রাম"
        "Sylhet" -> "সিলেট"
        "Rajshahi" -> "রাজশাহী"
        "Khulna" -> "খুলনা"
        "Barishal" -> "বরিশাল"
        "Rangpur" -> "রংপুর"
        "Mymensingh" -> "ময়মনসিংহ"
        else -> request.division
    }

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("request_card_${request.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Group blood category & needed timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Blood Red Circle Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = request.bloodGroup,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "রক্ত প্রয়োজন!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "রোগী: ${request.patientName}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.61f)
                        )
                    }
                }

                // Call Action and optional delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showDelete) {
                        IconButton(
                            onClick = { onDeleteClicked(request) },
                            modifier = Modifier.testTag("delete_request_${request.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "পোস্ট ডিলিট করুন",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                            .clickable { onCallClicked(request.phone) }
                            .testTag("call_requester_${request.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "কল দিন",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body Detail stats: Hospital & Area
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = request.hospitalName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$banglaDivision, ${request.area}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.8.dp)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Needed Info stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "তারিখ: ${request.neededDate}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "সময়: ${request.neededTime}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (request.details.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "অন্যান্য বিবরণ: ${request.details}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Requester label subtitle
            Text(
                text = "আবেদনকারী: ${request.requestedBy}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
            )
        }
    }
}
