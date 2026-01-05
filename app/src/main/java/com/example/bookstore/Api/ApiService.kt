package com.example.bookstore.Api

import com.example.bookstore.model.SachResponse
import retrofit2.http.GET

interface ApiService {
    @GET("/api/sach")
    suspend fun layDanhSachSach(): SachResponse
}