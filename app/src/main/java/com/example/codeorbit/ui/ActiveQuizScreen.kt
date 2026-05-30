package com.example.codeorbit.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
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
import com.example.codeorbit.ui.splash.PrimaryBlue
import kotlinx.coroutines.delay

@Composable
fun ActiveQuizScreen(
    quizId: Int = 0,
    userId: Int = 0,
    onQuizFinished: (correctAnswers: Int, totalQuestions: Int) -> Unit = { _, _ -> },
    onNavigateBack: () -> Unit = {},
    viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId

    var correctAnswers by remember { mutableIntStateOf(0) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionId by remember { mutableStateOf<Int?>(null) }
    var showFavoriteToast by remember { mutableStateOf(false) }
    var favoriteToastMessage by remember { mutableStateOf("") }

    val questions = uiState.questions
    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    val progress = if (questions.isEmpty()) 0f else (currentQuestionIndex + 1).toFloat() / questions.size.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300),
        label = "progress"
    )

    LaunchedEffect(showFavoriteToast) {
        if (showFavoriteToast) {
            delay(1500)
            showFavoriteToast = false
        }
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    if (currentQuestion == null) return

    val isFavorited = uiState.favoritedQuestions.contains(currentQuestion.questionId)
    val optionLabels = listOf("A", "B", "C", "D")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                        .clip(RoundedCornerShape(50))
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
                }
                Text("CodeOrbit", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Progress
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(PrimaryBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(uiState.categoryName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = PrimaryBlue,
                    trackColor = MaterialTheme.colorScheme.surface
                )
            }

            // Soru ve seçenekler
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            "Question ${currentQuestionIndex + 1}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isFavorited) Color(0xFFFBBF24).copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable {
                                    val willFavorite = !isFavorited
                                    viewModel.toggleFavorite(effectiveUserId, currentQuestion.questionId)
                                    favoriteToastMessage = if (willFavorite) "⭐ Favorilere eklendi!" else "Favorilerden kaldırıldı"
                                    showFavoriteToast = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isFavorited) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Favori",
                                tint = if (isFavorited) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        currentQuestion.questionText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 26.sp
                    )
                }

                // Seçenekler
                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = selectedOptionId == option.optionId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedOptionId = option.optionId }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryBlue.copy(alpha = 0.3f) else MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                optionLabels.getOrElse(index) { "?" },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            option.optionText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // Toast mesajı
        if (showFavoriteToast) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    favoriteToastMessage,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Next / Finish butonu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val chosenOption = selectedOptionId ?: return@Button
                    if (quizId > 0) {
                        viewModel.submitAnswer(
                            quizId = quizId,
                            quizQuestionId = currentQuestion.quizQuestionId,
                            selectedOptionId = chosenOption
                        )
                    } else {
                        val correctOption = currentQuestion.options.getOrNull(currentQuestion.quizQuestionId)
                        if (correctOption?.optionId == chosenOption) {
                            correctAnswers++
                        }
                    }
                    selectedOptionId = null

                    if (currentQuestionIndex < questions.size - 1) {
                        currentQuestionIndex++
                    } else {
                        if (quizId > 0) {
                            viewModel.completeQuiz(quizId, userId) { correct, total ->
                                onQuizFinished(correct, total)
                            }
                        } else {
                            // AI Quiz — direkt sonuç ekranına git
                            onQuizFinished(correctAnswers, questions.size)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedOptionId != null) PrimaryBlue else MaterialTheme.colorScheme.surface
                ),
                enabled = selectedOptionId != null
            ) {
                Text(
                    if (currentQuestionIndex < questions.size - 1) "Next Question" else "Finish Quiz",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}