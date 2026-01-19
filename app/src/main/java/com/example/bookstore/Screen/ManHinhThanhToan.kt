package com.example.bookstore.Screen

import PhuongThucThanhToan
import android.R
import android.widget.Toast
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.Model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

// ================= MODEL UI =================
data class PhuongThucThanhToanUI(
    val ma: String,
    val ten: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)



fun formatTienVND(value: Int): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return formatter.format(value) + " VNĐ"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManHinhThanhToan(
    navController: NavController,
    danhSachSanPham: List<SachtrongGioHang>,
    BamQuayLai: () -> Unit
) {
    val context = LocalContext.current
    val user = BienDungChung.userHienTai

    var diaChiDangChon by remember { mutableStateOf<DiaChi?>(null) }
    var phuongThucThanhToan by remember { mutableStateOf("TienMat") }
    var ghiChu by remember { mutableStateOf("") }
    var dangXuLy by remember { mutableStateOf(false) }

    // ===== NHẬN KHUYẾN MÃI (ĐÚNG CÁCH) =====
    val savedStateHandle =
        navController.currentBackStackEntry?.savedStateHandle

    val khuyenMaiDaChon by savedStateHandle
        ?.getStateFlow<KhuyenMai?>("khuyenMaiDaChon", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    // ===== TÍNH TIỀN =====
    val tongTienSanPham = danhSachSanPham.sumOf {
        it.GiaBan.toInt() * it.SoLuong
    }

    val danhSachVC = listOf(
        PhuongThucVanChuyen("Giao hàng tiết kiệm", 16000),
        PhuongThucVanChuyen("Giao hàng nhanh", 30000)
    )
    var vanChuyenDangChon by remember { mutableStateOf(danhSachVC.first()) }
    val donToiThieu = khuyenMaiDaChon?.DonToiThieu ?: 0.0
    val duDieuKien = tongTienSanPham >= donToiThieu

    // Logic: Nếu đủ điều kiện mới lấy giá trị giảm, ngược lại là 0
    val tienGiam = if (duDieuKien) {
        khuyenMaiDaChon?.GiaTriGiam?.toInt() ?: 0
    } else {
        0
    }

    // Hiển thị thông báo nếu chọn mã nhưng không đủ điều kiện
    LaunchedEffect(khuyenMaiDaChon) {
        if (khuyenMaiDaChon != null && !duDieuKien) {
            Toast.makeText(
                context,
                "Đơn hàng chưa đủ tối thiểu ${khuyenMaiDaChon!!.DonToiThieu.toString()}đ",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    val phiVC = vanChuyenDangChon.phi
//    val tienGiam = khuyenMaiDaChon?.GiaTriGiam?.toInt() ?: 0
    val tongThanhToan =
        (tongTienSanPham + phiVC - tienGiam).coerceAtLeast(0)

    // ================= UI =================
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = BamQuayLai) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D71A3)
                )
            )
        },
        bottomBar = {
            ThanhTongCongDatHang(
                tongTien = tongThanhToan,
                dangXuLy = dangXuLy
            ) {
                if (diaChiDangChon == null) {
                    Toast.makeText(context, "Chưa chọn địa chỉ", Toast.LENGTH_SHORT).show()
                    return@ThanhTongCongDatHang
                }
                if (user == null) {
                    Toast.makeText(context, "Hết phiên đăng nhập", Toast.LENGTH_SHORT).show()
                    return@ThanhTongCongDatHang
                }

                dangXuLy = true

                val chiTiet = danhSachSanPham.map {
                    ChiTietDonHangGui(
                        MaSach = it.MaSach,
                        SoLuong = it.SoLuong,
                        DonGia = it.GiaBan
                    )
                }

                val donHang = DonHangGui(
                    MaNguoiDung = user.MaNguoiDung,

                    // Đảm bảo dòng này lấy đúng ID của mã đã chọn (nếu null thì gửi null)
                    MaKhuyenMai = khuyenMaiDaChon?.MaKhuyenMai,

                    PhuongThucThanhToan = phuongThucThanhToan,
                    PhiVanChuyen = phiVC,
                    TongTien = tongThanhToan,
                    DiaChiGiaoHang = diaChiDangChon!!.DiaChiChiTiet,
                    GhiChu = ghiChu,
                    ChiTiet = chiTiet
                )

                RetrofitClient.api.taoDonHang(donHang)
                    .enqueue(object : Callback<PhanHoiApi> {
                        override fun onResponse(
                            call: Call<PhanHoiApi>,
                            response: Response<PhanHoiApi>
                        ) {
                            dangXuLy = false
                            if (response.isSuccessful && response.body()?.status == "success") {
                                Toast.makeText(context, "Đặt hàng thành công", Toast.LENGTH_LONG).show()
                                navController.navigate("home") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    response.body()?.message ?: "Lỗi đặt hàng",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<PhanHoiApi>, t: Throwable) {
                            dangXuLy = false
                            Toast.makeText(context, "Lỗi mạng", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            DiaChiNhanHang { diaChiDangChon = it }
            SanPhamDonHang(danhSachSanPham)
            // Trong ManHinhThanhToan.kt
            MaKhuyenMai(khuyenMaiDaChon) {
                // 1. Lưu tổng tiền hàng vào bộ nhớ tạm để màn hình kia đọc được
                navController.currentBackStackEntry?.savedStateHandle?.set("tongTienGioHang", tongTienSanPham)

                // 2. Chuyển màn hình
                navController.navigate("khuyenmai")
            }
            KhuVucGhiChuChoShop(ghiChu) { ghiChu = it }
            PTVanChuyen(danhSachVC, vanChuyenDangChon) { vanChuyenDangChon = it }
            PTThanhToan(phuongThucThanhToan) { phuongThucThanhToan = it }
            ChiTietThanhToan(tongTienSanPham, phiVC, tienGiam, tongThanhToan)
            Spacer(Modifier.height(20.dp))
        }
    }
}

/* ====================== CÁC HÀM CON ====================== */

@Composable
fun DiaChiNhanHang(khiChon: (DiaChi) -> Unit) {
    val user = BienDungChung.userHienTai
    var ds by remember { mutableStateOf<List<DiaChi>>(emptyList()) }

    // Logic lấy dữ liệu giữ nguyên
    LaunchedEffect(Unit) {
        user?.let {
            val res = RetrofitClient.api.layDanhSachDiaChi(it.MaNguoiDung)
            if (res.status == "success") {
                ds = res.data ?: emptyList()
                if (ds.isNotEmpty()) khiChon(ds.first())
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp), // Bo góc nhẹ
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Tiêu đề
            Text(
                text = "Thông tin nhận hàng",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            if (ds.isNotEmpty()) {
                val diaChi = ds.first()

                // 1. Dòng Tên (Icon Người)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF0D71A3), // Màu xanh chủ đạo
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = diaChi.TenNguoiNhan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // 2. Dòng Số điện thoại (Icon Điện thoại)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = Color(0xFF0D71A3),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = diaChi.SDTNguoiNhan,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }

                // 3. Dòng Địa chỉ (Icon Vị trí)
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF0D71A3),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = diaChi.DiaChiChiTiet,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp // Giãn dòng cho dễ đọc nếu địa chỉ dài
                    )
                }
            } else {
                Text(
                    "Chưa có địa chỉ. Vui lòng thêm địa chỉ mới!",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SanPhamDonHang(ds: List<SachtrongGioHang>) {
    Card(Modifier
        .fillMaxWidth()
        .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Column(Modifier.padding(12.dp))
        {
            Text("Sản phẩm", fontWeight = FontWeight.Bold)
            ds.forEach {
                Row(Modifier.padding(vertical = 6.dp)) {
                    AsyncImage(it.AnhBia, null, Modifier.size(60.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(it.TenSach, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            "${formatTienVND(it.GiaBan.toInt())} x${it.SoLuong}",
                            color = Color.Red
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun MaKhuyenMai(km: KhuyenMai?, onChon: () -> Unit) {
    Card(Modifier
        .fillMaxWidth()
        .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                km?.let { "Mã ${it.MaCode} (-${formatTienVND(it.GiaTriGiam.toInt())})" }
                    ?: "Chọn mã khuyến mãi",
                Modifier.weight(1f)
            )
            TextButton(onClick = onChon) { Text("Chọn") }
        }
    }
}

@Composable
fun KhuVucGhiChuChoShop(value: String, onChange: (String) -> Unit) {
    Card(Modifier
        .fillMaxWidth()
        .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Ghi chú cho shop") }
        )
    }
}

@Composable
fun PTVanChuyen(
    ds: List<PhuongThucVanChuyen>,
    dangChon: PhuongThucVanChuyen,
    onChon: (PhuongThucVanChuyen) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Column(Modifier.padding(12.dp)) {

            Text(
                text = "Vận chuyển",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ds.forEach { pt ->
                val isSelected = pt == dangChon
                val textColor =
                    if (isSelected) Color(0xFF0D71A3) else Color.Black

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChon(pt) }
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected = isSelected,
                        onClick = { onChon(pt) }
                    )

                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier
                            .size(22.dp)
                            .padding(end = 8.dp)
                    )

                    Text(
                        text = pt.ten,
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp,
                        color = textColor,
                        fontWeight = if (isSelected)
                            FontWeight.SemiBold
                        else
                            FontWeight.Normal
                    )

                    Text(
                        text = formatTienVND(pt.phi),
                        fontSize = 14.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}


@Composable
fun PTThanhToan(dangChon: String, onChon: (String) -> Unit) {
    val ds = listOf(
        PhuongThucThanhToan("TienMat", "Thanh toán khi nhận hàng", Icons.Default.Payments),
        PhuongThucThanhToan("ChuyenKhoan", "Chuyển khoản ngân hàng", Icons.Default.AccountBalance),
        PhuongThucThanhToan("ViDienTu", "Ví điện tử", Icons.Default.AccountBalanceWallet)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Thanh toán", fontWeight = FontWeight.Bold)

            ds.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChon(it.ma) }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected = it.ma == dangChon,
                        onClick = { onChon(it.ma) }
                    )

                    Icon(
                        imageVector = it.icon,
                        contentDescription = null,
                        tint = if (it.ma == dangChon)
                            Color(0xFF0D71A3)
                        else
                            Color.LightGray,
                        modifier = Modifier
                            .size(28.dp)
                            .padding(end = 8.dp)
                    )

                    Text(
                        text = it.ten,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}


@Composable
fun ChiTietThanhToan(tienHang: Int, ship: Int, giam: Int, tong: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Tiêu đề có icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong, // Icon hóa đơn
                    contentDescription = null,
                    tint = Color(0xFF0D71A3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Chi tiết thanh toán",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE)
            )

            // Các dòng chi tiết (Dãn cách bằng padding trong hàm DongTien)
            DongTien("Tổng tiền hàng", tienHang)
            DongTien("Phí vận chuyển", ship)

            if (giam > 0) {
                DongTien("Voucher giảm giá", -giam, Color.Red)
            }

            // Đường kẻ đậm hơn chút trước khi chốt Tổng
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray
            )

            // Dòng Tổng tiền (To và Nổi bật hẳn)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Tổng thanh toán",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    formatTienVND(tong),
                    color = Color(0xFF0D71A3), // Màu xanh chủ đạo
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp // Cỡ chữ to
                )
            }
        }
    }
}

@Composable
fun DongTien(label: String, value: Int, color: Color = Color.Black) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp), // Tăng khoảng cách giữa các dòng lên 6dp
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = Color.Gray, // Chữ tiêu đề màu xám cho dịu mắt
            fontSize = 15.sp
        )
        Text(
            formatTienVND(value),
            color = color,
            fontWeight = FontWeight.Medium, // Đậm vừa phải
            fontSize = 15.sp
        )
    }
}

@Composable
fun ThanhTongCongDatHang(tongTien: Int, dangXuLy: Boolean, onDatHang: () -> Unit) {
    Surface(shadowElevation = 8.dp, color = Color.LightGray  ) {
        Row(Modifier
            .fillMaxWidth()
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Tổng thanh toán")
                Text(
                    formatTienVND(tongTien),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

            }
            Button(
                onClick = onDatHang,
                enabled = !dangXuLy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F),   // đỏ chính
                    disabledContainerColor = Color(0xFFEF9A9A) // đỏ nhạt khi disable
                ),
                shape = RoundedCornerShape(12.dp)
                ) {
                Text(if (dangXuLy)
                    "Đang xử lý..."
                else "Đặt hàng",
                    )
            }
        }
    }
}
