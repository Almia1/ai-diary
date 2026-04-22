package com.diarybook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diarybook.ui.component.AmountDisplay
import com.diarybook.ui.component.BillItem
import com.diarybook.ui.component.BillItemCard
import com.diarybook.ui.component.SectionTitle
import com.diarybook.ui.component.WhiteCard
import com.diarybook.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen() {
    val currentDate = remember { mutableStateOf(Date()) }
    val selectedDate = remember { mutableStateOf(Date()) }
    val calendar = Calendar.getInstance()
    calendar.time = currentDate.value
    
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_WEEK)
    
    val dateFormat = SimpleDateFormat("yyyy年MM月", Locale.getDefault())
    val dayFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
    
    val sampleBills = remember {
        listOf(
            BillItem(1, "餐饮", "🍽️", 45.50, true, "今天", "星巴克咖啡"),
            BillItem(2, "交通", "🚗", 12.00, true, "今天", "地铁出行"),
            BillItem(3, "工资", "💰", 15000.00, false, "昨天", "4月份工资")
        )
    }
    
    var expense by remember { mutableStateOf(3240.80) }
    var income by remember { mutableStateOf(15821.30) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        CalendarTopBar(
            currentMonth = dateFormat.format(currentDate.value),
            onPreviousMonth = {
                calendar.time = currentDate.value
                calendar.add(Calendar.MONTH, -1)
                currentDate.value = calendar.time
            },
            onNextMonth = {
                calendar.time = currentDate.value
                calendar.add(Calendar.MONTH, 1)
                currentDate.value = calendar.time
            }
        )
        
        CalendarGridView(
            year = year,
            month = month,
            daysInMonth = daysInMonth,
            firstDayOfMonth = firstDayOfMonth,
            selectedDate = selectedDate.value,
            onDateSelected = { selectedDate.value = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DateStatsBar(
            date = dayFormat.format(selectedDate.value),
            expense = expense,
            income = income
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SectionTitle(
            text = "当日账单",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sampleBills, key = { it.id }) { bill ->
                BillItemCard(bill = bill)
            }
        }
    }
}

@Composable
private fun CalendarTopBar(
    currentMonth: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "上月",
            tint = TextPrimary,
            modifier = Modifier
                .size(28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onPreviousMonth
                )
        )
        
        Text(
            text = currentMonth,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "下月",
            tint = TextPrimary,
            modifier = Modifier
                .size(28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onNextMonth
                )
        )
    }
}

@Composable
private fun CalendarGridView(
    year: Int,
    month: Int,
    daysInMonth: Int,
    firstDayOfMonth: Int,
    selectedDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val weekDays = listOf("日", "一", "二", "三", "四", "五", "六")
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    val today = Calendar.getInstance()
    
    WhiteCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextTertiary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            
            val days = mutableListOf<Int?>()
            repeat(firstDayOfMonth - 1) { days.add(null) }
            (1..daysInMonth).forEach { days.add(it) }
            
            days.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    week.forEach { day ->
                        CalendarDayItem(
                            day = day,
                            isToday = day != null && 
                                calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                day == today.get(Calendar.DAY_OF_MONTH),
                            isSelected = day != null && 
                                calendar.get(Calendar.YEAR) == (selectedDate.let { 
                                    val c = Calendar.getInstance()
                                    c.time = it
                                    c.get(Calendar.YEAR)
                                }) &&
                                calendar.get(Calendar.MONTH) == (selectedDate.let { 
                                    val c = Calendar.getInstance()
                                    c.time = it
                                    c.get(Calendar.MONTH)
                                }) &&
                                day == (selectedDate.let { 
                                    val c = Calendar.getInstance()
                                    c.time = it
                                    c.get(Calendar.DAY_OF_MONTH)
                                }),
                            hasBill = day != null && day % 3 == 0,
                            onClick = {
                                if (day != null) {
                                    calendar.set(Calendar.DAY_OF_MONTH, day)
                                    onDateSelected(calendar.time)
                                }
                            }
                        )
                        if (day != null) {
                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayItem(
    day: Int?,
    isToday: Boolean,
    isSelected: Boolean,
    hasBill: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .then(
                if (day != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .then(
                        if (isSelected) {
                            Modifier.background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(PrimaryStart, PrimaryEnd)
                                )
                            )
                        } else if (isToday) {
                            Modifier.background(LightPurple)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 14.sp,
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else TextPrimary
                )
            }
            if (hasBill && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(ExpenseRed)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun DateStatsBar(
    date: String,
    expense: Double,
    income: Double
) {
    WhiteCard(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "支出",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                    AmountDisplay(
                        amount = expense,
                        isExpense = true,
                        fontSize = 16
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "收入",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                    AmountDisplay(
                        amount = income,
                        isExpense = false,
                        fontSize = 16
                    )
                }
            }
        }
    }
}
