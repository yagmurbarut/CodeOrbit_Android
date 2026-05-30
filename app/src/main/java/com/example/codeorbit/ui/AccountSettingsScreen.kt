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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

@Composable
fun AccountSettingsScreen(
    userId: Int = 0,
    currentUsername: String = "",
    onNavigateBack: () -> Unit = {},
    onUsernameUpdated: (String) -> Unit = {},
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId

    // Username state
    var newUsername by remember { mutableStateOf(currentUsername) }
    var usernameLoading by remember { mutableStateOf(false) }
    var usernameMessage by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    // Password state
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordLoading by remember { mutableStateOf(false) }
    var passwordMessage by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    // Visibility toggles
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
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
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text("Hesap Ayarları", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ===== KULLANICI ADI =====
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Text("Kullanıcı Adı", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Görünen adını değiştir", fontSize = 12.sp, color = SlateText)
                        }
                    }

                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = {
                            newUsername = it
                            usernameMessage = null
                        },
                        label = { Text("Yeni kullanıcı adı", color = SlateText) },
                        leadingIcon = {
                            Icon(Icons.Filled.AlternateEmail, contentDescription = null, tint = SlateText, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = PrimaryBlue,
                            focusedLabelColor = PrimaryBlue,
                            unfocusedLabelColor = SlateText
                        ),
                        singleLine = true
                    )

                    // Mesaj
                    usernameMessage?.let { (success, msg) ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (success) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                contentDescription = null,
                                tint = if (success) SuccessGreen else Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                msg,
                                fontSize = 13.sp,
                                color = if (success) SuccessGreen else Color.Red
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (newUsername.isBlank()) {
                                usernameMessage = Pair(false, "Kullanıcı adı boş olamaz")
                                return@Button
                            }
                            if (newUsername == currentUsername) {
                                usernameMessage = Pair(false, "Yeni kullanıcı adı aynı olamaz")
                                return@Button
                            }
                            if (newUsername.length < 3) {
                                usernameMessage = Pair(false, "En az 3 karakter olmalı")
                                return@Button
                            }
                            usernameLoading = true
                            viewModel.updateUsername(effectiveUserId, newUsername) { success ->
                                usernameLoading = false
                                usernameMessage = if (success) {
                                    onUsernameUpdated(newUsername) // bunu ekle
                                    Pair(true, "Kullanıcı adı güncellendi! ✓")
                                } else {
                                    Pair(false, "Bu kullanıcı adı zaten kullanımda")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !usernameLoading
                    ) {
                        if (usernameLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Kullanıcı Adını Güncelle", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                // ===== ŞİFRE =====
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF7C3AED).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Text("Şifre Değiştir", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Hesap güvenliğini güncelle", fontSize = 12.sp, color = SlateText)
                        }
                    }

                    // Mevcut şifre
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            passwordMessage = null
                        },
                        label = { Text("Mevcut şifre", color = SlateText) },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = SlateText, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    if (currentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null,
                                    tint = SlateText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7C3AED),
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF7C3AED),
                            focusedLabelColor = Color(0xFF7C3AED),
                            unfocusedLabelColor = SlateText
                        ),
                        singleLine = true
                    )

                    // Yeni şifre
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            passwordMessage = null
                        },
                        label = { Text("Yeni şifre", color = SlateText) },
                        leadingIcon = {
                            Icon(Icons.Filled.LockOpen, contentDescription = null, tint = SlateText, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null,
                                    tint = SlateText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7C3AED),
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF7C3AED),
                            focusedLabelColor = Color(0xFF7C3AED),
                            unfocusedLabelColor = SlateText
                        ),
                        singleLine = true
                    )

                    // Şifre tekrar
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            passwordMessage = null
                        },
                        label = { Text("Yeni şifre tekrar", color = SlateText) },
                        leadingIcon = {
                            Icon(Icons.Filled.LockOpen, contentDescription = null, tint = SlateText, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null,
                                    tint = SlateText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7C3AED),
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF7C3AED),
                            focusedLabelColor = Color(0xFF7C3AED),
                            unfocusedLabelColor = SlateText
                        ),
                        singleLine = true
                    )

                    // Şifre gücü göstergesi
                    if (newPassword.isNotEmpty()) {
                        val strength = when {
                            newPassword.length < 6 -> Pair(0.2f, "Çok zayıf")
                            newPassword.length < 8 -> Pair(0.4f, "Zayıf")
                            newPassword.length < 10 && !newPassword.any { it.isDigit() } -> Pair(0.6f, "Orta")
                            newPassword.length >= 8 && newPassword.any { it.isDigit() } && newPassword.any { it.isUpperCase() } -> Pair(1f, "Güçlü")
                            else -> Pair(0.7f, "İyi")
                        }
                        val strengthColor = when (strength.first) {
                            0.2f -> Color.Red
                            0.4f -> Color(0xFFEA580C)
                            0.6f -> Color(0xFFFBBF24)
                            0.7f -> Color(0xFF84CC16)
                            else -> SuccessGreen
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LinearProgressIndicator(
                                progress = { strength.first },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                color = strengthColor,
                                trackColor = SlateBorder
                            )
                            Text("Şifre gücü: ${strength.second}", fontSize = 11.sp, color = strengthColor)
                        }
                    }

                    // Mesaj
                    passwordMessage?.let { (success, msg) ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (success) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                contentDescription = null,
                                tint = if (success) SuccessGreen else Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(msg, fontSize = 13.sp, color = if (success) SuccessGreen else Color.Red)
                        }
                    }

                    Button(
                        onClick = {
                            when {
                                currentPassword.isBlank() -> {
                                    passwordMessage = Pair(false, "Mevcut şifre boş olamaz")
                                    return@Button
                                }
                                newPassword.isBlank() -> {
                                    passwordMessage = Pair(false, "Yeni şifre boş olamaz")
                                    return@Button
                                }
                                newPassword.length < 6 -> {
                                    passwordMessage = Pair(false, "Şifre en az 6 karakter olmalı")
                                    return@Button
                                }
                                newPassword != confirmPassword -> {
                                    passwordMessage = Pair(false, "Şifreler eşleşmiyor")
                                    return@Button
                                }
                                newPassword == currentPassword -> {
                                    passwordMessage = Pair(false, "Yeni şifre eski şifre ile aynı olamaz")
                                    return@Button
                                }
                            }
                            passwordLoading = true
                            viewModel.updatePassword(effectiveUserId, currentPassword, newPassword) { success ->
                                passwordLoading = false
                                if (success) {
                                    passwordMessage = Pair(true, "Şifre başarıyla güncellendi! ✓")
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                } else {
                                    passwordMessage = Pair(false, "Mevcut şifre yanlış")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        enabled = !passwordLoading
                    ) {
                        if (passwordLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Şifreyi Güncelle", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}