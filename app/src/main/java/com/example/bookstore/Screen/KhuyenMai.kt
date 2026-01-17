package com.example.bookstore.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.KhuyenMai
import com.example.bookstore.formatTienTe
import kotlinx.coroutines.launch

@Composable
fun KhuyenMai(navController: NavController,onBackClick: () -> Unit) {
    val tongTienGioHang = remember {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<Int>("tongTienGioHang") ?: 0
    }
    val scope = rememberCoroutineScope()
    var danhSach by remember { mutableStateOf<List<KhuyenMai>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }

    // ===== LOAD DANH SÁCH KHUYẾN MÃI =====
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val res = RetrofitClient.api.layDanhSachKhuyenMai()
                if (res.status == "success") {
                    danhSach = res.data ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                dangTai = false
            }
        }
    }

    KhungGiaoDien(
        tieuDe = "Khuyến mãi",
        onBackClick = onBackClick,
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = { navController.navigate("khuyenmai") }
    ) { paddingValues ->

        if (dangTai) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(danhSach) { km ->
                    ItemKhuyenMai(
                        km = km,
                        tongTienGioHang = tongTienGioHang,
                        onDung = {
                            // ✅ GỬI VỀ MÀN TRƯỚC (THANH TOÁN)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("khuyenMaiDaChon", km)

                            // QUAY LẠI THANH TOÁN
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemKhuyenMai(
    km: KhuyenMai,
    tongTienGioHang: Int, // Thêm tham số này
    onDung: () -> Unit
) {
    // 1. Kiểm tra điều kiện: Tổng tiền hàng có lớn hơn hoặc bằng Đơn tối thiểu không
    val donToiThieu = km.DonToiThieu ?: 0.0
    val duDieuKien = tongTienGioHang >= donToiThieu

    // 2. Màu sắc: Nếu đủ điều kiện thì màu Xanh, không thì màu Xám
    val cardColor = if (duDieuKien) Color(0xFFE3F3FB) else Color(0xFFF5F5F5)
    val contentColor = if (duDieuKien) Color.Black else Color.Gray
    val alpha = if (duDieuKien) 1f else 0.6f // Độ mờ

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Thêm chút khoảng cách
            .then(if (!duDieuKien) Modifier else Modifier) // Có thể thêm hiệu ứng nếu cần
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                // Nếu không đủ điều kiện, làm mờ toàn bộ nội dung bên trong
                .alpha(alpha)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Mã: ${km.MaCode}",
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                // Hiển thị trạng thái chưa đủ điều kiện
                if (!duDieuKien) {
                    Text(
                        "Chưa đủ ĐK",
                        fontSize = 12.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                "Giảm ${formatTienTe(km.GiaTriGiam)}",
                fontWeight = FontWeight.Bold,
                color = if (duDieuKien) Color.Red else Color.Gray // Đổi màu tiền giảm
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Đơn tối thiểu: ${formatTienTe(donToiThieu)}",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor
            )

            Spacer(Modifier.height(10.dp))

            // Nút bấm
            Button(
                onClick = onDung,
                enabled = duDieuKien, // <--- KHÓA NÚT NẾU KHÔNG ĐỦ ĐIỀU KIỆN
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBFDCEA),
                    contentColor = Color.Black,
                    disabledContainerColor = Color.LightGray, // Màu nền khi bị khóa
                    disabledContentColor = Color.White        // Màu chữ khi bị khóa
                )
            ) {
                Text(if (duDieuKien) "Dùng" else "Mua thêm để dùng")
            }

            Spacer(Modifier.height(6.dp))

            Text(
                "Hạn dùng: ${km.NgayHetHan?.take(10) ?: "Không giới hạn"}",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor
            )
        }
    }
}
