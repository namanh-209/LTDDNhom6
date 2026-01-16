package com.example.bookstore.Model

// 1. Class Chi Tiết (Sửa Double -> Int)
data class ChiTietDonHangGui(
    val MaSach: Int,
    val SoLuong: Int,
    val DonGia: Double // <-- Dùng Int
)

// 2. Class Đơn Hàng (Sửa Double -> Int)
data class DonHangGui(
    val MaNguoiDung: Int,
    val MaKhuyenMai: Int?,
    val PhuongThucThanhToan: String,
    val PhiVanChuyen: Int, // <-- Dùng Int
    val TongTien: Int,     // <-- Dùng Int
    val DiaChiGiaoHang: String,
    val GhiChu: String?,
    val ChiTiet: List<ChiTietDonHangGui>
)