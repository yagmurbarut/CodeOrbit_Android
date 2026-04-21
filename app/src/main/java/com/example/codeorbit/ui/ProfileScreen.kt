package com.example.codeorbit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

@Composable
fun ProfileScreen(
    userId: Int = 0,
    username: String = "",
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats = uiState.statistics

    var darkMode by remember { mutableStateOf(true) }
    var pushNotifications by remember { mutableStateOf(true) }
    var leaderboardUpdates by remember { mutableStateOf(false) }

    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId
    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId > 0) viewModel.loadStatistics(effectiveUserId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SlateBackground)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text("Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Profil fotoğrafı ve isim
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            username.firstOrNull()?.toString() ?: "?",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        username.ifEmpty { "Kullanıcı" },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("CodeOrbit üyesi", fontSize = 13.sp, color = SlateText)
                }
            }

            // Quick Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), value = "${stats?.totalCorrectAnswers ?: 0}", label = "CORRECT")
                StatCard(modifier = Modifier.weight(1f), value = "${stats?.totalQuizzes ?: 0}", label = "QUIZZES")
                StatCard(modifier = Modifier.weight(1f), value = "${stats?.currentStreak ?: 0}🔥", label = "STREAK")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Hesap
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "ACCOUNT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateText,
                        letterSpacing = 2.sp
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateBackground)
                    ) {
                        SettingsItem(
                            icon = Icons.Filled.Star,
                            label = "Favorilerim",
                            showArrow = true,
                            onClick = onNavigateToFavorites
                        )
                        HorizontalDivider(color = SlateBorder, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsItem(
                            icon = Icons.Filled.Lock,
                            label = "Change Password",
                            showArrow = true,
                            onClick = {}
                        )
                        HorizontalDivider(color = SlateBorder, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsToggleItem(
                            icon = Icons.Filled.DarkMode,
                            label = "Theme (Dark Mode)",
                            checked = darkMode,
                            onCheckedChange = { darkMode = it }
                        )
                    }
                }

                // Bildirimler
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "NOTIFICATION PREFERENCES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateText,
                        letterSpacing = 2.sp
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateBackground)
                    ) {
                        SettingsToggleItem(
                            icon = Icons.Filled.Notifications,
                            label = "Push Notifications",
                            subtitle = "Alerts for daily challenges",
                            checked = pushNotifications,
                            onCheckedChange = { pushNotifications = it }
                        )
                        HorizontalDivider(color = SlateBorder, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsToggleItem(
                            icon = Icons.Filled.Leaderboard,
                            label = "Leaderboard Updates",
                            subtitle = "When someone overtakes you",
                            checked = leaderboardUpdates,
                            onCheckedChange = { leaderboardUpdates = it }
                        )
                    }
                }

                // Logout butonu
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Red.copy(alpha = 0.1f))
                        .clickable { onLogout() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Logout, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                        Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PrimaryBlue.copy(alpha = 0.05f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Text(label, fontSize = 11.sp, color = SlateText, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    label: String,
    showArrow: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White, modifier = Modifier.weight(1f))
        if (showArrow) {
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = SlateText, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
            subtitle?.let {
                Text(it, fontSize = 12.sp, color = SlateText)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryBlue
            )
        )
    }
}