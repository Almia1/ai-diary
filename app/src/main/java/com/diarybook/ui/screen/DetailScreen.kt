package com.diarybook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.diarybook.data.local.entity.Bill
import com.diarybook.ui.component.BillItem
import com.diarybook.ui.component.BillItemCard
import com.diarybook.ui.component.SectionTitle
import com.diarybook.ui.theme.*
import com.diarybook.viewmodel.BillViewModel

@Composable
fun DetailScreen(
    onNavigateToAddBill: () -> Unit = {},
    billViewModel: BillViewModel
) {
    val bills by billViewModel.bills.collectAsState()
    val totalExpense by billViewModel.totalExpense.collectAsState()
    val totalIncome by billViewModel.totalIncome.collectAsState()
    
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
            DetailTopBar()
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
    }
}
