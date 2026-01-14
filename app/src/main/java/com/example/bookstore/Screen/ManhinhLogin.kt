package com.example.bookstore.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.Model.DangNhap
import com.example.bookstore.Model.User // Đảm bảo import model NguoiDung
import com.example.bookstore.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    // THAY ĐỔI 1: Callback nhận vào một NguoiDung
    onLoginSuccess: (User) -> Unit
) {
    var contactInput by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (contactInput.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Nhập thiếu thông tin!", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        try {
                            val req = DangNhap(contact = contactInput, password = password)
                            val res = RetrofitClient.api.dangNhap(req)

                            if (res.status == "success" && res.data != null) {
                                // Lấy user từ response
                                val user = res.data

                                // Lưu thông tin User vào biến dùng chung
                                BienDungChung.userHienTai = user
                                Toast.makeText(context, "Xin chào ${user.HoTen}", Toast.LENGTH_SHORT).show() // Lưu ý: hoTen viết thường theo Model chuẩn camelCase

                                // THAY ĐỔI 2: Truyền user ra ngoài để NavGraph điều hướng
                                onLoginSuccess(user)
                            } else {
                                Toast.makeText(context, res.message ?: "Lỗi đăng nhập", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
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