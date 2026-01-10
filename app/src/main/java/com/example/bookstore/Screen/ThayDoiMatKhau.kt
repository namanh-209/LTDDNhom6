package com.example.bookstore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManHinhThayDoiMatKhau(onBackClick: () -> Unit) {

    KhungGiaoDien(tieuDe = "Thay đổi mật khẩu",
        onBackClick = onBackClick,
        )

     { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(50.dp))


                Image(
                    painter = painterResource(id = R.drawable.icon_doi_mat_khau),
                    contentDescription = "Logo",
                    modifier = Modifier.size(170.dp),
                    colorFilter = ColorFilter.tint(Color(0xFF555555))
                )

                Spacer(modifier = Modifier.height(40.dp))


                ONhapMatKhau(tieuDe = "Mật khẩu hiện tại", goiY = "Nhập mật khẩu hiện tại")
                Spacer(modifier = Modifier.height(16.dp))

                ONhapMatKhau(tieuDe = "Mật khẩu mới", goiY = "Nhập mật khẩu mới")
                Spacer(modifier = Modifier.height(16.dp))

                ONhapMatKhau(tieuDe = "Xác nhận mật khẩu", goiY = "Nhập lại mật khẩu mới")

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { /* Xử lý lưu mật khẩu */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MauXanh),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(160.dp)
                        .height(50.dp)
                ) {
                    Text("Lưu", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
}

@Composable
fun ONhapMatKhau(tieuDe: String, goiY: String) {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = tieuDe,
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = goiY, color = Color.Gray, fontSize = 14.sp) },
            singleLine = true,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.o_khoa_nho),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = MauXanh,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

