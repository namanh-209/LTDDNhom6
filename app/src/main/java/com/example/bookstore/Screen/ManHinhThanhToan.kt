<<<<<<< HEAD
package com.example.bookstore


import PhuongThucThanhToan
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Components.BienDungChung
import com.example.bookstore.Model.DiaChi
import com.example.bookstore.Model.KhuyenMai
import com.example.bookstore.Model.PhuongThucVanChuyen
import com.example.bookstore.Model.SachtrongGioHang
import com.example.bookstore.Utils.datDonHang
import kotlin.collections.first


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManHinhThanhToan(
    navController: NavController,
    danhSachSanPham: List<SachtrongGioHang>,
    BamQuayLai: () -> Unit
) {
//KHAI BAO BIẾN LIÊN QUAN


    var phuongThucThanhToan by remember { mutableStateOf("TienMat") }

    val tongTienSanPham = danhSachSanPham.sumOf {
        it.GiaBan * it.SoLuong
    }

    var diaChiDangChon by remember {
        mutableStateOf<DiaChi?>(null)
    }

    val nguCanh = LocalContext.current

    var KhuyenMaiDaChon by remember {
        mutableStateOf<KhuyenMai?>(null)
    }

    var ghiChuChoShop by remember {
        mutableStateOf("")
    }

    val danhSachVanChuyen = listOf(
        PhuongThucVanChuyen("Giao hàng tiết kiệm", 16000),
        PhuongThucVanChuyen("Giao hàng nhanh", 30000),
        PhuongThucVanChuyen("Hỏa tốc", 50000)
    )
    var vanChuyenDangChon by remember {
        mutableStateOf(danhSachVanChuyen.first())
    }
//miễn ship khi có mã KM
    val phiVanChuyenThucTe =
        if (KhuyenMaiDaChon != null) 0
        else vanChuyenDangChon.phi

    val tongTien = tongTienSanPham + phiVanChuyenThucTe
//pt thanh toán
    val danhSachThanhToan = listOf(
        PhuongThucThanhToan(
            ma = "TienMat",
            ten = "Thanh toán khi nhận hàng",
            icon = Icons.Default.Payments
        ),
        PhuongThucThanhToan(
            ma = "ChuyenKhoan",
            ten = "Chuyển khoản ngân hàng",
            icon = Icons.Default.AccountBalance
        ),
        PhuongThucThanhToan(
            ma = "ViDienTu",
            ten = "Ví điện tử",
            icon = Icons.Default.AccountBalanceWallet
        )
    )
//Chi tiết thanh toán
    val phiVanChuyenGoc = vanChuyenDangChon.phi

    val giamPhiVanChuyen =
        if (KhuyenMaiDaChon != null) phiVanChuyenGoc else 0

    val tongThanhToan = tongTienSanPham + phiVanChuyenGoc - giamPhiVanChuyen




    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Thanh toán",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = BamQuayLai) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MauXanh
                )
            )
        },
        bottomBar = {
            ThanhTongCongDatHang(
                tongTien = tongTien,
                khiDatHang = {
                    if (diaChiDangChon == null) {
                        Toast.makeText(
                            nguCanh,
                            "Chưa có địa chỉ giao hàng",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@ThanhTongCongDatHang
                    }

                    datDonHang(
                        nguCanh = nguCanh,
                        phuongThuc = phuongThucThanhToan,
                        danhSachSanPham = danhSachSanPham,
                        diaChiGiaoHang = diaChiDangChon!!.DiaChiChiTiet,
                        ghiChu = ghiChuChoShop,
                        tongTien = tongTien,
                        maKhuyenMai = KhuyenMaiDaChon?.MaKhuyenMai,
                        phiVanChuyen = phiVanChuyenThucTe
                    )
                }
            )
        }
    ) { paddingValue ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .background(Color(0xFFF5F5F5))
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {

            DiaChiNhanHang(
                khiChonDiaChi = { diaChiDangChon = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SanPhamDonHang(danhSachSanPham)

            Spacer(modifier = Modifier.height(10.dp))
            MaKhuyenMai(
                khuyenMai = KhuyenMaiDaChon,
                onChonKhuyenMai = {
                    navController.navigate("khuyenmai")
                }
            )
            Spacer(modifier = Modifier.height(10.dp))

            KhuVucGhiChuChoShop(
                ghiChu = ghiChuChoShop,
                khiThayDoi = { ghiChuChoShop = it }
            )
            Spacer(modifier = Modifier.height(10.dp))

            PTVanChuyen(
                danhSach = danhSachVanChuyen,
                dangChon = vanChuyenDangChon,
                khiChon = { vanChuyenDangChon = it }
            )
            Spacer(modifier = Modifier.height(10.dp))

            PTThanhToan(
                phuongThucDangChon = phuongThucThanhToan,
                khiDoiPhuongThuc = { phuongThucThanhToan = it }
            )



            Spacer(modifier = Modifier.height(10.dp))
            //ktra có mã KM thì miễn ship
            if (KhuyenMaiDaChon != null) {
                Text(
                    "Phí vận chuyển: Miễn phí",
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text("Phí vận chuyển: ${vanChuyenDangChon.phi} VNĐ")
            }
            Spacer(modifier = Modifier.height(10.dp))
            ChiTietThanhToan(
                tongTienSanPham = tongTienSanPham,
                phiVanChuyen = phiVanChuyenGoc,
                giamPhiShip = giamPhiVanChuyen,
                tongThanhToan = tongThanhToan
            )
        }
    }
}


//CÁC THÀNH PHẦN CÓ TRONG TRANG THANH TOÁN
//Lấy điịa chỉ
@Composable
fun DiaChiNhanHang(
    khiChonDiaChi: (DiaChi) -> Unit
) {
    val user = BienDungChung.userHienTai
    var danhSachDiaChi by remember { mutableStateOf<List<DiaChi>>(emptyList()) }
    var dangTai by remember { mutableStateOf(true) }
    var loi by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val res = RetrofitClient.api.layDanhSachDiaChi(maNguoiDung = 1)
            if (res.status == "success") {
                danhSachDiaChi = res.data ?: emptyList()
                val diaChiMacDinh = danhSachDiaChi.find { it.MacDinh == 1 }
                diaChiMacDinh?.let { khiChonDiaChi(it) }
            }
            dangTai = false
        } catch (e: Exception) {
            dangTai = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {
        when {
            dangTai -> Text("Đang tải địa chỉ...")
            loi.isNotEmpty() -> Text(loi, color = Color.Red)
            danhSachDiaChi.isEmpty() -> Text("Chưa có địa chỉ giao hàng")
            else -> {
                val dc = danhSachDiaChi.find { it.MacDinh == 1 }
                    ?: danhSachDiaChi.first()

                Text(
                    "${dc.TenNguoiNhan} | ${dc.SDTNguoiNhan}",
                    fontWeight = FontWeight.Bold
                )
                Text(dc.DiaChiChiTiet)
            }
        }
    }
}




@Composable
fun SanPhamDonHang(
    danhSachSanPham: List<SachtrongGioHang>
) {
    Text(
        text="Sản phẩm",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {

        danhSachSanPham.forEach { sanPham ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //ảnh
                AsyncImage(
                    model = sanPham.AnhBia,
                    contentDescription = sanPham.TenSach,
                    modifier = Modifier
                        .size(width = 80.dp, height = 100.dp)
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        sanPham.TenSach,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "${sanPham.GiaBan} VNĐ",
                        color = Color.Red,
                        fontSize = 16.sp
                    )

                    Text(
                        "Số lượng: ${sanPham.SoLuong}",
                        fontSize = 14.sp
                    )
                }
            }

        }
    }
}

//Mã khuyến mãi
@Composable
fun MaKhuyenMai(
    khuyenMai: KhuyenMai?,
    onChonKhuyenMai: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                if (khuyenMai != null) {
                    Text(
                        "Mã: ${khuyenMai.MaCode}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Giảm ${khuyenMai.GiaTriGiam.toInt()} VNĐ",
                        color = Color.Red
                    )
                } else {
                    Text("Chưa chọn mã khuyến mãi")
                }
            }

            TextButton(onClick = onChonKhuyenMai) {
                Text("Chọn")
            }
        }
    }
}

