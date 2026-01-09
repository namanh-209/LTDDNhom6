import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.KhungGiaoDien
import com.example.bookstore.Model.Sach
import kotlinx.coroutines.launch

/* ===================== ITEM SÁCH YÊU THÍCH ===================== */
@Composable
fun SachItemYeuThich(
    sach: Sach,
    onAddCart: () -> Unit,
    onRemoveFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp).clickable{onClick()},
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = sach.AnhBia,
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(sach.TenSach, maxLines = 2, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tác giả: ${sach.TenTacGia ?: "Đang cập nhật"}",
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${sach.GiaBan.toInt()} VND",
                    color = Color.Red,
                    fontSize = 16.sp
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                IconButton(
                    onClick = onAddCart,
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFF1E88E5), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.ShoppingCart, null, tint = Color.White)
                }

                Spacer(modifier = Modifier.height(6.dp))

                IconButton(onClick = onRemoveFavorite) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

/* ===================== MÀN HÌNH DANH SÁCH YÊU THÍCH ===================== */
@Composable
fun DanhSachYeuThichScreen(
    onAddCart: (Sach) -> Unit = {},
    onRemoveFavorite: (Sach) -> Unit = {},
    navController: NavController,
    onBackClick: () -> Unit,
    onSachClick: (Sach) -> Unit
) {
    var dsYeuThich by remember { mutableStateOf<List<Sach>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    val userId = BienDungChung.userHienTai?.MaNguoiDung ?: return
    val scope = rememberCoroutineScope()

    // Load danh sách yêu thích từ API
    LaunchedEffect(Unit) {
        loading = true
        try {
            val res = RetrofitClient.api.layDanhSachYeuThich(userId)
            dsYeuThich = res.data ?: emptyList()
        } catch (e: Exception) {
            Log.e("API", "Lỗi load yêu thích: ${e.message}")
            dsYeuThich = emptyList()
        } finally {
            loading = false
        }
    }


    KhungGiaoDien(tieuDe = "Yêu thích",
        onBackClick = onBackClick, // TRANG CON -> CÓ BACK (được truyền từ AppNavGraph)
        onHomeClick = { navController.navigate("home") },
        onCategoryClick = { navController.navigate("trangdanhsach") },
        onCartClick = { navController.navigate("giohang") },
        onProfileClick = { navController.navigate("trangtaikhoan") }) { padding ->
        if (loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (dsYeuThich.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Bạn chưa có sách yêu thích nào", color = Color.Gray)
                        }
                    }
                } else {
                    items(dsYeuThich) { sach ->
                        SachItemYeuThich(
                            sach = sach,
                            onAddCart = {
                                scope.launch {
                                    try {
                                        RetrofitClient.api.capNhatGioHang(
                                            CapNhatGioHangRequest(
                                                MaNguoiDung = BienDungChung.userHienTai!!.MaNguoiDung,
                                                MaSach = sach.MaSach,
                                                SoLuong = 1
                                            )
                                        )
                                    } catch (e: Exception) { }
                                }
                            },
                            onRemoveFavorite = {
                                // Gọi API toggle yêu thích
                                scope.launch {
                                    try {
                                        RetrofitClient.api.toggleYeuThich(
                                            request = YeuThichRequest(
                                                MaNguoiDung = userId,
                                                MaSach = sach.MaSach
                                            )
                                        )
                                        // Xóa sách khỏi danh sách sau khi bỏ yêu thích
                                        dsYeuThich = dsYeuThich.filter { it.MaSach != sach.MaSach }
                                        onRemoveFavorite(sach)
                                    } catch (e: Exception) {
                                        Log.e("API", "Lỗi toggle yêu thích: ${e.message}")
                                    }
                                }
                            },
                            onClick = {onSachClick(sach)}
                        )
                    }
                }
            }
        }
    }
}
