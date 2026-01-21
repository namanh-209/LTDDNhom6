package com.example.bookstore.Model

data class DonHangSach(
    val MaSach: Int,
    val TenSach: String,
    val AnhBia: String,
    val GiaBan: Double,
    val TrangThai: String,
    val MaDonHang: Int,
    val TenTacGia: String? = ""
)