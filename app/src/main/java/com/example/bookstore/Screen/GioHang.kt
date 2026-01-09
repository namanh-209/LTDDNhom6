package com.example.bookstore.Screen


import CapNhatGioHangRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.SachtrongGioHang
import kotlinx.coroutines.launch




@Composable
fun GioHang(
    navController: NavHostController,
    onBackClick: (() -> Unit)? = null
) {
    var danhSachSach by remember { mutableStateOf<List<SachtrongGioHang>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }
    var loi by remember { mutableStateOf("") }

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



    KhungGiaoDien(
        tieuDe = "Giỏ hàng",
        onBackClick = onBackClick,
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = {},
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { padding ->

        when {
            dangTai -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            loi.isNotEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(loi, color = Color.Red)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(12.dp)
                ) {
                    items(danhSachSach) { sach ->
                        GioHangItem(
                            sach = sach,
                            onTang = {
                                xuLyTangGiam(
                                    sach = sach,
                                    soLuongThayDoi = 1,
                                    capNhatDanhSach = { danhSachSach = it },
                                    setLoi = { loi = it }
                                )
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
                            }
                        )

                    Spacer(modifier = Modifier.height(10.dp))
                    }

                }
            }
        }
    }
}

@Composable
fun GioHangItem(
    sach: SachtrongGioHang,
    onTang: () -> Unit,
    onGiam: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            Text(sach.TenSach, fontWeight = FontWeight.Bold)
            Text("Giá: ${sach.GiaBan} VNĐ", color = Color.Red)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onGiam) {
                Icon(Icons.Default.Remove, contentDescription = "Giảm")
            }

            Text(
                text = sach.SoLuong.toString(),
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onTang) {
                Icon(Icons.Default.Add, contentDescription = "Tăng")
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
            setLoi("Lỗi cập nhật giỏ hàng")
        }
    }
}
