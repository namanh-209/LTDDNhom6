import com.google.gson.annotations.SerializedName

data class CapNhatThongTinRequest(
    @SerializedName("MaNguoiDung") val maNguoiDung: Int,
    @SerializedName("HoTen") val hoTen: String,
    @SerializedName("SoDienThoai") val soDienThoai: String,
    @SerializedName("Email") val email: String,
    @SerializedName("GioiTinh") val gioiTinh: String,
    @SerializedName("NgaySinh") val ngaySinh: String,

    // Thêm dòng này:
    @SerializedName("DiaChi") val diaChi: String
)