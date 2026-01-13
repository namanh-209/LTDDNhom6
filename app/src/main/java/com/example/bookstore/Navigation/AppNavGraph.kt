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


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    var selectedSach by remember { mutableStateOf<Sach?>(null) }

    NavHost(navController = navController, startDestination = "login") {

        // --- LOGIN & REGISTER ---
        composable("login") {
            LoginScreen(
                onRegisterClick = { navController.navigate("register") },
                onLoginSuccess = {
                    navController.navigate("giohang")
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
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
                navController = navController, // Truyền navController để xử lý BottomBar
                onSachClick = { sach ->
                    selectedSach = sach
                    navController.navigate("detail")
                }
            )
        }

        // 2. Trang Danh mục
        composable("trangdanhsach") {
            DanhSachSach(
                navController = navController,
                onSachClick = { sach ->
                    selectedSach = sach
                    navController.navigate("detail")
                }
                // KHÔNG truyền onBackClick -> Mặc định là null -> Ẩn nút Back
            )
        }

        // 3. Trang Tài khoản
        composable("trangtaikhoan") {
            TaiKhoan(navController = navController)
            // KHÔNG truyền onBackClick -> Ẩn nút Back
        }

        //4. Trang khuyến mãi
        composable("khuyenmai") {
            KhuyenMai(
                navController = navController
            )
        }


        // --- CÁC TRANG CON (CÓ NÚT BACK) ---

        // 4. Chi tiết sách
        composable("detail") {
            if (selectedSach != null) {
                ManHinhChiTietSach(
                    sach = selectedSach!!,
                    onBackClick = { navController.popBackStack() } // TRUYỀN HÀM BACK
                )
            }
        }

        // 5. Đơn đã mua
        composable("dondamua") {
            DonDaMua(
                navController = navController,
                onBackClick = { navController.popBackStack() } // TRUYỀN HÀM BACK
            )
        }

        //Caidat
        composable("caidat") {
            CaiDat(
                navController = navController,
                onBackClick = { navController.popBackStack() } // TRUYỀN HÀM BACK
            )
        }

        composable("danhsachyeuthich") {
            DanhSachYeuThichScreen(
                navController = navController,
                onBackClick = { navController.navigate("trangtaikhoan") }, // quay lại màn trước
                onAddCart = { sach ->
                    // TODO: xử lý thêm giỏ hàng
                },
                onRemoveFavorite = { sach ->
                    // TODO: xử lý khi bỏ yêu thích nếu cần
                },
                onSachClick = { sach ->
                    // Điều hướng sang màn hình chi tiết sách
                    selectedSach = sach
                    navController.navigate("detail")
                }
            )
        }
        composable("giohang"){
            GioHang(
                navController = navController,
                onBackClick = { navController.popBackStack() } // TRUYỀN HÀM BACK
            )
        }
        composable("thaydoimatkhau") {
            ManHinhThayDoiMatKhau (
                navController = navController,onBackClick = { navController.popBackStack() } // Xử lý nút quay lại
            )
        }
        composable("chinhsuathongtin") {
            ChinhSuaThongTin(
                onBackClick = { navController.popBackStack() } // Xử lý nút quay lại
            )
        }

        //trang danh gia
        // 8. Trang đánh giá
        composable("danhgia/{maSach}/{maDonHang}") { backStackEntry ->
            DanhGia(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                maSach = backStackEntry.arguments!!.getString("maSach")!!.toInt(),
                maDonHang = backStackEntry.arguments!!.getString("maDonHang")!!.toInt()
            )
        }

        //THANH TOÁN
        composable("thanhtoan"){
            val gioHang =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<List<SachtrongGioHang>>("gioHang")
                    ?: emptyList()

            ManHinhThanhToan(
                navController=navController,
                danhSachSanPham = gioHang,
                BamQuayLai = { navController.popBackStack() }
            )
        }

    }
}
