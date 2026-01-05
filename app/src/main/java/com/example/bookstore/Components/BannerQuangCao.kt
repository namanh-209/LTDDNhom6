package com.example.bookstore.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

import com.example.bookstore.model.Sach

@Composable
fun BannerQuangCao(danhSachSach: List<Sach>,
                   onXemNgayClick: (Sach) -> Unit) {
    val sachBanner = danhSachSach.take(5)

    if (sachBanner.isNotEmpty()) {
        val pagerState = rememberPagerState(pageCount = { sachBanner.size })

        // Auto-scroll
        LaunchedEffect(Unit) {
            while (true) {
                delay(2500) // Tăng lên 4s để người dùng kịp đọc chữ
                val nextPage = (pagerState.currentPage + 1) % sachBanner.size
                pagerState.animateScrollToPage(nextPage)
            }
        }

        HorizontalPager(
            state = pagerState,
            // Tăng chiều cao lên 220dp để chứa đủ thông tin mới
            modifier = Modifier.fillMaxWidth().height(220.dp),
            contentPadding = PaddingValues(horizontal = 16.dp), // Hiệu ứng hở 2 bên
            pageSpacing = 8.dp
        ) { page ->
            val sach = sachBanner[page]

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. Ảnh nền
                    AsyncImage(
                        model = sach.AnhBia,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 2. Lớp phủ Gradient (QUAN TRỌNG: Giúp chữ không bị chìm)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.1f),
                                        Color.Black.copy(alpha = 0.8f), // Đậm dần xuống dưới
                                        Color.Black
                                    ),
                                    startY = 100f // Bắt đầu gradient từ phía trên một chút
                                )
                            )
                    )

                    // 3. Nội dung chữ
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        // Nhãn Thể loại nhỏ bên trên
                        ContainerTheLoai(tenTheLoai = sach.TenTheLoai ?: "Nổi bật")

                        Spacer(modifier = Modifier.height(4.dp))

                        // Tên sách
                        Text(
                            text = sach.TenSach,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Mô tả ngắn (Thêm cái này cho nhiều chữ)
                        Text(
                            text = sach.MoTa ?: "Một cuốn sách hấp dẫn đang chờ bạn khám phá.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            maxLines = 2, // Chỉ hiện 2 dòng mô tả để không bị tràn
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Hàng dưới cùng: Giá tiền và Nút xem
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatCurrency(sach.GiaBan),
                                color = Color(0xFFFFC107), // Màu vàng cam nổi bật
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            // Nút giả lập (Button nhỏ)
                            Button(
                                onClick = {onXemNgayClick(sach) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Xem ngay", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Component phụ: Badge thể loại
@Composable
fun ContainerTheLoai(tenTheLoai: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Red.copy(alpha = 0.8f)) // Màu đỏ làm điểm nhấn
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = tenTheLoai.uppercase(),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Hàm format tiền tệ (VND)
fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount)
}