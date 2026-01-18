package com.example.bookstore.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.bookstore.Model.DangKi
import com.example.bookstore.R
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit
) {
    // Các biến trạng thái lưu dữ liệu nhập
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // [MỚI] Biến trạng thái ẩn/hiện mật khẩu
    var passwordVisible by remember { mutableStateOf(false) }

    // Biến hỗ trợ
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

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
        Text(text = "Tên hiển thị", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nhập tên của bạn") },
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
            label = { Text("Nhập email hoặc số điện thoại") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Nhập Mật Khẩu (CÓ NÚT MẮT) ---
        Text(text = "Mật khẩu", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nhập mật khẩu") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },

            // [MỚI] Xử lý ẩn hiện mật khẩu
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

        Spacer(modifier = Modifier.height(24.dp))

        // --- Nút Đăng Ký (CÓ VALIDATE) ---
        Button(
            onClick = {
                // 1. Kiểm tra rỗng
                if (name.isBlank() || contact.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // 2. Kiểm tra định dạng SĐT hoặc Email
                val isNumber = contact.all { it.isDigit() } // Kiểm tra có phải toàn số không

                if (isNumber) {
                    // ==> NẾU LÀ SỐ: Phải đúng 10 ký tự
                    if (contact.length != 10) {
                        Toast.makeText(context, "Số điện thoại phải bao gồm đúng 10 chữ số!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                } else {
                    // ==> NẾU LÀ CHỮ: Phải đúng định dạng Email
                    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(contact).matches()
                    if (!isEmailValid) {
                        Toast.makeText(context, "Email không đúng định dạng!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                }

                // 3. Chuẩn bị dữ liệu gửi API
                val emailToSend = if (!isNumber) contact else ""
                val phoneToSend = if (isNumber) contact else ""

                // 4. Gọi API Đăng ký
                isLoading = true
                scope.launch {
                    try {
                        val request = DangKi(
                            HoTen = name,
                            Email = emailToSend,
                            SoDienThoai = phoneToSend,
                            MatKhau = password
                        )

                        val response = RetrofitClient.api.dangKy(request)

                        if (response.status == "success") {
                            Toast.makeText(context, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show()
                            onLoginClick() // Chuyển về màn hình đăng nhập
                        } else {
                            Toast.makeText(context, "Đăng ký thất bại: ${response.message}", Toast.LENGTH_SHORT).show()
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
            enabled = !isLoading
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