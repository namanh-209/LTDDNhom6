package com.example.bookstore.Api

import YeuThichRequest
import com.example.bookstore.Model.ApiResponse
import com.example.bookstore.Model.DangKi
import com.example.bookstore.Model.DangNhap
import com.example.bookstore.Model.DiaChi
import com.example.bookstore.Model.DonHangSach
import com.example.bookstore.Model.RegisterResponse
import com.example.bookstore.Model.Sach // ✅ Nhớ import model Sach
import com.example.bookstore.Model.SachtrongGioHang
import com.example.bookstore.Model.TheLoai
import com.example.bookstore.Model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @GET("api/diachi/{userId}")
    suspend fun layDiaChi(@Path("userId") userId: Int): ApiResponse<List<DiaChi>>
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

}
