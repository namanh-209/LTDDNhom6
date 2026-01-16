package com.example.bookstore.Screen


import android.icu.text.NumberFormat
import android.util.Log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.LichSuDonHang
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.Model.SachtrongGioHang
import java.util.Locale




fun formatGia(gia: Double): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${formatter.format(gia)} VND"
}
@Composable
fun ManHinhLichSuMuaHang(
    navController: NavController,
    onBackClick: (() -> Unit)? = null,
) {
    var danhSachDon: List<LichSuDonHang> by remember { mutableStateOf<List<LichSuDonHang>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val userId = BienDungChung.userHienTai?.MaNguoiDung
                ?: return@LaunchedEffect

            val res = RetrofitClient.api.getLichSuMuaHang(userId = userId)
            if (res.status == "success") {
                danhSachDon = res.data ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("API", e.message ?: "Lỗi API")
        } finally {
            dangTai = false
        }
    }


    KhungGiaoDien(
        tieuDe = "Lịch sử mua hàng",
        onBackClick = onBackClick,
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = { navController.navigate("khuyenmai") },
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { paddingValues ->

        if (dangTai) {
            Text("Đang tải...", modifier = Modifier.padding(paddingValues))
        } else if (danhSachDon.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Bạn chưa có đơn hàng nào",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                items(danhSachDon) { don ->
                    ItemDonHang(navController,don)
                }
            }
        }
    }
}

@Composable
fun ItemDonHang(
    navController: NavController,
    don: LichSuDonHang
) {
    Card(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // TRẠNG THÁI ĐƠN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val (mauTrangThai, textTrangThai) = when (don.TrangThai) {
                    "HoanThanh" -> Color(0xFF2E7D32) to "Hoàn thành"
                    "DangGiao" -> Color(0xFFF2994A) to "Đang giao"
                    "DaHuy" -> Color.Gray to "Đã hủy"
                    else -> Color.Black to don.TrangThai
                }

                Text(
                    text = textTrangThai,
                    color = mauTrangThai,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            //  THÔNG TIN SÁCH
            Row {
                AsyncImage(
                    model = don.AnhBia,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(don.TenSach, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formatGia(don.GiaBan),
                        color = Color(0xFFFF5722),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            //  TỔNG TIỀN
            val tongTien = don.GiaBan * don.SoLuong

            Text(
                text = "Tổng tiền (${don.SoLuong} sản phẩm): ${formatGia(tongTien)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )


            Spacer(modifier = Modifier.height(8.dp))

            // NÚT CHỨC NĂNG
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                // MUA LẠI  CHUYỂN SANG THANH TOÁN
                Button(
                    onClick = {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set(
                                "mua_lai_san_pham",
                                listOf(
                                    SachtrongGioHang(
                                        MaGioHang = 0,
                                        MaSach = don.MaSach,
                                        TenSach = don.TenSach,
                                        AnhBia = don.AnhBia,
                                        GiaBan = don.GiaBan,
                                        SoLuong = 1,
                                        TenTacGia = ""
                                    )
                                )
                            )

                        navController.navigate("thanhtoan")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Mua lại", color = Color.Black)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // ĐÁNH GIÁ – CHỈ KHI HOÀN THÀNH
                if (don.TrangThai == "HoanThanh") {
                    Button(
                        onClick = {
                            navController.navigate(
                                "danhgia/${don.MaSach}/${don.MaDonHang}"
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF2994A)
                        )
                    ) {
                        Text("Đánh giá", color = Color.White)
                    }
                }
            }
        }
    }
}
