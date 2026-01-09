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
import com.example.bookstore.Model.User // Đổi NguoiDung thành User cho khớp hệ thống
import kotlinx.coroutines.launch

@Composable
fun TaiKhoan(
    navController: NavController,
) {
    // 1. Lấy thông tin User từ Biến dùng chung (Đã lưu khi đăng nhập)
    val nguoiDung = BienDungChung.userHienTai

    // 2. State để lưu địa chỉ mặc định (Lấy từ API)
    var diaChiMacDinh by remember { mutableStateOf<DiaChi?>(null) }
    val scope = rememberCoroutineScope()

    // 3. Gọi API lấy địa chỉ khi màn hình mở
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val userId = nguoiDung?.MaNguoiDung ?: return@launch

                // Gọi API lấy danh sách địa chỉ
                val response = RetrofitClient.api.layDiaChi(userId)

                // Lọc lấy địa chỉ mặc định
                if (response.data != null && response.data.isNotEmpty()) {
                    val listDiaChi = response.data
                    // Tìm cái nào MacDinh == 1 (hoặc true), nếu không có lấy cái đầu tiên
                    diaChiMacDinh = listDiaChi.find { it.MacDinh == 1 } ?: listDiaChi.first()
                }
            } catch (e: Exception) {
                Log.e("TaiKhoan", "Lỗi lấy địa chỉ: ${e.message}")
            }
        }
    }

    KhungGiaoDien(tieuDe = "Tài khoản",
        onBackClick = null, // TRANG CHÍNH -> KHÔNG BACK
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { },
        onProfileClick = { /* Đang ở Tài khoản */ }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp)
        ) {

            // Truyền dữ liệu vào các thành phần con
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
                ThongTinNhanHang(diaChiMacDinh)
            }

            // Thêm nút Đăng xuất ở cuối cho đầy đủ chức năng
            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        BienDungChung.userHienTai = null // Xóa session
                        navController.navigate("login") { // Về màn login
                            popUpTo(0) { inclusive = true }
                        }
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
            // Nếu có ảnh đại diện thì hiện ảnh, không thì hiện icon
            // Nếu có ảnh đại diện thì hiện ảnh, không thì hiện icon
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
                        if (it.length >= 7)
                            it.replaceRange(3, 7, "****")
                        else it
                    } ?: "Chưa đăng nhập",
                color = Color.Gray
            )
        }
    }
}

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
            // Gắn sự kiện click chuyển màn hình
            ActionItem(Icons.Default.List, "Đơn hàng") { navController.navigate("dondamua") }
            ActionItem(Icons.Default.List, "Lịch sử") { }
            ActionItem(Icons.Default.ShoppingCart, "Giỏ hàng") {  }
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

@Composable
fun ThongTinCaNhan(nguoiDung: User?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Thông tin cá nhân", fontWeight = FontWeight.Bold)
                Text("Cập nhật", color = Color.Red, fontSize = 14.sp)
            }

            Spacer(Modifier.height(12.dp))

            // Hiển thị dữ liệu thật
            InfoLine("Họ và tên:", nguoiDung?.HoTen)
            InfoLine("Số điện thoại:", nguoiDung?.SoDienThoai)
            InfoLine("Email:", nguoiDung?.Email ?: "---")

            val gioiTinhText = when(nguoiDung?.GioiTinh) {
                "Nam" -> "Nam"; "Nu" -> "Nữ"; else -> "Khác"
            }
            InfoLine("Giới tính:", gioiTinhText)
        }
    }
}

@Composable
fun InfoLine(label: String, value: String?) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label ", color = Color.Gray)
        Text(value ?: "", fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ThongTinNhanHang(diaChi: DiaChi?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Thông tin nhận hàng", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(12.dp))

            if (diaChi != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(diaChi.TenNguoiNhan, fontWeight = FontWeight.Bold)
                        Text(diaChi.SDTNguoiNhan, color = Color.Gray, fontSize = 14.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(diaChi.DiaChiChiTiet, fontSize = 15.sp)
                    }
                }
            } else {
                Text(
                    "Chưa có địa chỉ mặc định",
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}