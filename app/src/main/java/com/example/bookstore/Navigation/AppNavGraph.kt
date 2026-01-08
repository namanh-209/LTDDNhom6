package com.example.bookstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookstore.Screen.LoginScreen
import com.example.bookstore.Screen.RegisterScreen
import com.example.bookstore.Model.Sach
import com.example.bookstore.Screen.DonDaMua
import com.example.bookstore.Screen.TaiKhoan
import com.example.bookstore.Screen.TaiKhoan


// Import thêm màn hình Trang chủ và Chi tiết
// (Đảm bảo bạn đã có các file này ở bước trước)

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // Biến tạm để lưu cuốn sách đang được chọn xem chi tiết
    var selectedSach by remember { mutableStateOf<Sach?>(null) }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // 1. MÀN HÌNH ĐĂNG NHẬP
        composable("login") {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    // Đăng nhập xong thì vào Home và xóa lịch sử Login để không back lại được
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 2. MÀN HÌNH ĐĂNG KÝ
        composable("register") {
            RegisterScreen(
                onLoginClick = {
                    navController.popBackStack() // Quay lại màn login
                }
            )
        }

        // 3. MÀN HÌNH TRANG CHỦ
        composable("home") {
            ManHinhTrangChu(
                onSachClick = { sach ->
                    // Khi bấm vào sách: Lưu sách đó lại và chuyển sang màn chi tiết
                    selectedSach = sach
                    navController.navigate("detail")
                },
                onProfileClick={

                    navController.navigate("trangtaikhoan")
                }
            )
        }

        // 4. MÀN HÌNH CHI TIẾT SÁCH
        composable("detail") {
            if (selectedSach != null) {
                ManHinhChiTietSach(
                    sach = selectedSach!!,
                    onBackClick = {
                        navController.popBackStack() // Quay lại trang chủ
                    }
                )
            }
        }
        composable("dondamua") {
            DonDaMua()
        }
        composable("trangtaikhoan") {
            TaiKhoan(navController=navController)
        }
    }
}