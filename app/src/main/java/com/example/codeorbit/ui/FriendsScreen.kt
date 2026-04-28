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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

@Composable
fun FriendsScreen(
    userId: Int = 0,
    onNavigateBack: () -> Unit = {},
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    var sentRequests by remember { mutableStateOf(setOf<Int>()) }
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId
    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId > 0) viewModel.loadFriends(effectiveUserId)
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            viewModel.searchUsers(searchQuery, effectiveUserId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                Text("Social & Friends", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SlateBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // Arama kutusu
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Kullanıcı adı ile ara...", color = SlateText) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = SlateText)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = null, tint = SlateText, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = PrimaryBlue
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (searchQuery.length < 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    listOf(
                        "My Friends",
                        "Requests${if (uiState.friendRequests.isNotEmpty()) " (${uiState.friendRequests.size})" else ""}"
                    ).forEachIndexed { index, tab ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    tab,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == index) PrimaryBlue else SlateText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(if (selectedTab == index) PrimaryBlue else Color.Transparent)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = SlateBorder)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                if (searchQuery.length >= 2) {
                    // Arama sonuçları
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "ARAMA SONUÇLARI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateText,
                            letterSpacing = 2.sp
                        )
                        if (uiState.isSearching) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(24.dp))
                            }
                        } else if (uiState.searchResults.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SlateBackground)
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Kullanıcı bulunamadı", fontSize = 13.sp, color = SlateText)
                            }
                        } else {
                            uiState.searchResults.forEach { user ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SlateBackground)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryBlue.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            user.username.firstOrNull()?.toString() ?: "?",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(user.username, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(
                                            "${user.stats.totalQuizzes} quiz · ${user.stats.currentStreak}🔥 · ${"%.0f".format(user.stats.overallSuccessRate)}%",
                                            fontSize = 12.sp,
                                            color = SlateText
                                        )
                                    }
                                    val alreadySent = sentRequests.contains(user.userId)
                                    Button(
                                        onClick = {
                                            if (!alreadySent) {
                                                viewModel.sendFriendRequest(effectiveUserId, user.userId)
                                                sentRequests = sentRequests + user.userId
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (alreadySent) SuccessGreen else PrimaryBlue
                                        ),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        modifier = Modifier.height(36.dp),
                                        enabled = !alreadySent
                                    ) {
                                        if (alreadySent) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                Text("Gönderildi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Text("Ekle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (selectedTab == 0) {
                    // Arkadaş listesi
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "YOUR ORBIT (${uiState.friends.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateText,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        if (uiState.isLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(24.dp))
                            }
                        } else if (uiState.friends.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SlateBackground)
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Henüz arkadaş yok", fontSize = 13.sp, color = SlateText)
                            }
                        } else {
                            uiState.friends.forEach { friend ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryBlue.copy(alpha = 0.3f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                friend.username.firstOrNull()?.toString() ?: "?",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Column {
                                            Text(friend.username, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    if (friend.stats.currentStreak > 0) Icons.Filled.LocalFireDepartment else Icons.Filled.TimerOff,
                                                    contentDescription = null,
                                                    tint = if (friend.stats.currentStreak > 0) Color(0xFFF97316) else SlateText,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    if (friend.stats.currentStreak > 0) "${friend.stats.currentStreak} Day Streak" else "No active streak",
                                                    fontSize = 12.sp,
                                                    color = if (friend.stats.currentStreak > 0) Color(0xFFF97316) else SlateText
                                                )
                                            }
                                        }
                                    }
                                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = SlateText, modifier = Modifier.size(20.dp))
                                }
                                HorizontalDivider(color = SlateBorder)
                            }
                        }
                    }
                } else {
                    // Gelen istekler
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "GELEN İSTEKLER (${uiState.friendRequests.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateText,
                            letterSpacing = 2.sp
                        )
                        if (uiState.friendRequests.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SlateBackground)
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Bekleyen istek yok", fontSize = 13.sp, color = SlateText)
                            }
                        } else {
                            uiState.friendRequests.forEach { request ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SlateBackground)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryBlue.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            request.senderUsername.firstOrNull()?.toString() ?: "?",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(request.senderUsername, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("İstek gönderdi", fontSize = 12.sp, color = SlateText)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(SuccessGreen)
                                                .clickable {
                                                    viewModel.respondFriendRequest(effectiveUserId, request.requestId, true)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.Red.copy(alpha = 0.7f))
                                                .clickable {
                                                    viewModel.respondFriendRequest(effectiveUserId, request.requestId, false)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}