package com.example.bookstore.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Model.DonHang

import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale

/* ===================== MAP TRẠNG THÁI BACKEND -> UI ===================== */
fun mapTrangThai(trangThai: String): String {
    return when (trangThai.trim()) {
        "MoiDat","DangXuLy" -> "Chờ xác nhận"
        "DangGiao" -> "Đang giao hàng"
        "HoanThanh" -> "Hoàn thành"
        "DaHuy" -> "Đã hủy"
        else -> "Không xác định"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuanLyDonHangAdmin(
    navController: NavController,
    bamQuayLai: () -> Unit // Hàm này sẽ xử lý logout về màn login
) {
    var danhSachDonHang by remember { mutableStateOf<List<DonHang>>(emptyList()) }
    var dangTaiDuLieu by remember { mutableStateOf(true) }

    // [MỚI] Biến trạng thái hiện Dialog Đăng xuất
    var hienThiDialogDangXuat by remember { mutableStateOf(false) }

    // Biến Tab
    var tabDangChon by remember { mutableIntStateOf(0) }
    val danhSachTab = listOf(
        "Tất cả",
        "Chờ xác nhận",
        "Đang giao hàng",
        "Hoàn thành",
        "Đã hủy"
    )

    val context = LocalContext.current

    /* ===================== LOAD DATA ===================== */
    LaunchedEffect(Unit) {
        try {
            val res = RetrofitClient.api.layDanhSachDonHang()
            if (res.status == "success" && res.data != null) {
                danhSachDonHang = res.data
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi tải: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            dangTaiDuLieu = false
        }
    }

    /* ===================== FILTER ===================== */
    val danhSachLoc = remember(tabDangChon, danhSachDonHang) {
        if (tabDangChon == 0) {
            danhSachDonHang
        } else {
            danhSachDonHang.filter {
                mapTrangThai(it.trangThai) == danhSachTab[tabDangChon]
            }
        }
    }

    /* ===================== DIALOG XÁC NHẬN ĐĂNG XUẤT (MỚI) ===================== */
    if (hienThiDialogDangXuat) {
        AlertDialog(
            onDismissRequest = { hienThiDialogDangXuat = false },
            title = { Text(text = "Đăng xuất", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn đăng xuất không?") },
            confirmButton = {
                Button(
                    onClick = {
                        hienThiDialogDangXuat = false
                        bamQuayLai() // Gọi hàm logout khi bấm Đồng ý
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Màu đỏ
                ) {
                    Text("Đăng xuất")
                }
            },
            dismissButton = {
                TextButton(onClick = { hienThiDialogDangXuat = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            }
        )
    }

    /* ===================== UI CHÍNH ===================== */
    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Quản Lý Đơn Hàng", fontWeight = FontWeight.Bold, color = Color.White)
                },
                navigationIcon = {
                    // [SỬA] Bấm nút này sẽ hiện Dialog chứ không thoát ngay
                    IconButton(onClick = { hienThiDialogDangXuat = true }) {
                        Icon(Icons.Default.Output, contentDescription = "Đăng xuất", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D71A3))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            /* ===================== TAB ===================== */
            ScrollableTabRow(
                selectedTabIndex = tabDangChon,
                containerColor = Color.White,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabDangChon]),
                        color = Color(0xFF0D71A3)
                    )
                }
            ) {
                danhSachTab.forEachIndexed { index, title ->
                    Tab(
                        selected = tabDangChon == index,
                        onClick = { tabDangChon = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (tabDangChon == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            /* ===================== CONTENT ===================== */
            when {
                dangTaiDuLieu -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                danhSachLoc.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Không có đơn hàng nào", color = Color.Gray)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(danhSachLoc) { donHang ->
                            ItemDonHang(donHang) {
                                val gson = Gson()
                                val json = gson.toJson(donHang)
                                val encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString())

                                // Chuyển sang màn hình chi tiết
                                navController.navigate("admin_order_detail/$encodedJson")
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ===================== ITEM ===================== */
@Composable
fun ItemDonHang(donHang: DonHang, onClick: () -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val trangThaiUI = mapTrangThai(donHang.trangThai)

    val (mauChu, mauNen, icon) = when (trangThaiUI) {
        "Hoàn thành" ->
            Triple(Color(0xFF155724), Color(0xFFD4EDDA), Icons.Default.CheckCircle)
        "Đã hủy" ->
            Triple(Color(0xFF721C24), Color(0xFFF8D7DA), Icons.Default.Cancel)
        "Đang giao hàng" ->
            Triple(Color(0xFF0C5460), Color(0xFFD1ECF1), Icons.Default.LocalShipping)
        "Chờ xác nhận" ->
            Triple(Color(0xFF856404), Color(0xFFFFF3CD), Icons.Default.HourglassEmpty)
        else ->
            Triple(Color.Gray, Color.LightGray, Icons.Default.Help)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Thêm màu nền trắng cho Card nổi bật
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            /* ===== MÃ ĐƠN + TRẠNG THÁI ===== */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#DH${donHang.maDonHang}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(mauNen)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, null, tint = mauChu, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(trangThaiUI, color = mauChu, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Divider(color = Color(0xFFEEEEEE))

            /* ===== THÔNG TIN NGƯỜI ĐẶT ===== */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(6.dp))
                Text("Người đặt: ${donHang.tenNguoiMua}", fontSize = 14.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Ngày đặt: ${donHang.ngayDat}",
                    fontSize = 14.sp
                )
            }

            Divider(color = Color(0xFFEEEEEE))

            /* ===== TỔNG TIỀN ===== */
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "Tổng tiền: ${formatter.format(donHang.tongTien)}",
                    color = Color(0xFFD32F2F), // Màu đỏ đậm
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}