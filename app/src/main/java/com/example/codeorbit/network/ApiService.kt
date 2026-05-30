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

    @POST("api/question/generate")
    suspend fun generateQuestions(@Body request: GenerateQuestionRequest): GenerateQuestionResponse

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
    @GET("api/Friend/requests/{userId}")
    suspend fun getFriendRequests(@Path("userId") userId: Int): List<FriendRequestResponse>

    @POST("api/Friend/respond/{userId}")
    suspend fun respondFriendRequest(
        @Path("userId") userId: Int,
        @Body request: RespondFriendRequestRequest
    ): retrofit2.Response<Unit>
    @GET("api/Friend/{userId}")
    suspend fun getFriends(@Path("userId") userId: Int): List<FriendResponse>

    @GET("api/Friend/search")
    suspend fun searchUsers(
        @Query("searchTerm") searchTerm: String,
        @Query("currentUserId") currentUserId: Int
    ): List<FriendSearchResponse>
    // ========== NOTIFICATIONS ==========
    @GET("api/Notification/{userId}")
    suspend fun getNotifications(@Path("userId") userId: Int): List<NotificationResponse>

    @GET("api/Notification/{userId}/unread-count")
    suspend fun getUnreadCount(@Path("userId") userId: Int): Int

    @PUT("api/Notification/{notificationId}/read")
    suspend fun markAsRead(@Path("notificationId") notificationId: Int): retrofit2.Response<Unit>

    @PUT("api/Notification/{userId}/read-all")
    suspend fun markAllAsRead(@Path("userId") userId: Int): retrofit2.Response<Unit>

    @DELETE("api/Notification/{notificationId}")
    suspend fun deleteNotification(@Path("notificationId") notificationId: Int): retrofit2.Response<Unit>
    // ========== DAILY CHALLENGE ==========
    @GET("api/Challenge/today/{userId}")
    suspend fun getDailyChallenge(@Path("userId") userId: Int): DailyChallengeResponse

    @POST("api/Challenge/submit")
    suspend fun submitChallenge(@Body request: SubmitChallengeRequest): DailyChallengeResultResponse

    @GET("api/Challenge/leaderboard")
    suspend fun getChallengeLeaderboard(): List<ChallengeLeaderboardResponse>
    @POST("api/Favorite")
    suspend fun addFavorite(@Body request: AddFavoriteRequest): retrofit2.Response<Unit>

    @DELETE("api/Favorite/{userId}/{questionId}")
    suspend fun removeFavorite(
        @Path("userId") userId: Int,
        @Path("questionId") questionId: Int
    ): retrofit2.Response<Unit>

    @GET("api/Favorite/{userId}")
    suspend fun getFavorites(@Path("userId") userId: Int): List<FavoriteQuestionResponse>

    @GET("api/Favorite/{userId}/check/{questionId}")
    suspend fun checkFavorite(
        @Path("userId") userId: Int,
        @Path("questionId") questionId: Int
    ): Boolean
    @GET("api/User/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: Int): UserProfileResponse

    @PUT("api/User/username")
    suspend fun updateUsername(@Body request: UpdateUsernameRequest): retrofit2.Response<Unit>

    @PUT("api/User/password")
    suspend fun updatePassword(@Body request: UpdatePasswordRequest): retrofit2.Response<Unit>

    @PUT("api/User/profile-photo")
    suspend fun updateProfilePhoto(@Body request: UpdateProfilePhotoRequest): retrofit2.Response<Unit>
    @PUT("api/User/avatar")
    suspend fun updateAvatar(@Body request: UpdateAvatarRequest): retrofit2.Response<Unit>
}