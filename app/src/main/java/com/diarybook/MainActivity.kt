package com.diarybook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.diarybook.ui.component.BottomNavBar
import com.diarybook.ui.navigation.AppNavHost
import com.diarybook.ui.theme.DiaryBookTheme
import com.diarybook.viewmodel.BillViewModel
import com.diarybook.viewmodel.BillViewModelFactory
import com.diarybook.viewmodel.BookViewModel
import com.diarybook.viewmodel.BookViewModelFactory
import com.diarybook.viewmodel.CategoryViewModel
import com.diarybook.viewmodel.CategoryViewModelFactory
import com.diarybook.viewmodel.BudgetViewModel
import com.diarybook.viewmodel.BudgetViewModelFactory
import com.diarybook.viewmodel.DebtViewModel
import com.diarybook.viewmodel.DebtViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiaryBookTheme {
                val billViewModel: BillViewModel = viewModel(
                    factory = BillViewModelFactory(application)
                )
                val bookViewModel: BookViewModel = viewModel(
                    factory = BookViewModelFactory(application)
                )
                val categoryViewModel: CategoryViewModel = viewModel(
                    factory = CategoryViewModelFactory(application)
                )
                val budgetViewModel: BudgetViewModel = viewModel(
                    factory = BudgetViewModelFactory(application)
                )
                val debtViewModel: DebtViewModel = viewModel(
                    factory = DebtViewModelFactory(application)
                )
                MainApp(
                    billViewModel = billViewModel,
                    bookViewModel = bookViewModel,
                    categoryViewModel = categoryViewModel,
                    budgetViewModel = budgetViewModel,
                    debtViewModel = debtViewModel
                )
            }
        }
    }
}

@Composable
fun MainApp(
    billViewModel: BillViewModel,
    bookViewModel: BookViewModel,
    categoryViewModel: CategoryViewModel,
    budgetViewModel: BudgetViewModel,
    debtViewModel: DebtViewModel
) {
    val navController = rememberNavController()
    var selectedRoute by remember { mutableStateOf("detail") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                selectedRoute = selectedRoute,
                onItemSelected = { route ->
                    selectedRoute = route
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            startDestination = "detail",
            billViewModel = billViewModel,
            bookViewModel = bookViewModel,
            categoryViewModel = categoryViewModel,
            budgetViewModel = budgetViewModel,
            debtViewModel = debtViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}