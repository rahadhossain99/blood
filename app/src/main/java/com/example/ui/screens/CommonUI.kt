package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// List of vibrant gradients for Avatars
val AvatarGradients = listOf(
    listOf(Color(0xFFE53935), Color(0xFF880E4F)), // Red - Wine
    listOf(Color(0xFFE91E63), Color(0xFF673AB7)), // Pink - Purple
    listOf(Color(0xFF2196F3), Color(0xFF006064)), // Blue - Cyan
    listOf(Color(0xFF4CAF50), Color(0xFF003300)), // Green - Dark Green
    listOf(Color(0xFFFF9800), Color(0xFFB71C1C)), // Orange - Red
    listOf(Color(0xFFFF5722), Color(0xFF3E2723)), // Coral - Brown
    listOf(Color(0xFF00BCD4), Color(0xFF004D40)), // Cyan - Teal
    listOf(Color(0xFF9C27B0), Color(0xFF311B92)), // Purple - Indigo
    listOf(Color(0xFF3F51B5), Color(0xFF1A237E)), // Blue - Navy
    listOf(Color(0xFF009688), Color(0xFF00796B)), // Teal - Slate Green
)

@Composable
fun DonorAvatar(
    avatarId: Int,
    size: Dp = 56.dp,
    borderWidth: Dp = 2.dp,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    initial: String? = null,
    customAvatarUrl: String? = null
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .border(borderWidth, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!customAvatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = customAvatarUrl,
                contentDescription = "Custom Avatar",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            val index = (avatarId - 1).coerceIn(0, AvatarGradients.size - 1)
            val colors = AvatarGradients[index]
            val brush = Brush.linearGradient(colors)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush),
                contentAlignment = Alignment.Center
            ) {
                if (!initial.isNullOrBlank()) {
                    Text(
                        text = initial.take(1).uppercase(),
                        color = Color.White,
                        fontSize = (size.value * 0.4f).sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    val icon = when (avatarId % 5) {
                        0 -> Icons.Default.Opacity // Blood drop symbol
                        1 -> Icons.Default.Favorite // Heart symbol
                        2 -> Icons.Default.Person // General profile
                        3 -> Icons.Default.MedicalServices // Cross symbol
                        else -> Icons.Default.VolunteerActivism // Helping hands symbol
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(size * 0.55f)
                    )
                }
            }
        }
    }
}

// 8 Districts of Jashore mapping (English internal key to Bengali readable name)
val JashoreUpazilas = listOf(
    "Sadar" to "যশোর সদর",
    "Jhikargachha" to "ঝিকরগাছা",
    "Abhaynagar" to "অভয়নগর",
    "Manirampur" to "মণিরামপুর",
    "Chougachha" to "চৌগাছা",
    "Bagherpara" to "বাঘারপাড়া",
    "Keshabpur" to "কেশবপুর",
    "Sharsha" to "শার্শা"
)

