package com.example.bookstore.ui.screen

import YeuThichRequest
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.Sach
import com.example.bookstore.Model.TheLoai
import com.example.bookstore.ThanhTimKiem // Đảm bảo đã có file này
import kotlinx.coroutines.launch

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

/* ===================== ITEM SÁCH (Đã thêm onClick) ===================== */
@Composable
fun SachItem(
    sach: Sach,
    isFavorite: Boolean, // <--- thêm trạng thái tim
    onAddCart: () -> Unit,
    onFavorite: () -> Unit,
    onClick: () -> Unit
) {
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
            AsyncImage(
                model = sach.AnhBia,
                contentDescription = sach.TenSach,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray, RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Inside
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = sach.TenSach, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Tác giả: ${sach.TenTacGia ?: "Đang cập nhật"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${sach.GiaBan.toInt()} VND", color = Color.Red, style = MaterialTheme.typography.bodyMedium)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onAddCart,
                    modifier = Modifier.background(Color(0xFF1E88E5), RoundedCornerShape(8.dp)).size(36.dp)
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


/* ===================== MÀN HÌNH CHÍNH (Đã sửa tìm kiếm & chuyển trang) ===================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DanhSachSach(
    navController: NavController,
    onSachClick: (Sach) -> Unit
) {
    var tuKhoaTimKiem by remember { mutableStateOf("") }
    var selectedTheLoaiId by remember { mutableIntStateOf(0) }

    var dsSachGoc by remember { mutableStateOf<List<Sach>>(emptyList()) }
    var dsTheLoai by remember { mutableStateOf<List<TheLoai>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    val favoriteStates = remember { mutableStateMapOf<Int, Boolean>() } // Lưu trạng thái tim
    val scope = rememberCoroutineScope()

    // ================= Tải Thể Loại =================
    LaunchedEffect(Unit) {
        try {
            val res = RetrofitClient.api.layDanhSachTheLoai()
            if (res.data != null) dsTheLoai = res.data
        } catch (e: Exception) { Log.e("API", "Lỗi: ${e.message}") }
    }

    // ================= Tải Sách theo Thể Loại =================
    LaunchedEffect(selectedTheLoaiId) {
        scope.launch {
            loading = true
            try {
                dsSachGoc = if (selectedTheLoaiId == 0) {
                    RetrofitClient.api.layDanhSachSach().data ?: emptyList()
                } else {
                    RetrofitClient.api.laySachTheoTheLoai(selectedTheLoaiId).data ?: emptyList()
                }
                // Khởi tạo trạng thái tim
                dsSachGoc.forEach { favoriteStates[it.MaSach] = false }
            } catch (e: Exception) {
                dsSachGoc = emptyList()
            } finally {
                loading = false
            }
        }
    }

    // ================= Lọc tìm kiếm (client-side) =================
    val dsSachHienThi = remember(dsSachGoc, tuKhoaTimKiem) {
        if (tuKhoaTimKiem.isBlank()) dsSachGoc
        else dsSachGoc.filter { it.TenSach.contains(tuKhoaTimKiem, ignoreCase = true) }
    }

    KhungGiaoDien(
        tieuDe = "Danh mục sách",
        onBackClick = null,
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { },
        onCartClick = { },
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // Thanh tìm kiếm
            ThanhTimKiem(
                tuKhoa = tuKhoaTimKiem,
                khiGoChu = { tuKhoaTimKiem = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tab Thể Loại
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
                                isFavorite = favoriteStates[sach.MaSach] ?: false, // <-- truyền trạng thái tim
                                onAddCart = { /* TODO: thêm giỏ hàng */ },
                                onFavorite = {
                                    // Toggle trạng thái tim đỏ
                                    favoriteStates[sach.MaSach] = !(favoriteStates[sach.MaSach] ?: false)

                                    // Gọi API toggle nếu muốn
                                    scope.launch {
                                        try {
                                            RetrofitClient.api.toggleYeuThich(
                                                YeuThichRequest(
                                                    MaNguoiDung = BienDungChung.userHienTai!!.MaNguoiDung,
                                                    MaSach = sach.MaSach
                                                )
                                            )
                                        } catch (_: Exception) { }
                                    }
                                },
                                onClick = { onSachClick(sach) }
                            )
                        }
                    }
                }
            }
        }
    }
}
