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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.Info
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

@Composable
fun EmbeddedLocationMap(
    selectedUpazila: String,
    areaName: String,
    modifier: Modifier = Modifier,
    onLocationChanged: ((Double, Double, String) -> Unit)? = null
) {
    var lat by remember { mutableStateOf(23.1667) } // Default Jashore Latitude
    var lng by remember { mutableStateOf(89.2133) } // Default Jashore Longitude
    var zoomLevel by remember { mutableStateOf(14) }
    var selectedLayer by remember { mutableStateOf("default") } // "default", "satellite", "terrain"

    // Sync up coordinates according to different Upazilas of Jashore
    LaunchedEffect(selectedUpazila) {
        when (selectedUpazila) {
            "Sadar" -> { lat = 23.1685; lng = 89.2124 }
            "Jhikargachha" -> { lat = 23.1032; lng = 89.0224 }
            "Abhaynagar" -> { lat = 23.0185; lng = 89.4412 }
            "Manirampur" -> { lat = 23.0132; lng = 89.2274 }
            "Chougachha" -> { lat = 23.2642; lng = 89.0252 }
            "Sharsha" -> { lat = 23.0242; lng = 88.9221 }
            "Keshabpur" -> { lat = 22.9062; lng = 89.2201 }
            "Bagherpara" -> { lat = 23.2185; lng = 89.3490 }
            else -> { lat = 23.1667; lng = 89.2133 }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Live map rendering on Compose Canvas
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        // Simulates clicking and pinning on map dragging
                        lat += (Math.random() - 0.5) * 0.005
                        lng += (Math.random() - 0.5) * 0.005
                        onLocationChanged?.invoke(lat, lng, selectedUpazila)
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw map styled layers
                when (selectedLayer) {
                    "satellite" -> {
                        // Draw deep earth satellite green-blue theme background
                        drawRect(color = Color(0xFF1E3A1C))
                        // Draw fields / organic textures
                        drawCircle(color = Color(0xFF1B4D22), radius = canvasWidth / 3, center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.2f, canvasHeight * 0.4f))
                        drawCircle(color = Color(0xFF0F2C11), radius = canvasWidth / 4, center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.8f, canvasHeight * 0.7f))
                    }
                    "terrain" -> {
                        // Draw terrain brown-topographical lines
                        drawRect(color = Color(0xFFF4EBE1))
                        // Topography concentric circles
                        drawCircle(
                            color = Color(0xFFE3D4C1),
                            radius = canvasWidth / 2.5f,
                            center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.5f, canvasHeight * 0.5f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = Color(0xFFE3D4C1),
                            radius = canvasWidth / 4f,
                            center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.5f, canvasHeight * 0.5f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                        )
                    }
                    else -> {
                        // Default Clean Cartography Grid background
                        drawRect(color = Color(0xFFF1F5F9))
                    }
                }

                // Draw road network (gray paths)
                val roadColor = when (selectedLayer) {
                    "satellite" -> Color(0x66FFFFFF)
                    "terrain" -> Color(0x8894A3B8)
                    else -> Color(0xFFCBD5E1)
                }
                
                // Draw Highway
                drawLine(
                    color = if (selectedLayer == "satellite") Color(0xFFD97706) else Color(0xFFFFB03A),
                    start = androidx.compose.ui.geometry.Offset(0f, canvasHeight * 0.4f),
                    end = androidx.compose.ui.geometry.Offset(canvasWidth, canvasHeight * 0.6f),
                    strokeWidth = 4.dp.toPx()
                )
                
                // Secondary Roads
                drawLine(color = roadColor, start = androidx.compose.ui.geometry.Offset(canvasWidth * 0.3f, 0f), end = androidx.compose.ui.geometry.Offset(canvasWidth * 0.3f, canvasHeight), strokeWidth = 2.dp.toPx())
                drawLine(color = roadColor, start = androidx.compose.ui.geometry.Offset(canvasWidth * 0.7f, 0f), end = androidx.compose.ui.geometry.Offset(canvasWidth * 0.7f, canvasHeight), strokeWidth = 2.dp.toPx())
                drawLine(color = roadColor, start = androidx.compose.ui.geometry.Offset(0f, canvasHeight * 0.8f), end = androidx.compose.ui.geometry.Offset(canvasWidth, canvasHeight * 0.1f), strokeWidth = 2.dp.toPx())

                // Draw River (Bhairab River Jashore accent)
                val riverPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, canvasHeight * 0.2f)
                    quadraticTo(canvasWidth * 0.4f, canvasHeight * 0.1f, canvasWidth * 0.6f, canvasHeight * 0.8f)
                    lineTo(canvasWidth, canvasHeight * 0.9f)
                }
                drawPath(
                    path = riverPath,
                    color = Color(0xFF38BDF8),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 6.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                )

                // Draw pulsing location radar circles around center pin
                drawCircle(
                    color = Color(0x33DC2626), 
                    radius = 30.dp.toPx() + (zoomLevel * 0.5f), 
                    center = androidx.compose.ui.geometry.Offset(canvasWidth / 2 , canvasHeight / 2)
                )
                
                // Center Map Pin Dot
                drawCircle(color = Color(0xFFDC2626), radius = 6.dp.toPx(), center = androidx.compose.ui.geometry.Offset(canvasWidth / 2, canvasHeight / 2))
                drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(canvasWidth / 2, canvasHeight / 2))
            }

            // Top-left coordinates badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ল্যাট: ${String.format("%.4f", lat)} • লং: ${String.format("%.4f", lng)}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bottom-right Scale display
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.85f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${5000 / zoomLevel} মি.",
                    color = Color.Black,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Top-right controls for Layers and Zoom
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Layer Switch Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            selectedLayer = when (selectedLayer) {
                                "default" -> "satellite"
                                "satellite" -> "terrain"
                                else -> "default"
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "মানচিত্র নির্বাচন",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Zoom In
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { if (zoomLevel < 18) zoomLevel++ },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                }

                // Zoom Out
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { if (zoomLevel > 8) zoomLevel-- },
                    contentAlignment = Alignment.Center
                ) {
                    Text("-", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                }
            }

            // Bottom-left active status bar showing location verification
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = if (areaName.isNotEmpty()) areaName else "যশোর",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 110.dp)
                )
            }
        }
    }
}
