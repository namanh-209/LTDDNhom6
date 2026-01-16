package com.example.bookstore.Screen // Đã sửa package cho đúng chuẩn

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.bookstore.Model.DiaChi
import com.example.bookstore.Model.KhuyenMai
import com.example.bookstore.Model.PhuongThucVanChuyen
import com.example.bookstore.Model.SachtrongGioHang
import com.example.bookstore.Model.PhanHoiApi
// Import class Model Gửi đi
import com.example.bookstore.Model.DonHangGui
import com.example.bookstore.Model.ChiTietDonHangGui
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Model UI cho phương thức thanh toán
data class PhuongThucThanhToanUI(
    val ma: String,
    val ten: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManHinhThanhToan(
    navController: NavController,
    danhSachSanPham: List<SachtrongGioHang>,
    BamQuayLai: () -> Unit
) {
    // --- 1. KHAI BÁO BIẾN STATE ---
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user = BienDungChung.userHienTai

    var phuongThucThanhToan by remember { mutableStateOf("TienMat") }
    var diaChiDangChon by remember { mutableStateOf<DiaChi?>(null) }
    var KhuyenMaiDaChon by remember { mutableStateOf<KhuyenMai?>(null) }
    var ghiChuChoShop by remember { mutableStateOf("") }

    // Quản lý trạng thái loading nút Đặt hàng
    var dangXuLyDatHang by remember { mutableStateOf(false) }

    // --- 2. TÍNH TOÁN TIỀN ---
    // Ép kiểu toInt() để tránh lỗi nếu GiaBan đang là Double
    val tongTienSanPham = danhSachSanPham.sumOf { it.GiaBan.toInt() * it.SoLuong }

    val danhSachVanChuyen = listOf(
        PhuongThucVanChuyen("Giao hàng tiết kiệm", 16000),
        PhuongThucVanChuyen("Giao hàng nhanh", 30000),
        PhuongThucVanChuyen("Hỏa tốc", 50000)
    )
    var vanChuyenDangChon by remember { mutableStateOf(danhSachVanChuyen.first()) }

    // Miễn ship nếu có mã KM
    val phiVanChuyenGoc = vanChuyenDangChon.phi
    val giamPhiVanChuyen = if (KhuyenMaiDaChon != null) phiVanChuyenGoc else 0
    val phiVanChuyenThucTe = phiVanChuyenGoc - giamPhiVanChuyen

    val tongThanhToan = tongTienSanPham + phiVanChuyenThucTe

    // --- 3. GIAO DIỆN CHÍNH ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Thanh toán",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = BamQuayLai) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF0D71A3)) // MauXanh
            )
        },
        bottomBar = {
            ThanhTongCongDatHang(
                tongTien = tongThanhToan, // Truyền Int vào đây
                dangXuLy = dangXuLyDatHang,
                khiDatHang = {
                    // --- LOGIC XỬ LÝ KHI BẤM NÚT ĐẶT HÀNG ---

                    // A. Kiểm tra dữ liệu
                    if (diaChiDangChon == null) {
                        Toast.makeText(context, "Vui lòng chọn địa chỉ giao hàng!", Toast.LENGTH_SHORT).show()
                        return@ThanhTongCongDatHang
                    }
                    if (user == null) {
                        Toast.makeText(context, "Phiên đăng nhập hết hạn!", Toast.LENGTH_SHORT).show()
                        return@ThanhTongCongDatHang
                    }

                    // B. Chuẩn bị dữ liệu gửi đi
                    dangXuLyDatHang = true // Hiện loading

                    // 1. Map danh sách sách sang Model ChiTietDonHangGui
                    val listChiTiet = danhSachSanPham.map {
                        ChiTietDonHangGui(
                            MaSach = it.MaSach,
                            SoLuong = it.SoLuong,
                            // QUAN TRỌNG: Thêm .toInt() để tránh lỗi Double
                            DonGia = it.GiaBan
                        )
                    }

                    // 2. Tạo object Đơn Hàng
                    val donHangRequest = DonHangGui(
                        MaNguoiDung = user.MaNguoiDung,
                        MaKhuyenMai = KhuyenMaiDaChon?.MaKhuyenMai,
                        PhuongThucThanhToan = phuongThucThanhToan,
                        // QUAN TRỌNG: Thêm .toInt() để đảm bảo đúng kiểu Int
                        PhiVanChuyen = phiVanChuyenThucTe,
                        TongTien = tongThanhToan,
                        DiaChiGiaoHang = diaChiDangChon!!.DiaChiChiTiet,
                        GhiChu = ghiChuChoShop,
                        ChiTiet = listChiTiet
                    )

                    // 3. Gọi API bằng .enqueue
                    RetrofitClient.api.taoDonHang(donHangRequest).enqueue(object : Callback<PhanHoiApi> {
                        override fun onResponse(call: Call<PhanHoiApi>, response: Response<PhanHoiApi>) {
                            dangXuLyDatHang = false // Tắt loading
                            if (response.isSuccessful) {
                                val result = response.body()
                                // Dùng ?. để an toàn
                                if (result != null && result.status == "success") {
                                    Toast.makeText(context, "Đặt hàng thành công!", Toast.LENGTH_LONG).show()
                                    // Quay về trang chủ
                                    navController.navigate("home") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "Lỗi: ${result?.message ?: "Không xác định"}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Lỗi Server: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<PhanHoiApi>, t: Throwable) {
                            dangXuLyDatHang = false // Tắt loading
                            Toast.makeText(context, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            )
        }
    ) { paddingValue ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .background(Color(0xFFF5F5F5))
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Địa chỉ
            DiaChiNhanHang(khiChonDiaChi = { diaChiDangChon = it })

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Danh sách sản phẩm
            SanPhamDonHang(danhSachSanPham)

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Mã khuyến mãi
            MaKhuyenMai(
                khuyenMai = KhuyenMaiDaChon,
                onChonKhuyenMai = { navController.navigate("khuyenmai") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Ghi chú
            KhuVucGhiChuChoShop(ghiChu = ghiChuChoShop, khiThayDoi = { ghiChuChoShop = it })

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Vận chuyển
            PTVanChuyen(
                danhSach = danhSachVanChuyen,
                dangChon = vanChuyenDangChon,
                khiChon = { vanChuyenDangChon = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 6. Thanh toán
            PTThanhToan(
                phuongThucDangChon = phuongThucThanhToan,
                khiDoiPhuongThuc = { phuongThucThanhToan = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 7. Chi tiết tiền
            ChiTietThanhToan(
                tongTienSanPham = tongTienSanPham,
                phiVanChuyen = phiVanChuyenGoc,
                giamPhiShip = giamPhiVanChuyen,
                tongThanhToan = tongThanhToan
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ================= CÁC COMPONENT CON =================

@Composable
fun DiaChiNhanHang(khiChonDiaChi: (DiaChi) -> Unit) {
    val user = BienDungChung.userHienTai
    var danhSachDiaChi by remember { mutableStateOf<List<DiaChi>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }
    var loi by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (user == null) {
            loi = "Vui lòng đăng nhập"
            dangTai = false
            return@LaunchedEffect
        }
        try {
            val res = RetrofitClient.api.layDanhSachDiaChi(maNguoiDung = user.MaNguoiDung)
            if (res.status == "success") {
                danhSachDiaChi = res.data ?: emptyList()
                if (danhSachDiaChi.isNotEmpty()) {
                    khiChonDiaChi(danhSachDiaChi.first())
                }
            }
            dangTai = false
        } catch (e: Exception) {
            dangTai = false
            loi = "Lỗi kết nối"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {
        when {
            dangTai -> Text("Đang tải địa chỉ...", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            loi.isNotEmpty() -> Text(loi, color = Color.Red)
            danhSachDiaChi.isEmpty() -> Text("Chưa có địa chỉ giao hàng. Hãy cập nhật trong hồ sơ.")
            else -> {
                val dc = danhSachDiaChi.first()
                LaunchedEffect(dc) { khiChonDiaChi(dc) }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF0D71A3))
                    Spacer(Modifier.width(8.dp))
                    Text("Địa chỉ nhận hàng", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                Text("${dc.TenNguoiNhan} | ${dc.SDTNguoiNhan}", fontWeight = FontWeight.Bold)
                Text(dc.DiaChiChiTiet)
            }
        }
    }
}

@Composable
fun SanPhamDonHang(danhSachSanPham: List<SachtrongGioHang>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text("Sản phẩm", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        danhSachSanPham.forEach { sanPham ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = sanPham.AnhBia,
                    contentDescription = sanPham.TenSach,
                    modifier = Modifier
                        .size(width = 60.dp, height = 80.dp)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(sanPham.TenSach, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    // Ép kiểu hiển thị
                    Text("${sanPham.GiaBan.toInt()} VNĐ", color = Color.Red, fontSize = 14.sp)
                    Text("x${sanPham.SoLuong}", fontSize = 14.sp, color = Color.Gray)
                }
            }
            Divider(color = Color(0xFFEEEEEE))
        }
    }
}

@Composable
fun MaKhuyenMai(khuyenMai: KhuyenMai?, onChonKhuyenMai: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.List, contentDescription = null, tint = Color(0xFF0D71A3))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (khuyenMai != null) {
                    Text("Mã: ${khuyenMai.MaCode}", fontWeight = FontWeight.Bold)
                    Text("Giảm ${khuyenMai.GiaTriGiam.toInt()} VNĐ", color = Color.Red)
                } else {
                    Text("Chọn mã khuyến mãi")
                }
            }
            TextButton(onClick = onChonKhuyenMai) { Text("Chọn >", color = Color.Gray) }
        }
    }
}

@Composable
fun KhuVucGhiChuChoShop(ghiChu: String, khiThayDoi: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(12.dp)
    ) {
        Text("Tin nhắn cho người bán", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = ghiChu,
            onValueChange = khiThayDoi,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Lưu ý cho người bán...") },
            maxLines = 2
        )
    }
}

@Composable
fun PTVanChuyen(danhSach: List<PhuongThucVanChuyen>, dangChon: PhuongThucVanChuyen, khiChon: (PhuongThucVanChuyen) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(12.dp)) {
        Text("Phương thức vận chuyển", fontWeight = FontWeight.Bold)
        danhSach.forEach { vc ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { khiChon(vc) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = vc == dangChon, onClick = { khiChon(vc) })
                Column {
                    Text(vc.ten)
                    Text("${vc.phi} VNĐ", color = Color.Red, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun PTThanhToan(phuongThucDangChon: String, khiDoiPhuongThuc: (String) -> Unit) {
    val danhSachThanhToan = listOf(
        PhuongThucThanhToanUI("TienMat", "Thanh toán khi nhận hàng", Icons.Default.Payments),
        PhuongThucThanhToanUI("ChuyenKhoan", "Chuyển khoản ngân hàng", Icons.Default.AccountBalance),
        PhuongThucThanhToanUI("ViDienTu", "Ví điện tử", Icons.Default.AccountBalanceWallet)
    )

    Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(12.dp)) {
        Text("Phương thức thanh toán", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        danhSachThanhToan.forEach { pt ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { khiDoiPhuongThuc(pt.ma) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = pt.icon, contentDescription = null, tint = if (pt.ma == phuongThucDangChon) Color(0xFF0D71A3) else Color.Gray)
                Spacer(Modifier.width(12.dp))
                Text(pt.ten, modifier = Modifier.weight(1f))
                RadioButton(selected = pt.ma == phuongThucDangChon, onClick = { khiDoiPhuongThuc(pt.ma) })
            }
        }
    }
}

@Composable
fun ChiTietThanhToan(tongTienSanPham: Int, phiVanChuyen: Int, giamPhiShip: Int, tongThanhToan: Int) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(12.dp)) {
        Text("Chi tiết thanh toán", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        DongTien("Tổng tiền hàng", tongTienSanPham)
        DongTien("Phí vận chuyển", phiVanChuyen)
        if (giamPhiShip > 0) DongTien("Giảm giá vận chuyển", -giamPhiShip, Color.Red)
        Divider(Modifier.padding(vertical = 8.dp))
        DongTien("Tổng thanh toán", tongThanhToan, Color(0xFF0D71A3), true)
    }
}

@Composable
fun DongTien(tieuDe: String, soTien: Int, mau: Color = Color.Black, dam: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(tieuDe)
        Text("$soTien VNĐ", color = mau, fontWeight = if (dam) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
// Đã sửa tham số thành Int cho đồng bộ
fun ThanhTongCongDatHang(tongTien: Int, dangXuLy: Boolean, khiDatHang: () -> Unit) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Tổng thanh toán", fontSize = 14.sp)
                Text("$tongTien VNĐ", color = Color(0xFF0D71A3), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = khiDatHang,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(4.dp),
                enabled = !dangXuLy
            ) {
                if (dangXuLy) Text("Đang xử lý...") else Text("Đặt hàng")
            }
        }
    }
}