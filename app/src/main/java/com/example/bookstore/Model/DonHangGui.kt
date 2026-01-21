package com.example.bookstore.Model

// 1. Class Chi Tiết
data class ChiTietDonHangGui(
    val MaSach: Int,
    val SoLuong: Int,
    val DonGia: Double
)

// 2. Class Đơn Hàng
data class DonHangGui(
    val MaNguoiDung: Int,
    val MaKhuyenMai: Int?,
    val PhuongThucThanhToan: String,
    val PhiVanChuyen: Int,
    val TongTien: Int,
    val DiaChiGiaoHang: String,
    val GhiChu: String?,
    val ChiTiet: List<ChiTietDonHangGui>
)