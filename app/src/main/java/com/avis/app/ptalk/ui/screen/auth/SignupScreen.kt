package com.avis.app.ptalk.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.avis.app.ptalk.navigation.Route
import com.avis.app.ptalk.ui.component.dialog.ForgotPasswordDialog
import com.avis.app.ptalk.ui.viewmodel.AuthEvent
import com.avis.app.ptalk.ui.viewmodel.AuthMode
import com.avis.app.ptalk.ui.viewmodel.VMAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    navController: NavController,
    vm: VMAuth = viewModel()
) {
    val uiState = vm.uiState.collectAsStateWithLifecycle().value
    vm.setMode(AuthMode.SIGNUP)

    var showForgotPassword by remember { mutableStateOf(false) }
    if (showForgotPassword) {
        ForgotPasswordDialog(
            onDismiss = {showForgotPassword = false},
            onSendCode = {},
        )
    }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is AuthEvent.SignupSuccess -> {

                }
                is AuthEvent.ShowError -> {

                }
                else -> Unit
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Quay lại trang đăng nhập",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Tên tài khoản",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.username,
                onValueChange = { vm.onUsernameChanged(value = it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Email/Phone") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(22.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mật khẩu",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { vm.onPasswordChanged(value = it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nhập mật khẩu") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if(uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { vm.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if(uiState.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Show password"
                        )
                    }
                },
                shape = RoundedCornerShape(22.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nhập lại mật khẩu",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { vm.onConfirmPasswordChanged(value = it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nhập mật khẩu") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if(uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { vm.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if(uiState.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Show password"
                        )
                    }
                },
                shape = RoundedCornerShape(22.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                enabled = true,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.55f),
                contentPadding = PaddingValues(vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Đăng ký", fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LoginPromptAnnotated {
                navController.popBackStack(Route.SIGNUP, inclusive = true)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Quên mật khẩu",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable { showForgotPassword = true }
            )
        }
    }
}

@Composable
fun LoginPromptAnnotated(onClickLogin: () -> Unit) {
    val annotated = buildAnnotatedString {
        append("Bạn đã có tài khoản vui lòng bấm ")
        val start = length
        append("Đăng nhập")
        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.inversePrimary,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline
            ),
            start = start,
            end = length
        )
        addStringAnnotation(tag = "signup", annotation = "signup", start = start, end = length)
    }
    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.clickable {
            annotated.getStringAnnotations(tag = "signup", start = 0, end = annotated.length)
            onClickLogin()
        },
    )
}