package com.example.bookstore.Model

data class KhuyenMai(
    val MaKhuyenMai: Int,
    val MaCode: String,
    val GiaTriGiam: Double,
    val DonToiThieu: Double?,
    val NgayBatDau: String?,
    val NgayHetHan: String?,
    val SoLuong: Int?
)
