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
import com.example.bookstore.Components.* import com.example.bookstore.Model.Sach

@Composable
fun ManHinhTrangChu(
    navController: NavController,
    onSachClick: (Sach) -> Unit,
) {
    var danhSachSach by remember { mutableStateOf<List<Sach>>(emptyList()) }
    var textTimKiem by remember { mutableStateOf("") }
    var currentFilter by remember { mutableStateOf<FilterCriteria?>(null) }

    // API Call
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

    // --- LOGIC LỌC ĐÃ SỬA ---
    val danhSachHienThi = remember(danhSachSach, textTimKiem, currentFilter) {
        var ketQua = danhSachSach

        // 1. Tìm kiếm theo tên
        if (textTimKiem.isNotBlank()) {
            ketQua = ketQua.filter { it.TenSach.contains(textTimKiem, ignoreCase = true) }
        }

        // 2. Bộ lọc nâng cao
        currentFilter?.let { filter ->
            // --- BƯỚC QUAN TRỌNG: Chuẩn hóa Input Giá ---
            // Input từ ThanhTimKiem chỉ cho nhập số, nhưng ta cần parse an toàn
            fun cleanPrice(input: String): Double? {
                if (input.isBlank()) return null
                return input.replace("[^\\d]".toRegex(), "").toDoubleOrNull()
            }

            val minVal = cleanPrice(filter.minPrice) ?: 0.0
            val maxVal = cleanPrice(filter.maxPrice) ?: Double.MAX_VALUE

            // Lọc theo khoảng giá (So sánh Double trực tiếp)
            if (filter.minPrice.isNotBlank() || filter.maxPrice.isNotBlank()) {
                ketQua = ketQua.filter { sach ->
                    sach.GiaBan in minVal..maxVal
                }
            }

            // Lọc theo đánh giá (Rating)
            if (filter.minRating > 0) {
                // Giả sử sach.DiemDanhGia có dữ liệu, nếu không mặc định là 5 để test hoặc 0
                ketQua = ketQua.filter { it.DiemDanhGia >= filter.minRating }
            }

            // Sắp xếp
            ketQua = when (filter.sortOption) {
                SortOption.PRICE_ASC -> ketQua.sortedBy { it.GiaBan }
                SortOption.PRICE_DESC -> ketQua.sortedByDescending { it.GiaBan }
                // Mới nhất: Dựa vào Mã Sách (ID càng lớn càng mới)
                SortOption.NEWEST -> ketQua.sortedByDescending { it.MaSach }
                // Bán chạy: Dựa vào SoLuongDaBan
                SortOption.BEST_SELLING -> ketQua.sortedByDescending { it.SoLuongDaBan }
                else -> ketQua
            }
        }
        ketQua
    }

    // Các danh sách cho màn hình chính (giữ nguyên logic)
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

            // Kiểm tra xem có đang lọc hay không
            val dangLoc = textTimKiem.isNotBlank() || currentFilter != null

            if (!dangLoc) {
                // GIAO DIỆN MẶC ĐỊNH
                item { BannerQuangCao(danhSachSach = danhSachSach, onXemNgayClick = onSachClick) }
                item { MucSachNgang("Sách Bán Chạy", sachBanChay, onSachClick) }
                item { MucSachNgang("Sách Kinh Dị", sachKinhDi, onSachClick) }
                item { MucSachNgang("Sách Văn Học", sachVanHoc, onSachClick) }
                item { MucSachNgang("Sách Tâm Lý", sachTamLy, onSachClick) }
                item { Spacer(modifier = Modifier.height(30.dp)) }
            } else {
                // GIAO DIỆN KẾT QUẢ LỌC
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

// --- HÀM FORMAT TIỀN (Giữ lại để hiển thị, không dùng để tính toán) ---
fun formatTienTe(gia: Double): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${formatter.format(gia)} đ"
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
    Column(modifier = Modifier.width(120.dp).clickable { onClick() }) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            AsyncImage(
                model = sach.AnhBia,
                contentDescription = null,
                modifier = Modifier.height(170.dp).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = sach.TenSach, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        // Sửa lại: Dùng trực tiếp sach.GiaBan (Double)
        Text(text = formatTienTe(sach.GiaBan), color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun ItemSachTimKiem(sach: Sach, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = sach.AnhBia,
                contentDescription = null,
                modifier = Modifier.width(60.dp).height(90.dp).clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = sach.TenSach, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(text = "Thể loại: ${sach.TenTheLoai ?: "Khác"}", fontSize = 12.sp, color = Color.Gray)
                // Sửa lại: Dùng trực tiếp sach.GiaBan (Double)
                Text(text = formatTienTe(sach.GiaBan), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Red)
            }
        }
    }
}

@Composable
fun TieuDeMuc(tieuDe: String) {
    Text(text = tieuDe, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, bottom = 12.dp))
}