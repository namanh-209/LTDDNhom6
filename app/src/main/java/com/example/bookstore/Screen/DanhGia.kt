package com.example.bookstore.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.DanhGiaRequest
import kotlinx.coroutines.launch

@Composable
fun DanhGia(
    navController: NavController,
    onBackClick: () -> Unit,
    maSach: Int,
    maDonHang: Int
) {
    var soSao by remember { mutableStateOf(5) }
    var noiDung by remember { mutableStateOf("") }

    var dangGui by remember { mutableStateOf(false) }              // 🔒 Disable nút
    var hienDialogThanhCong by remember { mutableStateOf(false) } // 🎉 Dialog

    val scope = rememberCoroutineScope()

    if (hienDialogThanhCong) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "Đánh giá thành công 🎉",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Cảm ơn bạn đã đánh giá sản phẩm.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        hienDialogThanhCong = false
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    KhungGiaoDien(
        tieuDe = "Đánh giá",
        onBackClick = onBackClick,
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = { navController.navigate("khuyenmai") },
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Bạn cảm thấy sản phẩm thế nào?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ⭐ Chọn sao
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 1..5) {
                    IconButton(
                        enabled = !dangGui,
                        onClick = { soSao = i }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (i <= soSao) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = noiDung,
                onValueChange = { noiDung = it },
                enabled = !dangGui,
                label = { Text("Nhận xét của bạn") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                enabled = !dangGui,
                onClick = {
                    if (noiDung.isBlank()) return@Button

                    scope.launch {
                        dangGui = true
                        try {
                            val response = RetrofitClient.api.guiDanhGia(
                                DanhGiaRequest(
                                    MaNguoiDung = BienDungChung.userHienTai!!.MaNguoiDung,
                                    MaSach = maSach,
                                    MaDonHang = maDonHang,
                                    SoSao = soSao,
                                    BinhLuan = noiDung
                                )
                            )

                            if (response.status == "success") {
                                hienDialogThanhCong = true
                            } else {
                                dangGui = false
                            }

                        } catch (e: Exception) {
                            dangGui = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                )
            ) {
                if (dangGui) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Gửi đánh giá",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
