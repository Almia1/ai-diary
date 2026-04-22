package com.diarybook.data.repository

import com.diarybook.data.local.dao.BookDao
import com.diarybook.data.local.entity.Book
import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao) {
    
    fun getAllBooks(): Flow<List<Book>> = 
        bookDao.getAllBooks()
    
    suspend fun getBookById(id: Long): Book? = 
        bookDao.getBookById(id)
    
    suspend fun getDefaultBook(): Book? = 
        bookDao.getDefaultBook()
    
    suspend fun insertBook(book: Book): Long = 
        bookDao.insertBook(book)
    
    suspend fun updateBook(book: Book) = 
        bookDao.updateBook(book)
    
    suspend fun deleteBook(book: Book) = 
        bookDao.deleteBook(book)
    
    suspend fun setDefaultBook(id: Long) {
        bookDao.clearDefaultBook()
        bookDao.setDefaultBook(id)
    }
}
