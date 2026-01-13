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
    val SoDienThoai: String, // Trong DB bạn để NOT NULL nên cái này ok
    val Email: String?,      // Thêm dấu ? vì trong DB là DEFAULT NULL
    val AnhDaiDien: String?, // Thêm dấu ?
    val VaiTro: String?,
    val NgaySinh: String?,
    val GioiTinh: String?,  // Thêm dấu ?

)