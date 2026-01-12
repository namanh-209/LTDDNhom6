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
import kotlinx.coroutines.launch

@Composable
fun KhuyenMai(navController: NavController) {

    val scope = rememberCoroutineScope()
    var danhSach by remember { mutableStateOf<List<KhuyenMai>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val res = RetrofitClient.api.layDanhSachKhuyenMai()
                if (res.status == "success") {
                    danhSach = res.data ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    KhungGiaoDien(
        tieuDe = "Khuyến mãi",
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onSaleClick = {
            navController.navigate("khuyenmai") {
                launchSingleTop = true
            }
        },
        onProfileClick = { navController.navigate("trangtaikhoan") }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(danhSach) { km ->
                ItemKhuyenMai(
                    km = km,
                    onApplyClick = {
                        // 👉 Áp dụng xong → chuyển sang giỏ hàng
                        navController.navigate("giohang") {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
@Composable
fun ItemKhuyenMai(
    km: KhuyenMai,
    onApplyClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F3FB)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = "Mã: ${km.MaCode}",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Giảm ${km.GiaTriGiam.toInt()} VND",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Đơn tối thiểu ${km.DonToiThieu?.toInt() ?: 0} VND",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Số lượt còn: ${km.SoLuong ?: "Không giới hạn"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = onApplyClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFBFDCEA),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Áp dụng")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider(thickness = 1.dp, color = Color.Black)

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Hạn sử dụng: ${km.NgayHetHan?.substring(0, 10) ?: "Không giới hạn"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
