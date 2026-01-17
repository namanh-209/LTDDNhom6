package com.example.bookstore.Screen

import android.icu.text.NumberFormat
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import com.example.bookstore.Model.SachtrongGioHang
import kotlinx.coroutines.launch
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Scope để chạy coroutine khi bấm nút
    var danhSachDon by remember { mutableStateOf<List<LichSuDonHang>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }

    // Hàm tải danh sách (Tách ra để tái sử dụng khi cần refresh)
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

    // Hàm xử lý hủy đơn
    fun xuLyHuyDon(maDonHang: Int) {
        scope.launch {
            try {
                val res = RetrofitClient.api.huyDonHang(maDonHang)
                if (res.body()?.status == "success") {
                    Toast.makeText(context, "Đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show()
                    taiDanhSach() // Tải lại danh sách để cập nhật trạng thái mới
                } else {
                    Toast.makeText(context, "Không thể hủy đơn này", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
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
            Text("Đang tải...", modifier = Modifier.padding(paddingValues).padding(16.dp))
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
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                items(danhSachDon) { don ->
                    ItemDonHang(
                        navController = navController,
                        don = don,
                        onHuyClick = { maDon -> xuLyHuyDon(maDon) } // Truyền hàm hủy xuống dưới
                    )
                }
            }
        }
    }
}

@Composable
fun ItemDonHang(
    navController: NavController,
    don: LichSuDonHang,
    onHuyClick: (Int) -> Unit // Nhận hàm callback hủy
) {
    Card(
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 10.dp) // Thêm padding ngang cho đẹp
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // TRẠNG THÁI ĐƠN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // Đẩy sang 2 bên
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "Mã đơn: #${don.MaDonHang}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                val (mauTrangThai, textTrangThai) = when (don.TrangThai) {
                    "MoiDat" -> Color(0xFF005985) to "Chờ xác nhận" // Thêm trạng thái này
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

            // THÔNG TIN SÁCH
            Row {
                AsyncImage(
                    model = don.AnhBia,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
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

            // TỔNG TIỀN
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

            Spacer(modifier = Modifier.height(12.dp))

            // NÚT CHỨC NĂNG
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // === NÚT HỦY ĐƠN (Chỉ hiện khi Mới đặt hoặc Đang xử lý) ===
                if (don.TrangThai == "MoiDat" || don.TrangThai == "DangXuLy") {
                    Button(
                        onClick = { onHuyClick(don.MaDonHang) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Red
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Hủy đơn", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                // ========================================================

                // MUA LẠI
                Button(
                    onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
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
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D71A3)),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text("Mua lại", fontSize = 12.sp)
                }

                // ĐÁNH GIÁ (Chỉ khi Hoàn thành)
                if (don.TrangThai == "HoanThanh") {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            navController.navigate("danhgia/${don.MaSach}/${don.MaDonHang}")
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2994A)),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Đánh giá", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}