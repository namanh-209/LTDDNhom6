package com.example.bookstore.Model

data class ApiResponse<T>(
    val status: String,
    val message: String?,
    val data: T?
)
data class RegisterResponse(
    val status: String,
    val message: String?,
    val userId: Int?
)

// Class User khớp với bảng nguoidung trong Database
data class User(
    val MaNguoiDung: Int,
    val HoTen: String,
    val SoDienThoai: String,
    val Email: String?,
    val AnhDaiDien: String?,
    val VaiTro: String?,
    val NgaySinh: String?,
    val GioiTinh: String?,
    val DiaChi: String?,
    // Thêm 2 trường này để hứng dữ liệu từ API Login
    val TenNguoiNhan: String?,
    val SDTNguoiNhan: String?
)