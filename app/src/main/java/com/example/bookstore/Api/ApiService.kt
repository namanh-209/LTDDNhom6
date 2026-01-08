package com.example.bookstore.Api

import com.example.bookstore.Model.ApiResponse
import com.example.bookstore.Model.DangKi
import com.example.bookstore.Model.DangNhap
import com.example.bookstore.Model.DiaChi
import com.example.bookstore.Model.DonHangSach
import com.example.bookstore.Model.RegisterResponse
import com.example.bookstore.Model.Sach // ✅ Nhớ import model Sach
import com.example.bookstore.Model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

}