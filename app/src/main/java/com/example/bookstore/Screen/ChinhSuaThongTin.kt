package com.example.bookstore.Screen

import CapNhatThongTinRequest
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.KhungGiaoDien // Import KhungGiaoDien của bạn
import kotlinx.coroutines.launch

@Composable
fun ChinhSuaThongTin(navController: NavController,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user = BienDungChung.userHienTai

    // State dữ liệu
    var hoTen by remember { mutableStateOf(user?.HoTen ?: "") }
    var soDienThoai by remember { mutableStateOf(user?.SoDienThoai ?: "") }
    var email by remember { mutableStateOf(user?.Email ?: "") }
    var gioiTinh by remember { mutableStateOf(user?.GioiTinh ?: "Nu") }
    var ngaySinh by remember { mutableStateOf(user?.NgaySinh ?: "") }
    var diaChi by remember { mutableStateOf(user?.DiaChi ?: "") }

    var dangXuLy by remember { mutableStateOf(false) }

    // === SỬ DỤNG KHUNG GIAO DIỆN CHUẨN ===
    KhungGiaoDien(
        tieuDe = "Chỉnh sửa hồ sơ",
        onBackClick = onBackClick, // TRANG CON -> CÓ BACK (được truyền từ AppNavGraph)
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = { navController.navigate("khuyenmai") },
        onProfileClick = { navController.navigate("trangtaikhoan")}
            ) { paddingValues ->

        // Nội dung chính nằm trong padding của KhungGiaoDien
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(paddingValues) // Quan trọng: Tránh bị che bởi TopBar/BottomBar
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // --- 1. ẢNH ĐẠI DIỆN ---
            Box(contentAlignment = Alignment.BottomEnd) {
                if (!user?.AnhDaiDien.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user!!.AnhDaiDien)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFF0D71A3), CircleShape) // Dùng màu xanh chuẩn
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                }
            }
            Text(
                "Thay đổi ảnh đại diện",
                color = Color(0xFF0D71A3),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(24.dp))

            // --- 2. FORM NHẬP LIỆU ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Thông tin cá nhân", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Spacer(Modifier.height(16.dp))

                    CustomTextField(value = hoTen, onValueChange = { hoTen = it }, label = "Họ và tên", icon = Icons.Default.Person)
                    CustomTextField(value = soDienThoai, onValueChange = { soDienThoai = it }, label = "Số điện thoại", icon = Icons.Default.Phone, isNumber = true)
                    CustomTextField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Default.Email)
                    CustomTextField(value = diaChi, onValueChange = { diaChi = it }, label = "Địa chỉ nhận hàng", icon = Icons.Default.LocationOn)
                    CustomTextField(value = ngaySinh, onValueChange = { ngaySinh = it }, label = "Ngày sinh (YYYY-MM-DD)", icon = Icons.Default.CalendarToday)

                    Spacer(Modifier.height(12.dp))

                    Text("Giới tính", fontSize = 14.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = gioiTinh == "Nam", onClick = { gioiTinh = "Nam" }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0D71A3)))
                        Text("Nam", modifier = Modifier.clickable { gioiTinh = "Nam" })
                        Spacer(Modifier.width(24.dp))
                        RadioButton(selected = gioiTinh == "Nu", onClick = { gioiTinh = "Nu" }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0D71A3)))
                        Text("Nữ", modifier = Modifier.clickable { gioiTinh = "Nu" })
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- 3. NÚT LƯU THÔNG TIN ---
            // Đặt ở đây thay vì bottomBar của Scaffold cũ
            Button(
                onClick = {
                    if (user == null) return@Button
                    scope.launch {
                        dangXuLy = true
                        try {
                            val request = CapNhatThongTinRequest(
                                maNguoiDung = user.MaNguoiDung,
                                hoTen = hoTen,
                                soDienThoai = soDienThoai,
                                email = email,
                                gioiTinh = gioiTinh,
                                ngaySinh = ngaySinh,
                                diaChi = diaChi
                            )

                            val response = RetrofitClient.api.capNhatThongTin(request)

                            if (response.status == "success") {
                                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                                BienDungChung.userHienTai = user.copy(
                                    HoTen = hoTen,
                                    SoDienThoai = soDienThoai,
                                    Email = email,
                                    GioiTinh = gioiTinh,
                                    NgaySinh = ngaySinh,
                                    DiaChi = diaChi
                                )
                                onBackClick()
                            } else {
                                Toast.makeText(context, "Lỗi: ${response.message}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            dangXuLy = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D71A3)), // Màu xanh đồng bộ
                shape = RoundedCornerShape(10.dp),
                enabled = !dangXuLy
            ) {
                if (dangXuLy) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("LƯU THAY ĐỔI", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(30.dp)) // Khoảng trống dưới cùng
        }
    }
}

// Component ô nhập liệu (Đã cấu hình sửa lỗi gõ Tiếng Việt)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isNumber: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFF0D71A3)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),

        // Cấu hình bàn phím để hỗ trợ Tiếng Việt tốt hơn
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = if (isNumber) KeyboardType.Phone else KeyboardType.Text,
            imeAction = ImeAction.Next
        ),

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0D71A3),
            focusedLabelColor = Color(0xFF0D71A3),
            cursorColor = Color(0xFF0D71A3)
        )
    )
}