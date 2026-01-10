package com.example.bookstore.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookstore.Components.BienDungChung

@Composable
fun ChinhSuaThongTin(
   //navController: NavController
    onBackClick: () -> Unit
) {
    val user = BienDungChung.userHienTai

    var hoTen by remember { mutableStateOf(user?.HoTen ?: "") }
    var soDienThoai by remember { mutableStateOf(user?.SoDienThoai ?: "") }
    var email by remember { mutableStateOf(user?.Email ?: "") }
    var gioiTinh by remember { mutableStateOf(user?.GioiTinh ?: "Nu") }
    var ngaySinh by remember { mutableStateOf(user?.NgaySinh ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        /// ===== TOP BAR =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3572A5))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { //navController.popBackStack()
                         }
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = "Chỉnh sửa thông tin",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(24.dp))

        /// ===== AVATAR =====
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(Color(0xFFE0E0E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!user?.AnhDaiDien.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user!!.AnhDaiDien)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Thay đổi ảnh", color = Color.Gray)
        }

        Spacer(Modifier.height(24.dp))

        /// ===== FORM =====
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(16.dp)) {

                Text("Thông tin cá nhân", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(16.dp))

                EditField("Họ và tên", hoTen) { hoTen = it }
                EditField("Số điện thoại", soDienThoai) { soDienThoai = it }
                EditField("Email", email) { email = it }

                Spacer(Modifier.height(8.dp))

                Text("Giới tính", color = Color.Gray, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gioiTinh == "Nam",
                        onClick = { gioiTinh = "Nam" }
                    )
                    Text("Nam")

                    Spacer(Modifier.width(16.dp))

                    RadioButton(
                        selected = gioiTinh == "Nu",
                        onClick = { gioiTinh = "Nu" }
                    )
                    Text("Nữ")
                }

                EditField("Ngày sinh", ngaySinh) { ngaySinh = it }
            }
        }

        Spacer(Modifier.height(32.dp))

        /// ===== BUTTON SAVE =====
        Button(
            onClick = {
                // TODO: Gọi API cập nhật thông tin
                // Sau khi update thành công:
                //navController.popBackStack()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3572A5))
        ) {
            Text("Lưu thông tin", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            singleLine = true
        )
    }
}
