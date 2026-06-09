package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donor
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonorSearchScreen(
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val searchTerms by viewModel.searchQuery.collectAsState()
    val activeGroupFilter by viewModel.selectedBloodGroup.collectAsState()
    val activeDivisionFilter by viewModel.selectedDivision.collectAsState()
    val donorList by viewModel.filteredDonors.collectAsState()

    val bloodGroups = listOf("সব", "O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-")
    val divisions = listOf(
        "সব" to "সব বিভাগ",
        "Dhaka" to "ঢাকা",
        "Chittagong" to "চট্টগ্রাম",
        "Sylhet" to "সিলেট",
        "Rajshahi" to "রাজশাহী",
        "Khulna" to "খুলনা",
        "Barishal" to "বরিশাল",
        "Rangpur" to "রংপুর",
        "Mymensingh" to "ময়মনসিংহ"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search text outline input
        OutlinedTextField(
            value = searchTerms,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("এলাকা, ডোনার নাম বা ফোন নাম্বার দিয়ে খুঁজুন...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input_field")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Blood group scrollable chip shelf
        Text(
            text = "রক্তের গ্রুপ ফিল্টার:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bloodGroups) { group ->
                val isSelected = activeGroupFilter == group
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setBloodGroupFilter(group) },
                    label = { Text(group, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("group_filter_$group")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Division flow shelf
        Text(
            text = "বিভাগ ফিল্টার:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            divisions.forEach { (eng, bng) ->
                val isSelected = activeDivisionFilter == eng
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setDivisionFilter(eng) },
                    label = { Text(bng, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.testTag("div_filter_$eng")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Donors count bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ডোনারদের তালিকা",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "মোট: ${donorList.size} জন",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Donors List
        if (donorList.isEmpty()) {
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
                        imageVector = Icons.Default.SentimentVeryDissatisfied,
                        contentDescription = "Empty State",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "দুঃখিত! এই ফিল্টারে কোনো রক্তদাতা পাওয়া যায়নি।",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "অন্য কোনো এলাকা বা রক্তের গ্রুপ নির্বাচন করে দেখুন।",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(donorList, key = { it.id }) { donor ->
                    DonorRow(donor = donor, onCallClicked = { phone ->
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Handler for non-cellular devices
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun DonorRow(
    donor: Donor,
    onCallClicked: (String) -> Unit
) {
    val isReady = donor.isAvailable
    val banglaDivision = when (donor.division) {
        "Dhaka" -> "ঢাকা"
        "Chittagong" -> "চট্টগ্রাম"
        "Sylhet" -> "সিলেট"
        "Rajshahi" -> "রাজশাহী"
        "Khulna" -> "খুলনা"
        "Barishal" -> "বরিশাল"
        "Rangpur" -> "রংপুর"
        "Mymensingh" -> "ময়মনসিংহ"
        else -> donor.division
    }

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("donor_card_${donor.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Donor Avatar with colored border reflecting group
            Box(contentAlignment = Alignment.BottomEnd) {
                DonorAvatar(
                    avatarId = donor.avatarId,
                    size = 56.dp,
                    borderWidth = 1.5.dp,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                // Small Availability Dot
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(if (isReady) Color(0xFF4CAF50) else Color.LightGray)
                        .padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Center: Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = donor.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$banglaDivision • ${donor.area}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Status info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isReady) Color(0xFF4CAF50) else Color(0xFFFF9800))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isReady) "অ্যাভেলেবল (প্রস্তুত)" else "বিরতিতে আছেন",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isReady) Color(0xFF2E7D32) else Color(0xFFD84315)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "শেষ দান: ${donor.lastDonationDate}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            // Right: Big red blood group badge & Immediate dialer icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Blood badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = donor.bloodGroup,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Call circular action button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onCallClicked(donor.phone) }
                        .testTag("call_button_${donor.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "সরাসরি কল দিন",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
