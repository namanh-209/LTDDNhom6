package com.example.bookstore.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.MauXanh
import com.example.bookstore.Model.DoiMatKhau

import com.example.bookstore.R
import kotlinx.coroutines.launch

@Composable
fun ManHinhThayDoiMatKhau(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. Tạo biến trạng thái ở đây để Nút Lưu có thể đọc được
    var matKhauCu by remember { mutableStateOf("") }
    var matKhauMoi by remember { mutableStateOf("") }
    var xacNhanMatKhau by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Trạng thái đang tải

    KhungGiaoDien(
        tieuDe = "Thay đổi mật khẩu",
        onBackClick = onBackClick,
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = R.drawable.icon_doi_mat_khau), // Đảm bảo có icon này
                contentDescription = "Logo",
                modifier = Modifier.size(170.dp),
                colorFilter = ColorFilter.tint(Color(0xFF555555))
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 2. Truyền biến và hàm thay đổi vào ô nhập
            ONhapMatKhau(
                tieuDe = "Mật khẩu hiện tại",
                goiY = "Nhập mật khẩu hiện tại",
                value = matKhauCu,
                onValueChange = { matKhauCu = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            ONhapMatKhau(
                tieuDe = "Mật khẩu mới",
                goiY = "Nhập mật khẩu mới",
                value = matKhauMoi,
                onValueChange = { matKhauMoi = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            ONhapMatKhau(
                tieuDe = "Xác nhận mật khẩu",
                goiY = "Nhập lại mật khẩu mới",
                value = xacNhanMatKhau,
                onValueChange = { xacNhanMatKhau = it }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    // 3. LOGIC XỬ LÝ KHI BẤM LƯU
                    if (matKhauCu.isEmpty() || matKhauMoi.isEmpty() || xacNhanMatKhau.isEmpty()) {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    } else if (matKhauMoi != xacNhanMatKhau) {
                        Toast.makeText(context, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
                    } else {
                        // Gọi API
                        scope.launch {
                            isLoading = true
                            try {
                                val userId = BienDungChung.userHienTai?.MaNguoiDung ?: 0
                                val request = DoiMatKhau(userId, matKhauCu, matKhauMoi)

                                val response = RetrofitClient.api.doiMatKhau(request)

                                if (response.status == "success") {
                                    Toast.makeText(context, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                                    onBackClick() // Quay lại màn hình trước
                                } else {
                                    Toast.makeText(context, response.message ?: "Thất bại", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Màu xanh lá
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(160.dp)
                    .height(50.dp),
                enabled = !isLoading // Vô hiệu hóa nút khi đang tải
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Lưu", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// 4. SỬA HÀM NÀY: Thêm tham số value và onValueChange
@Composable
fun ONhapMatKhau(
    tieuDe: String,
    goiY: String,
    value: String,              // <-- Thêm
    onValueChange: (String) -> Unit // <-- Thêm
) {
    // XÓA dòng: var text by remember { mutableStateOf("") } đi vì ta dùng biến của cha truyền vào

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = tieuDe,
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )

        OutlinedTextField(
            value = value,              // <-- Dùng biến truyền vào
            onValueChange = onValueChange, // <-- Gọi hàm truyền vào
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = goiY, color = Color.Gray, fontSize = 14.sp) },
            singleLine = true,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.o_khoa_nho), // Đảm bảo có icon
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
            },

            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = Color.Green,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}