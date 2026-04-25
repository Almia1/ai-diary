package com.diarybook.data.utils

import com.diarybook.data.local.dao.BillDao
import com.diarybook.data.local.dao.CategoryDao
import com.diarybook.data.local.entity.Category

/**
 * 分类数据清理工具类
 * 用于安全地清理数据库中的重复分类记录
 */
object CategoryCleanupUtil {
    
    /**
     * 清理指定账本中的重复分类
     * 
     * @param categoryDao 分类 DAO
     * @param billDao 账单 DAO
     * @param bookId 账本 ID，如果为 null 则清理所有账本
     * @return 清理的重复分类数量
     */
    suspend fun cleanupDuplicateCategories(
        categoryDao: CategoryDao,
        billDao: BillDao,
        bookId: Long? = null
    ): Int {
        // 获取需要清理的分类列表
        val allCategories = if (bookId != null) {
            categoryDao.getAllCategoriesSync(bookId)
        } else {
            // 获取所有分类
            categoryDao.getAllCategories()
        }
        
        // 按 (book_id, type, name) 分组
        val groupedCategories = allCategories.groupBy { 
            Triple(it.book_id, it.type, it.name) 
        }
        
        var deletedCount = 0
        
        // 处理每个分组
        groupedCategories.forEach { (key, categories) ->
            if (categories.size > 1) {
                // 按 id 排序，保留第一个（最小的），删除其余的
                val sorted = categories.sortedBy { it.id }
                val keepCategory = sorted.first()
                val duplicates = sorted.drop(1)
                
                // 对于每个重复分类
                duplicates.forEach { duplicateCategory ->
                    // 1. 将引用此分类的账单更新为指向保留的分类
                    updateBillsCategoryReference(billDao, duplicateCategory.id, keepCategory.id)
                    
                    // 2. 删除重复的分类
                    categoryDao.deleteCategory(duplicateCategory)
                    deletedCount++
                }
            }
        }
        
        return deletedCount
    }
    
    /**
     * 检查并修复孤立账单（category_id 指向不存在的分类）
     * 
     * @param categoryDao 分类 DAO
     * @param billDao 账单 DAO
     * @return 修复的账单数量
     */
    suspend fun fixOrphanedBills(
        categoryDao: CategoryDao,
        billDao: BillDao
    ): Int {
        val bills = billDao.getAllBillsSync()
        var fixedCount = 0
        
        bills.forEach { bill ->
            if (bill.category_id != 0L) {
                val category = categoryDao.getCategoryById(bill.category_id)
                if (category == null) {
                    // 分类不存在，重置为默认值
                    val updatedBill = bill.copy(category_id = 0)
                    billDao.updateBill(updatedBill)
                    fixedCount++
                }
            }
        }
        
        return fixedCount
    }
    
    /**
     * 验证数据库中是否存在重复分类
     * 
     * @param categoryDao 分类 DAO
     * @param bookId 账本 ID
     * @return 重复分类组的数量
     */
    suspend fun checkDuplicates(
        categoryDao: CategoryDao,
        bookId: Long
    ): Map<Triple<Long, Int, String>, List<Category>> {
        val allCategories = categoryDao.getAllCategoriesSync(bookId)
        
        val groupedCategories = allCategories.groupBy { 
            Triple(it.book_id, it.type, it.name) 
        }
        
        return groupedCategories.filter { it.value.size > 1 }
    }
    
    /**
     * 打印重复分类的详细信息（用于调试）
     */
    suspend fun printDuplicateInfo(
        categoryDao: CategoryDao,
        bookId: Long
    ) {
        val duplicates = checkDuplicates(categoryDao, bookId)
        
        if (duplicates.isEmpty()) {
            println("✓ 未发现重复分类")
            return
        }
        
        println("⚠ 发现 ${duplicates.size} 组重复分类：")
        duplicates.forEach { (key, categories) ->
            val (bookId, type, name) = key
            val typeStr = if (type == 0) "支出" else "收入"
            println("\n  [$typeStr] $name:")
            categories.sortedBy { it.id }.forEach { cat ->
                println("    - ID: ${cat.id}, 创建时间: ${cat.create_time}")
            }
            println("    → 建议保留 ID: ${categories.minByOrNull { it.id }?.id}")
        }
    }
    
    // ==================== 私有方法 ====================
    
    /**
     * 更新账单的分类引用
     */
    private suspend fun updateBillsCategoryReference(
        billDao: BillDao,
        oldCategoryId: Long,
        newCategoryId: Long
    ) {
        val bills = billDao.getAllBillsSync()
        bills.filter { it.category_id == oldCategoryId }.forEach { bill ->
            val updatedBill = bill.copy(category_id = newCategoryId)
            billDao.updateBill(updatedBill)
        }
    }
}
