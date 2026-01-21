package com.example.bookstore.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DonHang(
    @SerializedName("ma_don_hang")
    val maDonHang: Int,

    @SerializedName("ten_nguoi_mua")
    val tenNguoiMua: String,

    @SerializedName("ngay_dat")
    val ngayDat: String,

    @SerializedName("tong_tien")
    val tongTien: Double,

    @SerializedName("trang_thai")
    val trangThai: String ,
    @SerializedName("dia_chi") val diaChiGiaoHang: String?,
    @SerializedName("phi_van_chuyen")
    val phiVanChuyen: Double?,
    @SerializedName("ghi_chu") val GhiChu: String?,
    @SerializedName("SDT") val SDT: String?,
) : Serializable