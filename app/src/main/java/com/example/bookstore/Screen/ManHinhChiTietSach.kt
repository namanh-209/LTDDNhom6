package com.example.bookstore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bookstore.Model.Sach




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManHinhChiTietSach(
    sach: Sach,
    onBackClick: () -> Unit
) {
    // State để quản lý Tab (0: Thông tin, 1: Đánh giá)
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        // thanh trên cùng
        topBar = {
            TopAppBar(
                title = {
                    Text("Thông tin chi tiết", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MauXanh)
            )
        },
        // thanh chức năng dưới cùng
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFFEEEEEE),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Nút Yêu thích
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(28.dp))
                    }

                    // Đường kẻ dọc
                    Spacer(modifier = Modifier.width(8.dp).height(24.dp).background(Color.Gray))

                    // Nút Giỏ hàng
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Nút mua
                    Button(
                        onClick = { /* Xử lý mua hàng */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MauXanh),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Mua", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        // nội dung chính
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
           //phần header
            Row(modifier = Modifier.padding(16.dp)) {
                // Ảnh bìa sách
                Card(
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    AsyncImage(
                        model = sach.AnhBia,
                        contentDescription = null,
                        modifier = Modifier.width(100.dp).height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Thông tin bên cạnh ảnh
                Column {
                    Text(
                        text = sach.TenSach,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatTienTe(sach.GiaBan.toInt()),
                        color = Color.Red,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tác giả: ${sach.TenTacGia ?: "Đang cập nhật"}", fontSize = 14.sp)
                    Text("Đánh giá: 4.5/5", fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
                    // Giả lập năm xuất bản vì model chưa có
                    Text("Năm xuất bản: 2024", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // thông tin/ đánh giá
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 0 },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Thông tin",
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                    if (selectedTab == 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.height(2.dp).fillMaxWidth().background(Color.Black))
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 1 },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Đánh giá",
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                    if (selectedTab == 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.height(2.dp).fillMaxWidth().background(Color.Black))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            if (selectedTab == 0) {
                // tab thong tin
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ThongTinDong("Tên sách", sach.TenSach)
                    ThongTinDong("Tác giả", sach.TenTacGia ?: "Đang cập nhật")
                    ThongTinDong("Thể loại", sach.TenTheLoai ?: "Khác")
                    // Các thông tin giả lập cho giống mẫu (do model thiếu)
                    ThongTinDong("Nhà xuất bản", "NXB Văn Học")
                    ThongTinDong("Số trang", "272")
                    ThongTinDong("Hình thức", "Bìa mềm")

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tóm tắt nội dung:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Đây là cuốn sách hấp dẫn kể về hành trình đầy cảm xúc...", // Thay bằng sach.MoTa nếu có
                        style = LocalTextStyle.current.copy(lineHeight = 20.sp),
                        color = Color.DarkGray
                    )
                }
            } else {
                // === TAB ĐÁNH GIÁ (Giao diện giả lập) ===
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ItemDanhGia("Trần Văn A", "5", "Sách rất hay, đóng gói đẹp!")
                    ItemDanhGia("Nguyễn Thị B", "4", "Nội dung ổn, giao hàng hơi chậm.")
                    ItemDanhGia("Lê Văn C", "4", "Tuyệt vời, sẽ ủng hộ tiếp.")
                }
            }

            // Khoảng trống dưới cùng để không bị che bởi nút Mua
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

//  thông tin
@Composable
fun ThongTinDong(tieuDe: String, noiDung: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "$tieuDe: ", fontWeight = FontWeight.Medium, modifier = Modifier.width(110.dp))
        Text(text = noiDung, color = Color.Black)
    }
}

// Đánh giá
@Composable
fun ItemDanhGia(ten: String, sao: String, binhLuan: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar tròn giả lập
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(50)).background(Color.Gray), contentAlignment = Alignment.Center) {
                    Text(ten.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(ten, fontWeight = FontWeight.Bold)
            }
            Text("Đánh giá: 5⭐", fontSize = 12.sp, color = MauXanh, modifier = Modifier.padding(vertical = 4.dp))
            Text(binhLuan)
        }
    }
}