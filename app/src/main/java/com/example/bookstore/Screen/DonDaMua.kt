package com.example.bookstore.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.DonHangSach
import com.example.bookstore.R
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DonDaMua(navController: NavController, onBackClick: () -> Unit) {


    val tabs = listOf("Chờ xác nhận", "Chờ giao hàng", "Đã giao", "Đã huỷ")


    val maNguoiDung = BienDungChung.userHienTai?.MaNguoiDung ?: 0

    // Các biến trạng thái (State)
    var selectedTab by remember { mutableStateOf(0) }
    var danhSachFull by remember { mutableStateOf<List<DonHangSach>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // Gọi API khi mở màn hình
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // Gọi API lấy lịch sử mua hàng
                val response = RetrofitClient.api.laySachDaMua(maNguoiDung)
                // Lưu dữ liệu trả về (xử lý null safety)
                danhSachFull = response.data ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Vẽ giao diện chính
    KhungGiaoDien(
        tieuDe = "Đơn đã mua",
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
                .background(Color(0xFFF5F5F5)) // Nền xám nhạt
        ) {
            //  Thanh Tab
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Lọc danh sách theo Tab đang chọn
            val danhSachHienThi = when (selectedTab) {
                0 -> danhSachFull.filter { it.TrangThai == "DangXuLy" || it.TrangThai == "MoiDat" } // Gộp cả Mới đặt vào Chờ xác nhận
                1 -> danhSachFull.filter { it.TrangThai == "DangGiao" }
                2 -> danhSachFull.filter { it.TrangThai == "HoanThanh" }
                // Chấp nhận mọi trạng thái có chứa chữ "Huy" hoặc "DaHuy"
                else -> danhSachFull.filter {
                    it.TrangThai == "DaHuy" || it.TrangThai == "Huy"
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hiển thị danh sách hoặc thông báo trống
            if (danhSachHienThi.isEmpty()) {
                ManHinhTrong() // Gọi hàm vẽ màn hình trống
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(danhSachHienThi) { donHang ->
                        // Gọi hàm vẽ từng cuốn sách
                        BookOrderItem(don = donHang, navController = navController)
                    }
                }
            }
        }
    }
}


@Composable
fun BookOrderItem(
    don: DonHangSach,
    navController: NavController,
) {

    val (mauSac, trangThaiText) = when (don.TrangThai) {
        "DangXuLy", "MoiDat" -> Pair(Color(0xFFFFA000), "Chờ xác nhận")
        "DangGiao" -> Pair(Color(0xFF1976D2), "Đang giao")
        "HoanThanh" -> Pair(Color(0xFF388E3C), "Thành công")
        else -> Pair(Color(0xFFD32F2F), "Đã huỷ")
    }

    // Hàm format tiền tệ cục bộ
    fun formatGia(gia: Double): String {
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(gia)} VND"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("chitietdonhang/${don.MaDonHang}/${don.TrangThai}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh bìa
            AsyncImage(
                model = don.AnhBia,
                contentDescription = null,
                modifier = Modifier
                    .width(85.dp)
                    .height(110.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Thông tin chữ bên phải
            Column(modifier = Modifier.weight(1f)) {

                // Badge trạng thái nhỏ
                Surface(
                    color = mauSac.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = trangThaiText,
                        color = mauSac,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Tên sách
                Text(
                    text = don.TenSach,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tác giả
                Text(
                    text = don.TenTacGia ?: "Đang cập nhật",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Giá tiền
                Text(
                    text = formatGia(don.GiaBan),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFD32F2F), // Màu đỏ
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )


            }
        }
    }
}


@Composable
fun ManHinhTrong() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.icon_don_hang),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                alpha = 0.5f
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bạn chưa có đơn hàng nào",
                color = Color.Gray,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}