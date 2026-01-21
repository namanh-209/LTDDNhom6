package com.example.bookstore.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bookstore.R
import java.text.Normalizer
import java.util.regex.Pattern


// --- DATA CLASS CHO BỘ LỌC ---
data class FilterCriteria(
    val sortOption: SortOption = SortOption.BanChayNhat,
    val minPrice: String = "",
    val maxPrice: String = "",
)

enum class SortOption(val label: String) {
    BanChayNhat("Bán chạy nhất"),
    MoiNhat("Mới nhất"),
    GiaThapToiCao("Giá: Thấp -> Cao"),
    GiaCaoToiThap("Giá: Cao -> Thấp"),
}


// --- GIAO DIỆN CHÍNH ---
@Composable
fun ThanhTimKiem(
    tuKhoa: String,
    khiGoChu: (String) -> Unit,
    onApplyFilter: (FilterCriteria) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var showFilterDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(25.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (tuKhoa.isEmpty()) {
                Text("Tìm kiếm sách...", color = Color.Gray, fontSize = 14.sp)
            }
            BasicTextField(
                value = tuKhoa,
                onValueChange = khiGoChu,
                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                }),
                modifier = Modifier.fillMaxWidth()
            )
        }

        IconButton(onClick = { showFilterDialog = true }) {
            Icon(
                painter = painterResource(id = R.drawable.icon_loc), // Đảm bảo icon này tồn tại
                contentDescription = "Lọc",
                tint = Color(0xFF6200EE),
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            onDismiss = { showFilterDialog = false },
            onApply = { criteria ->
                onApplyFilter(criteria)
                showFilterDialog = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApply: (FilterCriteria) -> Unit
) {
    var selectedSort by remember { mutableStateOf(SortOption.BanChayNhat) }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().heightIn(max = 700.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bộ lọc", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    TextButton(onClick = {
                        selectedSort = SortOption.BanChayNhat
                        minPrice = ""
                        maxPrice = ""

                    }) {
                        Text("Thiết lập lại", color = Color.Red)
                    }
                }

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                Text("Sắp xếp theo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SortOption.values().forEach { option ->
                        FilterChip(
                            selected = (option == selectedSort),
                            onClick = { selectedSort = option },
                            label = { Text(option.label) },
                            leadingIcon = if (option == selectedSort) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Khoảng giá (VNĐ)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minPrice,
                        onValueChange = { if (it.all { c -> c.isDigit() }) minPrice = it },
                        placeholder = { Text("Tối thiểu") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Text("-", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = maxPrice,
                        onValueChange = { if (it.all { c -> c.isDigit() }) maxPrice = it },
                        placeholder = { Text("Tối đa") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        onApply(FilterCriteria(selectedSort, minPrice, maxPrice))
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Áp dụng bộ lọc", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Đóng", color = Color.Gray)
                }
            }
        }
    }
}