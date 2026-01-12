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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BannerQuangCao
import com.example.bookstore.Model.Sach

@Composable
fun ManHinhTrangChu(
    navController: NavController, // Thêm cái này
    onSachClick: (Sach) -> Unit,
) {
    var danhSachSach by remember { mutableStateOf<List<Sach>>(emptyList()) }
    var textTimKiem by remember { mutableStateOf("") }

    // Gọi API lấy danh sách sách
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

    // Lọc dữ liệu hiển thị theo thể loại
    val sachBanChay = remember(danhSachSach) { danhSachSach.take(5) }
    val sachKinhDi = remember(danhSachSach) { danhSachSach.filter { it.TenTheLoai?.contains("Trinh Thám", ignoreCase = true) == true } }
    val sachVanHoc = remember(danhSachSach) { danhSachSach.filter { it.TenTheLoai?.contains("Văn Học", ignoreCase = true) == true } }
    val sachTamLy = remember(danhSachSach) { danhSachSach.filter { it.TenTheLoai?.contains("Tâm Lý", ignoreCase = true) == true } }

    // Gọi khung màn hình chính (Đã xử lý ẩn nút Back bên trong)
    KhungGiaoDien(
        tieuDe = "BOOK STORE",
        onBackClick = null,
        onHomeClick = { /* Đang ở Home */ },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = { navController.navigate("khuyenmai") },
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Thanh tìm kiếm
            item { ThanhTimKiem(tuKhoa = textTimKiem, khiGoChu = { textTimKiem = it }) }

            if (textTimKiem.isEmpty()) {
                // --- TRƯỜNG HỢP 1: HIỂN THỊ MẶC ĐỊNH ---
                item { BannerQuangCao(danhSachSach = danhSachSach, onXemNgayClick = onSachClick) }

                item { MucSachNgang("Sách Bán Chạy", sachBanChay, onSachClick) }
                item { MucSachNgang("Sách Kinh Dị", sachKinhDi, onSachClick) }
                item { MucSachNgang("Sách Văn Học", sachVanHoc, onSachClick) }
                item { MucSachNgang("Sách Tâm Lý", sachTamLy, onSachClick) }
                item { Spacer(modifier = Modifier.height(30.dp)) }
            } else {
                // --- TRƯỜNG HỢP 2: TÌM KIẾM ---
                val ketQua = danhSachSach.filter { it.TenSach.contains(textTimKiem, ignoreCase = true) }
                if (ketQua.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Không tìm thấy sách!", color = Color.Gray)
                        }
                    }
                } else {
                    items(ketQua) { sach ->
                        ItemSachTimKiem(sach = sach, onClick = { onSachClick(sach) })
                    }
                }
            }
        }
    }
}

// --- KHUNG MÀN HÌNH CHÍNH (SCAFFOLD RIÊNG CHO HOME) ---

// --- CÁC COMPONENT CON (GIỮ NGUYÊN) ---

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
        Text(text = formatTienTe(sach.GiaBan.toInt()), color = MauXanh, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                Text(text = formatTienTe(sach.GiaBan.toInt()), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MauXanh)
            }
        }
    }
}

@Composable
fun TieuDeMuc(tieuDe: String) {
    Text(text = tieuDe, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, bottom = 12.dp))
}

fun formatTienTe(gia: Int): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${formatter.format(gia)} đ"
}
