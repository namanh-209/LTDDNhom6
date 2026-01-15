package com.example.bookstore.Components



import com.example.bookstore.Model.SachtrongGioHang
import com.example.bookstore.Model.User

object BienDungChung {
    var userHienTai: User? = null
    val gioHang = mutableListOf<SachtrongGioHang>()
    // DÙNG CHO MUA NGAY
    var danhSachMuaNgay: List<SachtrongGioHang> = emptyList()
}