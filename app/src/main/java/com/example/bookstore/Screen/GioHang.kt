@file:OptIn(DelicateCoroutinesApi::class)

package com.example.bookstore.Screen

import CapNhatGioHangRequest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Import hết layout
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // Cần context để Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.Sach
import com.example.bookstore.Model.SachtrongGioHang
import com.example.bookstore.formatTienTe
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GioHang(
    navController: NavHostController,
    onBackClick: (() -> Unit)? = null,
    onSachClick: (Sach) -> Unit
) {
    var danhSachSach by remember { mutableStateOf<List<SachtrongGioHang>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }
    var loi by remember { mutableStateOf("") }
    val context = LocalContext.current // Lấy context để hiển thị Toast

    LaunchedEffect(Unit) {
        val user = BienDungChung.userHienTai
        if (user == null) {
            loi = "Bạn chưa đăng nhập"
            dangTai = false
            return@LaunchedEffect
        }

        TaiLaiGioHang(
            userId = user.MaNguoiDung,
            setDanhSach = { danhSachSach = it },
            setLoi = { loi = it }
        )

        dangTai = false
    }

    val tongTien = danhSachSach.sumOf { it.GiaBan * it.SoLuong }

    KhungGiaoDien(
        tieuDe = "Giỏ hàng",
        onBackClick = onBackClick,
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = {},
        onSaleClick = { navController.navigate("khuyenmai") },
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { padding ->

        when {
            dangTai -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            loi.isNotEmpty() -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(loi, color = Color.Red)
                }
            }
            else -> {
                Column(Modifier.fillMaxSize().padding(padding)) {
                    if (danhSachSach.isEmpty()) {
                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Giỏ hàng của bạn đang trống", color = Color.Gray, fontSize = 16.sp)
                        }
                    }

                    if (danhSachSach.isNotEmpty()) {
                        LazyColumn(Modifier.weight(1f).padding(12.dp)) {
                            items(danhSachSach) { sach ->
                                GioHangItem(
                                    sach = sach,
                                    onClick = { },
                                    onTang = {
                                        // Kiểm tra ràng buộc tồn kho trước khi gọi hàm xử lý
                                        if (sach.SoLuong < sach.SoLuongTon) {
                                            xuLyTangGiam(
                                                sach = sach,
                                                soLuongThayDoi = 1,
                                                capNhatDanhSach = { danhSachSach = it },
                                                setLoi = { loi = it }
                                            )
                                        } else {
                                            Toast.makeText(context, "Đã đạt giới hạn số lượng tồn kho!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onGiam = {
                                        if (sach.SoLuong > 1) {
                                            xuLyTangGiam(
                                                sach = sach,
                                                soLuongThayDoi = -1,
                                                capNhatDanhSach = { danhSachSach = it },
                                                setLoi = { loi = it }
                                            )
                                        }
                                    },
                                    onXoa = {
                                        xuLyXoaSanPham(
                                            sach = sach,
                                            capNhatDanhSach = { danhSachSach = it },
                                            setLoi = { loi = it }
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }

                    // Thanh toán
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Tổng tiền", color = Color.Gray)
                                Text(
                                    text = formatTienTe(tongTien),
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            val coTheThanhToan = danhSachSach.isNotEmpty()
                            Button(
                                onClick = {
                                    // Kiểm tra lại lần cuối trước khi chuyển màn hình
                                    val coSanPhamHetHang = danhSachSach.any { it.SoLuong > it.SoLuongTon }
                                    if (coSanPhamHetHang) {
                                        Toast.makeText(context, "Vui lòng cập nhật lại số lượng do vượt quá tồn kho", Toast.LENGTH_LONG).show()
                                    } else {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("gioHang", danhSachSach)
                                        navController.navigate("thanhtoan")
                                    }
                                },
                                enabled = coTheThanhToan,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD32F2F),
                                    disabledContainerColor = Color(0xFFEF9A9A)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Thanh toán")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatGia(gia: Int): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${formatter.format(gia)} VND"
}

@Composable
fun GioHangItem(
    sach: SachtrongGioHang,
    onTang: () -> Unit,
    onGiam: () -> Unit,
    onXoa: () -> Unit,
    onClick: () -> Unit,
) {
    // Kiểm tra xem đã đạt giới hạn chưa để UI phản hồi (làm mờ nút)
    val daDatGioiHan = sach.SoLuong >= sach.SoLuongTon

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFFEFEFEF), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = sach.AnhBia,
            contentDescription = null,
            modifier = Modifier.size(70.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(sach.TenSach, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Tác giả: ${sach.TenTacGia}", color = Color.LightGray)

            // Hiển thị số lượng tồn kho để người dùng biết
            Text("Kho: ${sach.SoLuongTon}", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Giá: ${formatTienTe(sach.GiaBan)} ", color = Color.Red, fontSize = 16.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
            // Nút Xoá ở trên
            Text(
                text = "Xoá",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable { onXoa() }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onGiam,
                    enabled = sach.SoLuong > 1 // Disable nếu số lượng là 1
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Giảm",
                        tint = if (sach.SoLuong > 1) Color.Black else Color.Gray
                    )
                }

                Text(
                    text = sach.SoLuong.toString(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = onTang,
                    enabled = !daDatGioiHan // Disable nếu đã đạt giới hạn
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Tăng",
                        // Đổi màu icon thành xám nếu không bấm được
                        tint = if (!daDatGioiHan) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}

suspend fun TaiLaiGioHang(
    userId: Int,
    setDanhSach: (List<SachtrongGioHang>) -> Unit,
    setLoi: (String) -> Unit
) {
    try {
        val res = RetrofitClient.api.layGioHang(userId)
        if (res.status == "success") {
            setDanhSach(res.data ?: emptyList())
        } else {
            setLoi("Không tải được giỏ hàng")
        }
    } catch (e: Exception) {
        setLoi("Lỗi mạng")
    }
}

fun xuLyTangGiam(
    sach: SachtrongGioHang,
    soLuongThayDoi: Int,
    capNhatDanhSach: (List<SachtrongGioHang>) -> Unit,
    setLoi: (String) -> Unit
) {
    val user = BienDungChung.userHienTai ?: return

    // Ràng buộc logic chặn ở tầng xử lý (phòng trường hợp UI lag)
    if (soLuongThayDoi > 0 && sach.SoLuong >= sach.SoLuongTon) {
        // Không làm gì nếu vượt quá tồn
        return
    }

    kotlinx.coroutines.GlobalScope.launch {
        try {
            RetrofitClient.api.capNhatGioHang(
                CapNhatGioHangRequest(
                    MaNguoiDung = user.MaNguoiDung,
                    MaSach = sach.MaSach,
                    SoLuong = soLuongThayDoi
                )
            )

            val res = RetrofitClient.api.layGioHang(user.MaNguoiDung)
            if (res.status == "success") {
                capNhatDanhSach(res.data ?: emptyList())
            }

        } catch (e: Exception) {
            // Xử lý lỗi UI ở main thread nếu cần
        }
    }
}

fun xuLyXoaSanPham(
    sach: SachtrongGioHang,
    capNhatDanhSach: (List<SachtrongGioHang>) -> Unit,
    setLoi: (String) -> Unit
) {
    kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val resXoa = RetrofitClient.api.xoaGioHang(sach.MaGioHang)

            if (resXoa.status != "success") {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    setLoi("Không thể xoá sản phẩm")
                }
                return@launch
            }

            val user = BienDungChung.userHienTai ?: return@launch
            val res = RetrofitClient.api.layGioHang(user.MaNguoiDung)

            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                if (res.status == "success") {
                    capNhatDanhSach(res.data ?: emptyList())
                }
            }

        } catch (e: Exception) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                setLoi("Lỗi xoá sản phẩm")
            }
        }
    }
}