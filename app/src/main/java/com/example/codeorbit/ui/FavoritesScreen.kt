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
import com.example.codeorbit.network.FavoriteQuestionResponse
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

@Composable
fun FavoritesScreen(
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
        if (effectiveUserId > 0) viewModel.loadFavorites(effectiveUserId)
    }

    // Kategoriye göre grupla
    val groupedFavorites = uiState.favorites.groupBy { it.categoryName }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
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
                Text("Favorilerim ⭐", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("${uiState.favorites.size}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (uiState.favorites.isEmpty()) {
                // Boş durum
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("⭐", fontSize = 64.sp)
                        Text(
                            "Henüz favori soru yok",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Quiz çözerken ⭐ butonuna basarak\nsoruları favorilere ekleyebilirsin.",
                            fontSize = 14.sp,
                            color = SlateText,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    groupedFavorites.forEach { (category, questions) ->
                        FavoriteCategoryGroup(
                            categoryName = category,
                            questions = questions,
                            onRemoveFavorite = { questionId ->
                                viewModel.removeFavorite(effectiveUserId, questionId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteCategoryGroup(
    categoryName: String,
    questions: List<FavoriteQuestionResponse>,
    onRemoveFavorite: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    val categoryColor = categoryColors[categoryName] ?: PrimaryBlue

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SlateBackground)
    ) {
        // Kategori başlığı
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(categoryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        categoryName.take(2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }
                Column {
                    Text(
                        categoryName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "${questions.size} soru",
                        fontSize = 12.sp,
                        color = SlateText
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(categoryColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "${questions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }
                Icon(
                    if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = SlateText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Sorular
        if (isExpanded) {
            HorizontalDivider(color = SlateBorder, modifier = Modifier.padding(horizontal = 16.dp))

            questions.forEachIndexed { index, question ->
                FavoriteQuestionItem(
                    question = question,
                    onRemove = { onRemoveFavorite(question.questionId) }
                )
                if (index < questions.size - 1) {
                    HorizontalDivider(color = SlateBorder, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun FavoriteQuestionItem(
    question: FavoriteQuestionResponse,
    onRemove: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Soru tipi ikonu
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFBBF24).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                when (question.questionType) {
                    "MultipleChoice" -> "📝"
                    "TrueFalse" -> "✓✗"
                    "FillInTheBlank" -> "___"
                    "CompleteSentence" -> "💬"
                    else -> "❓"
                },
                fontSize = if (question.questionType == "TrueFalse" || question.questionType == "FillInTheBlank") 10.sp else 16.sp
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                question.questionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 20.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SlateBackground)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        question.difficultyLevel,
                        fontSize = 10.sp,
                        color = when (question.difficultyLevel) {
                            "Easy" -> SuccessGreen
                            "Medium" -> Color(0xFFFBBF24)
                            "Hard" -> Color(0xFFEF4444)
                            else -> SlateText
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Kaldır butonu
        if (showConfirm) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Red.copy(alpha = 0.2f))
                        .clickable { onRemove() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateBackground)
                        .clickable { showConfirm = false },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, tint = SlateText, modifier = Modifier.size(16.dp))
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFBBF24).copy(alpha = 0.15f))
                    .clickable { showConfirm = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}