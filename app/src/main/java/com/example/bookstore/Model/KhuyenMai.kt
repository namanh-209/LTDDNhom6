package com.example.bookstore.Model



import java.io.Serializable

data class   KhuyenMai(
    val MaKhuyenMai: Int,
    val MaCode: String,
    val GiaTriGiam: Double,
    val DonToiThieu: Double?,
    val SoLuong: Int?,
    val NgayHetHan: String?
) : Serializable
