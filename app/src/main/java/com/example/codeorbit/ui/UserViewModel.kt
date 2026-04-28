package com.example.codeorbit.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeorbit.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val statistics: StatisticsResponse? = null,
    val badges: List<BadgeResponse> = emptyList(),
    val globalLeaderboard: List<LeaderboardResponse> = emptyList(),
    val weeklyLeaderboard: List<LeaderboardResponse> = emptyList(),
    val friends: List<FriendResponse> = emptyList(),
    val searchResults: List<FriendSearchResponse> = emptyList(),
    val friendRequests: List<FriendRequestResponse> = emptyList(),
    val friendRequestSent: Boolean = false,
    val errorMessage: String? = null,
    val favorites: List<FavoriteQuestionResponse> = emptyList(),
    val notifications: List<NotificationResponse> = emptyList(),
    val unreadCount: Int = 0,
    val profilePhoto: String? = null,
    val userEmail: String = "",
    val avatar: String? = null
)

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState
    val favorites: List<FavoriteQuestionResponse> = emptyList()


    fun loadStatistics(userId: Int) {
        android.util.Log.d("StatsDebug", "loadStatistics called with userId=$userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val stats = RetrofitClient.apiService.getUserStatistics(userId)
                _uiState.value = _uiState.value.copy(isLoading = false, statistics = stats)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }
    fun loadFavorites(userId: Int) {
        viewModelScope.launch {
            try {
                val favorites = RetrofitClient.apiService.getFavorites(userId)
                _uiState.value = _uiState.value.copy(favorites = favorites)
            } catch (e: Exception) { }
        }
    }
    fun updateAvatar(userId: Int, avatar: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.updateAvatar(
                    UpdateAvatarRequest(userId = userId, avatar = avatar)
                )
                if (response.isSuccessful) {
                    // Fotoğrafı sıfırla, avatarı set et
                    _uiState.value = _uiState.value.copy(
                        avatar = avatar,
                        profilePhoto = null
                    )
                    // Backend'de de fotoğrafı sil
                    RetrofitClient.apiService.updateProfilePhoto(
                        UpdateProfilePhotoRequest(userId = userId, photoBase64 = "")
                    )
                }
            } catch (e: Exception) { }
        }
    }
    fun removeFavorite(userId: Int, questionId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.removeFavorite(userId, questionId)
                _uiState.value = _uiState.value.copy(
                    favorites = _uiState.value.favorites.filter { it.questionId != questionId }
                )
            } catch (e: Exception) { }
        }
    }
    fun clearData() {
        _uiState.value = UserUiState()
    }

    fun loadBadges(userId: Int) {
        viewModelScope.launch {
            try {
                val badges = RetrofitClient.apiService.getUserBadges(userId)
                _uiState.value = _uiState.value.copy(badges = badges)
            } catch (e: Exception) { }
        }
    }

    fun loadLeaderboard(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val global = RetrofitClient.apiService.getGlobalLeaderboard(userId)
                val weekly = RetrofitClient.apiService.getWeeklyLeaderboard(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    globalLeaderboard = global,
                    weeklyLeaderboard = weekly
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }

    fun loadFriends(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val friends = RetrofitClient.apiService.getFriends(userId)
                _uiState.value = _uiState.value.copy(isLoading = false, friends = friends)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }

    fun loadFriendRequests(userId: Int) {
        viewModelScope.launch {
            try {
                val requests = RetrofitClient.apiService.getFriendRequests(userId)
                _uiState.value = _uiState.value.copy(friendRequests = requests)
            } catch (e: Exception) { }
        }
    }

    fun respondFriendRequest(userId: Int, requestId: Int, accept: Boolean) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.respondFriendRequest(
                    userId = userId,
                    request = RespondFriendRequestRequest(
                        requestId = requestId,
                        accept = accept
                    )
                )
                loadFriendRequests(userId)
                if (accept) loadFriends(userId)
            } catch (e: Exception) { }
        }
    }

    fun searchUsers(searchTerm: String, currentUserId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            try {
                val results = RetrofitClient.apiService.searchUsers(searchTerm, currentUserId)
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    searchResults = results
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSearching = false)
            }
        }
    }
    fun loadNotifications(userId: Int) {
        viewModelScope.launch {
            try {
                val notifications = RetrofitClient.apiService.getNotifications(userId)
                val unreadCount = RetrofitClient.apiService.getUnreadCount(userId)
                _uiState.value = _uiState.value.copy(
                    notifications = notifications,
                    unreadCount = unreadCount
                )
            } catch (e: Exception) { }
        }
    }

    fun markAsRead(notificationId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.markAsRead(notificationId)
                loadNotifications(userId)
            } catch (e: Exception) { }
        }
    }

    fun markAllAsRead(userId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.markAllAsRead(userId)
                _uiState.value = _uiState.value.copy(
                    notifications = _uiState.value.notifications.map { it.copy(isRead = true) },
                    unreadCount = 0
                )
            } catch (e: Exception) { }
        }
    }

    fun deleteNotification(notificationId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteNotification(notificationId)
                _uiState.value = _uiState.value.copy(
                    notifications = _uiState.value.notifications.filter { it.id != notificationId }
                )
            } catch (e: Exception) { }
        }
    }
    fun updateProfilePhoto(userId: Int, photoBase64: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.updateProfilePhoto(
                    UpdateProfilePhotoRequest(userId = userId, photoBase64 = photoBase64)
                )
                android.util.Log.d("ProfileDebug", "Photo update response: ${response.code()}")
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(profilePhoto = photoBase64)
                    android.util.Log.d("ProfileDebug", "Photo saved successfully")
                } else {
                    android.util.Log.d("ProfileDebug", "Photo save failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                android.util.Log.d("ProfileDebug", "Photo save exception: ${e.message}")
            }
        }
    }

    fun loadUserProfile(userId: Int) {
        viewModelScope.launch {
            try {
                val profile = RetrofitClient.apiService.getUserProfile(userId)
                _uiState.value = _uiState.value.copy(
                    profilePhoto = profile.profilePhoto,
                    userEmail = profile.email,
                    avatar = profile.avatar
                )
            } catch (e: Exception) { }
        }
    }
    fun updateUsername(userId: Int, newUsername: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.updateUsername(
                    UpdateUsernameRequest(userId = userId, newUsername = newUsername)
                )
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun updatePassword(userId: Int, currentPassword: String, newPassword: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.updatePassword(
                    UpdatePasswordRequest(
                        userId = userId,
                        currentPassword = currentPassword,
                        newPassword = newPassword
                    )
                )
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
    fun sendFriendRequest(senderId: Int, receiverId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.sendFriendRequest(
                    SendFriendRequestRequest(
                        senderId = senderId,
                        receiverId = receiverId
                    )
                )
                _uiState.value = _uiState.value.copy(friendRequestSent = true)
            } catch (e: Exception) { }
        }
    }
}

class UserViewModelFactory(private val application: Application) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return UserViewModel(application) as T
    }
}