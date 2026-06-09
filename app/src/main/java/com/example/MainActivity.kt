package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.DonorSearchScreen
import com.example.ui.screens.MyProfileTab
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.RequestsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.screens.DonorAvatar
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BloodViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BloodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppHost(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainAppHost(viewModel: BloodViewModel) {
    val loggedInEmail by viewModel.loggedInEmail.collectAsState()
    val currentUserProfile by viewModel.currentUserProfile.collectAsState()

    // Screen toggles or local navigation controls
    var activeTab by remember { mutableIntStateOf(0) }
    var forceEditMode by remember { mutableStateOf(false) }

    when {
        // Step 1: User is not logged in with any Google account -> Show Greeting card and Google button
        loggedInEmail == null -> {
            WelcomeScreen(viewModel = viewModel)
        }

        // Step 2: User logged in, but profile is incomplete (or they clicked edit profile)
        currentUserProfile == null || currentUserProfile?.phone.isNullOrBlank() || forceEditMode -> {
            val defaultProfile = currentUserProfile ?: com.example.data.model.Donor(
                name = "নতুন দাতা",
                bloodGroup = "O+",
                division = "Dhaka",
                area = "",
                phone = "",
                email = loggedInEmail ?: "",
                isCurrentUser = true
            )
            ProfileSetupScreen(
                viewModel = viewModel,
                profile = defaultProfile,
                onSaved = {
                    forceEditMode = false
                }
            )
        }

        // Step 3: Profile is complete -> Show beautiful main three-tab dashboard
        else -> {
            val user = currentUserProfile!!

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini blood droplet decoration
                                Icon(
                                    imageVector = Icons.Default.Opacity,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "রক্তবন্ধু",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        actions = {
                            // Display the user's avatar in the appbar highlighting their account status
                            Row(
                                modifier = Modifier.padding(end = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "হ্যালো, ${user.name.takeWhile { it != ' ' }}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                DonorAvatar(
                                    avatarId = user.avatarId,
                                    size = 32.dp,
                                    borderWidth = 1.dp
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "সন্ধান করুন"
                                )
                            },
                            label = { Text("সন্ধান করুন") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("tab_search")
                        )

                        NavigationBarItem(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = "রক্তের পোস্ট"
                                )
                            },
                            label = { Text("রক্তের পোস্ট") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("tab_requests")
                        )

                        NavigationBarItem(
                            selected = activeTab == 2,
                            onClick = { activeTab = 2 },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "প্রোফাইল"
                                )
                            },
                            label = { Text("আমার প্রোফাইল") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("tab_profile")
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Responsive dynamic screen transitions
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { width -> width } with slideOutHorizontally { width -> -width }
                            } else {
                                slideInHorizontally { width -> -width } with slideOutHorizontally { width -> width }
                            }
                        }
                    ) { targetTab ->
                        when (targetTab) {
                            0 -> DonorSearchScreen(viewModel = viewModel)
                            1 -> RequestsScreen(viewModel = viewModel)
                            2 -> MyProfileTab(
                                viewModel = viewModel,
                                userProfile = user,
                                onEditClicked = {
                                    forceEditMode = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
