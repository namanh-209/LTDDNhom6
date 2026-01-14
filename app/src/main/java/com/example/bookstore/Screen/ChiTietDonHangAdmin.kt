package com.example.bookstore.Screen

import MChiTietDonHangAdmin
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Model.DonHang

import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// === HÀM MÀU SẮC TRẠNG THÁI (Chuẩn Material Design) ===
fun getStatusColor(status: String): Color {
    return when (status.trim()) {
        "HoanThanh" -> Color(0xFF4CAF50) // Green
        "DaHuy", "Huy" -> Color(0xFFE53935) // Red
        "DangGiao" -> Color(0xFF1976D2) // Blue
        "MoiDat", "DangXuLy" -> Color(0xFFFF9800) // Orange
        else -> Color.Gray
    }
}

// === HÀM TÊN TRẠNG THÁI ===
fun getStatusText(status: String): String {
    return when (status.trim()) {
        "MoiDat", "DangXuLy" -> "Chờ xác nhận"
        "DangGiao" -> "Đang giao hàng"
        "HoanThanh" -> "Hoàn thành"
        "DaHuy", "Huy" -> "Đã hủy"
        else -> "Không xác định"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChiTietDonHangAdmin(
    navController: NavController,
    donHang: DonHang
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    // State
    var danhSachSanPham by remember { mutableStateOf<List<MChiTietDonHangAdmin>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }
    var trangThaiHienTai by remember { mutableStateOf(donHang.trangThai) }

    // Gọi API
    LaunchedEffect(donHang.maDonHang) {
        try {
            val response = RetrofitClient.api.layChiTietDonHang(donHang.maDonHang)
            if (response.data != null) {
                danhSachSanPham = response.data
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi tải SP: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            dangTai = false
        }
    }

    // Xử lý cập nhật
    fun xuLyCapNhatTrangThai(trangThaiMoiBackend: String, thongBao: String) {
        scope.launch {
            try {
                RetrofitClient.api.capNhatTrangThai(donHang.maDonHang, trangThaiMoiBackend)
                Toast.makeText(context, thongBao, Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Quay về để refresh danh sách
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5), // Nền xám nhạt chuẩn App
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết đơn hàng",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D71A3)),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            // === BOTTOM BAR ACTION ===
            Surface(
                shadowElevation = 16.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding(), // Tránh bị che bởi thanh điều hướng điện thoại
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (trangThaiHienTai.trim()) {
                        "MoiDat", "DangXuLy" -> {
                            Button(
                                onClick = { xuLyCapNhatTrangThai("DaHuy", "Đã hủy đơn hàng") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFE53935)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935)),
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Hủy Đơn", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { xuLyCapNhatTrangThai("DangGiao", "Đã xác nhận giao hàng") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D71A3)),
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Xác Nhận", fontWeight = FontWeight.Bold)
                            }
                        }
                        "DangGiao" -> {
                            Button(
                                onClick = { xuLyCapNhatTrangThai("HoanThanh", "Đơn hàng hoàn thành") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Xác Nhận Đã Giao Thành Công", fontWeight = FontWeight.Bold)
                            }
                        }
                        else -> {
                            // Không hiện nút gì nếu đã xong/hủy
                            Spacer(Modifier.height(1.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // === 1. BANNER TRẠNG THÁI ===
            Card(
                colors = CardDefaults.cardColors(containerColor = getStatusColor(trangThaiHienTai)),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Đơn hàng ${getStatusText(trangThaiHienTai)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Mã đơn: #${donHang.maDonHang} - ${donHang.ngayDat}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // === 2. ĐỊA CHỈ NHẬN HÀNG ===
            AdminSectionCard(title = "Địa chỉ nhận hàng", icon = Icons.Default.LocationOn) {
                Text(
                    text = donHang.tenNguoiMua,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = donHang.diaChiGiaoHang ?: "Khách hàng chưa cập nhật địa chỉ cụ thể",
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            // === 3. DANH SÁCH SẢN PHẨM ===
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingBag, null, tint = Color(0xFF0D71A3), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sản phẩm", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                    if (dangTai) {
                        Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(30.dp), color = Color(0xFF0D71A3))
                        }
                    } else {
                        danhSachSanPham.forEachIndexed { index, sp ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Ảnh sản phẩm
                                Card(
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                                ) {
                                    AsyncImage(
                                        model = sp.anhBia,
                                        contentDescription = null,
                                        modifier = Modifier.size(70.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                // Thông tin
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = sp.tenSach,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "x${sp.soLuong}", color = Color.Gray, fontSize = 13.sp)
                                        Text(
                                            text = formatter.format(sp.donGia),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                            if (index < danhSachSanPham.size - 1) {
                                Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                            }
                        }
                    }
                }
            }

            // === 4. THANH TOÁN ===
            AdminSectionCard(title = "Chi tiết thanh toán", icon = Icons.Default.ReceiptLong) {
                RowInfoLine("Tổng tiền hàng", formatter.format(donHang.tongTien)) // Giả sử tổng tiền hàng = tổng bill
                RowInfoLine("Phí vận chuyển", "Miễn phí")
                RowInfoLine("Giảm giá voucher", "-0 ₫")

                Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thành tiền", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = formatter.format(donHang.tongTien),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFE53935) // Màu đỏ cho giá tiền
                    )
                }
            }

            Spacer(Modifier.height(20.dp)) // Khoảng trống cuối cùng
        }
    }
}

// === COMPONENT CON ===
@Composable
fun AdminSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF0D71A3), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
            }
            Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
            content()
        }
    }
}

@Composable
fun RowInfoLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = Color.Black, fontSize = 14.sp)
    }
}