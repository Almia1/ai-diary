package com.diarybook.data.repository

import com.diarybook.data.local.dao.CategoryDao
import com.diarybook.data.local.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    
    fun getCategoriesByBookAndType(bookId: Long, type: Int): Flow<List<Category>> =
        categoryDao.getCategoriesByBookAndType(bookId, type)
    
    fun getCategoriesByType(bookId: Long, type: Int): Flow<List<Category>> =
        categoryDao.getCategoriesByType(bookId, type)
    
    suspend fun getCategoryById(id: Long): Category? =
        categoryDao.getCategoryById(id)
    
    suspend fun insertCategory(category: Category): Long =
        categoryDao.insertCategory(category)
    
    suspend fun updateCategory(category: Category) =
        categoryDao.updateCategory(category)
    
    suspend fun deleteCategory(category: Category) =
        categoryDao.deleteCategory(category)
    
    suspend fun deleteCategoriesByBook(bookId: Long) =
        categoryDao.deleteCategoriesByBook(bookId)
    
    suspend fun insertCategories(categories: List<Category>) =
        categoryDao.insertCategories(categories)
}
