package com.example.bookstore.Model

import java.util.Date

data class LichSuDonHang(
    val MaSach: Int,
    val TenSach: String,
    val AnhBia: String,
    val GiaBan: Double,
    val SoLuong: Int,
    val TrangThai: String,
    val MaDonHang: Int,
    val NgayDat: Date
)



