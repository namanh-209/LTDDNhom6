package com.example.bookstore

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BannerQuangCao
import com.example.bookstore.model.Sach


@Composable
fun ManHinhTrangChu(
    onSachClick: (Sach) -> Unit // Nhận hàm chuyển trang từ MainActivity
) {
    // 1. State lưu dữ liệu
    var danhSachSach by remember { mutableStateOf<List<Sach>>(emptyList()) }
    var textTimKiem by remember { mutableStateOf("") }

    // 2. Gọi API lấy danh sách sách
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.layDanhSachSach()
            if (response.status == "success") {
                danhSachSach = response.data
            }
        } catch (e: Exception) {
            Log.e("LoiAPI", "Lỗi lấy sách: ${e.message}")
        }
    }

    // 3. Lọc dữ liệu hiển thị mặc định
    val sachBanChay = remember(danhSachSach) { danhSachSach.take(5) }
    val sachKinhDi = remember(danhSachSach) {
        danhSachSach.filter { it.TenTheLoai?.contains("Trinh Thám - Kinh Dị", ignoreCase = true) == true }
    }
    val sachVanHoc = remember(danhSachSach) {
        danhSachSach.filter { it.TenTheLoai?.contains("Văn Học", ignoreCase = true) == true }
    }
    val sachTamLy = remember(danhSachSach) {
        danhSachSach.filter { it.TenTheLoai?.contains("Tâm Lý - Kỹ Năng", ignoreCase = true) == true }
    }

    // 4. Giao diện chính
    KhungManHinhChinh(tieuDe = "BOOK STORE") { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // --- A. THANH TÌM KIẾM ---
            item {
                ThanhTimKiem(
                    tuKhoa = textTimKiem,
                    khiGoChu = { textTimKiem = it }
                )
            }

            // --- B. LOGIC HIỂN THỊ ---
            if (textTimKiem.isEmpty()) {
                // === TRƯỜNG HỢP 1: MẶC ĐỊNH (Banner + List Ngang) ===

                // 1. Banner (Đã truyền sự kiện click)
                item {
                    BannerQuangCao(
                        danhSachSach = danhSachSach,
                        onXemNgayClick = onSachClick
                    )
                }

                // 2. Sách bán chạy
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    TieuDeMuc("Sách Bán Chạy")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sachBanChay) { sach ->
                            ItemSach(sach = sach, onClick = { onSachClick(sach) })
                        }
                    }
                }

                // 3. Sách Kinh dị
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    TieuDeMuc("Sách Kinh Dị")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sachKinhDi) { sach ->
                            ItemSach(sach = sach, onClick = { onSachClick(sach) })
                        }
                    }
                }

                // 4. Sách Văn học
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    TieuDeMuc("Sách Văn Học")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sachVanHoc) { sach ->
                            ItemSach(sach = sach, onClick = { onSachClick(sach) })
                        }
                    }
                }

                // 5. Sách Tâm lý
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    TieuDeMuc("Sách Tâm Lý")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sachTamLy) { sach ->
                            ItemSach(sach = sach, onClick = { onSachClick(sach) })
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }

            } else {
                // === TRƯỜNG HỢP 2: TÌM KIẾM (List Dọc) ===

                val ketQua = danhSachSach.filter {
                    it.TenSach.contains(textTimKiem, ignoreCase = true)
                }

                if (ketQua.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(text = "Không tìm thấy sách nào!", color = Color.Gray)
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Tìm thấy ${ketQua.size} kết quả:",
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                            fontWeight = FontWeight.Bold,
                            color = MauXanh
                        )
                    }

                    // Dùng ItemSachTimKiem (Thẻ ngang)
                    items(ketQua) { sach ->
                        ItemSachTimKiem(
                            sach = sach,
                            onClick = { onSachClick(sach) }
                        )
                    }
                }
            }
        }
    }
}

// Item sách hiển thị ở trang chủ (Dạng dọc)
@Composable
fun ItemSach(
    sach: Sach,
    onClick: () -> Unit // Thêm tham số click
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() } // Bắt sự kiện click
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            AsyncImage(
                model = sach.AnhBia,
                contentDescription = null,
                modifier = Modifier.height(170.dp).fillMaxWidth(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = sach.TenSach,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 18.sp
        )
        Text(
            text = formatTienTe(sach.GiaBan),
            color = MauXanh,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

// Item sách hiển thị khi tìm kiếm (Dạng ngang)
@Composable
fun ItemSachTimKiem(
    sach: Sach,
    onClick: () -> Unit // Thêm tham số click
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }, // Bắt sự kiện click
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = sach.AnhBia,
                contentDescription = null,
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = sach.TenSach,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Thể loại: ${sach.TenTheLoai ?: "Khác"}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatTienTe(sach.GiaBan),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MauXanh
                )
            }
        }
    }
}

// --- KHUNG MÀN HÌNH CHÍNH (SCAFFOLD) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhungManHinhChinh(
    tieuDe: String,
    noiDung: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = tieuDe,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MauXanh)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MauXanh,
                contentColor = Color.White,
                tonalElevation = 0.dp
            ) {
                NutMenuVector1("Trang chủ", Icons.Default.Home)
                NutMenuAnh1("Theo dõi", R.drawable.list)
                NutMenuVector1("Giỏ hàng", Icons.Default.ShoppingCart)
                NutMenuAnh1("Khuyến mãi", R.drawable.icon_giam_gia)
                NutMenuVector1("Tài khoản", Icons.Default.Person)
            }
        }
    ) { paddingValues ->
        noiDung(paddingValues)
    }
}

// --- CÁC HÀM TIỆN ÍCH & MENU ---

@Composable
fun TieuDeMuc(tieuDe: String) {
    Text(
        text = tieuDe,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
    )
}

@Composable
fun RowScope.NutMenuVector1(ten: String, icon: ImageVector) {
    NavigationBarItem(
        selected = false,
        onClick = {},
        icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp)) },
        label = { Text(ten, fontSize = 10.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            unselectedIconColor = Color.White.copy(alpha = 0.7f),
            unselectedTextColor = Color.White.copy(alpha = 0.7f),
            indicatorColor = Color.Transparent
        )
    )
}

@Composable
fun RowScope.NutMenuAnh1(ten: String, idAnh: Int) {
    NavigationBarItem(
        selected = false,
        onClick = {},
        icon = { Icon(painter = painterResource(id = idAnh), contentDescription = null, modifier = Modifier.size(24.dp)) },
        label = { Text(ten, fontSize = 10.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            unselectedIconColor = Color.White.copy(alpha = 0.7f),
            unselectedTextColor = Color.White.copy(alpha = 0.7f),
            indicatorColor = Color.Transparent
        )
    )
}

// Hàm format tiền tệ (Ví dụ: 50000 -> 50.000 đ)
fun formatTienTe(gia: Double): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${formatter.format(gia)} đ"
}