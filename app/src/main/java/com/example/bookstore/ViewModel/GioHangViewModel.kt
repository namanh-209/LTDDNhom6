import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.Api.RetrofitClient
import com.example.bookstore.Model.SachtrongGioHang
import kotlinx.coroutines.launch

class GioHangViewModel : ViewModel() {

    var danhSachSach = mutableStateListOf<SachtrongGioHang>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf("")
        private set

    fun taiGioHang(maNguoiDung: Int) {
        viewModelScope.launch {
            isLoading = true
            error = ""

            try {
                val data = RetrofitClient.api.layGioHang(maNguoiDung)
                danhSachSach.clear()
                danhSachSach.addAll(data)
            } catch (e: Exception) {
                error = "Không tải được giỏ hàng"
                Log.e("GioHang", e.toString())
            }

            isLoading = false
        }
    }

    fun tangSoLuong(sach: SachtrongGioHang) {
        val soLuongMoi = sach.SoLuong + 1
        capNhatSoLuong(sach, soLuongMoi)
    }

    fun giamSoLuong(sach: SachtrongGioHang) {
        if (sach.SoLuong <= 1) return
        val soLuongMoi = sach.SoLuong - 1
        capNhatSoLuong(sach, soLuongMoi)
    }


    fun Hienthiloi(message: String) {
        error = message
    }


    private fun capNhatSoLuong(
        sach: SachtrongGioHang,
        soLuongMoi: Int
    ) {
        viewModelScope.launch {
            try {
                RetrofitClient.api.capNhatSoLuong(
                    CapNhatSLRequest(
                        maGioHang = sach.MaGioHang,
                        soLuongMoi = soLuongMoi
                    )
                )

                // cập nhật UI ngay (không cần gọi lại API)
                val index = danhSachSach.indexOfFirst {
                    it.MaGioHang == sach.MaGioHang
                }

                if (index != -1) {
                    danhSachSach[index] =
                        sach.copy(SoLuong = soLuongMoi)
                }

            } catch (e: Exception) {
                error = "Cập nhật số lượng thất bại"
            }
        }
    }

}
