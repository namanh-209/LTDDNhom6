package com.example.bookstore.Screen

import android.icu.text.NumberFormat
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.LichSuDonHang
import com.example.bookstore.Components.BienDungChung
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ManHinhLichSuMuaHang(
    navController: NavController,
    onBackClick: (() -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    var danhSachDon by remember { mutableStateOf<List<LichSuDonHang>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }

    // Hàm tải danh sách
    fun taiDanhSach() {
        scope.launch {
            dangTai = true
            try {
                val userId = BienDungChung.userHienTai?.MaNguoiDung
                if (userId != null) {
                    val res = RetrofitClient.api.getLichSuMuaHang(userId = userId)
                    if (res.status == "success") {
                        danhSachDon = res.data ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                Log.e("API", e.message ?: "Lỗi API")
            } finally {
                dangTai = false
            }
        }
    }

    // Gọi tải danh sách lần đầu
    LaunchedEffect(Unit) {
        taiDanhSach()
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
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (danhSachDon.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Bạn chưa có đơn hàng nào",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(danhSachDon) { don ->
                    ItemDonHang(
                        navController = navController,
                        don = don
                    )
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
    // Hàm format tiền
    fun formatGia(gia: Double): String {
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(gia)} VND"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                //click chuyển trang sanh chi tiết
                navController.navigate("chitietdonhang/${don.MaDonHang}/${don.TrangThai}")
            },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            //trạng thái đơn
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mã đơn: #${don.MaDonHang}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                val (mauTrangThai, textTrangThai) = when (don.TrangThai) {
                    "MoiDat" -> Color(0xFF005985) to "Chờ xác nhận"
                    "DangXuLy" -> Color(0xFF005985) to "Chờ Xử Lý"
                    "HoanThanh" -> Color(0xFF2E7D32) to "Hoàn thành"
                    "DangGiao" -> Color(0xFFF2994A) to "Đang giao"
                    "DaHuy", "Huy" -> Color.Red to "Đã hủy"
                    else -> Color.Black to don.TrangThai
                }

                Text(
                    text = textTrangThai,
                    color = mauTrangThai,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            //thông tin sách
            Row {
                AsyncImage(
                    model = don.AnhBia,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(don.TenSach, fontWeight = FontWeight.Bold, maxLines = 2)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formatGia(don.GiaBan),
                        color = Color(0xFFFF5722),
                        fontWeight = FontWeight.Bold
                    )
                    Text("Số lượng: x${don.SoLuong}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            //tổng tiền
            val tongTien = don.GiaBan * don.SoLuong
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(text = "Thành tiền: ", fontSize = 14.sp)
                Text(
                    text = formatGia(tongTien),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFFF5722)
                )
            }


        }
    }
}