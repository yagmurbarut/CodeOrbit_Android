package com.example.codeorbit.ui

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

val SlateBackground = Color(0xFF1E2733)
val SlateBorder = Color(0xFF2A3441)
val SlateText = Color(0xFF94A3B8)

@Composable
fun HomeScreen(
    userId: Int = 0,
    username: String = "Alex Chen",
    onNavigateToQuiz: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToFriends: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToChallenge: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.HomeViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId
    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId > 0) viewModel.loadHomeData(effectiveUserId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        val photo = uiState.profilePhoto?.takeIf { it.isNotEmpty() }
                        val avatar = uiState.avatar
                        when {
                            photo != null -> {
                                val bytes = android.util.Base64.decode(photo, android.util.Base64.DEFAULT)
                                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                bitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            avatar != null -> {
                                Text(avatar, fontSize = 22.sp, textAlign = TextAlign.Center)
                            }
                            else -> {
                                Text(
                                    text = username.firstOrNull()?.toString() ?: "?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    Column {
                        Text("Welcome back,", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(username, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onNavigateToNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
                    if (uiState.unreadNotificationCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd)
                                .offset(x = (-6).dp, y = 6.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                StoriesRow(
                    userId = userId,
                    onNavigate = { route ->
                        when (route) {
                            "daily_challenge" -> onNavigateToChallenge()
                        }
                    }
                )

                // Streak Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(colors = listOf(PrimaryBlue, Color(0xFF2563EB))))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("CURRENT STREAK", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("${uiState.streakDays} Days!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("🔥", fontSize = 24.sp)
                            }
                            Text(
                                when {
                                    uiState.streakDays == 0 -> "Bugün bir quiz çöz, seriyi başlat! 🚀"
                                    uiState.streakDays < 3 -> "İyi başlangıç, devam et! 💪"
                                    uiState.streakDays < 7 -> "Harika gidiyorsun! 🔥"
                                    uiState.streakDays < 30 -> "Muhteşem seri! Vazgeçme! ⚡"
                                    else -> "Efsane! ${uiState.streakDays} günlük seri! 👑"
                                },
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Box(
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }

                // Daily Challenge Card
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(PrimaryBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Code, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                            }
                            Column {
                                Text("Daily Challenge", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                Text(
                                    if (uiState.dailyChallengeTitle.isNotEmpty()) uiState.dailyChallengeTitle else "Yükleniyor...",
                                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFF78350F).copy(alpha = 0.3f)).padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(if (uiState.dailyChallengeXp > 0) "+${uiState.dailyChallengeXp} XP" else "+XP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                        }
                    }
                    Button(
                        onClick = onNavigateToChallenge,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Now", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                // Quick Actions
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("QUICK ACTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickActionCard(modifier = Modifier.weight(1f), icon = Icons.Filled.AddCircle, iconBg = Color(0xFF1E3A5F), iconColor = Color(0xFF60A5FA), label = "New Quiz", onClick = onNavigateToQuiz)
                        QuickActionCard(modifier = Modifier.weight(1f), icon = Icons.Filled.BarChart, iconBg = Color(0xFF2E1065).copy(alpha = 0.5f), iconColor = Color(0xFFA78BFA), label = "Stats", onClick = onNavigateToStats)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickActionCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Leaderboard, iconBg = Color(0xFF78350F).copy(alpha = 0.3f), iconColor = Color(0xFFFBBF24), label = "Leaderboard", onClick = onNavigateToLeaderboard)
                        QuickActionCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Group, iconBg = Color(0xFF064E3B).copy(alpha = 0.5f), iconColor = Color(0xFF34D399), label = "Friends", onClick = onNavigateToFriends)
                    }
                }

                // Recent Activity
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("RECENT ACTIVITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp)
                        TextButton(onClick = onNavigateToStats) {
                            Text("View All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }

                    if (uiState.isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(24.dp))
                        }
                    } else if (uiState.recentActivities.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("Henüz quiz tamamlanmadı", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        uiState.recentActivities.forEach { activity ->
                            ActivityItem(title = activity.title, subtitle = activity.completedAt, score = "%${activity.score}", emoji = "💻")
                        }
                    }
                }
            }
        }

        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onHome = {},
            onQuiz = onNavigateToQuiz,
            onAchievements = onNavigateToAchievements,
            onLeaderboard = onNavigateToLeaderboard,
            onProfile = onNavigateToProfile
        )
    }
}

@Composable
fun QuickActionCard(modifier: Modifier = Modifier, icon: ImageVector, iconBg: Color, iconColor: Color, label: String, onClick: () -> Unit) {
    Box(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).clickable { onClick() }.padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(iconBg), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun ActivityItem(title: String, subtitle: String, score: String, emoji: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.outline), contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 20.sp)
            }
            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(score, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
            Text("Mastery", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun BottomNavBar(modifier: Modifier = Modifier, onHome: () -> Unit, onQuiz: () -> Unit, onAchievements: () -> Unit, onLeaderboard: () -> Unit, onProfile: () -> Unit) {
    Box(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f)).padding(horizontal = 24.dp, vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            BottomNavItem(icon = Icons.Filled.Home, label = "Home", selected = true, onClick = onHome)
            BottomNavItem(icon = Icons.Filled.Laptop, label = "Quiz", selected = false, onClick = onQuiz)
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(PrimaryBlue).offset(y = (-16).dp).clickable { onAchievements() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            BottomNavItem(icon = Icons.Filled.EmojiEvents, label = "Arena", selected = false, onClick = onLeaderboard)
            BottomNavItem(icon = Icons.Filled.Person, label = "Profile", selected = false, onClick = onProfile)
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.clickable { onClick() }) {
        Icon(icon, contentDescription = null, tint = if (selected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (selected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)
    }
}