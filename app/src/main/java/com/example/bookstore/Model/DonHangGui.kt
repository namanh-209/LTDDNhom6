package com.example.bookstore.Model

data class DonHangGui(
    val MaNguoiDung: Int,
    val MaKhuyenMai: Int?,
    val PhuongThucThanhToan: String,
    val PhiVanChuyen: Int,
    val TongTien: Int,
    val DiaChiGiaoHang: String,
    val GhiChu: String?,
    val ChiTiet: List<ChiTietDonHang>
)
