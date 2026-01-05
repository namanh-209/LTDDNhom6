package com.example.bookstore.model

data class SachResponse(
    val status: String,
    val data: List<Sach>
)

data class Sach(
    val MaSach: Int,
    val TenSach: String,
    val GiaBan: Double,
    val GiaGoc: Int,
    val AnhBia: String,
    val TenTacGia: String?,
    val TenTheLoai: String?,
    val MaTheLoai: Int?,
    val MoTa: String
)