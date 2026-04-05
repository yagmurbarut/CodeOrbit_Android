package com.example.codeorbit.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeorbit.network.LoginRequest
import com.example.codeorbit.network.RegisterRequest
import com.example.codeorbit.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val userId: Int = 0,
    val username: String = "",
    val token: String = ""
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.apiService.login(
                    LoginRequest(email = email, password = password)
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    userId = response.userId,
                    username = response.username,
                    token = response.token
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Giriş başarısız: ${e.localizedMessage}"
                )
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(username = username, email = email, password = password)
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    userId = response.userId,
                    username = response.username,
                    token = response.token
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Kayıt başarısız: ${e.localizedMessage}"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}