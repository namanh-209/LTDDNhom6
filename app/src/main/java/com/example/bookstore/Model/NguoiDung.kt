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

    val TenNguoiNhan: String?,
    val SDTNguoiNhan: String?
)