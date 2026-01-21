package com.example.bookstore.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.Model.DangNhap
import com.example.bookstore.Model.User
import com.example.bookstore.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: (User) -> Unit
) {
    var contactInput by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(250.dp)
        )

        Text(
            text = "Email/Sđt",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        OutlinedTextField(
            value = contactInput,
            onValueChange = { contactInput = it },
            label = { Text("Nhập email/ số điện thoại") },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mật khẩu",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nhập mật khẩu") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {

                //Kiểm tra để trống
                if (contactInput.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                //Kiểm tra định dạng Email/Sdt
                val isNumber = contactInput.all { it.isDigit() } // Kiểm tra có phải toàn số không

                if (isNumber) {//nếu toàn số
                    if (contactInput.length != 10) {
                        Toast.makeText(context, "Số điện thoại phải bao gồm đúng 10 chữ số!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                } else {//nếu email
                    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(contactInput).matches()
                    if (!isEmailValid) {
                        Toast.makeText(context, "Email không đúng định dạng (ví dụ: abc@gmail.com)!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                }

              //thoả các đkiện thì gọi api
                scope.launch {
                    try {
                        val req = DangNhap(contact = contactInput, password = password)
                        val res = RetrofitClient.api.dangNhap(req)

                        if (res.status == "success" && res.data != null) {
                            val user = res.data
                            BienDungChung.userHienTai = user
                            Toast.makeText(context, "Xin chào ${user.HoTen}", Toast.LENGTH_SHORT).show()
                            onLoginSuccess(user)
                        } else {
                            Toast.makeText(context, res.message ?: "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.width(200.dp).height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D71A3))
        ) {
            Text("Đăng nhập", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bạn chưa có tài khoản? Đăng ký ngay",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }
}