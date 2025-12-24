package com.avis.app.ptalk.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.avis.app.ptalk.navigation.Route
import com.avis.app.ptalk.ui.viewmodel.AuthEvent
import com.avis.app.ptalk.ui.viewmodel.VMAuth

@Composable
fun LoginScreen(
    navController: NavController,
    vm: VMAuth = viewModel()
) {
    val uiState = vm.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is AuthEvent.LoginSuccess -> {

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
            .background(Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PTalk",
                style = TextStyle(
                    fontSize = 128.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFF06A6A) // pink-ish red like screenshot
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFFFF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 24.dp),
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
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEDEDED),
                        unfocusedContainerColor = Color(0xFFEDEDED),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
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
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEDEDED),
                        unfocusedContainerColor = Color(0xFFEDEDED),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { },
                    enabled = true,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.55f),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Đăng nhập", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                SignupPromptAnnotated(
                    onClickSignUp = {
                        navController.navigate(Route.SIGNUP)
                    }
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Quên mật khẩu",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}

@Composable
fun SignupPromptAnnotated(onClickSignUp: () -> Unit) {
    val annotated = buildAnnotatedString {
        append("Bạn chưa có tài khoản vui lòng bấm ")
        val start = length
        append("Đăng ký")
        addStyle(
            style = SpanStyle(
                color = Color.Red,
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
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.clickable {
            annotated.getStringAnnotations(tag = "signup", start = 0, end = annotated.length)
            onClickSignUp()
        },
    )
}