package com.example.bookstore.Model

data class DonHangSach(
    val MaSach: Int,
    val TenSach: String,
    val AnhBia: String,
    val GiaBan: Double,
    val TrangThai: String, // Quan trọng: Để lọc tab (MoiDat, DangGiao...)
    val MaDonHang: Int,
    val TenTacGia: String? = ""
)