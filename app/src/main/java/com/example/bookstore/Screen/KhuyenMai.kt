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
    val snackbarHostState = remember { SnackbarHostState() }

    var danhSach by remember { mutableStateOf<List<KhuyenMai>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }

    // LẤY TỔNG TIỀN ĐÚNG – CÓ RECOMPOSITION
    val tongTien by navController
        .previousBackStackEntry!!
        .savedStateHandle
        .getStateFlow("tongTien", 0.0)
        .collectAsState()

    // ===== LOAD DANH SÁCH KHUYẾN MÃI =====
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val res = RetrofitClient.api.layDanhSachKhuyenMai()
                if (res.status == "success") {
                    danhSach = res.data ?: emptyList()
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Lỗi tải khuyến mãi")
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (dangTai) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(danhSach) { km ->
                        ItemKhuyenMai(
                            km = km,
                            tongTien = tongTien,
                            snackbarHostState = snackbarHostState,
                            onDung = {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("khuyenMaiDaChon", km)

                                navController.popBackStack()
                            }
                        )
                    }
                }
            }

            SnackbarHost(
                snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun ItemKhuyenMai(
    km: KhuyenMai,
    tongTien: Double,
    snackbarHostState: SnackbarHostState,
    onDung: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // CHECK ĐIỀU KIỆN ĐÚNG
    val duDieuKien = tongTien.toInt() >= (km.DonToiThieu ?: 0).toInt()
    val conLuot = (km.SoLuong ?: 1) > 0

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (duDieuKien) Color(0xFFE3F3FB) else Color(0xFFF0F0F0)
        ),
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
                "Đơn tối thiểu: ${km.DonToiThieu ?: 0}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Số lượt còn: ${km.SoLuong ?: "Không giới hạn"}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    if (!duDieuKien) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Chưa đủ đơn tối thiểu")
                        }
                        return@Button
                    }

                    if (!conLuot) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Khuyến mãi đã hết lượt")
                        }
                        return@Button
                    }

                    // CHỈ CHỌN – TRỪ LƯỢT Ở BACKEND SAU KHI ĐẶT HÀNG
                    onDung()
                },
                enabled = duDieuKien && conLuot,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBFDCEA),
                    contentColor = Color.Black,
                    disabledContainerColor = Color.LightGray
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
