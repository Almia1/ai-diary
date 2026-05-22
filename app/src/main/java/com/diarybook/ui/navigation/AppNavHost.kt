package com.diarybook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diarybook.ui.screen.AddBillScreen
import com.diarybook.ui.screen.AssetScreen
import com.diarybook.ui.screen.BookManagementScreen
import com.diarybook.ui.screen.CalendarScreen
import com.diarybook.ui.screen.CategoryManagementScreen
import com.diarybook.ui.screen.DetailScreen
import com.diarybook.ui.screen.MineScreen
import com.diarybook.ui.screen.StatisticsScreen
import com.diarybook.viewmodel.BillViewModel
import com.diarybook.viewmodel.BookViewModel
import com.diarybook.viewmodel.CategoryViewModel
import com.diarybook.viewmodel.BudgetViewModel
import com.diarybook.viewmodel.DebtViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = "detail",
    billViewModel: BillViewModel,
    bookViewModel: BookViewModel,
    categoryViewModel: CategoryViewModel,
    budgetViewModel: BudgetViewModel,
    debtViewModel: DebtViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("detail") {
            DetailScreen(
                onNavigateToAddBill = {
                    navController.navigate("add_bill")
                },
                onNavigateToOcrRecognition = {
                    navController.navigate("ocr_recognition")
                },
                billViewModel = billViewModel,
                bookViewModel = bookViewModel
            )
        }
        composable("calendar") {
            CalendarScreen()
        }
        composable("statistics") {
            StatisticsScreen()
        }
        composable("asset") {
            AssetScreen(
                budgetViewModel = budgetViewModel,
                debtViewModel = debtViewModel
            )
        }
        composable("mine") {
            MineScreen(
                onNavigateToCategoryManagement = {
                    navController.navigate("category_management")
                },
                onNavigateToBookManagement = {
                    navController.navigate("book_management")
                }
            )
        }
        composable("book_management") {
            BookManagementScreen(
                onBackClick = { navController.popBackStack() },
                bookViewModel = bookViewModel
            )
        }
        composable("category_management") {
            CategoryManagementScreen(
                onBackClick = { navController.popBackStack() },
                categoryViewModel = categoryViewModel,
                bookViewModel = bookViewModel
            )
        }
        composable("add_bill") {
            AddBillScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { _, _, _, _, _, _, _ ->
                    navController.popBackStack()
                },
                billViewModel = billViewModel,
                categoryViewModel = categoryViewModel
            )
        }
        composable("ocr_recognition") {
            OcrRecognitionScreen(
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack("detail", false)
                },
                billViewModel = billViewModel,
                categoryViewModel = categoryViewModel
            )
        }
    }
}
