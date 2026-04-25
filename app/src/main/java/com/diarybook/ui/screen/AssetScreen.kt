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
import com.diarybook.data.local.entity.Budget
import com.diarybook.data.local.entity.Debt
import com.diarybook.ui.component.AmountDisplay
import com.diarybook.ui.component.GradientCard
import com.diarybook.ui.component.SectionTitle
import com.diarybook.ui.component.WhiteCard
import com.diarybook.ui.theme.*
import com.diarybook.viewmodel.BudgetViewModel
import com.diarybook.viewmodel.DebtViewModel
import java.util.Calendar

@Composable
fun AssetScreen(
    budgetViewModel: BudgetViewModel? = null,
    debtViewModel: DebtViewModel? = null
) {
    // 从 ViewModel 获取真实数据
    val debts by debtViewModel?.debts?.collectAsState(initial = emptyList<Debt>()) ?: remember { mutableStateOf(emptyList<Debt>()) }
    val budgets by budgetViewModel?.budgets?.collectAsState(initial = emptyList<Budget>()) ?: remember { mutableStateOf(emptyList<Budget>()) }
    
    // 计算总资产和总负债
    val totalDebt = debts.sumOf { it.amount }
    val totalBudget = budgets.sumOf { it.amount }
    val totalSpent = budgets.sumOf { it.used_amount ?: 0.0 }
    val totalAsset = totalBudget - totalSpent // 简化计算：预算剩余作为资产
    
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
            if (debts.isEmpty()) {
                item {
                    EmptyStateItem("暂无债务记录")
                }
            } else {
                items(debts, key = { it.id }) { debt ->
                    DebtItemCard(debt = debt)
                }
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
            
            if (budgets.isEmpty()) {
                item {
                    EmptyStateItem("暂无预算记录")
                }
            } else {
                items(budgets, key = { it.id }) { budget ->
                    BudgetItemCard(budget = budget)
                }
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

@Composable
private fun DebtItemCard(debt: Debt) {
    val color = if (debt.type == 0) ExpenseRed else IncomeGreen
    val icon = if (debt.type == 0) "💳" else "👤"
    val statusText = if (debt.status == 0) "未还清" else "已还清"
    
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
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "到期: ${debt.due_date}",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            color = if (debt.status == 0) ExpenseRed else IncomeGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    AmountDisplay(
                        amount = debt.amount,
                        isExpense = true,
                        fontSize = 16
                    )
                    if (debt.paid_amount != null && debt.paid_amount > 0) {
                        Text(
                            text = "已还: ¥${String.format("%.2f", debt.paid_amount)}",
                            fontSize = 11.sp,
                            color = TextTertiary
                        )
                    }
                }
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

@Composable
private fun BudgetItemCard(budget: Budget) {
    val percentage = if (budget.amount > 0) {
        ((budget.used_amount ?: 0.0) / budget.amount * 100).coerceAtMost(100.0)
    } else {
        0.0
    }
    val color = if (budget.type == 0) ExpenseRed else IncomeGreen
    val typeName = if (budget.type == 0) "支出" else "收入"
    
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
                            .background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (budget.type == 0) "💰" else "📈",
                            fontSize = 24.sp
                        )
                    }
                    Column {
                        Text(
                            text = "${typeName}预算",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "¥${String.format("%.2f", budget.used_amount ?: 0.0)} / ¥${String.format("%.2f", budget.amount)}",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                }
                
                Text(
                    text = "${String.format("%.1f", percentage)}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
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
                        .background(color)
                )
            }
        }
    }
}

@Composable
private fun EmptyStateItem(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = TextTertiary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
