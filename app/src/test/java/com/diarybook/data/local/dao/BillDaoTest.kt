package com.diarybook.data.local.dao

import androidx.test.core.app.ApplicationProvider
import com.diarybook.data.local.database.AppDatabase
import com.diarybook.data.local.entity.Book
import com.diarybook.data.local.entity.Bill
import com.diarybook.data.local.entity.Category
import com.diarybook.testing.TestDatabases
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BillDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var billDao: BillDao
    private var bookId: Long = 0
    private var foodCategoryId: Long = 0
    private var transportCategoryId: Long = 0
    private var salaryCategoryId: Long = 0

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = TestDatabases.inMemoryDb(context)
        billDao = db.billDao()

        runTest {
            bookId = db.bookDao().insertBook(
                Book(
                    name = "测试账本",
                    icon = "wallet",
                    color = "#000000",
                    is_default = 1,
                    sort = 0,
                    create_time = "1"
                )
            )
            transportCategoryId = db.categoryDao().insertCategory(
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
            foodCategoryId = db.categoryDao().insertCategory(
                Category(
                    book_id = bookId,
                    type = 0,
                    name = "餐饮",
                    icon = "restaurant",
                    color = "#FF5722",
                    is_default = 1,
                    sort = 2,
                    create_time = "1"
                )
            )
            salaryCategoryId = db.categoryDao().insertCategory(
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
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndGetById_returnsSameBill() = runTest {
        val bill = Bill(
            book_id = bookId,
            type = 0,
            amount = 100.0,
            category_id = foodCategoryId,
            category_name = "餐饮",
            category_icon = "🍽️",
            date = "2026-04-22",
            remark = "午饭",
            create_time = "1",
            update_time = "1"
        )

        val id = billDao.insertBill(bill)
        val loaded = billDao.getBillById(id)

        assertNotNull(loaded)
        assertEquals(id, loaded!!.id)
        assertEquals(100.0, loaded.amount, 0.0001)
        assertEquals("餐饮", loaded.category_name)
    }

    @Test
    fun totals_matchTypeAndDateRange() = runTest {
        val bills = listOf(
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
            ),
            Bill(
                book_id = bookId,
                type = 0,
                amount = 20.0,
                category_id = foodCategoryId,
                category_name = "餐饮",
                category_icon = "🍽️",
                date = "2026-04-15",
                create_time = "2",
                update_time = "2"
            ),
            Bill(
                book_id = bookId,
                type = 1,
                amount = 100.0,
                category_id = salaryCategoryId,
                category_name = "工资",
                category_icon = "💰",
                date = "2026-04-20",
                create_time = "3",
                update_time = "3"
            ),
            Bill(
                book_id = bookId,
                type = 0,
                amount = 999.0,
                category_id = foodCategoryId,
                category_name = "不在区间",
                category_icon = "❌",
                date = "2026-05-01",
                create_time = "4",
                update_time = "4"
            )
        )

        bills.forEach { billDao.insertBill(it) }

        val expense = billDao.getTotalExpense(bookId, "2026-04-01", "2026-04-30") ?: 0.0
        val income = billDao.getTotalIncome(bookId, "2026-04-01", "2026-04-30") ?: 0.0

        assertEquals(30.0, expense, 0.0001)
        assertEquals(100.0, income, 0.0001)
    }
}

