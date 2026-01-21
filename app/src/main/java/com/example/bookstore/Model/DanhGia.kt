package com.example.bookstore.Model

data class DanhGia(
    val MaDanhGia: Int,
    val MaNguoiDung: Int,
    val MaSach: Int,
    val SoSao: Int,
    val BinhLuan: String,
    val NgayDanhGia: String,
    val HoTen: String,
    val AnhDaiDien: String?
)