package com.example.bookstore.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookstore.Api.RetrofitClient // Giả sử bạn đã có object này
import com.example.bookstore.Model.DangKi
import com.example.bookstore.R
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit
) {
    // Các biến trạng thái lưu dữ liệu nhập
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") } // Đây là ô Email/SĐT
    var password by remember { mutableStateOf("") }

    // Biến hỗ trợ
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) } // Trạng thái loading khi gọi API

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(250.dp)
        )

        // --- Nhập Tên ---
        Text(text = "Tên", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nhập tên") },
            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Nhập Email/SĐT ---
        Text(text = "Email/SĐT", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            label = { Text("Nhập email / số điện thoại") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Nhập Mật Khẩu ---
        Text(text = "Mật khẩu", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nhập mật khẩu") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(), // Ẩn password
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Nút Đăng Ký ---
        Button(
            onClick = {
                // 1. Kiểm tra rỗng
                if (name.isBlank() || contact.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // 2. Xử lý logic tách Email và SĐT
                val isEmail = contact.contains("@")
                val emailToSend = if (isEmail) contact else ""
                val phoneToSend = if (!isEmail) contact else ""

                // 3. Gọi API
                isLoading = true
                scope.launch {
                    try {
                        val request = DangKi(
                            HoTen = name,
                            Email = emailToSend,
                            SoDienThoai = phoneToSend,
                            MatKhau = password
                        )

                        // Gọi hàm dangKy đã định nghĩa trong ApiService
                        val response = RetrofitClient.api.dangKy(request)

                        if (response.status == "success") { // Kiểm tra status trả về từ Server
                            Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                            onLoginClick() // Chuyển về màn hình đăng nhập
                        } else {
                            // Backend trả về message lỗi (ví dụ: SĐT trùng)
                            Toast.makeText(context, "Lỗi: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D71A3)),
            enabled = !isLoading // Vô hiệu hóa nút khi đang loading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Đăng ký", fontSize = 20.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Chuyển sang Đăng Nhập ---
        Text(
            text = "Đã có tài khoản? Đăng nhập",
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clickable { onLoginClick() },
            textAlign = TextAlign.Center
        )
    }
}