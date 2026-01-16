package com.example.bookstore.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.KhuyenMai
import com.example.bookstore.formatTienTe
import kotlinx.coroutines.launch

@Composable
fun KhuyenMai(navController: NavController) {

    val scope = rememberCoroutineScope()
    var danhSach by remember { mutableStateOf<List<KhuyenMai>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }

    // ===== LOAD DANH SÁCH KHUYẾN MÃI =====
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val res = RetrofitClient.api.layDanhSachKhuyenMai()
                if (res.status == "success") {
                    danhSach = res.data ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                dangTai = false
            }
        }
    }

    KhungGiaoDien(
        tieuDe = "Khuyến mãi",
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = {},
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { paddingValues ->

        if (dangTai) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(danhSach) { km ->
                    ItemKhuyenMai(
                        km = km,
                        onDung = {
                            // ✅ GỬI VỀ MÀN TRƯỚC (THANH TOÁN)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("khuyenMaiDaChon", km)

                            // QUAY LẠI THANH TOÁN
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemKhuyenMai(
    km: KhuyenMai,
    onDung: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F3FB)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text("Mã: ${km.MaCode}", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(6.dp))

            Text(
                "Giảm ${formatTienTe(km.GiaTriGiam)}",
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Đơn tối thiểu: ${formatTienTe(km.DonToiThieu ?: 0.0)}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Số lượt còn: ${km.SoLuong ?: "Không giới hạn"}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onDung,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBFDCEA),
                    contentColor = Color.Black
                )
            ) {
                Text("Dùng")
            }

            Spacer(Modifier.height(6.dp))

            Text(
                "Hạn dùng: ${km.NgayHetHan?.take(10) ?: "Không giới hạn"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
