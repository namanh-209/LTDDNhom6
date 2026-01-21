package com.example.bookstore

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.*
import com.example.bookstore.Model.Sach

@Composable
fun ManHinhTrangChu(
    navController: NavController,
    onSachClick: (Sach) -> Unit,
) {
    var danhSachSach by remember { mutableStateOf<List<Sach>>(emptyList()) }
    var textTimKiem by remember { mutableStateOf("") }
    var currentFilter by remember { mutableStateOf<FilterCriteria?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.layDanhSachSach()
            if (response.status == "success") {
                danhSachSach = response.data ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("LoiAPI", "Lỗi lấy sách: ${e.message}")
        }
    }

    val danhSachHienThi = remember(danhSachSach, textTimKiem, currentFilter) {
        var ketQua = danhSachSach
        if (textTimKiem.isNotBlank()) {
            ketQua = ketQua.filter { it.TenSach.contains(textTimKiem, ignoreCase = true) }
        }
        currentFilter?.let { filter ->
            fun cleanPrice(input: String): Double? {
                if (input.isBlank()) return null
                return input.replace("[^\\d]".toRegex(), "").toDoubleOrNull()
            }
            val minVal = cleanPrice(filter.minPrice) ?: 0.0
            val maxVal = cleanPrice(filter.maxPrice) ?: Double.MAX_VALUE
            if (filter.minPrice.isNotBlank() || filter.maxPrice.isNotBlank()) {
                ketQua = ketQua.filter { sach -> sach.GiaBan in minVal..maxVal }
            }
            if (filter.minRating > 0) {
                ketQua = ketQua.filter { it.DiemDanhGia >= filter.minRating }
            }
            ketQua = when (filter.sortOption) {
                SortOption.PRICE_ASC -> ketQua.sortedBy { it.GiaBan }
                SortOption.PRICE_DESC -> ketQua.sortedByDescending { it.GiaBan }
                SortOption.NEWEST -> ketQua.sortedByDescending { it.MaSach }
                SortOption.BEST_SELLING -> ketQua.sortedByDescending { it.SoLuongDaBan }
                else -> ketQua
            }
        }
        ketQua
    }

    val sachBanChay = remember(danhSachSach) { danhSachSach.sortedByDescending { it.SoLuongDaBan }.take(5) }
    val sachKinhDi = remember(danhSachSach) { danhSachSach.filter { it.TenTheLoai?.contains("Trinh Thám", ignoreCase = true) == true } }
    val sachVanHoc = remember(danhSachSach) { danhSachSach.filter { it.TenTheLoai?.contains("Văn Học", ignoreCase = true) == true } }
    val sachTamLy = remember(danhSachSach) { danhSachSach.filter { it.TenTheLoai?.contains("Tâm Lý", ignoreCase = true) == true } }

    KhungGiaoDien(
        tieuDe = "BOOK STORE",
        onBackClick = null,
        onHomeClick = { },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = { navController.navigate("khuyenmai") },
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)
        ) {
            item {
                ThanhTimKiem(
                    tuKhoa = textTimKiem,
                    khiGoChu = { textTimKiem = it },
                    onApplyFilter = { criteria -> currentFilter = criteria }
                )
            }
            val dangLoc = textTimKiem.isNotBlank() || currentFilter != null
            if (!dangLoc) {
                item { BannerQuangCao(danhSachSach = danhSachSach, onXemNgayClick = onSachClick) }
                item { MucSachNgang("Sách Bán Chạy", sachBanChay, onSachClick) }
                item { MucSachNgang("Sách Kinh Dị", sachKinhDi, onSachClick) }
                item { MucSachNgang("Sách Văn Học", sachVanHoc, onSachClick) }
                item { MucSachNgang("Sách Tâm Lý", sachTamLy, onSachClick) }
                item { Spacer(modifier = Modifier.height(30.dp)) }
            } else {
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Kết quả: ${danhSachHienThi.size} sách", fontWeight = FontWeight.Bold)
                        TextButton(onClick = {
                            currentFilter = null
                            textTimKiem = ""
                        }) {
                            Text("Xóa bộ lọc", color = Color.Red)
                        }
                    }
                }
                if (danhSachHienThi.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                            Text("Không tìm thấy sách phù hợp!", color = Color.Gray)
                        }
                    }
                } else {
                    items(danhSachHienThi) { sach ->
                        ItemSachTimKiem(sach = sach, onClick = { onSachClick(sach) })
                    }
                }
            }
        }
    }
}

fun formatTienTe(gia: Double): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${formatter.format(gia)} VND"
}

@Composable
fun MucSachNgang(tieuDe: String, listSach: List<Sach>, onClick: (Sach) -> Unit) {
    if (listSach.isNotEmpty()) {
        Spacer(modifier = Modifier.height(24.dp))
        TieuDeMuc(tieuDe)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listSach) { sach -> ItemSach(sach = sach, onClick = { onClick(sach) }) }
        }
    }
}

@Composable
fun ItemSach(sach: Sach, onClick: () -> Unit) {
    // Logic kiểm tra hết hàng
    val hetHang = sach.SoLuongTon <= 0

    Column(modifier = Modifier.width(120.dp).clickable { onClick() }) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box {
                AsyncImage(
                    model = sach.AnhBia,
                    contentDescription = null,
                    modifier = Modifier.height(170.dp).fillMaxWidth().alpha(if(hetHang) 0.5f else 1f),
                    contentScale = ContentScale.Crop
                )
                // Overlay Hết hàng
                if (hetHang) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "HẾT HÀNG",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = sach.TenSach, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Text(text = formatTienTe(sach.GiaBan), color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        // Hiển thị số lượng đã bán
        Text(text = "Đã bán: ${sach.SoLuongDaBan}", fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
fun ItemSachTimKiem(sach: Sach, onClick: () -> Unit) {
    val hetHang = sach.SoLuongTon <= 0

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box {
                AsyncImage(
                    model = sach.AnhBia,
                    contentDescription = null,
                    modifier = Modifier.width(60.dp).height(90.dp).clip(RoundedCornerShape(4.dp)).alpha(if(hetHang) 0.5f else 1f),
                    contentScale = ContentScale.Crop
                )
                if (hetHang) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(90.dp)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("HẾT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = sach.TenSach, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(text = "Thể loại: ${sach.TenTheLoai ?: "Khác"}", fontSize = 12.sp, color = Color.Gray)
                // Hiển thị đã bán
                Text(text = "Đã bán: ${sach.SoLuongDaBan}", fontSize = 12.sp, color = Color(0xFF0D71A3))
                Text(text = formatTienTe(sach.GiaBan), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Red)
            }
        }
    }
}

@Composable
fun TieuDeMuc(tieuDe: String) {
    Text(text = tieuDe, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, bottom = 12.dp))
}