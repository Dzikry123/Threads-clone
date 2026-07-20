package com.example.socialapp.ui.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.utils.GoogleAuthManager
import com.example.core.utils.SupabaseConfig
import com.example.socialapp.ui.viewmodel.AuthViewModel
import com.example.socialapp.ui.viewmodel.LoginUiState
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val googleAuthManager = remember { GoogleAuthManager(context) }
    val uiState by viewModel.uiState.collectAsState()

    // 1. Gunakan CoroutineScope bawaan Compose yang aman terhadap Lifecycle UI
    // untuk mengatasi behavior close app yang aneh saat bottomSheet google login terbuka
    val scope = rememberCoroutineScope()

    LoginContent(
        uiState = uiState,
        viewModel = viewModel,
        onLoginClick = { email, password ->
            viewModel.loginWithEmail(email, password)
        },
        onGoogleLoginClick = {
            scope.launch {
                try {
                    val idToken = googleAuthManager.signIn(
                        webClientId = SupabaseConfig.GOOGLE_WEB_CLIENT_ID
                    )
                    if (idToken != null) {
                        viewModel.loginWithGoogle(idToken)
                        Log.d("Google Click", "$idToken")
                    }
                } catch (e: Exception) {
                    // 3. Tangkap pembatalan (tombol back / X) agar tidak menghancurkan Activity aplikasi
                    Log.e("Google Auth", "User cancelled or error occurred: ${e.localizedMessage}")
                }
            }
        }
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onLoginClick: (String, String) -> Unit,
    onGoogleLoginClick: () -> Unit,
    viewModel: AuthViewModel
) {
    // FIX: passwordValue sekarang membaca passwordInput, bukan emailInput
    val emailValue by viewModel.emailInput.collectAsState()
    val passwordValue by viewModel.passwordInput.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Konten Utama
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.4f))

            // Simbol / Nama Aplikasi ala Threads (Ganti dengan R.drawable.ic_threads jika ada)
            Text(
                text = "Threads",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1).sp,
                    fontSize = 36.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Log in with your email account",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Input Email
            TextField(
                value = emailValue,
                onValueChange = { viewModel.updateEmailInput(it) },
                placeholder = { Text("Email or username", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Input Password
            TextField(
                value = passwordValue,
                onValueChange = { viewModel.updatePasswordInput(it) },
                placeholder = { Text("Password", color = Color.Gray) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tombol Log In Utama
            Button(
                onClick = { onLoginClick(emailValue, passwordValue) },
                enabled = !uiState.isLoading && emailValue.isNotBlank() && passwordValue.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Log in", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            // Error message panel
            uiState.error?.let {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Invalid credentials. Please try again.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Tombol Google diletakkan di bagian paling bawah layar secara elegan
            OutlinedButton(
                onClick = { onGoogleLoginClick() },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 0.5.dp,
                    color = if (uiState.isLoading) Color.LightGray.copy(alpha = 0.3f) else Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp) // Ditinggikan sedikit menjadi 66.dp karena ada padding bottom 16.dp agar tinggi tombolnya tetap 50.dp
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Sign in with Google",
                    color = if (uiState.isLoading) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Loading Overlay
        if (uiState.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.15f))
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground, strokeWidth = 2.dp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPrev() {
    LoginContent(
        uiState = LoginUiState(),
        onLoginClick = { _, _ -> },
        onGoogleLoginClick = {},
        viewModel = viewModel()
    )
}