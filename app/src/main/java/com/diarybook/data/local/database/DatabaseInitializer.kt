package com.diarybook.data.local.database

import android.content.Context
import com.diarybook.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DatabaseInitializer(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val db = AppDatabase.getDatabase(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun initialize() {
        scope.launch {
            try {
                initDefaultUserSetting()
                val book = initDefaultBook()
                val categories = initDefaultCategories(book)
                initSampleBills(book, categories)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun initDefaultUserSetting() {
        val existing = db.userSettingDao().getUserSettingsSync()
        if (existing == null) {
            val now = dateFormat.format(Date())
            db.userSettingDao().insertUserSetting(
                UserSetting(
                    update_time = now
                )
            )
        }
    }

    private suspend fun initDefaultBook(): Book {
        var existing = db.bookDao().getDefaultBook()
        if (existing == null) {
            val now = dateFormat.format(Date())
            val bookId = db.bookDao().insertBook(
                Book(
                    name = "日常账本",
                    icon = "wallet",
                    color = "#6A5ACD",
                    is_default = 1,
                    sort = 0,
                    create_time = now
                )
            )
            existing = db.bookDao().getBookById(bookId)
        }
        return existing!!
    }

    private suspend fun initDefaultCategories(book: Book): List<Category> {
        val existingCategories = db.categoryDao().getAllCategoriesSync(0)
        if (existingCategories.isNotEmpty()) {
            return existingCategories
        }
        
        val now = dateFormat.format(Date())
        
        val expenseCategories = listOf(
            Category(book_id = book.id, type = 0, name = "餐饮", icon = "restaurant", color = "#FF5722", is_default = 1, sort = 1, create_time = now),
            Category(book_id = book.id, type = 0, name = "交通", icon = "directions_car", color = "#2196F3", is_default = 1, sort = 2, create_time = now),
            Category(book_id = book.id, type = 0, name = "购物", icon = "shopping_cart", color = "#9C27B0", is_default = 1, sort = 3, create_time = now),
            Category(book_id = book.id, type = 0, name = "娱乐", icon = "movie", color = "#FF9800", is_default = 1, sort = 4, create_time = now),
            Category(book_id = book.id, type = 0, name = "医疗", icon = "local_hospital", color = "#F44336", is_default = 1, sort = 5, create_time = now),
            Category(book_id = book.id, type = 0, name = "教育", icon = "school", color = "#4CAF50", is_default = 1, sort = 6, create_time = now),
            Category(book_id = book.id, type = 0, name = "住房", icon = "home", color = "#3F51B5", is_default = 1, sort = 7, create_time = now),
            Category(book_id = book.id, type = 0, name = "其他", icon = "more_horiz", color = "#607D8B", is_default = 1, sort = 8, create_time = now)
        )
        
        val incomeCategories = listOf(
            Category(book_id = book.id, type = 1, name = "工资", icon = "attach_money", color = "#4CAF50", is_default = 1, sort = 1, create_time = now),
            Category(book_id = book.id, type = 1, name = "奖金", icon = "card_giftcard", color = "#FF9800", is_default = 1, sort = 2, create_time = now),
            Category(book_id = book.id, type = 1, name = "投资", icon = "trending_up", color = "#2196F3", is_default = 1, sort = 3, create_time = now),
            Category(book_id = book.id, type = 1, name = "兼职", icon = "work", color = "#9C27B0", is_default = 1, sort = 4, create_time = now),
            Category(book_id = book.id, type = 1, name = "其他", icon = "more_horiz", color = "#607D8B", is_default = 1, sort = 5, create_time = now)
        )
        
        val allCategories = expenseCategories + incomeCategories
        
        val insertedCategories = mutableListOf<Category>()
        allCategories.forEach { category ->
            val id = db.categoryDao().insertCategory(category)
            insertedCategories.add(category.copy(id = id))
        }
        
        return insertedCategories
    }
    
    private suspend fun initSampleBills(book: Book, categories: List<Category>) {
        val existingBills = db.billDao().getAllBillsSync()
        if (existingBills.isNotEmpty()) return
        
        val now = System.currentTimeMillis().toString()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.let { 
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.time) 
        }
        
        val foodCategory = categories.find { it.name == "餐饮" && it.type == 0 }
        val transportCategory = categories.find { it.name == "交通" && it.type == 0 }
        val salaryCategory = categories.find { it.name == "工资" && it.type == 1 }
        
        val sampleBills = listOf(
            Bill(
                book_id = book.id,
                type = 0,
                category_id = transportCategory?.id ?: 0,
                category_name = "交通",
                category_icon = "🚗",
                amount = 10.0,
                date = today,
                remark = "地铁出行",
                create_time = now,
                update_time = now
            ),
            Bill(
                book_id = book.id,
                type = 0,
                category_id = foodCategory?.id ?: 0,
                category_name = "餐饮",
                category_icon = "🍽️",
                amount = 45.50,
                date = today,
                remark = "星巴克咖啡",
                create_time = now,
                update_time = now
            ),
            Bill(
                book_id = book.id,
                type = 1,
                category_id = salaryCategory?.id ?: 0,
                category_name = "工资",
                category_icon = "💰",
                amount = 15000.00,
                date = yesterday,
                remark = "4月份工资",
                create_time = now,
                update_time = now
            )
        )
        
        sampleBills.forEach { bill ->
            db.billDao().insertBill(bill)
        }
    }
}