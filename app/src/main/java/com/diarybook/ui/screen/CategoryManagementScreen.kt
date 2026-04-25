package com.diarybook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarybook.data.local.entity.Category
import com.diarybook.ui.component.WhiteCard
import com.diarybook.ui.theme.*
import com.diarybook.util.mapCategoryIcon
import com.diarybook.util.parseColorSafely
import com.diarybook.viewmodel.BookViewModel
import com.diarybook.viewmodel.CategoryViewModel

@Composable
fun CategoryManagementScreen(
    onBackClick: () -> Unit = {},
    categoryViewModel: CategoryViewModel,
    bookViewModel: BookViewModel? = null
) {
    val expenseCategories by categoryViewModel.expenseCategories.collectAsState()
    val incomeCategories by categoryViewModel.incomeCategories.collectAsState()
    val currentBook = bookViewModel?.currentBook?.collectAsState(initial = null)?.value
    
    var selectedTab by remember { mutableStateOf(0) } // 0: 支出, 1: 收入
    
    var showAddDialog by remember { mutableStateOf<Int?>(null) } // null: 隐藏, 0: 添加支出, 1: 添加收入
    var showEditDialog by remember { mutableStateOf<Category?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Category?>(null) }
    
    val currentCategories = if (selectedTab == 0) expenseCategories else incomeCategories
    
    // 当账本变化时，加载对应分类
    LaunchedEffect(currentBook?.id) {
        val bookId = currentBook?.id ?: 0
        if (bookId > 0) {
            categoryViewModel.loadCategories(bookId)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        CategoryManagementTopBar(onBackClick = onBackClick)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = PrimaryStart
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("支出") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("收入") }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(currentCategories) { category ->
                CategoryItemCard(
                    category = category,
                    onEdit = { showEditDialog = category },
                    onDelete = if (category.is_default == 1) null else ( { showDeleteDialog = category } )
                )
            }
        }
        
        AddCategoryButton(
            onClick = { showAddDialog = selectedTab },
            modifier = Modifier.padding(16.dp)
        )
    }
    
    showAddDialog?.let { type ->
        CategoryEditDialog(
            title = "添加分类",
            category = null,
            type = type,
            onDismiss = { showAddDialog = null },
            onSave = { name, icon, color ->
                categoryViewModel.addCategory(name, icon, color, type)
                showAddDialog = null
            }
        )
    }
    
    showEditDialog?.let { category ->
        CategoryEditDialog(
            title = "编辑分类",
            category = category,
            type = category.type,
            onDismiss = { showEditDialog = null },
            onSave = { name, icon, color ->
                val updatedCategory = category.copy(name = name, icon = icon, color = color)
                categoryViewModel.updateCategory(updatedCategory)
                showEditDialog = null
            }
        )
    }
    
    showDeleteDialog?.let { category ->
        DeleteCategoryDialog(
            category = category,
            onDismiss = { showDeleteDialog = null },
            onDelete = {
                categoryViewModel.deleteCategory(category)
                showDeleteDialog = null
            }
        )
    }
}

@Composable
private fun CategoryManagementTopBar(onBackClick: () -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp
    ) {
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
                text = "分类管理",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Spacer(modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun CategoryItemCard(
    category: Category,
    onEdit: () -> Unit,
    onDelete: (() -> Unit)? // 如果为 null，表示不可删除
) {
    WhiteCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(parseColorSafely(category.color)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = mapCategoryIcon(category.icon), fontSize = 24.sp)
                }
                Column {
                    Text(
                        text = category.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    if (category.is_default == 1) {
                        Text(
                            text = "默认分类",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onEdit
                        )
                )
                if (onDelete != null) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = ExpenseRed,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onDelete
                            )
                    )
                } else {
                    // 显示禁用的删除图标
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "不可删除",
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCategoryButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = PrimaryStart,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "添加",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "添加分类",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
fun CategoryEditDialog(
    title: String,
    category: Category?,
    type: Int,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var icon by remember { mutableStateOf(category?.icon ?: if (type == 0) "🍽️" else "💰") }
    var color by remember { mutableStateOf(category?.color ?: if (type == 0) "#FF6B6B" else "#4ECDC4" ) }
    
    val availableIcons = if (type == 0) {
        listOf("🍽️", "🚗", "🛒", "🎮", "🏥", "📚", "🏠", "📌")
    } else {
        listOf("💰", "🎁", "📈", "💼", "🏆", "🎯", "💵", "📌")
    }
    val availableColors = listOf("#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7", "#DDA0DD", "#98D8C8", "#6A5ACD")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("分类名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("选择图标", fontSize = 14.sp, color = TextSecondary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableIcons.forEach { availableIcon ->
                        Surface(
                            color = if (icon == availableIcon) LightPurple else Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { icon = availableIcon }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = availableIcon, fontSize = 20.sp)
                            }
                        }
                    }
                }
                
                Text("选择颜色", fontSize = 14.sp, color = TextSecondary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableColors.forEach { availableColor ->
                        Surface(
                            color = parseColorSafely(availableColor),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { color = availableColor }
                                .then(
                                    if (color == availableColor) {
                                        Modifier.border(
                                            width = 3.dp,
                                            color = TextPrimary,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                        ) {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                if (name.isNotEmpty()) {
                    onSave(name, icon, color)
                }
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun DeleteCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("删除分类") },
        text = {
            Column {
                Text("确定要删除分类\"${category.name}\"吗？此操作不可恢复。")
                if (category.is_default == 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "注意：这是默认分类，删除后可能会影响已有记录的显示。",
                        fontSize = 12.sp,
                        color = ExpenseRed
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text("删除", color = ExpenseRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
