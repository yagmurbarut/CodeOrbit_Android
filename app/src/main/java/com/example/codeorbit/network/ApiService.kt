package com.example.codeorbit.network

import retrofit2.http.*

interface ApiService {

    // ========== AUTH ==========
    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // ========== QUIZ ==========
    @POST("api/Quiz/start")
    suspend fun startQuiz(@Body request: StartQuizRequest): QuizResponse

    @POST("api/Quiz/answer")
    suspend fun submitAnswer(@Body request: SubmitAnswerRequest): retrofit2.Response<Unit>

    @POST("api/Quiz/{quizId}/complete")
    suspend fun completeQuiz(@Path("quizId") quizId: Int): QuizResultResponse

    @GET("api/Quiz/history/{userId}")
    suspend fun getQuizHistory(@Path("userId") userId: Int): List<QuizHistoryResponse>

    // ========== CATEGORY ==========
    @GET("api/Category")
    suspend fun getAllCategories(): List<CategoryResponse>

    // ========== STATISTICS ==========
    @GET("api/Statistics/{userId}")
    suspend fun getUserStatistics(@Path("userId") userId: Int): StatisticsResponse

    // ========== BADGES ==========
    @GET("api/Badge/{userId}")
    suspend fun getUserBadges(@Path("userId") userId: Int): List<BadgeResponse>

    // ========== LEADERBOARD ==========
    @GET("api/Leaderboard/global/{userId}")
    suspend fun getGlobalLeaderboard(@Path("userId") userId: Int): List<LeaderboardResponse>

    @GET("api/Leaderboard/weekly/{userId}")
    suspend fun getWeeklyLeaderboard(@Path("userId") userId: Int): List<LeaderboardResponse>

    // ========== FRIENDS ==========
    @POST("api/Friend/request")
    suspend fun sendFriendRequest(@Body request: SendFriendRequestRequest): retrofit2.Response<Unit>

    @GET("api/Friend/{userId}")
    suspend fun getFriends(@Path("userId") userId: Int): List<FriendResponse>

    // ========== NOTIFICATIONS ==========
    @GET("api/Notification/{userId}")
    suspend fun getNotifications(@Path("userId") userId: Int): List<NotificationResponse>

    // ========== DAILY CHALLENGE ==========
    @GET("api/Challenge/today/{userId}")
    suspend fun getDailyChallenge(@Path("userId") userId: Int): DailyChallengeResponse
}