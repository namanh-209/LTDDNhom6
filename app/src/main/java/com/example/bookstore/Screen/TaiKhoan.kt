package com.example.bookstore.Screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.DiaChi
import com.example.bookstore.Model.User

@Composable
fun TaiKhoan(
    navController: NavController,
) {
    val nguoiDung = BienDungChung.userHienTai
    var diaChiHienThi by remember { mutableStateOf<DiaChi?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val userId = nguoiDung?.MaNguoiDung ?: return@LaunchedEffect
            val response = RetrofitClient.api.layDanhSachDiaChi(userId)
            if (response.status == "success" && !response.data.isNullOrEmpty()) {
                diaChiHienThi = response.data.first()
            } else {
                diaChiHienThi = null
            }
        } catch (e: Exception) {
            Log.e("TaiKhoan", "Lỗi lấy địa chỉ: ${e.message}")
            diaChiHienThi = null
        }
    }

    // Hiển thị xác nhận Đăng xuất
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Đăng xuất", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn đăng xuất không?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Thực hiện đăng xuất
                        BienDungChung.userHienTai = null
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Màu đỏ
                ) {
                    Text("Đồng ý")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    KhungGiaoDien(
        tieuDe = "Tài khoản",
        onBackClick = null,
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = { navController.navigate("khuyenmai") },
        onProfileClick = { }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp)
        ) {

            item { HeaderNguoiDung(nguoiDung) }

            item {
                Spacer(Modifier.height(16.dp))
                ActionRow(navController)
            }

            item {
                Spacer(Modifier.height(16.dp))
                ThongTinCaNhan(nguoiDung)
            }

            item {
                Spacer(Modifier.height(16.dp))
                ThongTinNhanHang(diaChiHienThi)
            }

            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        showLogoutDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Đăng xuất", color = Color.Red)
                }
            }
        }
    }
}

//Phần người dùng
@Composable
fun HeaderNguoiDung(nguoiDung: User?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(Color(0xFFE0E0E0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!nguoiDung?.AnhDaiDien.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(nguoiDung!!.AnhDaiDien)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    Icons.Default.Person,
                    null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = nguoiDung?.HoTen ?: "Khách",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = nguoiDung?.SoDienThoai
                    ?.let {
                        if (it.length >= 7) it.replaceRange(3, 7, "****") else it
                    } ?: "Chưa đăng nhập",
                color = Color.Gray
            )
        }
    }
}

// Các nút chức năng
@Composable
fun ActionRow(navController: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionItem(Icons.Default.Assignment, "Đơn hàng") { navController.navigate("dondamua") }
            ActionItem(Icons.Default.List, "Lịch sử") { navController.navigate("lichsumuahang")}
            ActionItem(Icons.Default.ShoppingCart, "Giỏ hàng") { navController.navigate("giohang") }
            ActionItem(Icons.Default.Favorite, "Yêu thích") { navController.navigate("danhsachyeuthich") }
            ActionItem(Icons.Default.Settings, "Cài đặt") { navController.navigate("caidat") }
        }
    }
}

@Composable
fun RowScope.ActionItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() }
    ) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(4.dp))
        Text(title, fontSize = 12.sp)
    }
}

// Thông tin cá nhân
@Composable
fun ThongTinCaNhan(nguoiDung: User?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                "Thông tin cá nhân",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    InfoLine("Họ và tên:", nguoiDung?.HoTen)
                    InfoLine("Số điện thoại:", nguoiDung?.SoDienThoai)
                    InfoLine("Email:", nguoiDung?.Email ?: "---")
                    InfoLine(
                        "Ngày sinh:",
                        dinhDangNgay(nguoiDung?.NgaySinh)
                    )
                    val gioiTinhText = when (nguoiDung?.GioiTinh) {
                        "Nam" -> "Nam"
                        "Nu" -> "Nữ"
                        else -> "Khác"
                    }
                    InfoLine("Giới tính:", gioiTinhText)
                }
            }
        }
    }
}

fun dinhDangNgay(ngay: String?): String {
    if (ngay.isNullOrBlank() || ngay == "0000-00-00") return "---"

    try {
        val ngaySach = if (ngay.contains("T")) ngay.split("T")[0] else ngay
        val parts = ngaySach.split("-")
        if (parts.size == 3) {
            return "${parts[2]}/${parts[1]}/${parts[0]}"
        }
    } catch (e: Exception) {
        return ngay
    }
    return ngay
}

@Composable
fun InfoLine(label: String, value: String?) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label ", color = Color.Gray)
        Text(value ?: "", fontWeight = FontWeight.Medium)
    }
}

//Thông tin nhận hàng
@Composable
fun ThongTinNhanHang(diaChi: DiaChi?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                "Thông tin nhận hàng",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(Modifier.height(12.dp))

            if (diaChi != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        InfoLine("Họ và tên:", diaChi.TenNguoiNhan)
                        InfoLine("Số điện thoại:", diaChi.SDTNguoiNhan)
                        InfoLine("Địa chỉ:", diaChi.DiaChiChiTiet)
                    }
                }
            } else {
                Text(
                    "Chưa có địa chỉ nhận hàng",
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}