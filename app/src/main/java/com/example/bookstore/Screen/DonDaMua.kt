package com.example.bookstore.Screen // Hoặc package com.example.bookstore.ui tùy cấu trúc của bạn

import CapNhatGioHangRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.bookstore.Model.Sach
import com.example.bookstore.Model.SachtrongGioHang
import com.example.bookstore.R
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DonDaMua(navController: NavController, onBackClick: () -> Unit) {

    // 1. Chuẩn bị dữ liệu
    val tabs = listOf("Chờ xác nhận", "Chờ giao hàng", "Đã giao", "Đã huỷ")

    // Lấy ID người dùng từ biến chung (Nếu null thì lấy 0)
    val maNguoiDung = BienDungChung.userHienTai?.MaNguoiDung ?: 0

    val decimalFormat = DecimalFormat("###,###") // Để format tiền (100.000 đ)

    // 2. Các biến trạng thái (State)
    var selectedTab by remember { mutableStateOf(0) }
    var danhSachFull by remember { mutableStateOf<List<DonHangSach>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // 3. Gọi API khi mở màn hình
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // Gọi API với ID lấy từ biến chung
                val response = RetrofitClient.api.laySachDaMua(maNguoiDung)

                // Lưu dữ liệu trả về (xử lý null safety)
                danhSachFull = response.data ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 4. Vẽ giao diện chính
    KhungGiaoDien(tieuDe = "Đơn đã mua",
        onBackClick = onBackClick, // TRANG CON -> CÓ BACK (được truyền từ AppNavGraph)
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = { navController.navigate("khuyenmai") },
        onProfileClick = { navController.navigate("trangtaikhoan") }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)) // Nền xám nhạt
        ) {
            // A. Thanh Tab (Menu ngang)
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

            // B. Lọc danh sách theo Tab đang chọn
            // (Phải khớp với trạng thái trong Database: MoiDat, DangGiao, HoanThanh, DaHuy)
            val danhSachHienThi = when (selectedTab) {
                0 -> danhSachFull.filter { it.TrangThai == "DangXuLy" }
                1 -> danhSachFull.filter { it.TrangThai == "DangGiao" }
                2 -> danhSachFull.filter { it.TrangThai == "HoanThanh" }
                // Chấp nhận mọi trạng thái có chứa chữ "Huy"
                else -> danhSachFull.filter {
                    it.TrangThai != "DangXuLy" &&
                            it.TrangThai != "DangGiao" &&
                            it.TrangThai != "HoanThanh"
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // C. Hiển thị danh sách hoặc thông báo trống
            if (danhSachHienThi.isEmpty()) {
                ManHinhTrong() // Gọi hàm vẽ màn hình trống
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(danhSachHienThi) { donHang ->
                        // Gọi hàm vẽ từng cuốn sách
                        BookOrderItem(don = donHang, formatter = decimalFormat,  navController = navController)
                    }
                }
            }
        }
    }
}

// =================================================================
// PHẦN 2: ITEM CON (VẼ 1 CUỐN SÁCH)
// =================================================================
@Composable
fun BookOrderItem(
    don: DonHangSach,
    formatter: DecimalFormat,
    navController: NavController,
) {

    // Logic chọn màu sắc theo trạng thái
    val (mauSac, trangThaiText) = when (don.TrangThai) {
        "DangXuLy" -> Pair(Color(0xFFFFA000), "Chờ xác nhận")     // Cam
        "DangGiao" -> Pair(Color(0xFF1976D2), "Đang giao")      // Xanh dương
        "HoanThanh" -> Pair(Color(0xFF388E3C), "Thành công")    // Xanh lá
        else -> Pair(Color(0xFFD32F2F), "Đã huỷ")               // Đỏ
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            fun formatGia(gia: Double): String {
                val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
                return "${formatter.format(gia)} VND"
            }
            // Ảnh bìa (Hình chữ nhật đứng tỷ lệ 2:3)
            AsyncImage(
                model = don.AnhBia,
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
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
                    maxLines = 3,
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
                    text = "${formatGia(don.GiaBan)} ",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFD32F2F), // Màu đỏ
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                if (don.TrangThai == "HoanThanh") {
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val muaNgay = listOf(
                                    SachtrongGioHang(
                                        MaGioHang = 0,
                                        MaSach = don.MaSach,
                                        TenSach = don.TenSach,
                                        TenTacGia = don.TenTacGia ?: "Đang cập nhật",
                                        GiaBan = don.GiaBan,
                                        SoLuong = 1,
                                        AnhBia = don.AnhBia
                                    )
                                )

                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("gioHang", muaNgay)

                                navController.navigate("thanhtoan")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp), // 🔥 tăng chiều cao
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray
                            ),
                            contentPadding = PaddingValues(vertical = 6.dp) // 🔥 tránh cắt chữ
                        ) {
                            Text(
                                text = "Mua lại",
                                color = Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                        }

                        Button(
                            onClick = {
                                navController.navigate("danhgia/${don.MaSach}/${don.MaDonHang}")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800)
                            ),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text(
                                text = "Đánh giá",
                                color = Color.White,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                        }
                    }

                }

                if (don.TrangThai == "DaHuy"){
                    Button(
                        onClick = { /* TODO: Xử lý nút Trả hàng/Hoàn tiền */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = "Mua lại", color = Color.Black, fontSize = 14.sp) // Màu chữ nên đen để nhìn rõ trên nền sáng
                    }
                }

            }
        }
    }
}

// =================================================================
// PHẦN 3: GIAO DIỆN KHI KHÔNG CÓ ĐƠN HÀNG (MÀN HÌNH TRỐNG)
// =================================================================
@Composable
fun ManHinhTrong() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.icon_don_hang), // Đảm bảo bạn có icon này
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