package com.example.bookstore

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val MauXanh = Color(0xFF0E73A9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhungGiaoDien(
    tieuDe: String,
    onBackClick: (() -> Unit)? = null, // Nếu null => Ẩn nút Back
    onHomeClick: () -> Unit = {},
    onCategoryClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    noiDung: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(tieuDe, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    // CHỈ HIỆN NÚT BACK NẾU onBackClick KHÁC NULL
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MauXanh)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MauXanh, contentColor = Color.White) {
                NutMenuVector("Trang chủ", Icons.Default.Home, onClick = onHomeClick)
                NutMenuAnh("Danh mục", R.drawable.list, onClick = onCategoryClick)
                NutMenuVector("Giỏ hàng", Icons.Default.ShoppingCart, onClick = {})
                NutMenuAnh("Khuyến mãi", R.drawable.icon_giam_gia, onClick = {})
                NutMenuVector("Tài khoản", Icons.Default.Person, onClick = onProfileClick)
            }
        }
    ) { paddingValues ->
        noiDung(paddingValues)
    }
}

@Composable
fun RowScope.NutMenuVector(ten: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    NavigationBarItem(
        selected = false,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp)) },
        label = { Text(ten, fontSize = 10.sp) },
        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = Color.White, unselectedIconColor = Color.White, unselectedTextColor = Color.White)
    )
}

@Composable
fun RowScope.NutMenuAnh(ten: String, idAnh: Int, onClick: () -> Unit) {
    NavigationBarItem(
        selected = false,
        onClick = onClick,
        icon = { Icon(painter = painterResource(id = idAnh), contentDescription = null, modifier = Modifier.size(24.dp)) },
        label = { Text(ten, fontSize = 10.sp) },
        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = Color.White, unselectedIconColor = Color.White, unselectedTextColor = Color.White)
    )
}