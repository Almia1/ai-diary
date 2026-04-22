package com.diarybook.data.local.dao

import androidx.room.*
import com.diarybook.data.local.entity.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM tb_book ORDER BY sort ASC, create_time DESC")
    fun getAllBooks(): Flow<List<Book>>
    
    @Query("SELECT * FROM tb_book WHERE id = :id")
    suspend fun getBookById(id: Long): Book?
    
    @Query("SELECT * FROM tb_book WHERE is_default = 1 LIMIT 1")
    suspend fun getDefaultBook(): Book?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long
    
    @Update
    suspend fun updateBook(book: Book)
    
    @Delete
    suspend fun deleteBook(book: Book)
    
    @Query("UPDATE tb_book SET is_default = 0 WHERE is_default = 1")
    suspend fun clearDefaultBook()
    
    @Query("UPDATE tb_book SET is_default = 1 WHERE id = :id")
    suspend fun setDefaultBook(id: Long)
}
