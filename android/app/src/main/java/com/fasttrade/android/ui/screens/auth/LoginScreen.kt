package com.fasttrade.android.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.fasttrade.android.ui.components.*
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.loginLoading.collectAsState()
    val errorMsg by viewModel.loginError.collectAsState()

    // Clear error when user edits
    LaunchedEffect(email, password) { viewModel.clearLoginError() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Title
            Text(
                text = "LOGIN",
                style = MaterialTheme.typography.titleLarge.copy(
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FT",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            FtTextField(
                value = email,
                onValueChange = { email = it },
                label = "E-mail",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            FtTextField(
                value = password,
                onValueChange = { password = it },
                label = "Senha",
                isPassword = !passwordVisible,
                imeAction = ImeAction.Done,
                onImeAction = {
                    if (email.isNotBlank() && password.isNotBlank() && !isLoading) {
                        viewModel.login(email, password, onSuccess = onLoginSuccess)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
            )

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorMessage(message = errorMsg ?: "E-mail ou senha inválidos")
            }

            Spacer(modifier = Modifier.height(24.dp))

            FtButton(
                text = "Entrar",
                onClick = {
                    viewModel.login(email, password, onSuccess = onLoginSuccess)
                },
                loading = isLoading,
                enabled = email.isNotBlank() && password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onForgotPassword) {
                Text(
                    text = "Esqueci minha senha",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Primeiro acesso?", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Clique aqui",
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { /* TODO: navigate to register */ }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }

    val isLoading by viewModel.loginLoading.collectAsState()
    val success by viewModel.forgotPasswordSuccess.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FtTopBar(title = "RECUPERAR SENHA", onBack = onBack)

        Spacer(modifier = Modifier.height(32.dp))

        if (success) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SuccessLight)
                    .padding(16.dp)
            ) {
                Text(
                    text = "✅ Enviamos um código para seu e-mail. Verifique sua caixa de entrada.",
                    color = Success,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = "Esqueceu sua senha?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enviaremos um código de recuperação para seu e-mail.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            FtTextField(value = email, onValueChange = { email = it }, label = "E-mail")
            Spacer(modifier = Modifier.height(24.dp))
            FtButton(
                text = "Enviar código",
                onClick = { viewModel.forgotPassword(email) },
                loading = isLoading,
                enabled = email.isNotBlank()
            )
        }
    }
}
