package com.diarybook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarybook.ui.component.PrimaryButton
import com.diarybook.ui.component.WhiteCard
import com.diarybook.ui.theme.*
import com.diarybook.viewmodel.BillViewModel
import java.text.SimpleDateFormat
import java.util.*

data class CategoryItem(
    val id: Long,
    val name: String,
    val icon: String,
    val color: Color
)

@Composable
fun AddBillScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: (
        isExpense: Boolean,
        amount: Double,
        categoryId: Long,
        categoryName: String,
        categoryIcon: String,
        date: String,
        remark: String
    ) -> Unit = { _, _, _, _, _, _, _ -> },
    billViewModel: BillViewModel? = null
) {
    var isExpense by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryItem?>(null) }
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var remark by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val defaultBook = billViewModel?.defaultBook?.collectAsState()?.value
    val defaultBookId = defaultBook?.id ?: 0

    val expenseCategories = remember {
        listOf(
            CategoryItem(1, "餐饮", "🍽️", Color(0xFFFF5722)),
            CategoryItem(2, "交通", "🚗", Color(0xFF2196F3)),
            CategoryItem(3, "购物", "🛒", Color(0xFF9C27B0)),
            CategoryItem(4, "娱乐", "🎬", Color(0xFFFF9800)),
            CategoryItem(5, "医疗", "🏥", Color(0xFFF44336)),
            CategoryItem(6, "教育", "📚", Color(0xFF4CAF50)),
            CategoryItem(7, "住房", "🏠", Color(0xFF3F51B5)),
            CategoryItem(8, "其他", "📌", Color(0xFF607D8B))
        )
    }

    val incomeCategories = remember {
        listOf(
            CategoryItem(9, "工资", "💰", Color(0xFF4CAF50)),
            CategoryItem(10, "奖金", "🎁", Color(0xFFFF9800)),
            CategoryItem(11, "投资", "📈", Color(0xFF2196F3)),
            CategoryItem(12, "兼职", "💼", Color(0xFF9C27B0)),
            CategoryItem(13, "其他", "📌", Color(0xFF607D8B))
        )
    }

    val categories = if (isExpense) expenseCategories else incomeCategories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        TopBar(onBackClick = onBackClick)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TypeSwitcher(
            isExpense = isExpense,
            onTypeChange = { isExpense = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AmountInputField(
            amount = amount,
            onAmountChange = { amount = it },
            isExpense = isExpense
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CategorySelector(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DateSelector(
            date = selectedDate,
            onClick = { showDatePicker = true }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        RemarkInputField(
            remark = remark,
            onRemarkChange = { remark = it }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        SaveButton(
            enabled = amount.isNotEmpty() && selectedCategory != null,
            onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                val categoryIdValue = selectedCategory?.id ?: 0
                val categoryNameValue = selectedCategory?.name ?: ""
                val categoryIconValue = selectedCategory?.icon ?: ""
                
                billViewModel?.insertBill(
                    bookId = defaultBookId, // 使用默认账本ID
                    type = if (isExpense) 0 else 1,
                    categoryId = categoryIdValue,
                    categoryName = categoryNameValue,
                    categoryIcon = categoryIconValue,
                    amount = amountValue,
                    date = selectedDate,
                    remark = remark.ifEmpty { null }
                )
                
                onSaveClick(
                    isExpense,
                    amountValue,
                    categoryIdValue,
                    categoryNameValue,
                    categoryIconValue,
                    selectedDate,
                    remark
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "返回",
            tint = TextPrimary,
            modifier = Modifier
                .size(28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBackClick
                )
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "记一笔",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Spacer(modifier = Modifier.size(28.dp))
    }
}

@Composable
private fun TypeSwitcher(
    isExpense: Boolean,
    onTypeChange: (Boolean) -> Unit
) {
    WhiteCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TypeItem(
                text = "支出",
                isSelected = isExpense,
                color = ExpenseRed,
                onClick = { onTypeChange(true) }
            )
            TypeItem(
                text = "收入",
                isSelected = !isExpense,
                color = IncomeGreen,
                onClick = { onTypeChange(false) }
            )
        }
    }
}

@Composable
private fun TypeItem(
    text: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) color else TextTertiary
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun AmountInputField(
    amount: String,
    onAmountChange: (String) -> Unit,
    isExpense: Boolean
) {
    WhiteCard {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "金额",
                fontSize = 14.sp,
                color = TextTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¥",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) ExpenseRed else IncomeGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            onAmountChange(newValue)
                        }
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExpense) ExpenseRed else IncomeGreen
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (amount.isEmpty()) {
                                Text(
                                    text = "0.00",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CategorySelector(
    categories: List<CategoryItem>,
    selectedCategory: CategoryItem?,
    onCategorySelected: (CategoryItem) -> Unit
) {
    WhiteCard {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "分类",
                fontSize = 14.sp,
                color = TextTertiary
            )
            Spacer(modifier = Modifier.height(12.dp))
            GridLayout(
                columns = 4,
                items = categories,
                modifier = Modifier.fillMaxWidth()
            ) { category ->
                CategoryItemView(
                    category = category,
                    isSelected = selectedCategory?.id == category.id,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
private fun CategoryItemView(
    category: CategoryItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) category.color else LightPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.icon,
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category.name,
            fontSize = 12.sp,
            color = if (isSelected) TextPrimary else TextSecondary
        )
    }
}

@Composable
private fun <T> GridLayout(
    columns: Int,
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    Column(modifier = modifier) {
        items.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        itemContent(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun DateSelector(
    date: String,
    onClick: () -> Unit
) {
    WhiteCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "日期",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = date,
                fontSize = 16.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun RemarkInputField(
    remark: String,
    onRemarkChange: (String) -> Unit
) {
    WhiteCard {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "备注",
                fontSize = 14.sp,
                color = TextTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = remark,
                onValueChange = onRemarkChange,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    color = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (remark.isEmpty()) {
                            Text(
                                text = "添加备注...",
                                fontSize = 16.sp,
                                color = TextTertiary
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun SaveButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PrimaryButton(
        text = "保存",
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    )
}
