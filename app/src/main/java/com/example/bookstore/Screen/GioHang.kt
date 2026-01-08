package com.example.bookstore.Screen

import GioHangViewModel
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.SachtrongGioHang


@Composable
fun GioHang(navController: NavController,onBackClick:()-> Unit,
    viewModel: GioHangViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val danhSachSach = viewModel.danhSachSach
    val isLoading = viewModel.isLoading
    val error = viewModel.error

    LaunchedEffect(Unit) {
        val user = BienDungChung.userHienTai

        if (user != null) {
            viewModel.taiGioHang(maNguoiDung = user.MaNguoiDung)
        } else {
            viewModel.Hienthiloi("Chưa đăng nhập")
        }
    }



    KhungGiaoDien(
        tieuDe = "Cài đặt",
        onBackClick = onBackClick, // TRANG CON -> CÓ BACK (được truyền từ AppNavGraph)
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { paddingValues ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error.isNotEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = error, color = Color.Red)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(12.dp)
                ) {
                    items(danhSachSach) { sach ->
                        GioHangItem(
                            sach = sach,
                            //nút tăng giảm đổi trực tiếp từ viewmodel
                            onTang = { viewModel.tangSoLuong(sach) },
                            onGiam = { viewModel.giamSoLuong(sach) }
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
            .background(
                color = Color(0xFFEFEFEF),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = sach.AnhBia,
            contentDescription = "Ảnh bìa",
            modifier = Modifier.size(70.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(text = sach.TenSach, fontWeight = FontWeight.Bold)
            Text(text = "Tác giả: ${sach.TenTacGia}", fontSize = 12.sp)
            Text(
                text = "${sach.GiaBan} VNĐ",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onGiam) {
                Icon(Icons.Default.Remove, contentDescription = "Giảm")
            }

            Text(text = sach.SoLuong.toString())

            IconButton(onClick = onTang) {
                Icon(Icons.Default.Add, contentDescription = "Tăng")
            }
        }
    }
}
