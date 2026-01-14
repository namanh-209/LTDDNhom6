import com.google.gson.annotations.SerializedName

data class CapNhatTrangThaiRequest(
    @SerializedName("ma_don_hang") val maDonHang: Int,
    @SerializedName("trang_thai") val trangThai: String
)