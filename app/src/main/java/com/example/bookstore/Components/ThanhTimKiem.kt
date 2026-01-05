
package com.example.bookstore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThanhTimKiem(
    tuKhoa: String,                  // 1. Nhận chữ từ màn hình chính
    khiGoChu: (String) -> Unit       // 2. Báo lại cho màn hình chính khi gõ
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp)
            .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Kính lúp
        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(12.dp))

        // Ô nhập liệu (Logic ẩn hiện chữ mờ)
        Box(modifier = Modifier.weight(1f)) {
            if (tuKhoa.isEmpty()) {
                Text("Nhập từ khóa...", color = Color.Gray, fontSize = 14.sp)
            }

            BasicTextField(
                value = tuKhoa,
                onValueChange = khiGoChu, // Khi gõ, gọi hàm này
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Icon Lọc
        Image(
            painter = painterResource(id = R.drawable.icon_loc),
            contentDescription = null,
            modifier = Modifier.size(27.dp)
        )
    }
}