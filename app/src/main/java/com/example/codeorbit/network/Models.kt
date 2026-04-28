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

// ========== QUIZ ==========
data class QuizResponse(
    val quizId: Int,
    val categoryName: String,
    val difficultyLevel: String,
    val totalQuestions: Int,
    val questions: List<QuestionResponse>
)

data class QuestionResponse(
    val quizQuestionId: Int,
    val questionId: Int,
    val questionText: String,
    val questionType: String,
    val options: List<OptionResponse>
)

data class OptionResponse(
    @com.google.gson.annotations.SerializedName("id")
    val optionId: Int,
    val optionText: String
)

data class SubmitAnswerRequest(
    val quizId: Int,
    val quizQuestionId: Int,
    val selectedOptionId: Int
)

data class QuizResultResponse(
    val quizId: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val successRate: Double,
    val completedAt: String
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
    @com.google.gson.annotations.SerializedName("id")
    val categoryId: Int,
    val name: String,
    val language: String? = null,
    val iconUrl: String? = null
)

// ========== STATISTICS ==========
data class StatisticsResponse(
    val totalQuizzes: Int,
    val totalQuestionsSolved: Int,
    val totalCorrectAnswers: Int,
    val totalWrongAnswers: Int,
    val overallSuccessRate: Double,
    val currentStreak: Int,
    val longestStreak: Int,
    val categoryStats: List<CategoryStatResponse>,
    val difficultyStats: List<DifficultyStatResponse>,
    val mostWrongQuestions: List<MostWrongQuestionResponse>
)

data class CategoryStatResponse(
    val categoryName: String,
    val questionsSolved: Int,
    val correctAnswers: Int,
    val successRate: Double
)

data class DifficultyStatResponse(
    val difficultyLevel: String,
    val questionsSolved: Int,
    val correctAnswers: Int,
    val successRate: Double
)

data class MostWrongQuestionResponse(
    val questionId: Int,
    val questionText: String,
    val categoryName: String,
    val timesAnswered: Int,
    val timesWrong: Int,
    val wrongRate: Double
)

// ========== BADGE ==========
data class BadgeResponse(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String?,
    val isEarned: Boolean = false,
    val earnedAt: String? = null,
    val progress: Int = 0,
    val requiredCount: Int = 0
)
data class LeaderboardResponse(
    val rank: Int,
    val userId: Int,
    val username: String,
    val score: Int,
    val successRate: Double,
    val badgeCount: Int,
    val currentStreak: Int,
    val isCurrentUser: Boolean
)
// ========== FRIENDS ==========
data class SendFriendRequestRequest(
    val senderId: Int,
    val receiverId: Int
)

data class FriendResponse(
    val userId: Int,
    val username: String,
    val email: String,
    val friendsSince: String,
    val stats: FriendStatsResponse
)
data class FriendSearchResponse(
    val userId: Int,
    val username: String,
    val email: String,
    val stats: FriendStatsResponse
)
data class FriendRequestResponse(
    val requestId: Int,
    val senderId: Int,
    val senderUsername: String,
    val status: String,
    val sentAt: String
)

data class RespondFriendRequestRequest(
    val requestId: Int,
    val accept: Boolean
)
data class FriendStatsResponse(
    val totalQuizzes: Int,
    val currentStreak: Int,
    val overallSuccessRate: Double
)
// ========== NOTIFICATIONS ==========
data class NotificationResponse(
    val id: Int,
    val type: String,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String,
    val actionUrl: String?
)
data class AddFavoriteRequest(
    val userId: Int,
    val questionId: Int
)

data class FavoriteQuestionResponse(
    val id: Int,
    val questionId: Int,
    val questionText: String,
    val categoryName: String,
    val difficultyLevel: String,
    val questionType: String,
    val addedAt: String
)
// ========== DAILY CHALLENGE ==========
data class DailyChallengeResponse(
    val challengeId: Int,
    val date: String,
    val categoryName: String,
    val difficultyLevel: String,
    val totalQuestions: Int,
    val hasCompleted: Boolean,
    val questions: List<ChallengeQuestionResponse>
)

data class ChallengeQuestionResponse(
    val questionId: Int,
    val questionText: String,
    val questionType: String,
    val options: List<OptionResponse>
)

data class SubmitChallengeRequest(
    val userId: Int,
    val dailyChallengeId: Int,
    val answers: List<ChallengeAnswerRequest>
)

data class ChallengeAnswerRequest(
    val questionId: Int,
    val selectedOptionId: Int
)

data class DailyChallengeResultResponse(
    val correctAnswers: Int,
    val totalQuestions: Int,
    val successRate: Double,
    val rank: Int,
    val totalParticipants: Int,
    val answerResults: List<ChallengeAnswerResultResponse>
)

data class ChallengeAnswerResultResponse(
    val questionId: Int,
    val isCorrect: Boolean,
    val correctOptionId: Int
)

data class ChallengeLeaderboardResponse(
    val rank: Int,
    val username: String,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val successRate: Double,
    val completedAt: String
)
data class UserProfileResponse(
    val id: Int,
    val username: String,
    val email: String,
    val profilePhoto: String?,
    val avatar: String?,
    val createdAt: String
)

data class UpdateUsernameRequest(
    val userId: Int,
    val newUsername: String
)

data class UpdatePasswordRequest(
    val userId: Int,
    val currentPassword: String,
    val newPassword: String
)

data class UpdateProfilePhotoRequest(
    val userId: Int,
    val photoBase64: String
)
data class UpdateAvatarRequest(
    val userId: Int,
    val avatar: String
)