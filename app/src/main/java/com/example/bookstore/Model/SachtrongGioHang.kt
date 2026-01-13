package com.example.bookstore.Model

data class SachtrongGioHang(
    val MaSach: Int,
    val MaGioHang:Int,
    val TenSach: String,
    val TenTacGia: String,
    val GiaBan: Int,
    val AnhBia: String,
    val SoLuong: Int
)
fun SachtrongGioHang.toSach(): Sach {
    return Sach(
        MaSach = MaSach,
        TenSach = TenSach,
        GiaBan = GiaBan.toDouble(),
        GiaGoc = GiaBan.toDouble(), // tạm thời
        AnhBia = AnhBia,
        TenTacGia = TenTacGia,
        TenTheLoai = null,
        MaTheLoai = null,
        MoTa = "Sách được mở từ giỏ hàng"
    )
}