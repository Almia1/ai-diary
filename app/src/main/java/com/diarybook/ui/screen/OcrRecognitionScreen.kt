package com.diarybook.ui.screen

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.diarybook.data.local.entity.Category
import com.diarybook.data.model.BillRecognitionResult
import com.diarybook.ui.component.PrimaryButton
import com.diarybook.ui.component.WhiteCard
import com.diarybook.ui.theme.*
import com.diarybook.util.BillOcrRecognizer
import com.diarybook.util.mapCategoryIcon
import com.diarybook.util.parseColorSafely
import com.diarybook.viewmodel.BillViewModel
import com.diarybook.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrRecognitionScreen(
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    billViewModel: BillViewModel? = null,
    categoryViewModel: CategoryViewModel? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var recognitionResult by remember { mutableStateOf<BillRecognitionResult?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var isExpense by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var remark by remember { mutableStateOf("") }
    var showCategorySelector by remember { mutableStateOf(false) }
    
    val defaultBook = billViewModel?.defaultBook?.collectAsState()?.value
    val defaultBookId = defaultBook?.id ?: 0
    
    LaunchedEffect(defaultBookId) {
        if (defaultBookId > 0) {
            categoryViewModel?.loadCategories(defaultBookId)
        }
    }
    
    val expenseCategories by categoryViewModel?.expenseCategories?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList<Category>()) }
    val incomeCategories by categoryViewModel?.incomeCategories?.collectAsState(initial = emptyList<Category>()) ?: remember { mutableStateOf(emptyList<Category>()) }
    val categories = if (isExpense) expenseCategories else incomeCategories
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            selectedBitmap = loadBitmapFromUri(context, it)
            scope.launch {
                processImage(it, context) { result ->
                    recognitionResult = result
                    result.amount?.let { amt -> amount = String.format("%.2f", amt) }
                    result.date?.let { date -> selectedDate = date }
                    result.merchant?.let { mcht -> remark = mcht }
                    result.type.let { type -> isExpense = type == 0 }
                    findMatchingCategory(result, categories)?.let { cat -> selectedCategory = cat }
                }
            }
        }
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            selectedBitmap = it
            isProcessing = true
            scope.launch {
                try {
                    val result = BillOcrRecognizer.recognizeFromBitmap(it)
                    recognitionResult = result
                    result.amount?.let { amt -> amount = String.format("%.2f", amt) }
                    result.date?.let { date -> selectedDate = date }
                    result.merchant?.let { mcht -> remark = mcht }
                    result.type.let { type -> isExpense = type == 0 }
                    findMatchingCategory(result, categories)?.let { cat -> selectedCategory = cat }
                } catch (e: Exception) {
                    errorMessage = "识别失败: ${e.message}"
                } finally {
                    isProcessing = false
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        OcrTopBar(onBackClick = onBackClick)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ImageSourceSelector(
            selectedBitmap = selectedBitmap,
            onSelectFromGallery = { imagePickerLauncher.launch("image/*") },
            onTakePhoto = { cameraLauncher.launch(null) }
        )
        
        if (isProcessing) {
            Spacer(modifier = Modifier.height(16.dp))
            ProcessingIndicator()
        }
        
        if (recognitionResult != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
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
                selectedCategory = selectedCategory,
                onClick = { showCategorySelector = true }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DateSelector(
                date = selectedDate,
                onClick = { }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            RemarkInputField(
                remark = remark,
                onRemarkChange = { remark = it }
            )
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
    
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
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        PrimaryButton(
            text = "保存",
            onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                val categoryIdValue = selectedCategory?.id ?: 0
                val categoryNameValue = selectedCategory?.name ?: recognitionResult?.suggestedCategoryName ?: ""
                val categoryIconValue = selectedCategory?.icon ?: recognitionResult?.suggestedCategoryIcon ?: "📌"
                
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
                
                onSaveSuccess()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = amount.isNotEmpty()
        )
    }
    
    errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("识别失败") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text("确定")
                }
            }
        )
    }
}

private suspend fun processImage(uri: Uri, context: Context, onResult: (BillRecognitionResult) -> Unit) {
    try {
        val result = BillOcrRecognizer.recognizeFromUri(uri, context)
        onResult(result)
    } catch (e: Exception) {
        onResult(BillRecognitionResult(rawText = "识别失败: ${e.message}"))
    }
}

private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        null
    }
}

private fun findMatchingCategory(result: BillRecognitionResult, categories: List<Category>): Category? {
    val categoryName = result.suggestedCategoryName ?: return null
    return categories.find { it.name == categoryName }
        ?: categories.find { it.name.contains(categoryName) || categoryName.contains(it.name) }
}

@Composable
private fun OcrTopBar(onBackClick: () -> Unit) {
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
            text = "识图记账",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Spacer(modifier = Modifier.size(28.dp))
    }
}

@Composable
private fun ImageSourceSelector(
    selectedBitmap: Bitmap?,
    onSelectFromGallery: () -> Unit,
    onTakePhoto: () -> Unit
) {
    WhiteCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedBitmap != null) {
                Image(
                    bitmap = selectedBitmap.asImageBitmap(),
                    contentDescription = "选中的图片",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(LightPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = PrimaryStart,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "选择账单图片",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = onSelectFromGallery,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("相册")
                }
                
                OutlinedButton(
                    onClick = onTakePhoto,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("拍照")
                }
            }
        }
    }
}

@Composable
private fun ProcessingIndicator() {
    WhiteCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = PrimaryStart,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "正在识别账单...",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun TypeSwitcher(
    isExpense: Boolean,
    onTypeChange: (Boolean) -> Unit
) {
    WhiteCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
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
    WhiteCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "金额",
                fontSize = 14.sp,
                color = TextTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¥",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) ExpenseRed else IncomeGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
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
                        Box(contentAlignment = Alignment.CenterStart) {
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
    WhiteCard(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "分类",
                fontSize = 14.sp,
                color = TextTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                            Text(text = mapCategoryIcon(selectedCategory.icon), fontSize = 22.sp)
                        }
                        Text(
                            text = selectedCategory.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                } else {
                    Text(text = "请选择分类", fontSize = 16.sp, color = TextTertiary)
                }
                Text(text = ">", fontSize = 18.sp, color = TextTertiary)
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
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
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
            Text(text = mapCategoryIcon(category.icon), fontSize = 26.sp)
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
private fun DateSelector(date: String, onClick: () -> Unit) {
    WhiteCard(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "日期", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(text = date, fontSize = 16.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun RemarkInputField(remark: String, onRemarkChange: (String) -> Unit) {
    WhiteCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "备注", fontSize = 14.sp, color = TextTertiary)
            Spacer(modifier = Modifier.height(8.dp))
            BasicTextField(
                value = remark,
                onValueChange = onRemarkChange,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, color = TextPrimary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (remark.isEmpty()) {
                            Text(text = "添加备注...", fontSize = 16.sp, color = TextTertiary)
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}
