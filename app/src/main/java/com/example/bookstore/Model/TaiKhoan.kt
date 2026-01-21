package com.example.bookstore.Model


data class DangNhap(
    val contact: String,
    val password: String
)


data class DangKi(
    val HoTen: String,
    val Email: String?,
    val SoDienThoai: String?,
    val MatKhau: String
)