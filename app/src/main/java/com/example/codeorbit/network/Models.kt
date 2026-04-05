package com.example.codeorbit.network

// ========== AUTH ==========
data class LoginRequest(
    @com.google.gson.annotations.SerializedName("email")
    val email: String,
    @com.google.gson.annotations.SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @com.google.gson.annotations.SerializedName("username")
    val username: String,
    @com.google.gson.annotations.SerializedName("email")
    val email: String,
    @com.google.gson.annotations.SerializedName("password")
    val password: String
)

data class AuthResponse(
    val token: String,
    val userId: Int,
    val username: String,
    val email: String
)

// ========== QUIZ ==========
data class StartQuizRequest(
    val userId: Int,
    val categoryId: Int,
    val difficultyLevel: Int,
    val questionCount: Int,
    val fromFavoritesOnly: Boolean = false
)

data class QuizResponse(
    val quizId: Int,
    val questions: List<QuestionResponse>
)

data class QuestionResponse(
    val questionId: Int,
    val questionText: String,
    val options: List<OptionResponse>,
    val difficultyLevel: Int,
    val categoryId: Int
)

data class OptionResponse(
    val optionId: Int,
    val optionText: String
)

data class SubmitAnswerRequest(
    val quizId: Int,
    val questionId: Int,
    val selectedOptionId: Int
)

data class QuizResultResponse(
    val quizId: Int,
    val score: Int,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val successRate: Double,
    val newBadges: List<BadgeResponse>
)

data class QuizHistoryResponse(
    val quizId: Int,
    val categoryName: String,
    val score: Int,
    val successRate: Double,
    val completedAt: String
)

// ========== CATEGORY ==========
data class CategoryResponse(
    val categoryId: Int,
    val name: String,
    val iconUrl: String?
)

// ========== STATISTICS ==========
data class StatisticsResponse(
    val totalQuizzes: Int,
    val averageScore: Double,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCorrectAnswers: Int,
    val totalQuestions: Int
)

// ========== BADGE ==========
data class BadgeResponse(
    val badgeId: Int,
    val name: String,
    val description: String,
    val iconUrl: String?,
    val earnedAt: String?
)

// ========== LEADERBOARD ==========
data class LeaderboardResponse(
    val rank: Int,
    val userId: Int,
    val username: String,
    val score: Int,
    val avatarUrl: String?
)

// ========== FRIENDS ==========
data class SendFriendRequestRequest(
    val senderId: Int,
    val receiverId: Int
)

data class FriendResponse(
    val userId: Int,
    val username: String,
    val avatarUrl: String?,
    val currentStreak: Int
)

// ========== NOTIFICATIONS ==========
data class NotificationResponse(
    val notificationId: Int,
    val message: String,
    val isRead: Boolean,
    val createdAt: String
)

// ========== DAILY CHALLENGE ==========
data class DailyChallengeResponse(
    val challengeId: Int,
    val question: QuestionResponse,
    val isCompleted: Boolean
)