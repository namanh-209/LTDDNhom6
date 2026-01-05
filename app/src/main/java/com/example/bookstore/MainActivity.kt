package com.example.bookstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.bookstore.model.Sach

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
<<<<<<< HEAD
//            ManHinhThayDoiMatKhau()
//            ManHinhTrangChu()
            var selectedSach by remember { mutableStateOf<Sach?>(null) }

            if (selectedSach == null) {
                // Hiện trang chủ
                ManHinhTrangChu(
                    onSachClick = { sach ->
                        selectedSach = sach // Khi bấm sách, lưu sách lại để chuyển trang
                    }
                )
            } else {
                // Hiện trang chi tiết
                ManHinhChiTietSach(
                    sach = selectedSach!!,
                    onBackClick = {
                        selectedSach = null // Bấm nút quay lại thì xóa sách đi để về trang chủ
                    }
                )
            }
=======
            //ManHinhThayDoiMatKhau()
            //gọi trang login & đăng kí
            //Appnavgraph()
            DonDaMua()
>>>>>>> 4a61e7f0f92e39a558a623d137309a02c48c9b1e
        }
    }
}

