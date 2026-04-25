package com.diarybook.viewmodel

import androidx.test.core.app.ApplicationProvider
import com.diarybook.DiaryBookApplication
import com.diarybook.data.local.entity.Bill
import com.diarybook.data.local.entity.Book
import com.diarybook.data.local.entity.Category
import com.diarybook.testing.MainDispatcherRule
import com.diarybook.testing.TestDatabases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = DiaryBookApplication::class)
@OptIn(ExperimentalCoroutinesApi::class)
class BillViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var app: DiaryBookApplication

    @Before
    fun setUp() {
        System.setProperty("diarybook.skipDbInit", "true")
        app = ApplicationProvider.getApplicationContext()
        val inMemory = TestDatabases.inMemoryDb(app)
        TestDatabases.setSingletonInstance(inMemory)
    }

    @After
    fun tearDown() {
        val db = app.database
        db.close()
        TestDatabases.setSingletonInstance(null)
        System.clearProperty("diarybook.skipDbInit")
    }

    @Test
    fun totals_areCalculatedFromBillsFlow() = runTest {
        val db = app.database
        db.clearAllTables()
        val billDao = db.billDao()
        val bookId = db.bookDao().insertBook(
            Book(
                name = "测试账本",
                icon = "wallet",
                color = "#000000",
                is_default = 1,
                sort = 0,
                create_time = "1"
            )
        )
        val transportCategoryId = db.categoryDao().insertCategory(
            Category(
                book_id = bookId,
                type = 0,
                name = "交通",
                icon = "directions_car",
                color = "#2196F3",
                is_default = 1,
                sort = 1,
                create_time = "1"
            )
        )
        val salaryCategoryId = db.categoryDao().insertCategory(
            Category(
                book_id = bookId,
                type = 1,
                name = "工资",
                icon = "attach_money",
                color = "#4CAF50",
                is_default = 1,
                sort = 1,
                create_time = "1"
            )
        )

        billDao.insertBill(
            Bill(
                book_id = bookId,
                type = 0,
                amount = 10.0,
                category_id = transportCategoryId,
                category_name = "交通",
                category_icon = "🚗",
                date = "2026-04-01",
                create_time = "1",
                update_time = "1"
            )
        )
        billDao.insertBill(
            Bill(
                book_id = bookId,
                type = 1,
                amount = 100.0,
                category_id = salaryCategoryId,
                category_name = "工资",
                category_icon = "💰",
                date = "2026-04-02",
                create_time = "2",
                update_time = "2"
            )
        )

        val vm = BillViewModel(app)
        advanceUntilIdle()

        assertEquals(2, vm.bills.value.size)
        assertEquals(10.0, vm.totalExpense.value, 0.0001)
        assertEquals(100.0, vm.totalIncome.value, 0.0001)
    }
}

