package com.example.bookstore.Api

import CapNhatGioHangRequest
import CapNhatThongTinRequest
import CapNhatTrangThaiRequest

import MChiTietDonHangAdmin
import YeuThichRequest
import com.example.bookstore.Model.ApiResponse
import com.example.bookstore.Model.DangKi
import com.example.bookstore.Model.DangNhap
import com.example.bookstore.Model.DanhGia
import com.example.bookstore.Model.DiaChi
import com.example.bookstore.Model.DoiMatKhau
import com.example.bookstore.Model.DonHang

import com.example.bookstore.Model.DonHangGui
import com.example.bookstore.Model.DonHangSach
import com.example.bookstore.Model.KhuyenMai
import com.example.bookstore.Model.LichSuDonHang
import com.example.bookstore.Model.PhanHoiApi
import com.example.bookstore.Model.RegisterResponse
import com.example.bookstore.Model.Sach // ✅ Nhớ import model Sach
import com.example.bookstore.Model.SachtrongGioHang
import com.example.bookstore.Model.TheLoai
import com.example.bookstore.Model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // 1. Lấy danh sách tất cả sách
    // Server trả về: { status: 'success', data: [Danh sách sách] }
    // Sửa: Dùng ApiResponse<List<Sach>> thay vì SachResponse để đồng bộ
    @GET("api/sach")
    suspend fun layDanhSachSach(): ApiResponse<List<Sach>>

    // 2. Đăng ký tài khoản
    // Server trả về: { status: 'success', message: '...', userId: 123 }
    // Giữ nguyên RegisterResponse vì server trả về 'userId' chứ không phải 'data'
    @POST("api/register")
    suspend fun dangKy(@Body request: DangKi): RegisterResponse

    // 3. Đăng nhập
    // Server trả về: { status: 'success', message: '...', data: {User...} }
    @POST("api/login")
    suspend fun dangNhap(@Body request: DangNhap): ApiResponse<User>

    // 4. Lấy sách đã mua (Lịch sử đơn hàng)
    // Server trả về: { status: 'success', data: [Danh sách đơn hàng sách] }
    @GET("api/donhang/sach/{userId}")
    suspend fun laySachDaMua(@Path("userId") userId: Int): ApiResponse<List<DonHangSach>>

    @GET("api/theloai")
    suspend fun layDanhSachTheLoai(): ApiResponse<List<TheLoai>>

    // 2. Lấy sách thuộc thể loại đó (khi bấm vào Tab)
    @GET("api/sach/theloai/{id}")
    suspend fun laySachTheoTheLoai(@Path("id") maTheLoai: Int): ApiResponse<List<Sach>>

    @GET("api/yeuthich/{userId}")
    suspend fun layDanhSachYeuThich(
        @Path("userId") userId: Int
    ): ApiResponse<List<Sach>>

    //  THÊM / XOÁ YÊU THÍCH (TOGGLE)
    @POST("api/yeuthich")
    suspend fun toggleYeuThich(
        @Body request: YeuThichRequest
    ): ApiResponse<Any>

    //Giỏ hàng
    @GET("api/giohang/{userId}")
    suspend fun layGioHang(
        @Path("userId") userId: Int
    ): ApiResponse<List<SachtrongGioHang>>

    ///có sửa nhưng không đáng kể
    @GET("api/diachi/{maNguoiDung}")
    suspend fun layDanhSachDiaChi(
        @Path("maNguoiDung") maNguoiDung: Int
    ): ApiResponse<List<DiaChi>>


    //cập nhật số lượng giỏ hàng
    @POST("api/giohang")
    suspend fun capNhatGioHang(
        @Body request: CapNhatGioHangRequest
    ): ApiResponse<Any>

    @DELETE("api/giohang/{id}")
    suspend fun xoaGioHang(
        @Path("id") maGioHang: Int
    ): ApiResponse<Unit>

// ... các api khác ...

    // API Đổi mật khẩu
    @POST("api/changepassword")
    suspend fun doiMatKhau(@Body request: DoiMatKhau): ApiResponse<Any>

    //api đánh giá
    @POST("api/danhgia")
    suspend fun guiDanhGia(
        @Body request: com.example.bookstore.Model.DanhGiaRequest
    ): ApiResponse<Any>


    //api khuyen mai
    @GET("/api/khuyenmai")
    suspend fun layDanhSachKhuyenMai(): ApiResponse<List<KhuyenMai>>


    //API hiển thị đánh giá
    @GET("api/danhgia/{bookId}")
    suspend fun layDanhSachDanhGia(@Path("bookId") bookId: Int): ApiResponse<List<DanhGia>>



    @GET("api/diachi/{maNguoiDung}")
    suspend fun layDiaChi(
        @Path("maNguoiDung") maNguoiDung: Int
    ): ApiResponse<DiaChi>

    //tạo đơn hàng
    @POST("api/donhang")
    fun taoDonHang(
        @Body donHang: DonHangGui
    ): Call<PhanHoiApi>


    // 1. Lấy danh sách đơn hàng
    @GET("api/admin/donhang") // Đã bỏ .php
    suspend fun layDanhSachDonHang(): ApiResponse<List<DonHang>>

    // 2. Cập nhật trạng thái
    @POST("api/admin/capnhattrangthai")
    suspend fun capNhatTrangThai(
        @Body request: CapNhatTrangThaiRequest
    ): ApiResponse<Any>


    // SỬA LẠI THÀNH:

    @GET("api/donhang/sach/{userId}")
    suspend fun getLichSuMuaHang(
        @Path("userId") userId: Int
    ): ApiResponse<List<LichSuDonHang>>


    // Dùng @Path và thêm {maDonHang} vào đường dẫn để khớp với Node.js
    @GET("api/donhang/chitiet/{maDonHang}")
    suspend fun layChiTietDonHang(@Path("maDonHang") maDonHang: Int): ApiResponse<List<MChiTietDonHangAdmin>>


    // Thêm vào interface ApiService
    @POST("api/nguoidung/update")
    suspend fun capNhatThongTin(
        @Body request: CapNhatThongTinRequest
    ): ApiResponse<User> // Server trả về data user mới cập nhật

}




