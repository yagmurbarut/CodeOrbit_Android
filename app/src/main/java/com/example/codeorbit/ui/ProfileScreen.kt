package com.example.codeorbit.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.PrimaryBlue
import kotlinx.coroutines.launch

val avatarList = listOf(
    "🧑‍💻", "👨‍🚀", "🦊", "🐼", "🦁", "🤖",
    "🧙‍♂️", "🦸", "🐯", "🐸", "🦋", "🐲",
    "👾", "🎮", "🚀", "⚡", "🔥", "💎",
    "🦄", "🐺", "🎯", "🏆", "🌟", "💻"
)

val avatarColors = listOf(
    Color(0xFF1D4ED8), Color(0xFF7C3AED), Color(0xFF065F46),
    Color(0xFF9A3412), Color(0xFF1E3A5F), Color(0xFF4C1D95),
    Color(0xFF064E3B), Color(0xFF7F1D1D), Color(0xFF1E40AF),
    Color(0xFF5B21B6), Color(0xFF047857), Color(0xFFB45309)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Int = 0,
    username: String = "",
    onNavigateBack: () -> Unit = {},
    onNavigateToAccountSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    onThemeChanged: (Boolean) -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats = uiState.statistics
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("codeorbit_prefs", Context.MODE_PRIVATE)
    val scope = rememberCoroutineScope()

    var darkMode by remember { mutableStateOf(prefs.getBoolean("dark_theme", true)) }
    var pushNotifications by remember { mutableStateOf(true) }
    var leaderboardUpdates by remember { mutableStateOf(false) }
    var showAvatarSheet by remember { mutableStateOf(false) }

    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId

    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId > 0) {
            viewModel.loadStatistics(effectiveUserId)
            viewModel.loadUserProfile(effectiveUserId)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            bytes?.let { b ->
                val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                val scaled = android.graphics.Bitmap.createScaledBitmap(bitmap, 200, 200, true)
                val stream = java.io.ByteArrayOutputStream()
                scaled.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, stream)
                val base64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
                viewModel.updateProfilePhoto(effectiveUserId, base64)
            }
        }
    }

    if (showAvatarSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAvatarSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Avatar Seç",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(avatarList) { emoji ->
                        val isSelected = uiState.avatar == emoji
                        val colorIndex = avatarList.indexOf(emoji) % avatarColors.size
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(avatarColors[colorIndex].copy(alpha = if (isSelected) 0.8f else 0.3f))
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    viewModel.updateAvatar(effectiveUserId, emoji)
                                    showAvatarSheet = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 24.sp, textAlign = TextAlign.Center)
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryBlue.copy(alpha = 0.15f))
                        .clickable {
                            showAvatarSheet = false
                            galleryLauncher.launch("image/*")
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
                    Text("Galeriden Fotoğraf Seç", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
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
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(20.dp))
                }
                Text("Profil & Ayarlar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Profil kartı — gradient arka plan, beyaz yazılar kalmalı
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryBlue.copy(alpha = 0.8f), Color(0xFF2563EB).copy(alpha = 0.6f))
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { showAvatarSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            val photo = uiState.profilePhoto?.takeIf { it.isNotEmpty() }
                            val avatar = uiState.avatar
                            when {
                                photo != null -> {
                                    val bytes = Base64.decode(photo, Base64.DEFAULT)
                                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                    bitmap?.let {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = "Profil fotoğrafı",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                                avatar != null -> {
                                    val colorIndex = avatarList.indexOf(avatar) % avatarColors.size
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                if (colorIndex >= 0) avatarColors[colorIndex].copy(alpha = 0.5f)
                                                else Color.Transparent
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(avatar, fontSize = 40.sp, textAlign = TextAlign.Center)
                                    }
                                }
                                else -> {
                                    Text(
                                        username.firstOrNull()?.toString() ?: "?",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            username.ifEmpty { "Kullanıcı" },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("CodeOrbit üyesi", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniStatCard(modifier = Modifier.weight(1f), emoji = "✅", value = "${stats?.totalCorrectAnswers ?: 0}", label = "Doğru")
                MiniStatCard(modifier = Modifier.weight(1f), emoji = "📝", value = "${stats?.totalQuizzes ?: 0}", label = "Quiz")
                MiniStatCard(modifier = Modifier.weight(1f), emoji = "🔥", value = "${stats?.currentStreak ?: 0}", label = "Seri")
                MiniStatCard(modifier = Modifier.weight(1f), emoji = "🏆", value = "${"%.0f".format(stats?.overallSuccessRate ?: 0.0)}%", label = "Başarı")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsSection(title = "HESABIM") {
                    SettingsItem(icon = Icons.Filled.Star, label = "Favorilerim", subtitle = "Kaydettiğin sorular", showArrow = true, onClick = onNavigateToFavorites)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Filled.Lock, label = "Şifre Değiştir", subtitle = "Hesap güvenliği", showArrow = true, onClick = onNavigateToAccountSettings)
                }

                SettingsSection(title = "GÖRÜNÜM") {
                    SettingsToggleItem(icon = Icons.Filled.DarkMode, label = "Karanlık Tema", subtitle = "Göz dostu koyu tema", checked = darkMode, onCheckedChange = {
                        darkMode = it
                        onThemeChanged(it)
                    })
                }

                SettingsSection(title = "BİLDİRİMLER") {
                    SettingsToggleItem(icon = Icons.Filled.Notifications, label = "Anlık Bildirimler", subtitle = "Günlük challenge ve streak uyarıları", checked = pushNotifications, onCheckedChange = { pushNotifications = it })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsToggleItem(icon = Icons.Filled.Leaderboard, label = "Sıralama Bildirimleri", subtitle = "Biri seni geçtiğinde bildir", checked = leaderboardUpdates, onCheckedChange = { leaderboardUpdates = it })
                }

                SettingsSection(title = "UYGULAMA") {
                    SettingsItem(icon = Icons.Filled.Info, label = "Hakkında", subtitle = "CodeOrbit v1.0.0", showArrow = false, onClick = {})
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Red.copy(alpha = 0.1f))
                        .clickable { onLogout() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Logout, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                        Text("Çıkış Yap", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp, modifier = Modifier.padding(horizontal = 4.dp))
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface), content = content)
    }
}

@Composable
fun MiniStatCard(modifier: Modifier = Modifier, emoji: String, value: String, label: String) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 18.sp)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, value: String, label: String) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp)).background(PrimaryBlue.copy(alpha = 0.05f)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
    }
}

@Composable
fun SettingsItem(icon: ImageVector, label: String, subtitle: String? = null, showArrow: Boolean = false, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryBlue.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
            subtitle?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        if (showArrow) Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun SettingsToggleItem(icon: ImageVector, label: String, subtitle: String? = null, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryBlue.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
            subtitle?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue))
    }
}