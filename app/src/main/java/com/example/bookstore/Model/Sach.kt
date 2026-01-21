package com.example.bookstore.Model

data class SachResponse(
    val status: String,
    val data: List<Sach>
)

data class Sach(
    val MaSach: Int,
    val TenSach: String,
    val GiaBan: Double,
    val GiaGoc: Double,
    val AnhBia: String,
    val TenTacGia: String?,
    val TenTheLoai: String?,
    val MaTheLoai: Int?,
    val MoTa: String,
    val DiemDanhGia: Double,
    val SoLuongDaBan: Int,       // Đã có từ trước
    val SoLuongTon: Int = 0,     // MỚI: Thêm trường này, mặc định 0 để tránh lỗi null
    val NgayThem: String? = null
)