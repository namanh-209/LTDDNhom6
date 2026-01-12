package com.example.bookstore.Utils

import android.content.Context
import android.widget.Toast

import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Model.ChiTietDonHang
import com.example.bookstore.Model.DonHangGui
import com.example.bookstore.Model.PhanHoiApi
import com.example.bookstore.Model.SachtrongGioHang


fun datDonHang(
    nguCanh: Context,
    phuongThuc: String,
    danhSachSanPham: List<SachtrongGioHang>,
    diaChiGiaoHang: String,
    ghiChu:String,
    tongTien: Int,
    maKhuyenMai: Int?,
    phiVanChuyen: Int
) {

    val donHang = DonHangGui(
        MaNguoiDung = 1,
        MaKhuyenMai = maKhuyenMai,
        PhuongThucThanhToan = phuongThuc,
        PhiVanChuyen = phiVanChuyen, // ✅
        TongTien = tongTien,
        DiaChiGiaoHang = diaChiGiaoHang,
        GhiChu = ghiChu,
        ChiTiet = danhSachSanPham.map {
            ChiTietDonHang(

                MaSach = it.MaSach,
                SoLuong = it.SoLuong,
                DonGia = it.GiaBan
            )
        }
    )

    RetrofitClient.api.taoDonHang(donHang)
        .enqueue(object : retrofit2.Callback<PhanHoiApi> {

            override fun onResponse(
                call: retrofit2.Call<PhanHoiApi>,
                response: retrofit2.Response<PhanHoiApi>
            ) {
                Toast.makeText(
                    nguCanh,
                    "Đặt hàng thành công",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(
                call: retrofit2.Call<PhanHoiApi>,
                loi: Throwable
            ) {
                Toast.makeText(
                    nguCanh,
                    "Lỗi: ${loi.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
}
