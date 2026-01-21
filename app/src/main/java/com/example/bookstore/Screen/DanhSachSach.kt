package com.example.bookstore.ui.screen

import CapNhatGioHangRequest
import YeuThichRequest
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.*
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.Sach
import com.example.bookstore.Model.TheLoai
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/* ===================== TAB THỂ LOẠI ===================== */
@Composable
fun TabTheLoai(
    danhSach: List<TheLoai>,
    selectedId: Int,
    onSelect: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TabItem(ten = "Tất cả", isSelected = selectedId == 0, onClick = { onSelect(0) })
        }
        items(danhSach) { theLoai ->
            TabItem(
                ten = theLoai.TenTheLoai,
                isSelected = selectedId == theLoai.MaTheLoai,
                onClick = { onSelect(theLoai.MaTheLoai) }
            )
        }
    }
}

@Composable
fun TabItem(ten: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = ten,
        color = if (isSelected) Color.White else Color.Black,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .background(
                if (isSelected) Color(0xFF0E73A9) else Color(0xFFE0E0E0),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    )
}

/* ===================== ITEM SÁCH (ĐÃ SỬA) ===================== */
@Composable
fun SachItem(
    sach: Sach,
    isFavorite: Boolean,
    onAddCart: () -> Unit,
    onFavorite: () -> Unit,
    onClick: () -> Unit
) {
    fun formatGia(gia: Double): String {
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(gia)} VND"
    }

    // Kiểm tra hết hàng
    val hetHang = sach.SoLuongTon <= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh sách có xử lý mờ + overlay nếu hết
            Box {
                AsyncImage(
                    model = sach.AnhBia,
                    contentDescription = null,
                    modifier = Modifier
                        .width(80.dp)
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .alpha(if(hetHang) 0.5f else 1f),
                    contentScale = ContentScale.Crop
                )
                if (hetHang) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(120.dp)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("HẾT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = sach.TenSach, maxLines = 2, style = MaterialTheme.typography.bodyMedium, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                // Hiển thị đã bán
                Text(
                    text = "Đã bán: ${sach.SoLuongDaBan}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tác giả: ${sach.TenTacGia ?: "Đang cập nhật"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = formatGia(sach.GiaBan), color = Color.Red, style = MaterialTheme.typography.bodyMedium, fontSize = 16.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Nút giỏ hàng: Vô hiệu hóa nếu hết hàng
                IconButton(
                    onClick = {
                        if (!hetHang) onAddCart()
                    },
                    enabled = !hetHang, // Khoá nếu hết hàng
                    modifier = Modifier
                        .background(
                            if(hetHang) Color.Gray else Color(0xFF1E88E5), // Đổi màu xám nếu hết
                            RoundedCornerShape(8.dp)
                        )
                        .size(36.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                }

                Spacer(modifier = Modifier.height(6.dp))

                IconButton(onClick = onFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else Color.Black
                    )
                }
            }
        }
    }
}

