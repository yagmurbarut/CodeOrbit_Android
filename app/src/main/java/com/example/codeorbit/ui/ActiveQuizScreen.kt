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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeorbit.network.QuestionResponse
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

@Composable
fun ActiveQuizScreen(
    questions: List<QuestionResponse> = emptyList(),
    categoryName: String = "Python Basics",
    onQuizFinished: (correctAnswers: Int) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionId by remember { mutableStateOf<Int?>(null) }
    var correctAnswers by remember { mutableIntStateOf(0) }

    // Demo sorular (API bağlanana kadar)
    val demoQuestions = listOf(
        QuestionResponse(
            questionId = 1,
            questionText = "Which of the following is the correct syntax to output 'Hello World' in Python?",
            options = listOf(
                com.example.codeorbit.network.OptionResponse(1, "print(\"Hello World\")"),
                com.example.codeorbit.network.OptionResponse(2, "p(\"Hello World\")"),
                com.example.codeorbit.network.OptionResponse(3, "echo(\"Hello World\")"),
                com.example.codeorbit.network.OptionResponse(4, "printf(\"Hello World\")")
            ),
            difficultyLevel = 1,
            categoryId = 1
        ),
        QuestionResponse(
            questionId = 2,
            questionText = "What is the correct way to create a variable in Python?",
            options = listOf(
                com.example.codeorbit.network.OptionResponse(5, "var x = 5"),
                com.example.codeorbit.network.OptionResponse(6, "x = 5"),
                com.example.codeorbit.network.OptionResponse(7, "int x = 5"),
                com.example.codeorbit.network.OptionResponse(8, "let x = 5")
            ),
            difficultyLevel = 1,
            categoryId = 1
        )
    )

    val activeQuestions = if (questions.isEmpty()) demoQuestions else questions
    val currentQuestion = activeQuestions.getOrNull(currentQuestionIndex)
    val progress = (currentQuestionIndex + 1).toFloat() / activeQuestions.size.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300),
        label = "progress"
    )

    if (currentQuestion == null) {
        onQuizFinished(correctAnswers)
        return
    }

    val optionLabels = listOf("A", "B", "C", "D")

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
                        .clip(RoundedCornerShape(50))
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Text("CodeOrbit", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = SlateText, modifier = Modifier.size(22.dp))
                }
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
                        "Question ${currentQuestionIndex + 1} of ${activeQuestions.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(PrimaryBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            categoryName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryBlue,
                    trackColor = SlateBackground
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

                // Soru başlığı
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Question ${currentQuestionIndex + 1}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        currentQuestion.questionText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateText,
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
                            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else SlateBackground)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) PrimaryBlue else SlateBorder,
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
                                .background(if (isSelected) PrimaryBlue.copy(alpha = 0.3f) else Color(0xFF2A3441)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                optionLabels.getOrElse(index) { "?" },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PrimaryBlue else SlateText
                            )
                        }
                        Text(
                            option.optionText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Next Question butonu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(BackgroundDark.copy(alpha = 0.95f))
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (selectedOptionId != null) {
                        selectedOptionId = null
                        if (currentQuestionIndex < activeQuestions.size - 1) {
                            currentQuestionIndex++
                        } else {
                            onQuizFinished(correctAnswers)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedOptionId != null) PrimaryBlue else SlateBackground
                ),
                enabled = selectedOptionId != null
            ) {
                Text(
                    if (currentQuestionIndex < activeQuestions.size - 1) "Next Question" else "Finish Quiz",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}