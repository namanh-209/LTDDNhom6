package com.example.bookstore

import DanhSachYeuThichScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookstore.Model.DonHang
import com.example.bookstore.Screen.LoginScreen
import com.example.bookstore.Screen.RegisterScreen
import com.example.bookstore.Model.Sach
import com.example.bookstore.Model.SachtrongGioHang
import com.example.bookstore.Screen.CaiDat
import com.example.bookstore.Screen.ChinhSuaThongTin
import com.example.bookstore.Screen.DanhGia
import com.example.bookstore.Screen.DonDaMua
import com.example.bookstore.Screen.GioHang
import com.example.bookstore.Screen.KhuyenMai
import com.example.bookstore.Screen.ManHinhThayDoiMatKhau
import com.example.bookstore.Screen.TaiKhoan
import com.example.bookstore.ui.screen.DanhSachSach

// === THÊM CÁC IMPORT NÀY ===
import com.example.bookstore.Screen.QuanLyDonHangAdmin
import com.example.bookstore.Screen.ChiTietDonHangAdmin // Màn hình chi tiết
import com.example.bookstore.Screen.ManHinhLichSuMuaHang
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    var selectedSach by remember { mutableStateOf<Sach?>(null) }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onRegisterClick = { navController.navigate("register") },
                onLoginSuccess = { user ->
                    val vaiTro = user.VaiTro?.trim() ?: ""

                    if (vaiTro.equals("admin", ignoreCase = true) || vaiTro.equals("quanly", ignoreCase = true)) {
                        // Điều hướng sang Admin
                        navController.navigate("admin_quanlydonhang") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        // Điều hướng sang Khách hàng
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(onLoginClick = { navController.popBackStack() })
        }

        // --- CÁC TRANG CHÍNH (TRANG LỚN - KHÔNG CÓ BACK) ---

        // 1. Trang chủ
        composable("home") {
            ManHinhTrangChu(
                navController = navController,
                onSachClick = { sach ->
                    selectedSach = sach
                    navController.navigate("detail")
                }
            )
        }

        // === KHU VỰC ADMIN (SỬA Ở ĐÂY) ===

        // 1. Màn hình Danh sách đơn hàng
        composable("admin_quanlydonhang") {
            QuanLyDonHangAdmin(
                navController = navController,
                bamQuayLai = {
                    navController.navigate("login") {
                        popUpTo("admin_quanlydonhang") { inclusive = true }
                    }
                }
            )
        }

        // 2. Màn hình Chi tiết đơn hàng (THÊM MỚI)
        composable("admin_order_detail/{donHangJson}") { backStackEntry ->
            // Lấy chuỗi JSON từ argument
            val jsonEncoded = backStackEntry.arguments?.getString("donHangJson") ?: ""

            // Giải mã URL (vì lúc gửi ta đã Encode)
            val json = URLDecoder.decode(jsonEncoded, StandardCharsets.UTF_8.toString())

            // Chuyển JSON thành Object DonHang
            val donHang = Gson().fromJson(json, DonHang::class.java)

            // Gọi màn hình chi tiết
            ChiTietDonHangAdmin(
                navController = navController,
                donHang = donHang
            )
        }

        // === KẾT THÚC KHU VỰC ADMIN ===

        // 2. Trang Danh mục
        composable("trangdanhsach") {
            DanhSachSach(
                navController = navController,
                onSachClick = { sach ->
                    selectedSach = sach
                    navController.navigate("detail")
                }
            )
        }

        // 3. Trang Tài khoản
        composable("trangtaikhoan") {
            TaiKhoan(navController = navController)
        }

        // 4. Trang khuyến mãi
        composable("khuyenmai") {
            KhuyenMai(navController = navController)
        }

        // --- CÁC TRANG CON ---

        composable("detail") {
            if (selectedSach != null) {
                ManHinhChiTietSach(
                    sach = selectedSach!!,
                    navController = navController,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }


        composable("dondamua") {
            DonDaMua(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("caidat") {
            CaiDat(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("danhsachyeuthich") {
            DanhSachYeuThichScreen(
                navController = navController,
                onBackClick = { navController.navigate("trangtaikhoan") },
                onAddCart = { sach -> },
                onRemoveFavorite = { sach -> },
                onSachClick = { sach ->
                    selectedSach = sach
                    navController.navigate("detail")
                }
            )
        }

        composable("giohang") {
            GioHang(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onSachClick = { sach ->
                    selectedSach = sach
                    navController.navigate("detail")
                }
            )
        }

        composable("thaydoimatkhau") {
            ManHinhThayDoiMatKhau (
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("chinhsuathongtin") {
            ChinhSuaThongTin(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("danhgia/{maSach}/{maDonHang}") { backStackEntry ->
            DanhGia(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                maSach = backStackEntry.arguments!!.getString("maSach")!!.toInt(),
                maDonHang = backStackEntry.arguments!!.getString("maDonHang")!!.toInt()
            )
        }

        composable("thanhtoan") {
            // 1. Lấy dữ liệu giỏ hàng từ màn hình trước gửi sang
            val gioHang = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<List<SachtrongGioHang>>("gioHang")
                ?: emptyList()

            // 2. Gọi màn hình thanh toán
            ManHinhThanhToan(
                navController = navController,
                danhSachSanPham = gioHang,
                BamQuayLai = { navController.popBackStack() }
            )
        }

        composable("lichsumuahang"){
            ManHinhLichSuMuaHang(
                navController=navController,
                onBackClick = { navController.popBackStack()  }
            )
        }
    }
}