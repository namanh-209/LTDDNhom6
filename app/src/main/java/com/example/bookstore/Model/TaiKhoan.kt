package com.example.bookstore.Model


data class DangNhap(
    val contact: String,  // Chứa Email hoặc SĐT
    val password: String  // Chứa mật khẩu
)

// Đây là khuôn chứa dữ liệu Đăng Ký gửi lên Server
data class DangKi(
    val HoTen: String,
    val Email: String?,
    val SoDienThoai: String?,
    val MatKhau: String
)