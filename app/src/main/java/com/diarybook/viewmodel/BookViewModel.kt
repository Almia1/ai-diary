package com.diarybook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diarybook.DiaryBookApplication
import com.diarybook.data.local.entity.Book
import com.diarybook.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: BookRepository
    
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()
    
    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook: StateFlow<Book?> = _currentBook.asStateFlow()
    
    private val _defaultBook = MutableStateFlow<Book?>(null)
    val defaultBook: StateFlow<Book?> = _defaultBook.asStateFlow()
    
    init {
        val app = application as DiaryBookApplication
        repository = BookRepository(app.database.bookDao())
        loadBooks()
        loadDefaultBook()
    }
    
    private fun loadBooks() {
        viewModelScope.launch {
            repository.getAllBooks().collect {
                _books.value = it
            }
        }
    }
    
    private fun loadDefaultBook() {
        viewModelScope.launch {
            val book = repository.getDefaultBook()
            _defaultBook.value = book
            _currentBook.value = book
        }
    }
    
    fun setCurrentBook(book: Book) {
        _currentBook.value = book
    }
    
    fun addBook(name: String, icon: String, color: String) {
        viewModelScope.launch {
            val book = Book(
                name = name,
                icon = icon,
                color = color,
                is_default = 0,
                sort = 0,
                create_time = System.currentTimeMillis().toString()
            )
            val id = repository.insertBook(book)
            val newBook = book.copy(id = id)
            _books.value = _books.value + newBook
        }
    }
    
    fun updateBook(book: Book) {
        viewModelScope.launch {
            repository.updateBook(book)
            _books.value = _books.value.map {
                if (it.id == book.id) book else it
            }
            if (_currentBook.value?.id == book.id) {
                _currentBook.value = book
            }
            if (_defaultBook.value?.id == book.id) {
                _defaultBook.value = book
            }
        }
    }
    
    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
            _books.value = _books.value.filter { it.id != book.id }
            if (_currentBook.value?.id == book.id) {
                val defaultBook = repository.getDefaultBook()
                _currentBook.value = defaultBook
            }
        }
    }
    
    fun setDefaultBook(book: Book) {
        viewModelScope.launch {
            repository.setDefaultBook(book.id)
            val updatedBook = book.copy(is_default = 1)
            _books.value = _books.value.map {
                if (it.id == book.id) updatedBook else it.copy(is_default = 0)
            }
            _defaultBook.value = updatedBook
            _currentBook.value = updatedBook
        }
    }
}

class BookViewModelFactory(
    private val application: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
