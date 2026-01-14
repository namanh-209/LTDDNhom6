import com.google.gson.annotations.SerializedName

data class MChiTietDonHangAdmin(
    @SerializedName("MaSach") val maSach: Int,
    @SerializedName("TenSach") val tenSach: String,
    @SerializedName("AnhBia") val anhBia: String,
    @SerializedName("SoLuong") val soLuong: Int,
    @SerializedName("GiaTien") val donGia: Double
)