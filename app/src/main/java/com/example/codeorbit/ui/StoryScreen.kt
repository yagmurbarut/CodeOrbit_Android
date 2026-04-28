package com.example.codeorbit.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue
import kotlinx.coroutines.delay

// ========== STORY ÇUBUĞU — HomeScreen'e eklenecek ==========
@Composable
fun StoriesRow(
    userId: Int = 0,
    onNavigate: (String) -> Unit = {},
    viewModel: StoryViewModel = viewModel(
        factory = StoryViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId

    var selectedStoryIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId > 0) viewModel.loadStories(effectiveUserId)
    }

    if (uiState.stories.isEmpty()) return

    // Story viewer dialog
    selectedStoryIndex?.let { index ->
        StoryViewer(
            stories = uiState.stories,
            initialIndex = index,
            onDismiss = { selectedStoryIndex = null },
            onNavigate = { route ->
                selectedStoryIndex = null
                onNavigate(route)
            },
            onViewed = { storyId -> viewModel.markAsViewed(storyId) }
        )
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(uiState.stories) { story ->
            StoryCircle(
                story = story,
                onClick = {
                    selectedStoryIndex = uiState.stories.indexOf(story)
                }
            )
        }
    }
}

// ========== TEKİL STORY DAİRESİ ==========
@Composable
fun StoryCircle(
    story: Story,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Gradient halka — görülmemişse renkli, görülmüşse gri
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        if (!story.isViewed)
                            Brush.linearGradient(story.gradientColors)
                        else
                            Brush.linearGradient(listOf(SlateText, SlateText))
                    )
            )
            // İç daire
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(BackgroundDark),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                story.gradientColors.map { it.copy(alpha = 0.3f) }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(story.emoji, fontSize = 24.sp)
                }
            }
        }

        Text(
            story.type.name.take(8).lowercase().replaceFirstChar { it.uppercase() },
            fontSize = 10.sp,
            color = if (!story.isViewed) Color.White else SlateText,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

// ========== FULL SCREEN STORY VIEWER ==========
@Composable
fun StoryViewer(
    stories: List<Story>,
    initialIndex: Int,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    onViewed: (Int) -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val currentStory = stories.getOrNull(currentIndex) ?: return

    var progress by remember { mutableStateOf(0f) }
    var isPaused by remember { mutableStateOf(false) }

    // Auto progress
    LaunchedEffect(currentIndex, isPaused) {
        progress = 0f
        onViewed(currentStory.id)
        while (progress < 1f && !isPaused) {
            delay(50)
            progress += 0.05f / 4f
        }
        if (progress >= 1f) {
            if (currentIndex < stories.size - 1) {
                currentIndex++
            } else {
                onDismiss()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(currentStory.gradientColors)
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPaused = true
                            tryAwaitRelease()
                            isPaused = false
                        },
                        onTap = { offset ->
                            if (offset.x < size.width / 2) {
                                // Sol — önceki
                                if (currentIndex > 0) {
                                    currentIndex--
                                } else {
                                    onDismiss()
                                }
                            } else {
                                // Sağ — sonraki
                                if (currentIndex < stories.size - 1) {
                                    currentIndex++
                                } else {
                                    onDismiss()
                                }
                            }
                        }
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Progress bar'lar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    stories.forEachIndexed { index, _ ->
                        LinearProgressIndicator(
                            progress = {
                                when {
                                    index < currentIndex -> 1f
                                    index == currentIndex -> progress
                                    else -> 0f
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Kapat butonu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Story içeriği — ortada
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(currentStory.emoji, fontSize = 80.sp, textAlign = TextAlign.Center)

                    Text(
                        currentStory.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )

                    Text(
                        currentStory.subtitle,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action butonu — varsa göster
                currentStory.actionRoute?.let { route ->
                    Button(
                        onClick = { onNavigate(route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            when (currentStory.type) {
                                StoryType.DAILY_CHALLENGE -> "🎯 Challenge'ı Başlat!"
                                else -> "Görüntüle →"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}