//GHI CHÚ CHO SHOP
@Composable
fun KhuVucGhiChuChoShop(
    ghiChu: String,
    khiThayDoi: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(
            text = "Ghi chú cho shop",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = ghiChu,
            onValueChange = khiThayDoi,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Ví dụ: Giao giờ hành chính, gọi trước khi giao...")
            },
            maxLines = 3
        )
    }
}

//PHƯƠNG THỨC VẬN CHUYỂN
@Composable
fun PTVanChuyen(
    danhSach: List<PhuongThucVanChuyen>,
    dangChon: PhuongThucVanChuyen,
    khiChon: (PhuongThucVanChuyen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(
            "Phương thức vận chuyển",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        danhSach.forEach { vc ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { khiChon(vc) }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = vc == dangChon,
                    onClick = { khiChon(vc) }
                )

                Column {
                    Text(vc.ten)
                    Text(
                        "${vc.phi} VNĐ",
                        color = Color.Red,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

//PHƯƠNG THỨC THANH TOÁN
@Composable
fun PTThanhToan(
    phuongThucDangChon: String,
    khiDoiPhuongThuc: (String) -> Unit
) {
    val danhSachThanhToan = listOf(
        PhuongThucThanhToan("TienMat", "Thanh toán khi nhận hàng", Icons.Default.Payments),
        PhuongThucThanhToan("ChuyenKhoan", "Chuyển khoản ngân hàng", Icons.Default.AccountBalance),
        PhuongThucThanhToan("ViDienTu", "Ví điện tử", Icons.Default.AccountBalanceWallet)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(
            text = "Phương thức thanh toán",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        danhSachThanhToan.forEach { pt ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(
                        if (pt.ma == phuongThucDangChon)
                            Color(0xFFE3F2FD)
                        else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { khiDoiPhuongThuc(pt.ma) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = pt.icon,
                    contentDescription = null,
                    tint = if (pt.ma == phuongThucDangChon)
                        MauXanh
                    else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = pt.ten,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp
                )

                RadioButton(
                    selected = pt.ma == phuongThucDangChon,
                    onClick = { khiDoiPhuongThuc(pt.ma) }
                )
            }
        }
    }
}

//CHI TIẾT THANH TOÁN
@Composable
fun ChiTietThanhToan(
    tongTienSanPham: Int,
    phiVanChuyen: Int,
    giamPhiShip: Int,
    tongThanhToan: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {

        Text(
            "Chi tiết thanh toán",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        DongTien("Tổng tiền sách", tongTienSanPham)
        DongTien("Phí vận chuyển", phiVanChuyen)

        if (giamPhiShip > 0) {
            DongTien(
                tieuDe = "Giảm giá phí vận chuyển",
                soTien = -giamPhiShip,
                mau = Color.Red
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        DongTien(
            tieuDe = "Tổng thanh toán",
            soTien = tongThanhToan,
            mau = Color.Red,
            dam = true
        )
    }
}

@Composable
fun DongTien(
    tieuDe: String,
    soTien: Int,
    mau: Color = Color.Black,
    dam: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(tieuDe)
        Text(
            text = "${soTien} VNĐ",
            color = mau,
            fontWeight = if (dam) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ThanhTongCongDatHang(
    tongTien: Int,
    khiDatHang: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Tổng cộng",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "${tongTien} VNĐ",
                    color = Color.Red,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = khiDatHang,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935) // đỏ đẹp
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Đặt hàng",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
=======
//package com.example.bookstore
//
//
//import PhuongThucThanhToan
//import android.widget.Toast
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountBalance
//import androidx.compose.material.icons.filled.AccountBalanceWallet
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Payments
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Divider
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.RadioButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import coil.compose.AsyncImage
//import com.example.bookstore.Api.RetrofitClient
//import com.example.bookstore.Model.DiaChi
//import com.example.bookstore.Model.KhuyenMai
//import com.example.bookstore.Model.PhuongThucVanChuyen
//import com.example.bookstore.Model.SachtrongGioHang
//import com.example.bookstore.Utils.datDonHang
//import kotlin.collections.first
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ManHinhThanhToan(
//    navController: NavController,
//    danhSachSanPham: List<SachtrongGioHang>,
//    BamQuayLai: () -> Unit
//) {
////KHAI BAO BIẾN LIÊN QUAN
//    var phuongThucThanhToan by remember { mutableStateOf("TienMat") }
//
//    val tongTienSanPham = danhSachSanPham.sumOf {
//        it.GiaBan * it.SoLuong
//    }
//
//    var diaChiDangChon by remember {
//        mutableStateOf<DiaChi?>(null)
//    }
//
//    val nguCanh = LocalContext.current
//
//    var KhuyenMaiDaChon by remember {
//        mutableStateOf<KhuyenMai?>(null)
//    }
//
//    var ghiChuChoShop by remember {
//        mutableStateOf("")
//    }
//
//    val danhSachVanChuyen = listOf(
//        PhuongThucVanChuyen("Giao hàng tiết kiệm", 16000),
//        PhuongThucVanChuyen("Giao hàng nhanh", 30000),
//        PhuongThucVanChuyen("Hỏa tốc", 50000)
//    )
//    var vanChuyenDangChon by remember {
//        mutableStateOf(danhSachVanChuyen.first())
//    }
////miễn ship khi có mã KM
//    val phiVanChuyenThucTe =
//        if (KhuyenMaiDaChon != null) 0
//        else vanChuyenDangChon.phi
//
//    val tongTien = tongTienSanPham + phiVanChuyenThucTe
////pt thanh toán
//    val danhSachThanhToan = listOf(
//        PhuongThucThanhToan(
//            ma = "TienMat",
//            ten = "Thanh toán khi nhận hàng",
//            icon = Icons.Default.Payments
//        ),
//        PhuongThucThanhToan(
//            ma = "ChuyenKhoan",
//            ten = "Chuyển khoản ngân hàng",
//            icon = Icons.Default.AccountBalance
//        ),
//        PhuongThucThanhToan(
//            ma = "ViDienTu",
//            ten = "Ví điện tử",
//            icon = Icons.Default.AccountBalanceWallet
//        )
//    )
////Chi tiết thanh toán
//    val phiVanChuyenGoc = vanChuyenDangChon.phi
//
//    val giamPhiVanChuyen =
//        if (KhuyenMaiDaChon != null) phiVanChuyenGoc else 0
//
//    val tongThanhToan = tongTienSanPham + phiVanChuyenGoc - giamPhiVanChuyen
//
//
//
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        "Thanh toán",
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 24.sp,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = BamQuayLai) {
//                        Icon(
//                            Icons.Default.ArrowBack,
//                            contentDescription = "Quay lại",
//                            tint = Color.White
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = MauXanh
//                )
//            )
//        },
//        bottomBar = {
//            ThanhTongCongDatHang(
//                tongTien = tongTien,
//                khiDatHang = {
//                    if (diaChiDangChon == null) {
//                        Toast.makeText(
//                            nguCanh,
//                            "Chưa có địa chỉ giao hàng",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        return@ThanhTongCongDatHang
//                    }
//
//                    datDonHang(
//                        nguCanh = nguCanh,
//                        phuongThuc = phuongThucThanhToan,
//                        danhSachSanPham = danhSachSanPham,
//                        diaChiGiaoHang = diaChiDangChon!!.DiaChiChiTiet,
//                        ghiChu = ghiChuChoShop,
//                        tongTien = tongTien,
//                        maKhuyenMai = KhuyenMaiDaChon?.MaKhuyenMai,
//                        phiVanChuyen = phiVanChuyenThucTe
//                    )
//                }
//            )
//        }
//    ) { paddingValue ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValue)
//                .background(Color(0xFFF5F5F5))
//                .padding(12.dp)
//                .verticalScroll(rememberScrollState())
//        ) {
//
//            DiaChiNhanHang(
//                maNguoiDung = 1,
//                khiChonDiaChi = { diaChiDangChon = it }
//            )
//
//            Spacer(modifier = Modifier.height(10.dp))
//
//            SanPhamDonHang(danhSachSanPham)
//
//            Spacer(modifier = Modifier.height(10.dp))
//            MaKhuyenMai(
//                khuyenMai = KhuyenMaiDaChon,
//                onChonKhuyenMai = {
//                    navController.navigate("khuyenmai")
//                }
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//
//            KhuVucGhiChuChoShop(
//                ghiChu = ghiChuChoShop,
//                khiThayDoi = { ghiChuChoShop = it }
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//
//            PTVanChuyen(
//                danhSach = danhSachVanChuyen,
//                dangChon = vanChuyenDangChon,
//                khiChon = { vanChuyenDangChon = it }
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//
//            PTThanhToan(
//                phuongThucDangChon = phuongThucThanhToan,
//                khiDoiPhuongThuc = { phuongThucThanhToan = it }
//            )
//
//
//
//            Spacer(modifier = Modifier.height(10.dp))
//            //ktra có mã KM thì miễn ship
//            if (KhuyenMaiDaChon != null) {
//                Text(
//                    "Phí vận chuyển: Miễn phí",
//                    color = Color.Green,
//                    fontWeight = FontWeight.Bold
//                )
//            } else {
//                Text("Phí vận chuyển: ${vanChuyenDangChon.phi} VNĐ")
//            }
//            Spacer(modifier = Modifier.height(10.dp))
//            ChiTietThanhToan(
//                tongTienSanPham = tongTienSanPham,
//                phiVanChuyen = phiVanChuyenGoc,
//                giamPhiShip = giamPhiVanChuyen,
//                tongThanhToan = tongThanhToan
//            )
//        }
//    }
//}
//
//
////CÁC THÀNH PHẦN CÓ TRONG TRANG THANH TOÁN
////Lấy điịa chỉ
//@Composable
//fun DiaChiNhanHang(
//    maNguoiDung: Int,
//    khiChonDiaChi: (DiaChi) -> Unit
//) {
//    var danhSachDiaChi by remember { mutableStateOf<List<DiaChi>>(emptyList()) }
//    var dangTai by remember { mutableStateOf(true) }
//
//    LaunchedEffect(maNguoiDung) {
//        try {
//            danhSachDiaChi = RetrofitClient.api.layDanhSachDiaChi(maNguoiDung)
//            dangTai = false
//
//            val diaChiMacDinh = danhSachDiaChi.find { it.MacDinh == 1 }
//            if (diaChiMacDinh != null) {
//                khiChonDiaChi(diaChiMacDinh)
//            }
//        } catch (e: Exception) {
//            dangTai = false
//        }
//    }
//
//    val diaChiMacDinh = danhSachDiaChi.find { it.MacDinh == 1 }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(12.dp)
//    ) {
//        when {
//            dangTai -> Text("Đang tải địa chỉ...")
//            diaChiMacDinh != null -> {
//                Text(
//                    "${diaChiMacDinh.TenNguoiNhan} | ${diaChiMacDinh.SDTNguoiNhan}",
//                    fontWeight = FontWeight.Bold
//                )
//                Text(diaChiMacDinh.DiaChiChiTiet)
//            }
//            else -> Text("Chưa có địa chỉ mặc định")
//        }
//    }
//}
//
//
//
//@Composable
//fun SanPhamDonHang(
//    danhSachSanPham: List<SachtrongGioHang>
//) {
//    Text(
//        text="Sản phẩm",
//        fontSize = 14.sp,
//        fontWeight = FontWeight.Bold
//    )
//    Spacer(modifier = Modifier.height(8.dp))
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(12.dp)
//    ) {
//
//        danhSachSanPham.forEach { sanPham ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                //ảnh
//                AsyncImage(
//                    model = sanPham.AnhBia,
//                    contentDescription = sanPham.TenSach,
//                    modifier = Modifier
//                        .size(width = 80.dp, height = 100.dp)
//                        .background(Color.LightGray)
//                )
//
//                Spacer(modifier = Modifier.width(10.dp))
//
//                Column(
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text(
//                        sanPham.TenSach,
//                        fontWeight = FontWeight.Bold
//                    )
//
//                    Text(
//                        "${sanPham.GiaBan} VNĐ",
//                        color = Color.Red,
//                        fontSize = 16.sp
//                    )
//
//                    Text(
//                        "Số lượng: ${sanPham.SoLuong}",
//                        fontSize = 14.sp
//                    )
//                }
//            }
//
//        }
//    }
//}
//
////Mã khuyến mãi
//@Composable
//fun MaKhuyenMai(
//    khuyenMai: KhuyenMai?,
//    onChonKhuyenMai: () -> Unit
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(12.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            Column(modifier = Modifier.weight(1f)) {
//                if (khuyenMai != null) {
//                    Text(
//                        "Mã: ${khuyenMai.MaCode}",
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        "Giảm ${khuyenMai.GiaTriGiam.toInt()} VNĐ",
//                        color = Color.Red
//                    )
//                } else {
//                    Text("Chưa chọn mã khuyến mãi")
//                }
//            }
//
//            TextButton(onClick = onChonKhuyenMai) {
//                Text("Chọn")
//            }
//        }
//    }
//}
//
////GHI CHÚ CHO SHOP
//@Composable
//fun KhuVucGhiChuChoShop(
//    ghiChu: String,
//    khiThayDoi: (String) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(12.dp)
//    ) {
//        Text(
//            text = "Ghi chú cho shop",
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(modifier = Modifier.height(6.dp))
//
//        OutlinedTextField(
//            value = ghiChu,
//            onValueChange = khiThayDoi,
//            modifier = Modifier.fillMaxWidth(),
//            placeholder = {
//                Text("Ví dụ: Giao giờ hành chính, gọi trước khi giao...")
//            },
//            maxLines = 3
//        )
//    }
//}
//
////PHƯƠNG THỨC VẬN CHUYỂN
//@Composable
//fun PTVanChuyen(
//    danhSach: List<PhuongThucVanChuyen>,
//    dangChon: PhuongThucVanChuyen,
//    khiChon: (PhuongThucVanChuyen) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(12.dp)
//    ) {
//        Text(
//            "Phương thức vận chuyển",
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        danhSach.forEach { vc ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { khiChon(vc) }
//                    .padding(vertical = 6.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                RadioButton(
//                    selected = vc == dangChon,
//                    onClick = { khiChon(vc) }
//                )
//
//                Column {
//                    Text(vc.ten)
//                    Text(
//                        "${vc.phi} VNĐ",
//                        color = Color.Red,
//                        fontSize = 13.sp
//                    )
//                }
//            }
//        }
//    }
//}
//
////PHƯƠNG THỨC THANH TOÁN
//@Composable
//fun PTThanhToan(
//    phuongThucDangChon: String,
//    khiDoiPhuongThuc: (String) -> Unit
//) {
//    val danhSachThanhToan = listOf(
//        PhuongThucThanhToan("TienMat", "Thanh toán khi nhận hàng", Icons.Default.Payments),
//        PhuongThucThanhToan("ChuyenKhoan", "Chuyển khoản ngân hàng", Icons.Default.AccountBalance),
//        PhuongThucThanhToan("ViDienTu", "Ví điện tử", Icons.Default.AccountBalanceWallet)
//    )
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(12.dp)
//    ) {
//        Text(
//            text = "Phương thức thanh toán",
//            fontWeight = FontWeight.Bold,
//            fontSize = 16.sp
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        danhSachThanhToan.forEach { pt ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 6.dp)
//                    .background(
//                        if (pt.ma == phuongThucDangChon)
//                            Color(0xFFE3F2FD)
//                        else Color.Transparent,
//                        RoundedCornerShape(12.dp)
//                    )
//                    .clickable { khiDoiPhuongThuc(pt.ma) }
//                    .padding(12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//                Icon(
//                    imageVector = pt.icon,
//                    contentDescription = null,
//                    tint = if (pt.ma == phuongThucDangChon)
//                        MauXanh
//                    else Color.Gray,
//                    modifier = Modifier.size(28.dp)
//                )
//
//                Spacer(modifier = Modifier.width(12.dp))
//
//                Text(
//                    text = pt.ten,
//                    modifier = Modifier.weight(1f),
//                    fontSize = 15.sp
//                )
//
//                RadioButton(
//                    selected = pt.ma == phuongThucDangChon,
//                    onClick = { khiDoiPhuongThuc(pt.ma) }
//                )
//            }
//        }
//    }
//}
//
////CHI TIẾT THANH TOÁN
//@Composable
//fun ChiTietThanhToan(
//    tongTienSanPham: Int,
//    phiVanChuyen: Int,
//    giamPhiShip: Int,
//    tongThanhToan: Int
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(12.dp)
//    ) {
//
//        Text(
//            "Chi tiết thanh toán",
//            fontWeight = FontWeight.Bold,
//            fontSize = 16.sp
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        DongTien("Tổng tiền sách", tongTienSanPham)
//        DongTien("Phí vận chuyển", phiVanChuyen)
//
//        if (giamPhiShip > 0) {
//            DongTien(
//                tieuDe = "Giảm giá phí vận chuyển",
//                soTien = -giamPhiShip,
//                mau = Color.Red
//            )
//        }
//
//        Divider(modifier = Modifier.padding(vertical = 8.dp))
//
//        DongTien(
//            tieuDe = "Tổng thanh toán",
//            soTien = tongThanhToan,
//            mau = Color.Red,
//            dam = true
//        )
//    }
//}
//
//@Composable
//fun DongTien(
//    tieuDe: String,
//    soTien: Int,
//    mau: Color = Color.Black,
//    dam: Boolean = false
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(tieuDe)
//        Text(
//            text = "${soTien} VNĐ",
//            color = mau,
//            fontWeight = if (dam) FontWeight.Bold else FontWeight.Normal
//        )
//    }
//}
//
//@Composable
//fun ThanhTongCongDatHang(
//    tongTien: Int,
//    khiDatHang: () -> Unit
//) {
//    Surface(
//        shadowElevation = 8.dp,
//        color = Color.White
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = "Tổng cộng",
//                    color = Color.Gray,
//                    fontSize = 14.sp
//                )
//                Text(
//                    text = "${tongTien} VNĐ",
//                    color = Color.Red,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            Button(
//                onClick = khiDatHang,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFE53935) // đỏ đẹp
//                ),
//                shape = RoundedCornerShape(12.dp),
//                modifier = Modifier.height(48.dp)
//            ) {
//                Text(
//                    text = "Đặt hàng",
//                    color = Color.White,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//}
>>>>>>> 5bd0042cb709372681b352da084eacfd9a9d67d3
