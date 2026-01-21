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
    //biến trạng thái lưu dữ liệu nhập
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    //biến trạng thái ẩn/hiện mật khẩu
    var passwordVisible by remember { mutableStateOf(false) }

    //các biến hỗ trợ
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

        // Nhập Tên
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

        // Nhập Email/SĐT
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

        // Nhập mk
        Text(text = "Mật khẩu", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nhập mật khẩu") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },

            // xử lý ẩn/ hiện mk
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

        // Nút đky
        Button(
            onClick = {
                // Kiểm tra rỗng
                if (name.isBlank() || contact.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Kiểm tra định dạng SĐT/Email
                val isNumber = contact.all { it.isDigit() }

                if (isNumber) {
                    if (contact.length != 10) {
                        Toast.makeText(context, "Số điện thoại phải bao gồm đúng 10 chữ số!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                } else {
                    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(contact).matches()
                    if (!isEmailValid) {
                        Toast.makeText(context, "Email không đúng định dạng!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                }

                // Dữ liệu gửi API
                val emailToSend = if (!isNumber) contact else ""
                val phoneToSend = if (isNumber) contact else ""

                // Gọi API Đăng ký
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
                            onLoginClick()
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

        // Chuyển sang trang đnhap
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