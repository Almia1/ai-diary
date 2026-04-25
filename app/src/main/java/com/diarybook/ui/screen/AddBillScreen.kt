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
import com.diarybook.data.local.entity.Category
import com.diarybook.ui.component.PrimaryButton
import com.diarybook.ui.component.WhiteCard
import com.diarybook.ui.theme.*
import com.diarybook.util.mapCategoryIcon
import com.diarybook.util.parseColorSafely
import com.diarybook.viewmodel.BillViewModel
import com.diarybook.viewmodel.CategoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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
    billViewModel: BillViewModel? = null,
    categoryViewModel: CategoryViewModel? = null
) {
    var isExpense by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var remark by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategorySelector by remember { mutableStateOf(false) }
    
    val defaultBook = billViewModel?.defaultBook?.collectAsState()?.value
    val defaultBookId = defaultBook?.id ?: 0
    
    // 当账本变化时，加载对应分类
    LaunchedEffect(defaultBookId) {
        if (defaultBookId > 0) {
            categoryViewModel?.loadCategories(defaultBookId)
        }
    }
    
    // 从 CategoryViewModel 获取分类数据
    val expenseCategories by categoryViewModel?.expenseCategories?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList<Category>()) }
    val incomeCategories by categoryViewModel?.incomeCategories?.collectAsState(initial = emptyList<Category>()) ?: remember { mutableStateOf(emptyList<Category>()) }
    
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
            onTypeChange = { 
                isExpense = it
                selectedCategory = null // 切换类型时重置分类选择
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AmountInputField(
            amount = amount,
            onAmountChange = { amount = it },
            isExpense = isExpense
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CategorySelector(
            selectedCategory = selectedCategory,
            onClick = { showCategorySelector = true }
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
        
        Spacer(modifier = Modifier.height(80.dp))
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
                    bookId = defaultBookId,
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
    
    // 分类选择器 BottomSheet
    if (showCategorySelector) {
        CategorySelectorBottomSheet(
            categories = categories,
            isExpense = isExpense,
            selectedCategoryId = selectedCategory?.id,
            onDismiss = { showCategorySelector = false },
            onCategorySelected = { category ->
                selectedCategory = category
                showCategorySelector = false
            }
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
    selectedCategory: Category?,
    onClick: () -> Unit
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
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedCategory != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(parseColorSafely(selectedCategory.color).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mapCategoryIcon(selectedCategory.icon),
                                fontSize = 22.sp
                            )
                        }
                        Text(
                            text = selectedCategory.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                } else {
                    Text(
                        text = "请选择分类",
                        fontSize = 16.sp,
                        color = TextTertiary
                    )
                }
                
                Text(
                    text = ">",
                    fontSize = 18.sp,
                    color = TextTertiary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelectorBottomSheet(
    categories: List<Category>,
    isExpense: Boolean,
    selectedCategoryId: Long?,
    onDismiss: () -> Unit,
    onCategorySelected: (Category) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (isExpense) "支出分类" else "收入分类",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // 网格布局显示分类
            val columns = 4
            categories.chunked(columns).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowItems.forEach { category ->
                        CategoryGridItem(
                            category = category,
                            isSelected = category.id == selectedCategoryId,
                            onClick = { onCategorySelected(category) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // 如果最后一行不足4个，用空白填充
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryGridItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isSelected) parseColorSafely(category.color) else LightPurple
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = mapCategoryIcon(category.icon),
                fontSize = 26.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.name,
            fontSize = 12.sp,
            color = if (isSelected) TextPrimary else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
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
