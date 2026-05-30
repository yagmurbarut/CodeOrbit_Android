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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

@Composable
fun NotificationScreen(
    userId: Int = 0,
    onNavigateBack: () -> Unit = {},
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId

    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId > 0) viewModel.loadNotifications(effectiveUserId)
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface).clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Bildirimler", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (uiState.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Red)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("${uiState.unreadCount}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                // Tümünü okundu işaretle
                if (uiState.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryBlue.copy(alpha = 0.15f))
                            .clickable { viewModel.markAllAsRead(effectiveUserId) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Tümü okundu", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

            HorizontalDivider(color = SlateBorder)

            if (uiState.notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("🔔", fontSize = 64.sp)
                        Text("Henüz bildirim yok", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Bildirimler burada görünecek", fontSize = 14.sp, color = SlateText, textAlign = TextAlign.Center)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.notifications.forEach { notification ->
                        NotificationItem(
                            notification = notification,
                            onMarkRead = {
                                if (!notification.isRead) {
                                    viewModel.markAsRead(notification.id, effectiveUserId)
                                }
                            },
                            onDelete = {
                                viewModel.deleteNotification(notification.id, effectiveUserId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: com.example.codeorbit.network.NotificationResponse,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit
) {
    val typeEmoji = when (notification.type) {
        "DailyChallenge" -> "🎯"
        "StreakWarning" -> "🔥"
        "FriendRequest" -> "👥"
        "FriendAchievement" -> "🏆"
        "BadgeEarned" -> "🏅"
        else -> "🔔"
    }

    val typeColor = when (notification.type) {
        "DailyChallenge" -> Color(0xFF0891B2)
        "StreakWarning" -> Color(0xFFEA580C)
        "FriendRequest" -> PrimaryBlue
        "FriendAchievement" -> Color(0xFFD97706)
        "BadgeEarned" -> Color(0xFF7C3AED)
        else -> SlateText
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (!notification.isRead) SlateBackground
                else SlateBackground.copy(alpha = 0.5f)
            )
            .clickable { onMarkRead() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // İkon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(typeColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(typeEmoji, fontSize = 22.sp)
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    notification.title,
                    fontSize = 14.sp,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                // Okunmamış nokta
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                    )
                }
            }
            Text(
                notification.message,
                fontSize = 13.sp,
                color = SlateText,
                lineHeight = 18.sp
            )
            Text(
                formatDate(notification.createdAt),
                fontSize = 11.sp,
                color = SlateText.copy(alpha = 0.7f)
            )
        }

        // Sil butonu
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Red.copy(alpha = 0.1f))
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
        }
    }
}

private fun formatDate(dateStr: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        val date = inputFormat.parse(dateStr) ?: return dateStr
        val now = java.util.Date()
        val diffMs = now.time - date.time
        val diffHours = diffMs / (1000 * 60 * 60)
        val diffDays = diffMs / (1000 * 60 * 60 * 24)
        when {
            diffHours < 1 -> "Az önce"
            diffHours < 24 -> "$diffHours saat önce"
            diffDays == 1L -> "Dün"
            diffDays < 7 -> "$diffDays gün önce"
            else -> {
                val outputFormat = java.text.SimpleDateFormat("dd MMM", java.util.Locale("tr"))
                outputFormat.format(date)
            }
        }
    } catch (e: Exception) {
        dateStr
    }
}