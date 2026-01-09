package com.example.bookstore.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookstore.KhungGiaoDien

@Composable
fun CaiDat( navController: NavController,onBackClick:()-> Unit) {
    KhungGiaoDien(tieuDe = "Cài đặt",
        onBackClick = onBackClick, // TRANG CON -> CÓ BACK (được truyền từ AppNavGraph)
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onProfileClick = { navController.navigate("trangtaikhoan") }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF2F2F2))
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {

            NhomCaiDat(
                "Tài khoản",
                listOf(
                    "Thay đổi mật khẩu",
                    "Chỉnh sửa thông tin",
                    "Tài khoản/ Thẻ ngân hàng"
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            NhomCaiDat(
                "Cài đặt",
                listOf(
                    "Cài đặt chat",
                    "Cài đặt thông báo",
                    "Cài đặt riêng tư",
                    "Ngôn ngữ / Language\nTiếng Việt"
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            NhomCaiDat(
                "Hỗ trợ",
                listOf(
                    "Trung tâm hỗ trợ",
                    "Tiêu chuẩn cộng đồng",
                    "Giới thiệu",
                    "Xóa tài khoản"
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        // viết sự kiện đăng xuất

                    },
                    modifier = Modifier
                        .width(170.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Đăng xuất", fontWeight = FontWeight.Bold)
                }
            }

        }
    }
}

@Composable
fun NhomCaiDat(tieuDe: String, items: List<String>) {
    Column {
        Text(
            text = tieuDe,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Card(
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFD9D9D9)

            )
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    HangCaiDat(item)
                    if (index < items.lastIndex) {
                        Divider()
                    }
                }
            }
        }

    }
}

@Composable
fun HangCaiDat(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text)
    }
}
