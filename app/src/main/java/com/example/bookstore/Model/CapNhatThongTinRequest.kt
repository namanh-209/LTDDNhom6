import com.google.gson.annotations.SerializedName

data class CapNhatThongTinRequest(
    @SerializedName("MaNguoiDung") val maNguoiDung: Int,
    // Thông tin tài khoản
    @SerializedName("HoTen") val hoTen: String,
    @SerializedName("SoDienThoai") val soDienThoai: String,
    @SerializedName("Email") val email: String,
    @SerializedName("GioiTinh") val gioiTinh: String,
    @SerializedName("NgaySinh") val ngaySinh: String,

    // Thông tin người nhận
    @SerializedName("TenNguoiNhan") val tenNguoiNhan: String,
    @SerializedName("SDTNguoiNhan") val sdtNguoiNhan: String,
    @SerializedName("DiaChi") val diaChi: String
)