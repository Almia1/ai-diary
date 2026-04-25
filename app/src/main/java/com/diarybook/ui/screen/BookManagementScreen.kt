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
import com.diarybook.data.local.entity.Book
import com.diarybook.ui.component.WhiteCard
import com.diarybook.ui.theme.*
import com.diarybook.viewmodel.BookViewModel

@Composable
fun BookManagementScreen(
    onBackClick: () -> Unit = {},
    bookViewModel: BookViewModel
) {
    val books by bookViewModel.books.collectAsState()
    val defaultBook by bookViewModel.defaultBook.collectAsState()
    val currentBook by bookViewModel.currentBook.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Book?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Book?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        BookManagementTopBar(onBackClick = onBackClick)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(books) { book ->
                BookItemCard(
                    book = book,
                    isCurrentBook = book.id == currentBook?.id,
                    isDefaultBook = book.is_default == 1,
                    onEdit = { showEditDialog = book },
                    onDelete = { showDeleteDialog = book },
                    onSetAsCurrent = { bookViewModel.setCurrentBook(book) },
                    onSetAsDefault = { bookViewModel.setDefaultBook(book) }
                )
            }
        }
        
        AddBookButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.padding(16.dp)
        )
    }
    
    if (showAddDialog) {
        BookEditDialog(
            title = "添加账本",
            book = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, icon, color ->
                bookViewModel.addBook(name, icon, color)
                showAddDialog = false
            }
        )
    }
    
    showEditDialog?.let { book ->
        BookEditDialog(
            title = "编辑账本",
            book = book,
            onDismiss = { showEditDialog = null },
            onSave = { name, icon, color ->
                val updatedBook = book.copy(name = name, icon = icon, color = color)
                bookViewModel.updateBook(updatedBook)
                showEditDialog = null
            }
        )
    }
    
    showDeleteDialog?.let { book ->
        DeleteBookDialog(
            book = book,
            onDismiss = { showDeleteDialog = null },
            onDelete = {
                bookViewModel.deleteBook(book)
                showDeleteDialog = null
            }
        )
    }
}

@Composable
private fun BookManagementTopBar(onBackClick: () -> Unit) {
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
                text = "账本管理",
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
private fun BookItemCard(
    book: Book,
    isCurrentBook: Boolean,
    isDefaultBook: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetAsCurrent: () -> Unit,
    onSetAsDefault: () -> Unit
) {
    WhiteCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSetAsCurrent
                )
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
                        .background(Color(android.graphics.Color.parseColor(book.color))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = book.icon ?: "📒", fontSize = 24.sp)
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = book.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        if (isDefaultBook) {
                            Surface(
                                color = PrimaryStart.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "默认",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    color = PrimaryStart
                                )
                            }
                        }
                        if (isCurrentBook) {
                            Surface(
                                color = IncomeGreen.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "当前",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    color = IncomeGreen
                                )
                            }
                        }
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
            }
        }
    }
}

@Composable
private fun AddBookButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
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
                text = "添加账本",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
fun BookEditDialog(
    title: String,
    book: Book?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(book?.name ?: "") }
    var icon by remember { mutableStateOf(book?.icon ?: "📒") }
    var color by remember { mutableStateOf(book?.color ?: "#6A5ACD") }
    
    val availableIcons = listOf("📒", "💰", "🏠", "💼", "🎮", "✈️", "📚", "🍽️")
    val availableColors = listOf("#6A5ACD", "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7", "#DDA0DD", "#98D8C8")
    
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
                    label = { Text("账本名称") },
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
                            color = Color(android.graphics.Color.parseColor(availableColor)),
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
fun DeleteBookDialog(
    book: Book,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("删除账本") },
        text = {
            Text("确定要删除账本\"${book.name}\"吗？此操作不可恢复。")
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
