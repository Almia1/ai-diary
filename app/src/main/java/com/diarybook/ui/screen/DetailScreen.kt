package com.diarybook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarybook.data.local.entity.Bill
import com.diarybook.ui.component.BillItem
import com.diarybook.ui.component.BillItemCard
import com.diarybook.ui.component.SectionTitle
import com.diarybook.ui.theme.*
import com.diarybook.viewmodel.BillViewModel
import com.diarybook.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onNavigateToAddBill: () -> Unit = {},
    billViewModel: BillViewModel,
    bookViewModel: BookViewModel
) {
    val bills by billViewModel.bills.collectAsState()
    val totalExpense by billViewModel.totalExpense.collectAsState()
    val totalIncome by billViewModel.totalIncome.collectAsState()
    val currentBook by bookViewModel.currentBook.collectAsState()
    val books by bookViewModel.books.collectAsState()
    var showBookSelector by remember { mutableStateOf(false) }
    
    // 当账本切换时，同步刷新账单列表
    LaunchedEffect(currentBook?.id) {
        currentBook?.let { book ->
            billViewModel.syncWithBook(book.id)
        }
    }
    
    val billItems = remember(bills) {
        bills.map { bill ->
            BillItem(
                id = bill.id,
                category = bill.category_name,
                categoryIcon = bill.category_icon,
                amount = bill.amount,
                isExpense = bill.type == 0,
                date = bill.date,
                remark = bill.remark
            )
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            DetailTopBar(
                currentBookName = currentBook?.name ?: "日常账本",
                onBookClick = { showBookSelector = true },
                onCameraClick = onNavigateToOcrRecognition
            )
            BalanceOverviewCard(
                balance = totalIncome - totalExpense,
                expense = totalExpense,
                income = totalIncome
            )
            QuickActionsRow()
            SectionTitle(
                text = "最近账单",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(billItems, key = { it.id }) { bill ->
                    BillItemCard(bill = bill)
                }
            }
        }
        
        FloatingActionButton(
            onClick = onNavigateToAddBill,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(56.dp),
            containerColor = Color.Transparent,
            contentColor = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryStart, PrimaryEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "记一笔",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        if (showBookSelector) {
            BookSelectorBottomSheet(
                books = books,
                currentBookId = currentBook?.id,
                onDismiss = { showBookSelector = false },
                onBookSelected = { book ->
                    bookViewModel.setCurrentBook(book)
                    showBookSelector = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookSelectorBottomSheet(
    books: List<com.diarybook.data.local.entity.Book>,
    currentBookId: Long?,
    onDismiss: () -> Unit,
    onBookSelected: (com.diarybook.data.local.entity.Book) -> Unit
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
                text = "选择账本",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            books.forEach { book ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBookSelected(book) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = book.icon ?: "📖",
                            fontSize = 24.sp
                        )
                        Text(
                            text = book.name,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    }
                    
                    if (book.id == currentBookId) {
                        Text(
                            text = "✓",
                            fontSize = 18.sp,
                            color = PrimaryStart,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
