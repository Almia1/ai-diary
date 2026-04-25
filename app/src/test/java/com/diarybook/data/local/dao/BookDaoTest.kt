package com.diarybook.data.local.dao

import androidx.test.core.app.ApplicationProvider
import com.diarybook.data.local.database.AppDatabase
import com.diarybook.data.local.entity.Book
import com.diarybook.testing.TestDatabases
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BookDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var bookDao: BookDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = TestDatabases.inMemoryDb(context)
        bookDao = db.bookDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun defaultBook_isNullWhenNoneSet() = runTest {
        assertNull(bookDao.getDefaultBook())
    }

    @Test
    fun setDefaultBook_switchesDefaultFlag() = runTest {
        val id1 = bookDao.insertBook(
            Book(
                name = "A",
                icon = "a",
                color = "#000000",
                is_default = 1,
                sort = 0,
                create_time = "1"
            )
        )
        val id2 = bookDao.insertBook(
            Book(
                name = "B",
                icon = "b",
                color = "#111111",
                is_default = 0,
                sort = 0,
                create_time = "2"
            )
        )

        assertEquals(id1, bookDao.getDefaultBook()!!.id)

        bookDao.clearDefaultBook()
        bookDao.setDefaultBook(id2)

        assertEquals(id2, bookDao.getDefaultBook()!!.id)
    }
}

