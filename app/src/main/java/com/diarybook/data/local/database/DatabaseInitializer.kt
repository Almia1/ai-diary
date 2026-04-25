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
        val now = dateFormat.format(Date())
        
        val defaultExpenseCategories = listOf(
            Category(book_id = book.id, type = 0, name = "餐饮", icon = "🍽️", color = "#FF5722", is_default = 1, sort = 1, create_time = now),
            Category(book_id = book.id, type = 0, name = "交通", icon = "🚗", color = "#2196F3", is_default = 1, sort = 2, create_time = now),
            Category(book_id = book.id, type = 0, name = "购物", icon = "🛒", color = "#9C27B0", is_default = 1, sort = 3, create_time = now),
            Category(book_id = book.id, type = 0, name = "娱乐", icon = "🎬", color = "#FF9800", is_default = 1, sort = 4, create_time = now),
            Category(book_id = book.id, type = 0, name = "医疗", icon = "🏥", color = "#F44336", is_default = 1, sort = 5, create_time = now),
            Category(book_id = book.id, type = 0, name = "教育", icon = "📚", color = "#4CAF50", is_default = 1, sort = 6, create_time = now),
            Category(book_id = book.id, type = 0, name = "住房", icon = "🏠", color = "#3F51B5", is_default = 1, sort = 7, create_time = now),
            Category(book_id = book.id, type = 0, name = "其他", icon = "📌", color = "#607D8B", is_default = 1, sort = 8, create_time = now)
        )
        
        val defaultIncomeCategories = listOf(
            Category(book_id = book.id, type = 1, name = "工资", icon = "💰", color = "#4CAF50", is_default = 1, sort = 1, create_time = now),
            Category(book_id = book.id, type = 1, name = "奖金", icon = "🎁", color = "#FF9800", is_default = 1, sort = 2, create_time = now),
            Category(book_id = book.id, type = 1, name = "投资", icon = "📈", color = "#2196F3", is_default = 1, sort = 3, create_time = now),
            Category(book_id = book.id, type = 1, name = "兼职", icon = "💼", color = "#9C27B0", is_default = 1, sort = 4, create_time = now),
            Category(book_id = book.id, type = 1, name = "其他", icon = "📌", color = "#607D8B", is_default = 1, sort = 5, create_time = now)
        )
        
        val allDefaultCategories = defaultExpenseCategories + defaultIncomeCategories
        
        // 检查并清理重复分类，保留最早创建的记录
        cleanupDuplicateCategories(book.id)
        
        // 获取现有的分类列表
        val existingCategories = db.categoryDao().getAllCategoriesSync(book.id)
        
        // 仅插入不存在的默认分类
        val insertedCategories = mutableListOf<Category>()
        allDefaultCategories.forEach { defaultCategory ->
            // 检查是否已存在相同的分类（同一账本、同类型、同名）
            val existing = existingCategories.find { 
                it.book_id == defaultCategory.book_id && 
                it.type == defaultCategory.type && 
                it.name == defaultCategory.name 
            }
            
            if (existing != null) {
                // 已存在，使用现有分类
                insertedCategories.add(existing)
            } else {
                // 不存在，插入新分类
                val id = db.categoryDao().insertCategory(defaultCategory)
                insertedCategories.add(defaultCategory.copy(id = id))
            }
        }
        
        return insertedCategories
    }
    
    /**
     * 清理重复的分类记录
     * 对于同一 book_id 和 type 下具有相同 name 的分类，只保留 id 最小（最早创建）的那一条
     */
    private suspend fun cleanupDuplicateCategories(bookId: Long) {
        // 查询所有分类
        val allCategories = db.categoryDao().getAllCategoriesSync(bookId)
        
        // 按 (book_id, type, name) 分组
        val groupedCategories = allCategories.groupBy { 
            Triple(it.book_id, it.type, it.name) 
        }
        
        // 找出每个组中的重复项（保留 id 最小的）
        val categoriesToDelete = mutableListOf<Category>()
        groupedCategories.forEach { (_, categories) ->
            if (categories.size > 1) {
                // 按 id 排序，保留第一个（最小的），删除其余的
                val sorted = categories.sortedBy { it.id }
                categoriesToDelete.addAll(sorted.drop(1))
            }
        }
        
        // 删除重复分类（注意：需要先将关联的账单指向保留的分类）
        categoriesToDelete.forEach { duplicateCategory ->
            // 找到该分组的原始分类（id 最小的那个）
            val originalCategory = allCategories.find { 
                it.book_id == duplicateCategory.book_id && 
                it.type == duplicateCategory.type && 
                it.name == duplicateCategory.name && 
                it.id < duplicateCategory.id 
            }
            
            if (originalCategory != null) {
                // 将引用此分类的账单更新为指向原始分类
                updateBillsCategoryReference(duplicateCategory.id, originalCategory.id)
            }
            
            // 删除重复的分类
            db.categoryDao().deleteCategory(duplicateCategory)
        }
    }
    
    /**
     * 更新账单的分类引用
     * 将所有引用旧分类 ID 的账单更新为引用新分类 ID
     */
    private suspend fun updateBillsCategoryReference(oldCategoryId: Long, newCategoryId: Long) {
        val bills = db.billDao().getAllBillsSync()
        bills.filter { it.category_id == oldCategoryId }.forEach { bill ->
            val updatedBill = bill.copy(category_id = newCategoryId)
            db.billDao().updateBill(updatedBill)
        }
    }
    
    /**
     * 初始化示例账单数据
     * @param book 所属账本对象
     * @param categories 分类列表，用于获取分类ID
     */
    private suspend fun initSampleBills(book: Book, categories: List<Category>) {
        // 检查是否已有账单数据，如果有则不初始化示例数据
        val existingBills = db.billDao().getAllBillsSync()
        if (existingBills.isNotEmpty()) return
        
        // 准备时间相关数据
        val now = System.currentTimeMillis().toString() // 当前时间戳，用于创建和更新时间
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) // 今天日期
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.let { 
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.time) 
        } // 昨天日期
        
        // 从分类列表中查找所需的分类
        val foodCategory = categories.find { it.name == "餐饮" && it.type == 0 } // 餐饮支出分类
        val transportCategory = categories.find { it.name == "交通" && it.type == 0 } // 交通支出分类
        val salaryCategory = categories.find { it.name == "工资" && it.type == 1 } // 工资收入分类
        
        // 创建示例账单列表
        val sampleBills = listOf(
            Bill(
                book_id = book.id,
                type = 0, // 支出类型
                category_id = transportCategory?.id ?: 0,
                category_name = "交通",
                category_icon = "🚗",
                amount = 10.0, // 金额：10元
                date = today,
                remark = "地铁出行", // 备注信息
                create_time = now,
                update_time = now
            ),
            Bill(
                book_id = book.id,
                type = 0, // 支出类型
                category_id = foodCategory?.id ?: 0,
                category_name = "餐饮",
                category_icon = "🍽️",
                amount = 45.50, // 金额：45.50元
                date = today,
                remark = "星巴克咖啡", // 备注信息
                create_time = now,
                update_time = now
            ),
            Bill(
                book_id = book.id,
                type = 1, // 收入类型
                category_id = salaryCategory?.id ?: 0,
                category_name = "工资",
                category_icon = "💰",
                amount = 15000.00, // 金额：15000元
                date = yesterday,
                remark = "4月份工资", // 备注信息
                create_time = now,
                update_time = now
            )
        )
        
        // 将示例账单插入数据库
        sampleBills.forEach { bill ->
            db.billDao().insertBill(bill)
        }
    }
}