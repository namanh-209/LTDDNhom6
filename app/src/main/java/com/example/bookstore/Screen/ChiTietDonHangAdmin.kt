package com.example.bookstore.Screen

import CapNhatTrangThaiRequest
import MChiTietDonHangAdmin
import android.widget.Toast
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
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

// === HÀM MÀU SẮC TRẠNG THÁI ===
fun getStatusColor(status: String): Color {
    return when (status.trim()) {
        "HoanThanh" -> Color(0xFF4CAF50)
        "DaHuy", "Huy" -> Color(0xFFE53935)
        "DangGiao" -> Color(0xFF1976D2)
        "MoiDat", "DangXuLy" -> Color(0xFFFF9800)
        else -> Color.Gray
    }
}

// === HÀM ICON TRẠNG THÁI ===
fun getStatusIcon(status: String): ImageVector {
    return when (status.trim()) {
        "HoanThanh" -> Icons.Default.CheckCircle
        "DaHuy", "Huy" -> Icons.Default.Cancel
        "DangGiao" -> Icons.Default.LocalShipping
        "MoiDat", "DangXuLy" -> Icons.Default.PendingActions
        else -> Icons.Default.Info
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
    var showCancelDialog by remember { mutableStateOf(false) }

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
                val request = CapNhatTrangThaiRequest(donHang.maDonHang, trangThaiMoiBackend)
                RetrofitClient.api.capNhatTrangThai(request)
                Toast.makeText(context, thongBao, Toast.LENGTH_SHORT).show()
                trangThaiHienTai = trangThaiMoiBackend
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Dialog Hủy
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE53935)) },
            title = { Text(text = "Xác nhận hủy đơn", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn hủy đơn hàng này không?\nHành động này không thể hoàn tác.") },
            confirmButton = {
                Button(
                    onClick = {
                        xuLyCapNhatTrangThai("DaHuy", "Đã hủy đơn hàng")
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Hủy ngay")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Thoát", color = Color.Gray) }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Chi tiết đơn hàng", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
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
            Surface(shadowElevation = 16.dp, color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth().navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (trangThaiHienTai.trim()) {
                        "MoiDat", "DangXuLy" -> {
                            Button(
                                onClick = { showCancelDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFE53935)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935)),
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Hủy Đơn", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { xuLyCapNhatTrangThai("DangGiao", "Đã xác nhận giao hàng") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D71A3)),
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Giao Hàng", fontWeight = FontWeight.Bold)
                            }
                        }
                        "DangGiao" -> {
                            Button(
                                onClick = { xuLyCapNhatTrangThai("HoanThanh", "Đơn hàng hoàn thành") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Xác Nhận Đã Giao", fontWeight = FontWeight.Bold)
                            }
                        }
                        else -> Spacer(Modifier.height(1.dp))
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(scrollState).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // === 1. BANNER TRẠNG THÁI ===
            Card(
                colors = CardDefaults.cardColors(containerColor = getStatusColor(trangThaiHienTai)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(getStatusIcon(trangThaiHienTai), contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(getStatusText(trangThaiHienTai), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Mã đơn: #${donHang.maDonHang} • ${donHang.ngayDat}", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                    }
                }
            }

            // === 2. THÔNG TIN NHẬN HÀNG (SỬA Ở ĐÂY) ===
            AdminSectionCard(title = "Thông tin nhận hàng", icon = Icons.Default.LocationOn) {

                // Tên người nhận
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(donHang.tenNguoiMua, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                }

                Spacer(Modifier.height(8.dp))

                // [MỚI] Số điện thoại
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    // Lưu ý: Đảm bảo Model DonHang có trường soDienThoai
                    // Nếu Model bạn tên khác (vd: sdt), hãy sửa lại ở đây
                    val sdt = if (donHang.SDT.isNullOrBlank()) "Không có SĐT" else donHang.SDT
                    Text(sdt, fontSize = 15.sp, color = Color.Black)
                }

                Spacer(Modifier.height(8.dp))

                // Địa chỉ
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Home, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    val diaChi = if (donHang.diaChiGiaoHang.isNullOrBlank()) "Chưa cập nhật địa chỉ" else donHang.diaChiGiaoHang
                    Text(diaChi, color = if (donHang.diaChiGiaoHang.isNullOrBlank()) Color.Red else Color.DarkGray, fontSize = 15.sp, lineHeight = 22.sp)
                }
            }

            // === 3. GHI CHÚ ===
            AdminSectionCard(title = "Ghi chú của khách", icon = Icons.Default.EditNote) {
                if (donHang.GhiChu.isNullOrBlank()) {
                    Text("Không có ghi chú", color = Color.Gray, fontSize = 14.sp, fontStyle = FontStyle.Italic)
                } else {
                    Text(donHang.GhiChu, color = Color.Black, fontSize = 15.sp, lineHeight = 22.sp)
                }
            }

            // === 4. DANH SÁCH SẢN PHẨM ===
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingBag, null, tint = Color(0xFF0D71A3), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sản phẩm", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                    if (dangTai) {
                        Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(30.dp), color = Color(0xFF0D71A3))
                        }
                    } else {
                        danhSachSanPham.forEachIndexed { index, sp ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
                                Card(
                                    shape = RoundedCornerShape(6.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    modifier = Modifier.width(85.dp).height(120.dp)
                                ) {
                                    AsyncImage(
                                        model = sp.anhBia, contentDescription = null,
                                        modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f).height(120.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                    Text(sp.tenSach, maxLines = 3, overflow = TextOverflow.Ellipsis, fontSize = 15.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("x${sp.soLuong}", color = Color.Gray, fontSize = 14.sp)
                                        Text(formatter.format(sp.donGia), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFD32F2F))
                                    }
                                }
                            }
                            if (index < danhSachSanPham.size - 1) Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                        }
                    }
                }
            }

            // === 5. THANH TOÁN ===
            AdminSectionCard(title = "Thanh toán", icon = Icons.Default.Payments) {
                val tongTienHangGoc = if (danhSachSanPham.isNotEmpty()) danhSachSanPham.sumOf { it.donGia * it.soLuong } else 0.0
                val phiShip = donHang.phiVanChuyen ?: 0.0
                val tienGiamGia = (tongTienHangGoc + phiShip - donHang.tongTien).coerceAtLeast(0.0)

                RowInfoLine("Tổng tiền hàng", formatter.format(tongTienHangGoc))
                RowInfoLine("Phí vận chuyển", formatter.format(phiShip))
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalOffer, null, tint = Color.Red, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Voucher giảm giá", color = Color.Gray, fontSize = 14.sp)
                    }
                    Text("-${formatter.format(tienGiamGia)}", color = Color.Red, fontSize = 14.sp)
                }
                Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Thành tiền", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(formatter.format(donHang.tongTien), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF0D71A3))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

// === HELPER COMPONENTS ===
@Composable
fun AdminSectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF0D71A3), modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            }
            Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
            content()
        }
    }
}

@Composable
fun RowInfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = Color.Black, fontSize = 14.sp)
    }
}