/* ===================== MÀN HÌNH CHÍNH ===================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DanhSachSach(
    navController: NavController,
    onSachClick: (Sach) -> Unit
) {
    var tuKhoaTimKiem by remember { mutableStateOf("") }
    var selectedTheLoaiId by remember { mutableIntStateOf(0) }
    var currentFilter by remember { mutableStateOf<FilterCriteria?>(null) }
    val context = LocalContext.current
    var dsSachGoc by remember { mutableStateOf<List<Sach>>(emptyList()) }
    var dsTheLoai by remember { mutableStateOf<List<TheLoai>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    val favoriteStates = remember { mutableStateMapOf<Int, Boolean>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val res = RetrofitClient.api.layDanhSachTheLoai()
            if (res.data != null) dsTheLoai = res.data
        } catch (e: Exception) { Log.e("API", "Lỗi: ${e.message}") }
    }

    LaunchedEffect(selectedTheLoaiId) {
        loading = true
        try {
            dsSachGoc = if (selectedTheLoaiId == 0) {
                RetrofitClient.api.layDanhSachSach().data ?: emptyList()
            } else {
                RetrofitClient.api.laySachTheoTheLoai(selectedTheLoaiId).data ?: emptyList()
            }
        } finally {
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        try {
            val userId = BienDungChung.userHienTai?.MaNguoiDung ?: return@LaunchedEffect
            val res = RetrofitClient.api.layDanhSachYeuThich(userId)
            res.data?.forEach { sach -> favoriteStates[sach.MaSach] = true }
        } catch (e: Exception) { Log.e("API", "Lỗi load yêu thích") }
    }

    val dsSachHienThi = remember(dsSachGoc, tuKhoaTimKiem, currentFilter) {
        var ketQua = dsSachGoc
        if (tuKhoaTimKiem.isNotBlank()) {
            ketQua = ketQua.filter {
                it.TenSach.contains(tuKhoaTimKiem, ignoreCase = true) ||
                        (it.TenTacGia?.contains(tuKhoaTimKiem, ignoreCase = true) == true)
            }
        }
        currentFilter?.let { filter ->
            fun cleanPrice(input: String): Double {
                if (input.isBlank()) return 0.0
                return input.replace("[^\\d]".toRegex(), "").toDoubleOrNull() ?: 0.0
            }
            val minVal = cleanPrice(filter.minPrice)
            val maxVal = if (filter.maxPrice.isBlank()) Double.MAX_VALUE else cleanPrice(filter.maxPrice)
            if (filter.minPrice.isNotBlank() || filter.maxPrice.isNotBlank()) {
                ketQua = ketQua.filter { it.GiaBan in minVal..maxVal }
            }
            if (filter.minRating > 0) {
                ketQua = ketQua.filter { it.DiemDanhGia >= filter.minRating }
            }
            ketQua = when (filter.sortOption) {
                SortOption.PRICE_ASC -> ketQua.sortedBy { it.GiaBan }
                SortOption.PRICE_DESC -> ketQua.sortedByDescending { it.GiaBan }
                SortOption.NEWEST -> ketQua.sortedByDescending { it.NgayThem ?: it.MaSach.toString() }
                SortOption.BEST_SELLING -> ketQua.sortedByDescending { it.SoLuongDaBan }
                else -> ketQua
            }
        }
        ketQua
    }

    KhungGiaoDien(
        tieuDe = "Danh mục sách",
        onBackClick = null,
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = { navController.navigate("khuyenmai") },
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ThanhTimKiem(
                tuKhoa = tuKhoaTimKiem,
                khiGoChu = { tuKhoaTimKiem = it },
                onApplyFilter = { criteria -> currentFilter = criteria }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TabTheLoai(
                danhSach = dsTheLoai,
                selectedId = selectedTheLoaiId,
                onSelect = { selectedTheLoaiId = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
                    if (dsSachHienThi.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                Text("Không tìm thấy sách nào", color = Color.Gray)
                            }
                        }
                    } else {
                        items(dsSachHienThi) { sach ->
                            SachItem(
                                sach = sach,
                                isFavorite = favoriteStates[sach.MaSach] ?: false,
                                onAddCart = {
                                    scope.launch {
                                        if(BienDungChung.userHienTai == null) {
                                            Toast.makeText(context, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show()
                                            return@launch
                                        }
                                        try {
                                            RetrofitClient.api.capNhatGioHang(
                                                CapNhatGioHangRequest(
                                                    MaNguoiDung = BienDungChung.userHienTai!!.MaNguoiDung,
                                                    MaSach = sach.MaSach,
                                                    SoLuong = 1
                                                )
                                            )
                                            Toast.makeText(context, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onFavorite = {
                                    if(BienDungChung.userHienTai == null) return@SachItem
                                    val currentState = favoriteStates[sach.MaSach] ?: false
                                    favoriteStates[sach.MaSach] = !currentState
                                    scope.launch {
                                        try {
                                            RetrofitClient.api.toggleYeuThich(
                                                YeuThichRequest(
                                                    MaNguoiDung = BienDungChung.userHienTai!!.MaNguoiDung,
                                                    MaSach = sach.MaSach
                                                )
                                            )
                                        } catch (e: Exception) { }
                                    }
                                },
                                onClick = {onSachClick(sach)}
                            )
                        }
                    }
                }
            }
        }
    }
}