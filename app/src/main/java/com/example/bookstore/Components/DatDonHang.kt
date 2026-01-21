package com.example.bookstore.Utils

import android.content.Context
import android.widget.Toast
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.Model.ChiTietDonHangGui
import com.example.bookstore.Model.DonHangGui
import com.example.bookstore.Model.PhanHoiApi
import com.example.bookstore.Model.SachtrongGioHang
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun datDonHang(
    nguCanh: Context,
    phuongThuc: String,
    danhSachSanPham: List<SachtrongGioHang>,
    diaChiGiaoHang: String,
    ghiChu: String,

    tongTien: Int,
    maKhuyenMai: Int?,

    phiVanChuyen: Int
) {
    val user = BienDungChung.userHienTai
    if (user == null) {
        Toast.makeText(nguCanh, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
        return
    }

    val donHang = DonHangGui(
        MaNguoiDung = user.MaNguoiDung,
        MaKhuyenMai = maKhuyenMai,
        PhuongThucThanhToan = phuongThuc,
        PhiVanChuyen = phiVanChuyen,
        TongTien = tongTien,
        DiaChiGiaoHang = diaChiGiaoHang,
        GhiChu = ghiChu,
        ChiTiet = danhSachSanPham.map {
            ChiTietDonHangGui(
                MaSach = it.MaSach,
                SoLuong = it.SoLuong,
                DonGia = it.GiaBan
            )
        }
    )

    RetrofitClient.api.taoDonHang(donHang).enqueue(object : Callback<PhanHoiApi> {
        override fun onResponse(call: Call<PhanHoiApi>, response: Response<PhanHoiApi>) {
            if (response.isSuccessful && response.body()?.status == "success") {
                Toast.makeText(nguCanh, "Đặt hàng thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    nguCanh,
                    response.body()?.message ?: "Đặt hàng thất bại",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onFailure(call: Call<PhanHoiApi>, t: Throwable) {
            Toast.makeText(nguCanh, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}