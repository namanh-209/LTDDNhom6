package com.example.bookstore.Screen

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

// ================= MODEL UI =================
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
        PhuongThucVanChuyen("Giao hàng nhanh", 30000),
        PhuongThucVanChuyen("Hỏa tốc", 50000)
    )
    var vanChuyenDangChon by remember { mutableStateOf(danhSachVC.first()) }

    val phiVC = vanChuyenDangChon.phi
    val tienGiam = khuyenMaiDaChon?.GiaTriGiam?.toInt() ?: 0
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
                .background(Color(0xFFF5F5F5))
        ) {
            DiaChiNhanHang { diaChiDangChon = it }
            SanPhamDonHang(danhSachSanPham)
            MaKhuyenMai(khuyenMaiDaChon) {

                // TRUYỀN TỔNG TIỀN HÀNG (CHƯA SHIP, CHƯA GIẢM)
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("tongTien", tongTienSanPham.toDouble())

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

    LaunchedEffect(Unit) {
        user?.let {
            val res = RetrofitClient.api.layDanhSachDiaChi(it.MaNguoiDung)
            if (res.status == "success") {
                ds = res.data ?: emptyList()
                if (ds.isNotEmpty()) khiChon(ds.first())
            }
        }
    }

    Card(Modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Địa chỉ nhận hàng", fontWeight = FontWeight.Bold)
            if (ds.isNotEmpty()) {
                Text(ds.first().TenNguoiNhan)
                Text(ds.first().DiaChiChiTiet)
            }
        }
    }
}

@Composable
fun SanPhamDonHang(ds: List<SachtrongGioHang>) {
    Card(Modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Sản phẩm", fontWeight = FontWeight.Bold)
            ds.forEach {
                Row(Modifier.padding(vertical = 6.dp)) {
                    AsyncImage(it.AnhBia, null, Modifier.size(60.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(it.TenSach, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${it.GiaBan.toInt()} VNĐ x${it.SoLuong}", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun MaKhuyenMai(km: KhuyenMai?, onChon: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(8.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                km?.let { "Mã ${it.MaCode} (-${it.GiaTriGiam.toInt()}đ)" }
                    ?: "Chọn mã khuyến mãi",
                Modifier.weight(1f)
            )
            TextButton(onClick = onChon) { Text("Chọn") }
        }
    }
}

@Composable
fun KhuVucGhiChuChoShop(value: String, onChange: (String) -> Unit) {
    Card(Modifier.fillMaxWidth().padding(8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
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
    Card(Modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Vận chuyển", fontWeight = FontWeight.Bold)
            ds.forEach {
                Row(Modifier.clickable { onChon(it) }) {
                    RadioButton(it == dangChon, onClick = { onChon(it) })
                    Text("${it.ten} - ${it.phi}đ")
                }
            }
        }
    }
}

@Composable
fun PTThanhToan(dangChon: String, onChon: (String) -> Unit) {
    val ds = listOf(
        PhuongThucThanhToanUI("TienMat", "COD", Icons.Default.Payments),
        PhuongThucThanhToanUI("ChuyenKhoan", "Chuyển khoản", Icons.Default.AccountBalance)
    )
    Card(Modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Thanh toán", fontWeight = FontWeight.Bold)
            ds.forEach {
                Row(Modifier.clickable { onChon(it.ma) }) {
                    RadioButton(it.ma == dangChon, onClick = { onChon(it.ma) })
                    Text(it.ten)
                }
            }
        }
    }
}

@Composable
fun ChiTietThanhToan(tienHang: Int, ship: Int, giam: Int, tong: Int) {
    Card(Modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Chi tiết", fontWeight = FontWeight.Bold)
            DongTien("Tiền hàng", tienHang)
            DongTien("Phí ship", ship)
            if (giam > 0) DongTien("Giảm giá", -giam, Color.Red)
            Divider()
            DongTien("Tổng", tong, Color(0xFF0D71A3), true)
        }
    }
}

@Composable
fun DongTien(label: String, value: Int, color: Color = Color.Black, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label)
        Text("$value đ", color = color, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun ThanhTongCongDatHang(tongTien: Int, dangXuLy: Boolean, onDatHang: () -> Unit) {
    Surface(shadowElevation = 8.dp) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Tổng thanh toán")
                Text("$tongTien đ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onDatHang, enabled = !dangXuLy) {
                Text(if (dangXuLy) "Đang xử lý..." else "Đặt hàng")
            }
        }
    }
}
