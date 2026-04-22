package com.diarybook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun StatisticsScreen() {
    var isMonthly by remember { mutableStateOf(true) }
    var expense by remember { mutableStateOf(3240.80) }
    var income by remember { mutableStateOf(15821.30) }
    
    val categoryStats = remember {
        listOf(
            CategoryStatItem("餐饮", "🍽️", 1250.00, 38.6, ExpenseRed),
            CategoryStatItem("购物", "🛒", 890.50, 27.5, Color(0xFF9C27B0)),
            CategoryStatItem("交通", "🚗", 480.00, 14.8, Color(0xFF2196F3)),
            CategoryStatItem("娱乐", "🎬", 350.00, 10.8, Color(0xFFFF9800)),
            CategoryStatItem("其他", "📌", 270.30, 8.3, Color(0xFF607D8B))
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        StatisticsTopBar(
            isMonthly = isMonthly,
            onToggle = { isMonthly = it }
        )
        
        BalanceOverviewCard(
            expense = expense,
            income = income,
            balance = income - expense
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SectionTitle(
            text = "支出排行",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categoryStats, key = { it.name }) { stat ->
                CategoryStatCard(stat = stat)
            }
        }
    }
}

@Composable
private fun StatisticsTopBar(
    isMonthly: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TypeToggleItem(
            text = "月度",
            isSelected = isMonthly,
            onClick = { onToggle(true) }
        )
        Spacer(modifier = Modifier.width(16.dp))
        TypeToggleItem(
            text = "年度",
            isSelected = !isMonthly,
            onClick = { onToggle(false) }
        )
    }
}

@Composable
private fun TypeToggleItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) PrimaryStart else Color(0xFFF5F5F5))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

@Composable
private fun BalanceOverviewCard(
    expense: Double,
    income: Double,
    balance: Double
) {
    GradientCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "结余",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¥${String.format("%.2f", balance)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "支出",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AmountDisplay(
                        amount = expense,
                        isExpense = true,
                        fontSize = 18
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "收入",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AmountDisplay(
                        amount = income,
                        isExpense = false,
                        fontSize = 18
                    )
                }
            }
        }
    }
}

data class CategoryStatItem(
    val name: String,
    val icon: String,
    val amount: Double,
    val percentage: Double,
    val color: Color
)

@Composable
private fun CategoryStatCard(stat: CategoryStatItem) {
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
                        .background(stat.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stat.icon,
                        fontSize = 24.sp
                    )
                }
                Column {
                    Text(
                        text = stat.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${stat.percentage}%",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                AmountDisplay(
                    amount = stat.amount,
                    isExpense = true,
                    fontSize = 16
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFEEEEEE))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(stat.percentage.toFloat() / 100)
                            .clip(RoundedCornerShape(2.dp))
                            .background(stat.color)
                    )
                }
            }
        }
    }
}
