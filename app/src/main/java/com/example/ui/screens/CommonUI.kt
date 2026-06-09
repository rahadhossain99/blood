package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    initial: String? = null
) {
    val index = (avatarId - 1).coerceIn(0, AvatarGradients.size - 1)
    val colors = AvatarGradients[index]
    val brush = Brush.linearGradient(colors)

    val icon = when (avatarId % 5) {
        0 -> Icons.Default.Male
        1 -> Icons.Default.Favorite
        2 -> Icons.Default.Person
        3 -> Icons.Default.Female
        else -> Icons.Default.VolunteerActivism
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(brush)
            .border(borderWidth, borderColor, CircleShape),
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
            Icon(
                imageVector = icon,
                contentDescription = "Avatar",
                tint = Color.White,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}
