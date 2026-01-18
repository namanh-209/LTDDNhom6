package com.example.bookstore

import DanhSachYeuThichScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

// Import các màn hình quản lý đơn hàng
import com.example.bookstore.Screen.QuanLyDonHangAdmin
import com.example.bookstore.Screen.ChiTietDonHangAdmin
import com.example.bookstore.Screen.ChiTietDonHangDat
import com.example.bookstore.Screen.ManHinhLichSuMuaHang
import com.example.bookstore.Screen.ManHinhThanhToan
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    var selectedSach by remember { mutableStateOf<Sach?>(null) }

    NavHost(navController = navController, startDestination = "login") {

        // --- ĐĂNG NHẬP & ĐĂNG KÝ ---
        composable("login") {
            LoginScreen(
                onRegisterClick = { navController.navigate("register") },
                onLoginSuccess = { user ->
                    val vaiTro = user.VaiTro?.trim() ?: ""
                    if (vaiTro.equals("admin", ignoreCase = true) || vaiTro.equals("quanly", ignoreCase = true)) {
                        navController.navigate("admin_quanlydonhang") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
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

        // --- TRANG CHỦ & CÁC TRANG CHÍNH ---
        composable("home") {
            ManHinhTrangChu(
                navController = navController,
                onSachClick = { sach ->
                    selectedSach = sach
                    navController.navigate("detail")
                }
            )
        }

        composable("trangdanhsach") {
            DanhSachSach(
                navController = navController,
                onSachClick = { sach ->
                    selectedSach = sach
                    navController.navigate("detail")
                }
            )
        }

        composable("trangtaikhoan") {
            TaiKhoan(navController = navController)
        }

        composable("khuyenmai") {
            KhuyenMai(navController = navController, onBackClick = { navController.popBackStack() })
        }

        // --- KHU VỰC ADMIN ---
        composable("admin_quanlydonhang") {
            QuanLyDonHangAdmin(
                navController = navController,
                bamQuayLai = {
                    navController.navigate("login") { popUpTo("admin_quanlydonhang") { inclusive = true } }
                }
            )
        }

        composable("admin_order_detail/{donHangJson}") { backStackEntry ->
            val jsonEncoded = backStackEntry.arguments?.getString("donHangJson") ?: ""
            val json = URLDecoder.decode(jsonEncoded, StandardCharsets.UTF_8.toString())
            val donHang = Gson().fromJson(json, DonHang::class.java)
            ChiTietDonHangAdmin(navController = navController, donHang = donHang)
        }

        // --- CÁC TRANG CHỨC NĂNG (USER) ---

        composable("detail") {
            if (selectedSach != null) {
                ManHinhChiTietSach(
                    sach = selectedSach!!,
                    navController = navController,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // === ĐÂY LÀ ĐOẠN BẠN CẦN THÊM ĐỂ HẾT VĂNG APP ===
        composable("dondamua") {
            DonDaMua(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("lichsumuahang"){
            ManHinhLichSuMuaHang(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Màn hình chi tiết đơn hàng (User)
        composable(
            route = "chitietdonhang/{maDonHang}/{trangThai}",
            arguments = listOf(
                navArgument("maDonHang") { type = NavType.IntType },
                navArgument("trangThai") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val maDonHang = backStackEntry.arguments?.getInt("maDonHang") ?: 0
            val trangThai = backStackEntry.arguments?.getString("trangThai") ?: ""
            ChiTietDonHangDat(navController, maDonHang, trangThai)
        }

        // --- CÁC TRANG KHÁC ---
        composable("caidat") {
            CaiDat(navController = navController, onBackClick = { navController.popBackStack() })
        }

        composable("danhsachyeuthich") {
            DanhSachYeuThichScreen(
                navController = navController,
                onBackClick = { navController.navigate("trangtaikhoan") },
                onAddCart = { }, onRemoveFavorite = { },
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
            ManHinhThayDoiMatKhau(navController = navController, onBackClick = { navController.popBackStack() })
        }

        composable("chinhsuathongtin") {
            ChinhSuaThongTin(navController = navController, onBackClick = { navController.popBackStack() })
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
            val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
            val nguonThanhToan = savedStateHandle?.get<String>("nguon_thanh_toan")
            val muaLai = savedStateHandle?.get<List<SachtrongGioHang>>("mua_lai_san_pham")
            val gioHang = savedStateHandle?.get<List<SachtrongGioHang>>("gioHang")
            val danhSachSanPham = when {
                !muaLai.isNullOrEmpty() -> muaLai
                !gioHang.isNullOrEmpty() -> gioHang
                else -> emptyList()
            }

            ManHinhThanhToan(
                danhSachSanPham = danhSachSanPham,
                navController = navController,
                BamQuayLai = {
                    when (nguonThanhToan) {
                        "giohang" -> navController.navigate("giohang") { popUpTo("giohang") { inclusive = true } }
                        "mualai" -> navController.navigate("dondamua") { popUpTo("dondamua") { inclusive = true } } // Sửa lại về DonDaMua
                        else -> navController.popBackStack()
                    }
                }
            )
        }
    }
}