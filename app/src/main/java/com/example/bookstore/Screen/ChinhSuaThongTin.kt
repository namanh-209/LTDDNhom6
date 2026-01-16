package com.example.bookstore.Screen

import CapNhatThongTinRequest
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import com.example.bookstore.Components.unAccent // Import hàm unAccent
import com.example.bookstore.KhungGiaoDien
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun ChinhSuaThongTin(
    navController: NavController,
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

    var tenNguoiNhan by remember { mutableStateOf(user?.TenNguoiNhan ?: user?.HoTen ?: "") }
    var sdtNguoiNhan by remember { mutableStateOf(user?.SDTNguoiNhan ?: user?.SoDienThoai ?: "") }
    var diaChi by remember { mutableStateOf(user?.DiaChi ?: "") }

    var dangXuLy by remember { mutableStateOf(false) }

    KhungGiaoDien(
        tieuDe = "Chỉnh sửa hồ sơ",
        onBackClick = onBackClick,
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = { navController.navigate("khuyenmai") },
        onProfileClick = { navController.navigate("trangtaikhoan")}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // --- ẢNH ĐẠI DIỆN ---
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
                        .background(Color(0xFF0D71A3), CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                }
            }
            Text("Thay đổi ảnh đại diện", color = Color(0xFF0D71A3), fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))

            Spacer(Modifier.height(24.dp))

            // === THÔNG TIN TÀI KHOẢN ===
            Text("THÔNG TIN TÀI KHOẢN", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    CustomTextField(value = hoTen, onValueChange = { hoTen = it }, label = "Họ và tên (User)", icon = Icons.Default.Person)

                    CustomTextField(value = soDienThoai, onValueChange = { soDienThoai = it }, label = "Số điện thoại đăng nhập", icon = Icons.Default.Phone, isNumber = true)

                    // CHO PHÉP NHẬP DẤU BÌNH THƯỜNG (Sẽ bỏ dấu khi bấm Lưu)
                    CustomTextField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Default.Email)

                    DatePickerField(
                        value = ngaySinh,
                        onDateSelected = { selectedDate -> ngaySinh = selectedDate },
                        label = "Ngày sinh"
                    )

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

            Spacer(Modifier.height(20.dp))

            // === THÔNG TIN NHẬN HÀNG ===
            Text("THÔNG TIN NHẬN HÀNG", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    CustomTextField(value = tenNguoiNhan, onValueChange = { tenNguoiNhan = it }, label = "Tên người nhận", icon = Icons.Default.PersonPin)
                    CustomTextField(value = sdtNguoiNhan, onValueChange = { sdtNguoiNhan = it }, label = "SĐT người nhận", icon = Icons.Default.ContactPhone, isNumber = true)
                    CustomTextField(value = diaChi, onValueChange = { diaChi = it }, label = "Địa chỉ giao hàng", icon = Icons.Default.LocationOn)
                }
            }

            Spacer(Modifier.height(24.dp))

            // === NÚT LƯU THAY ĐỔI ===
            Button(
                onClick = {
                    if (user == null) return@Button
                    scope.launch {
                        dangXuLy = true
                        try {
                            // 🔥 QUAN TRỌNG: BỎ DẤU EMAIL TẠI ĐÂY (TRƯỚC KHI GỬI ĐI)
                            val emailClean = unAccent(email)

                            val request = CapNhatThongTinRequest(
                                maNguoiDung = user.MaNguoiDung,
                                hoTen = hoTen,
                                soDienThoai = soDienThoai,
                                email = emailClean, // Gửi email đã xử lý
                                gioiTinh = gioiTinh,
                                ngaySinh = ngaySinh,
                                tenNguoiNhan = tenNguoiNhan,
                                sdtNguoiNhan = sdtNguoiNhan,
                                diaChi = diaChi
                            )

                            val response = RetrofitClient.api.capNhatThongTin(request)

                            if (response.status == "success") {
                                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                                BienDungChung.userHienTai = user.copy(
                                    HoTen = hoTen,
                                    SoDienThoai = soDienThoai,
                                    Email = emailClean, // Cập nhật lại email không dấu vào app
                                    GioiTinh = gioiTinh,
                                    NgaySinh = ngaySinh,
                                    TenNguoiNhan = tenNguoiNhan,
                                    SDTNguoiNhan = sdtNguoiNhan,
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D71A3)),
                shape = RoundedCornerShape(10.dp),
                enabled = !dangXuLy
            ) {
                if (dangXuLy) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("LƯU THAY ĐỔI", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(50.dp))
        }
    }
}

// === COMPONENT CHỌN NGÀY ===
@Composable
fun DatePickerField(value: String, onDateSelected: (String) -> Unit, label: String) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    try {
        if (value.isNotEmpty()) {
            val parts = value.split("-")
            if (parts.size == 3) calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        }
    } catch (e: Exception) { }

    val datePickerDialog = DatePickerDialog(
        context, { _, y, m, d -> onDateSelected(String.format("%d-%02d-%02d", y, m + 1, d)) },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        value = value, onValueChange = {}, label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = Color(0xFF0D71A3)) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        singleLine = true, readOnly = true, shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0D71A3), focusedLabelColor = Color(0xFF0D71A3)
        ),
        interactionSource = remember { MutableInteractionSource() }.also { src ->
            LaunchedEffect(src) { src.interactions.collect { if (it is PressInteraction.Release) datePickerDialog.show() } }
        }
    )
}

// === COMPONENT Ô NHẬP LIỆU (ĐÃ FIX LỖI GÕ) ===
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
        onValueChange = onValueChange, // Không can thiệp sửa đổi text tại đây
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFF0D71A3)) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
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