package com.example.bookstore

import CapNhatGioHangRequest
import YeuThichRequest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.Model.DanhGia
import com.example.bookstore.Model.Sach
import com.example.bookstore.Model.SachtrongGioHang
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManHinhChiTietSach(
    sach: Sach,
    navController: NavController,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user = BienDungChung.userHienTai

    // --- STATE QUẢN LÝ ---
    var selectedTab by remember { mutableIntStateOf(0) }
    var listDanhGia by remember { mutableStateOf<List<DanhGia>>(emptyList()) }

    // State cho nút Yêu thích (Tim)
    var isFavorite by remember { mutableStateOf(false) }

    // --- LOGIC KHỞI TẠO (Gọi API khi mở màn hình) ---
    LaunchedEffect(sach.MaSach) {
        scope.launch {
            try {
                // 1. Lấy danh sách đánh giá
                val responseDG = RetrofitClient.api.layDanhSachDanhGia(sach.MaSach)
                listDanhGia = responseDG.data ?: emptyList()

                // 2. Kiểm tra xem sách này có trong danh sách yêu thích của User không
                if (user != null) {
                    val responseYT = RetrofitClient.api.layDanhSachYeuThich(user.MaNguoiDung)
                    val danhSachYeuThich = responseYT.data ?: emptyList()
                    // Nếu tìm thấy sách trong list yêu thích -> set tim màu đỏ
                    isFavorite = danhSachYeuThich.any { it.MaSach == sach.MaSach }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin chi tiết", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFFEEEEEE),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // --- NÚT YÊU THÍCH (XỬ LÝ LOGIC) ---
                    IconButton(onClick = {
                        if (user == null) {
                            Toast.makeText(context, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show()
                        } else {
                            // 1. Đổi trạng thái UI ngay lập tức cho mượt (Optimistic Update)
                            isFavorite = !isFavorite

                            // 2. Gọi API cập nhật server
                            scope.launch {
                                try {
                                    RetrofitClient.api.toggleYeuThich(
                                        YeuThichRequest(user.MaNguoiDung, sach.MaSach)
                                    )
                                    // Thông báo nhẹ

                                } catch (e: Exception) {
                                    isFavorite = !isFavorite
                                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }) {
                        // Đổi Icon và Màu sắc dựa trên biến isFavorite
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp), // To hơn xíu cho dễ bấm
                            tint = if (isFavorite) Color.Red else Color.Gray // Đỏ nếu thích, Xám nếu không
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp).height(24.dp).background(Color.Gray))

                    // --- NÚT THÊM VÀO GIỎ HÀNG (XỬ LÝ LOGIC) ---
                    IconButton(onClick = {
                        if (user == null) {
                            Toast.makeText(context, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show()
                        } else {
                            scope.launch {
                                try {
                                    RetrofitClient.api.capNhatGioHang(
                                        CapNhatGioHangRequest(
                                            MaNguoiDung = user.MaNguoiDung,
                                            MaSach = sach.MaSach,
                                            SoLuong = 1 // Mặc định thêm 1 cuốn
                                        )
                                    )
                                    Toast.makeText(context, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF1976D2) // Màu xanh cho giỏ hàng
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Nút Mua ( chuyển sang màn thanh toán)// mới thêm
                    Button(
                        onClick = {
                            if (user == null) {
                                Toast.makeText(context, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val muaNgay = listOf(
                                SachtrongGioHang(
                                    MaGioHang = 0,
                                    MaSach = sach.MaSach,
                                    TenSach = sach.TenSach,
                                    TenTacGia = sach.TenTacGia ?: "Đang cập nhật",
                                    GiaBan = sach.GiaBan,
                                    SoLuong = 1,
                                    AnhBia = sach.AnhBia
                                )
                            )

                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("gioHang", muaNgay)

                            navController.navigate("thanhtoan")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Mua ngay", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        // ... (Phần hiển thị nội dung bên dưới GIỮ NGUYÊN CODE CŨ của bạn) ...
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // Phần Header Sách
            Row(modifier = Modifier.padding(16.dp)) {
                Card(elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(4.dp)) {
                    AsyncImage(
                        model = sach.AnhBia, contentDescription = null,
                        modifier = Modifier.width(100.dp).height(150.dp), contentScale = ContentScale.Crop
                    )
                }
                fun formatGia(gia: Double): String {
                    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
                    return "${formatter.format(gia)} VND"
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(sach.TenSach, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${formatGia(sach.GiaBan)} ", color = Color.Red, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tác giả: ${sach.TenTacGia ?: "Đang cập nhật"}", fontSize = 14.sp)

                    val diemTB = if (listDanhGia.isNotEmpty()) {
                        String.format("%.1f", listDanhGia.map { it.SoSao }.average())
                    } else "0"
                    Text("Đánh giá: $diemTB/5 (${listDanhGia.size} lượt)", fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tabs
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Column(modifier = Modifier.weight(1f).clickable { selectedTab = 0 }, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Thông tin", fontSize = 16.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                    if (selectedTab == 0) Box(modifier = Modifier.height(2.dp).fillMaxWidth().background(Color.Black))
                }
                Column(modifier = Modifier.weight(1f).clickable { selectedTab = 1 }, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Đánh giá", fontSize = 16.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                    if (selectedTab == 1) Box(modifier = Modifier.height(2.dp).fillMaxWidth().background(Color.Black))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Tab Thông tin
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ThongTinDong("Tên sách", sach.TenSach)
                    ThongTinDong("Tác giả", sach.TenTacGia ?: "Đang cập nhật")
                    ThongTinDong("Thể loại", sach.TenTheLoai ?: "Khác")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tóm tắt nội dung:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = sach.MoTa ?: "Đang cập nhật mô tả...", style = LocalTextStyle.current.copy(lineHeight = 20.sp), color = Color.DarkGray)
                }
            } else {
                // Tab Đánh giá (Dùng ItemDanhGiaThat đẹp đã sửa ở bước trước)
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    if (listDanhGia.isEmpty()) {
                        Text("Chưa có đánh giá nào.", modifier = Modifier.padding(20.dp).align(Alignment.CenterHorizontally), color = Color.Gray)
                    } else {
                        listDanhGia.forEach { danhGia ->
                            ItemDanhGiaThat(danhGia)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ... Các hàm phụ trợ ItemDanhGiaThat, ThongTinDong giữ nguyên như cũ ...
@Composable
fun ThongTinDong(tieuDe: String, noiDung: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "$tieuDe: ", fontWeight = FontWeight.Medium, modifier = Modifier.width(110.dp))
        Text(text = noiDung, color = Color.Black)
    }
}

@Composable
fun ItemDanhGiaThat(danhGia: DanhGia) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (danhGia.AnhDaiDien != null && danhGia.AnhDaiDien.isNotEmpty()) {
                    AsyncImage(
                        model = danhGia.AnhDaiDien,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(Color(0xFFE0E0E0)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = danhGia.HoTen.take(1).uppercase(), color = Color(0xFF1565C0), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = danhGia.HoTen, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF333333))
                    Text(text = danhGia.NgayDanhGia.take(10), fontSize = 12.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    val starColor = if (index < danhGia.SoSao) Color(0xFFFFC107) else Color(0xFFE0E0E0)
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = starColor, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFFAFAFA), shape = RoundedCornerShape(8.dp)).padding(10.dp)
            ) {
                Text(text = danhGia.BinhLuan, fontSize = 14.sp, color = Color(0xFF424242), lineHeight = 20.sp)
            }
        }
    }
}