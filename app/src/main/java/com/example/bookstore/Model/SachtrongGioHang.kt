package com.example.bookstore.Model

import java.io.Serializable


data class SachtrongGioHang(
    val MaGioHang: Int,
    val MaSach: Int,
    val TenSach: String,
    val AnhBia: String,
    val GiaBan: Double,
    val SoLuong: Int,
    val TenTacGia: String
) : Serializable