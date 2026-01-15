import com.google.gson.annotations.SerializedName

data class MChiTietDonHangAdmin(
    @SerializedName("MaSach") val maSach: Int,
    @SerializedName("TenSach") val tenSach: String,
    @SerializedName("AnhBia") val anhBia: String,
    @SerializedName("SoLuong") val soLuong: Int,

    // SỬA: Đổi "GiaTien" thành "DonGia" để khớp với database
    @SerializedName("DonGia") val donGia: Double
)