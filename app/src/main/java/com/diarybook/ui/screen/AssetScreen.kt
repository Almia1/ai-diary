package com.diarybook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
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
import com.diarybook.ui.component.AmountDisplay
import com.diarybook.ui.component.GradientCard
import com.diarybook.ui.component.SectionTitle
import com.diarybook.ui.component.WhiteCard
import com.diarybook.ui.theme.*

@Composable
fun AssetScreen() {
    var totalAsset by remember { mutableStateOf(125800.50) }
    var totalDebt by remember { mutableStateOf(5000.00) }
    
    val debts = remember {
        listOf(
            DebtItem("信用卡", "💳", 3000.00, "2026-04-25", ExpenseRed),
            DebtItem("朋友借款", "👤", 2000.00, "2026-05-01", Color(0xFFFF9800))
        )
    }
    
    val budgets = remember {
        listOf(
            BudgetItem("餐饮", "🍽️", 2000.00, 1250.00, ExpenseRed),
            BudgetItem("购物", "🛒", 1500.00, 890.50, Color(0xFF9C27B0)),
            BudgetItem("交通", "🚗", 800.00, 480.00, Color(0xFF2196F3))
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        AssetOverviewCard(
            totalAsset = totalAsset,
            totalDebt = totalDebt,
            netAsset = totalAsset - totalDebt
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SectionTitle(
            text = "债务列表",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(debts, key = { it.name }) { debt ->
                DebtItemCard(debt = debt)
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            item {
                SectionTitle(
                    text = "预算进度",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(budgets, key = { it.name }) { budget ->
                BudgetItemCard(budget = budget)
            }
        }
    }
}

@Composable
private fun AssetOverviewCard(
    totalAsset: Double,
    totalDebt: Double,
    netAsset: Double
) {
    GradientCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "总资产",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "¥${String.format("%.2f", totalAsset)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "负债",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    AmountDisplay(
                        amount = totalDebt,
                        isExpense = true,
                        fontSize = 18
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "净资产",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    AmountDisplay(
                        amount = netAsset,
                        isExpense = false,
                        fontSize = 18
                    )
                }
            }
        }
    }
}

data class DebtItem(
    val name: String,
    val icon: String,
    val amount: Double,
    val dueDate: String,
    val color: Color
)

@Composable
private fun DebtItemCard(debt: DebtItem) {
    WhiteCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        .background(debt.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = debt.icon,
                        fontSize = 24.sp
                    )
                }
                Column {
                    Text(
                        text = debt.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = "到期: ${debt.dueDate}",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AmountDisplay(
                    amount = debt.amount,
                    isExpense = true,
                    fontSize = 16
                )
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

data class BudgetItem(
    val name: String,
    val icon: String,
    val budget: Double,
    val spent: Double,
    val color: Color
)

@Composable
private fun BudgetItemCard(budget: BudgetItem) {
    val percentage = (budget.spent / budget.budget * 100).coerceAtMost(100.0)
    
    WhiteCard {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .background(budget.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = budget.icon,
                            fontSize = 24.sp
                        )
                    }
                    Column {
                        Text(
                            text = budget.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "¥${String.format("%.2f", budget.spent)} / ¥${String.format("%.2f", budget.budget)}",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                }
                
                Text(
                    text = "${String.format("%.1f", percentage)}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = budget.color
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFEEEEEE))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(percentage.toFloat() / 100)
                        .clip(RoundedCornerShape(4.dp))
                        .background(budget.color)
                )
            }
        }
    }
}
