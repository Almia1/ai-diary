package com.diarybook.data.local.dao

import androidx.room.*
import com.diarybook.data.local.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM tb_category WHERE book_id = :bookId AND type = :type ORDER BY sort ASC, create_time ASC")
    fun getCategoriesByBookAndType(bookId: Long, type: Int): Flow<List<Category>>
    
    @Query("SELECT * FROM tb_category WHERE (book_id = :bookId OR book_id = 0) AND type = :type ORDER BY sort ASC, create_time ASC")
    fun getCategoriesByType(bookId: Long, type: Int): Flow<List<Category>>
    
    @Query("SELECT * FROM tb_category WHERE (book_id = :bookId OR book_id = 0) ORDER BY sort ASC, create_time ASC")
    suspend fun getAllCategoriesSync(bookId: Long): List<Category>
    
    @Query("SELECT * FROM tb_category WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    @Update
    suspend fun updateCategory(category: Category)
    
    @Delete
    suspend fun deleteCategory(category: Category)
    
    @Query("DELETE FROM tb_category WHERE book_id = :bookId")
    suspend fun deleteCategoriesByBook(bookId: Long)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)
}