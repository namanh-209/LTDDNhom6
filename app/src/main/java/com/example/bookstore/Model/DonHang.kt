package com.example.bookstore.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DonHang(
    @SerializedName("ma_don_hang") // Tên trường trong Database/API (giữ nguyên snake_case)
    val maDonHang: Int,

    @SerializedName("ten_nguoi_mua")
    val tenNguoiMua: String,

    @SerializedName("ngay_dat")
    val ngayDat: String,

    @SerializedName("tong_tien")
    val tongTien: Double,

    @SerializedName("trang_thai")
    val trangThai: String ,// Ví dụ: "Chờ xác nhận", "Đang giao",...
    @SerializedName("DiaChiGiaoHang") val diaChiGiaoHang: String?
) : Serializable