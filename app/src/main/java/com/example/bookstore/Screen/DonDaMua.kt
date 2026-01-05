package com.example.bookstore

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun DonDaMua() {

    val tabs = listOf(
        "Chờ xác nhận",
        "Chờ giao hàng",
        "Đã giao",
        "Đã huỷ"
    )

    // Mặc định mở tab "Chờ giao hàng"
    var selectedTab by remember { mutableStateOf(0) }

    KhungGiaoDien(tieuDe = "Đơn đã mua") { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            /* -------- TAB -------- */
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index)
                                    Color.Black
                                else
                                    Color.DarkGray
                            )
                        }
                    )
                }
            }

            /* -------- NỘI DUNG THEO TAB -------- */
            when (selectedTab) {

                // Chờ xác nhận
                0 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.icon_don_hang),
                                contentDescription = null,
                                modifier = Modifier.size(140.dp),
                                colorFilter = ColorFilter.tint(Color.Gray)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Bạn chưa có đơn hàng nào",
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Chờ giao hàng
                1 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.icon_don_hang),
                                contentDescription = null,
                                modifier = Modifier.size(140.dp),
                                colorFilter = ColorFilter.tint(Color.Gray)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Bạn chưa có đơn hàng nào",
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Đã giao
                2 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.icon_don_hang),
                                contentDescription = null,
                                modifier = Modifier.size(140.dp),
                                colorFilter = ColorFilter.tint(Color.Gray)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Bạn chưa có đơn hàng nào",
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Đã hủy
                3 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.icon_don_hang),
                                contentDescription = null,
                                modifier = Modifier.size(140.dp),
                                colorFilter = ColorFilter.tint(Color.Gray)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Bạn chưa có đơn hàng nào",
                                color = Color.Gray
                            )
                        }
                    }
                }

            }
        }
    }
}