@Composable
fun JashoreUpazilaMap(
    selectedUpazila: String,
    onUpazilaSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "যশোর জেলার ইন্টারেক্টিভ ম্যাপ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "যশোরের উপজেলাগুলোতে ক্লিক করে রক্তদাতা খুঁজুন",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            // Geographical interactive blueprint of Jashore District Upazilas
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ROW 1: North Section (Chougachha North-West, Bagherpara North-East)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MapTile(
                        title = "চৌগাছা",
                        englishKey = "Chougachha",
                        isSelected = selectedUpazila == "Chougachha",
                        onClick = { onUpazilaSelected("Chougachha") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(0.1f)) // geography separation
                    MapTile(
                        title = "বাঘারপাড়া",
                        englishKey = "Bagherpara",
                        isSelected = selectedUpazila == "Bagherpara",
                        onClick = { onUpazilaSelected("Bagherpara") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // ROW 2: Central-West Section (Sharsha West, Sadar Center, Abhaynagar East)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MapTile(
                        title = "শার্শা",
                        englishKey = "Sharsha",
                        isSelected = selectedUpazila == "Sharsha",
                        onClick = { onUpazilaSelected("Sharsha") },
                        modifier = Modifier.weight(0.9f)
                    )
                    MapTile(
                        title = "যশোর সদর\n(কেন্দ্র)",
                        englishKey = "Sadar",
                        isSelected = selectedUpazila == "Sadar",
                        onClick = { onUpazilaSelected("Sadar") },
                        modifier = Modifier.weight(1.2f),
                        isCenter = true
                    )
                    MapTile(
                        title = "অভয়নগর",
                        englishKey = "Abhaynagar",
                        isSelected = selectedUpazila == "Abhaynagar",
                        onClick = { onUpazilaSelected("Abhaynagar") },
                        modifier = Modifier.weight(0.9f)
                    )
                }

                // ROW 3: South Section (Jhikargachha West-S, Manirampur South, Keshabpur Extreme South)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MapTile(
                        title = "ঝিকরগাছা",
                        englishKey = "Jhikargachha",
                        isSelected = selectedUpazila == "Jhikargachha",
                        onClick = { onUpazilaSelected("Jhikargachha") },
                        modifier = Modifier.weight(1f)
                    )
                    MapTile(
                        title = "মণিরামপুর",
                        englishKey = "Manirampur",
                        isSelected = selectedUpazila == "Manirampur",
                        onClick = { onUpazilaSelected("Manirampur") },
                        modifier = Modifier.weight(1.1f)
                    )
                    MapTile(
                        title = "কেশবপুর",
                        englishKey = "Keshabpur",
                        isSelected = selectedUpazila == "Keshabpur",
                        onClick = { onUpazilaSelected("Keshabpur") },
                        modifier = Modifier.weight(0.9f)
                    )
                }
            }

            if (selectedUpazila != "সব" && selectedUpazila.isNotEmpty()) {
                val banglaName = JashoreUpazilas.firstOrNull { it.first == selectedUpazila }?.second ?: selectedUpazila
                androidx.compose.material3.TextButton(
                    onClick = { onUpazilaSelected("সব") },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "ফ্যাক্টরি রিসেট (সব উপজেলা দেখুন • বর্তমান: $banglaName)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun MapTile(
    title: String,
    englishKey: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCenter: Boolean = false
) {
    val bg = if (isSelected) MaterialTheme.colorScheme.primary else if (isCenter) Color(0xFFFFF1F1) else Color(0xFFF8F9FA)
    val borderCol = if (isSelected) MaterialTheme.colorScheme.primary else if (isCenter) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color(0xFFEEEEEE)
    val textCol = if (isSelected) Color.White else if (isCenter) MaterialTheme.colorScheme.primary else Color(0xFF333333)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.2.dp, borderCol, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected || isCenter) FontWeight.Bold else FontWeight.Medium,
                color = textCol,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun SimulatedMobileStatusBar(
    modifier: Modifier = Modifier
) {
    var currentTime by androidx.compose.runtime.remember { mutableStateOf("10:30 AM") }

    // Start a coroutine to update the time in real-time
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            val calendar = java.util.Calendar.getInstance()
            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = calendar.get(java.util.Calendar.MINUTE)
            val isPm = hour >= 12
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            val formattedMinute = String.format("%02d", minute)
            val displayAmPm = if (isPm) "PM" else "AM"
            
            // Convert numbers to beautiful Bengali numbers
            val bengaliHour = displayHour.toString().replace('1','১').replace('2','২')
                .replace('3','৩').replace('4','৪').replace('5','৫').replace('6','৬')
                .replace('7','৭').replace('8','৮').replace('9','৯').replace('0','০')
            
            val bengaliMinute = formattedMinute.replace('1','১').replace('2','২')
                .replace('3','৩').replace('4','৪').replace('5','৫').replace('6','৬')
                .replace('7','৭').replace('8','৮').replace('9','৯').replace('0','০')

            val bengaliAmPm = if (isPm) "অপরাহ্ন" else "পূর্বাহ্ন"

            currentTime = "$bengaliHour:$bengaliMinute $bengaliAmPm"
            kotlinx.coroutines.delay(15000) // update every 15 seconds
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Present real-time
        Text(
            text = currentTime,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Center notch or indicator spacer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF1F1))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "রক্তবন্ধু নেটওয়ার্ক",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        // Right side: Signal bars, WiFi, Battery indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Signal Bars
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.SignalCellular4Bar,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(14.dp)
            )

            // WiFi Icon
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Wifi,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(14.dp)
            )

            // Battery status text + Battery charging icon
            Text(
                text = "৯৮%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.BatteryChargingFull,
                contentDescription = null,
                tint = Color(0xFF4CAF50), // Healthy Green
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
