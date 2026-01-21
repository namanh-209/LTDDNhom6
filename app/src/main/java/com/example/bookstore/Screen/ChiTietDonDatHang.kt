package com.example.bookstore.Screen

import MChiTietDonHangAdmin
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.bookstore.Model.SachtrongGioHang
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// --- ĐỊNH NGHĨA MÀU SẮC RIÊNG ---
val PrimaryBlue = Color(0xFF0D71A3)
val SuccessGreen = Color(0xFF4CAF50)
val WarningOrange = Color(0xFFFF9800)
val ErrorRed = Color(0xFFE53935)
val GrayText = Color(0xFF757575)
val BackgroundColor = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChiTietDonHangDat(
    navController: NavController,
    maDonHang: Int,
    trangThaiBanDau: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    // State
    var danhSachSanPham by remember { mutableStateOf<List<MChiTietDonHangAdmin>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }
    var hienThiDialogHuy by remember { mutableStateOf(false) }
    var trangThaiHienTai by remember { mutableStateOf(trangThaiBanDau) }

    // API Call
    LaunchedEffect(maDonHang) {
        try {
            val response = RetrofitClient.api.layChiTietDonHang(maDonHang)
            if (response.data != null) danhSachSanPham = response.data
        } catch (e: Exception) {
        } finally {
            dangTai = false
        }
    }

    // Logic Hủy
    fun xuLyHuyDon() {
        scope.launch {
            try {
                val response = RetrofitClient.api.huyDonHang(maDonHang)
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show()
                    trangThaiHienTai = "DaHuy"
                    hienThiDialogHuy = false
                } else {
                    Toast.makeText(context, "Không thể hủy đơn này", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi mạng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Logic Mua Lại
    fun xuLyMuaLai() {
        if (danhSachSanPham.isNotEmpty()) {
            val listMuaLai = danhSachSanPham.map { sp ->
                SachtrongGioHang(0, sp.maSach, sp.tenSach, sp.anhBia, sp.donGia, sp.soLuong, "", SoLuongTon = 1)
            }
            navController.currentBackStackEntry?.savedStateHandle?.set("mua_lai_san_pham", listMuaLai)
            navController.navigate("thanhtoan")
        }
    }

    // Xác nhận hủy
    if (hienThiDialogHuy) {
        AlertDialog(
            onDismissRequest = { hienThiDialogHuy = false },
            icon = { Icon(Icons.Outlined.Cancel, contentDescription = null, tint = ErrorRed) },
            title = { Text("Hủy Đơn Hàng?", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn hủy đơn hàng #${maDonHang} không?\nHành động này không thể hoàn tác.") },
            confirmButton = {
                Button(onClick = { xuLyHuyDon() }, colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)) {
                    Text("Hủy Đơn", color = Color.White)
                }
            },
            dismissButton = { OutlinedButton(onClick = { hienThiDialogHuy = false }) { Text("Quay lại") } }
        )
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đơn hàng", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue),
                actions = {
                    Text("#$maDonHang", color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(end = 16.dp))
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 16.dp, color = Color.White) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (trangThaiHienTai) {
                        "MoiDat", "DangXuLy" -> {
                            OutlinedButton(
                                onClick = { hienThiDialogHuy = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Close, null); Spacer(Modifier.width(8.dp))
                                Text("Hủy Đơn Hàng")
                            }
                        }
                        "HoanThanh" -> {
                            OutlinedButton(
                                onClick = { xuLyMuaLai() },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("Mua Lại") }

                            Button(
                                onClick = {
                                    if(danhSachSanPham.isNotEmpty()) navController.navigate("danhgia/${danhSachSanPham[0].maSach}/$maDonHang")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WarningOrange),
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Star, null, tint = Color.White); Spacer(Modifier.width(4.dp))
                                Text("Đánh Giá", color = Color.White)
                            }
                        }
                        "DaHuy", "Huy" -> {
                            Button(
                                onClick = { xuLyMuaLai() },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Outlined.ShoppingBag, null); Spacer(Modifier.width(8.dp))
                                Text("Mua Lại Đơn Này")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //  HEADER TRẠNG THÁI
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    if (trangThaiHienTai == "DaHuy" || trangThaiHienTai == "Huy") {
                        // Giao diện khi Hủy
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Cancel, null, tint = ErrorRed, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Đơn hàng đã hủy", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                                Text("Rất tiếc, đơn hàng này đã bị hủy.", fontSize = 14.sp, color = GrayText)
                            }
                        }
                    } else {
                        // Giao diện Timeline các bước
                        OrderStatusTimeline(trangThaiHienTai)
                    }
                }
            }

            // DANH SÁCH SẢN PHẨM
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.ShoppingBag, null, tint = PrimaryBlue)
                        Spacer(Modifier.width(8.dp))
                        Text("Sản phẩm", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Divider(color = Color(0xFFEEEEEE))

                    if(dangTai) {
                        Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    } else {
                        danhSachSanPham.forEach { sp ->
                            ProductItemView(sp, formatter)
                            Divider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }

            // 3. TỔNG KẾT TIỀN
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Chi tiết thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))

                    val tamTinh = if(danhSachSanPham.isNotEmpty()) danhSachSanPham.sumOf { it.donGia * it.soLuong } else 0.0

                    InvoiceRow("Tạm tính", formatter.format(tamTinh))
                    InvoiceRow("Phí vận chuyển", "Miễn phí", isHightlight = false)

                    Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng cộng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(formatter.format(tamTinh), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ErrorRed)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

// --- CÁC COMPONENT CON (UI) ---

@Composable
fun OrderStatusTimeline(currentStatus: String) {
    val currentStep = when (currentStatus) {
        "MoiDat" -> 1; "DangXuLy" -> 2; "DangGiao" -> 3; "HoanThanh" -> 4; else -> 1
    }

    Column(Modifier.fillMaxWidth()) {
        Text("Trạng thái đơn hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 16.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TimelineStep(active = currentStep >= 1, icon = Icons.Outlined.Assignment, label = "Đã đặt", isLast = false)
            TimelineStep(active = currentStep >= 2, icon = Icons.Outlined.Inventory2, label = "Xử lý", isLast = false)
            TimelineStep(active = currentStep >= 3, icon = Icons.Outlined.LocalShipping, label = "Đang giao", isLast = false)
            TimelineStep(active = currentStep >= 4, icon = Icons.Outlined.CheckCircle, label = "Hoàn thành", isLast = true)
        }
    }
}

@Composable
fun RowScope.TimelineStep(active: Boolean, icon: ImageVector, label: String, isLast: Boolean) {
    val color = if (active) SuccessGreen else Color(0xFFE0E0E0)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                shape = CircleShape,
                color = if (active) color.copy(alpha = 0.1f) else Color.Transparent,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(icon, null, tint = if (active) color else Color.Gray, modifier = Modifier.padding(6.dp))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = if(active) FontWeight.Bold else FontWeight.Normal, color = if(active) Color.Black else Color.Gray, maxLines = 1, overflow = TextOverflow.Visible)
    }
    if (!isLast) {
        Divider(color = if (active) SuccessGreen else Color(0xFFE0E0E0), thickness = 2.dp, modifier = Modifier.padding(top = 18.dp).weight(0.5f))
    }
}

@Composable
fun ProductItemView(sp: MChiTietDonHangAdmin, formatter: java.text.NumberFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        
        Card(
            shape = RoundedCornerShape(6.dp),
            elevation = CardDefaults.cardElevation(4.dp), // Tạo bóng đổ nhẹ
            modifier = Modifier
                .width(90.dp)   // Chiều rộng nhỏ lại
                .height(130.dp) // Chiều cao tăng lên => Dáng hình chữ nhật đứng của sách
        ) {
            AsyncImage(
                model = sp.anhBia,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Crop ảnh cho vừa khít khung
            )
        }
        // ------------------------------

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .height(130.dp), // Căn chiều cao bằng với ảnh để dàn đều nội dung
            verticalArrangement = Arrangement.SpaceBetween // Đẩy tên sách lên trên, giá tiền xuống dưới
        ) {
            // Tên sách
            Text(
                sp.tenSach,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Giá tiền & Số lượng
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatter.format(sp.donGia),
                    color = ErrorRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "x${sp.soLuong}",
                    color = GrayText,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun InvoiceRow(label: String, value: String, isHightlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = GrayText, fontSize = 15.sp)
        Text(value, color = if(isHightlight) ErrorRed else Color.Black, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}