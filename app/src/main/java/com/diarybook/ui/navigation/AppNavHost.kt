package com.diarybook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diarybook.ui.screen.AddBillScreen
import com.diarybook.ui.screen.AssetScreen
import com.diarybook.ui.screen.CalendarScreen
import com.diarybook.ui.screen.DetailScreen
import com.diarybook.ui.screen.MineScreen
import com.diarybook.ui.screen.StatisticsScreen
import com.diarybook.viewmodel.BillViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = "detail",
    billViewModel: BillViewModel,
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
                billViewModel = billViewModel
            )
        }
        composable("calendar") {
            CalendarScreen()
        }
        composable("statistics") {
            StatisticsScreen()
        }
        composable("asset") {
            AssetScreen()
        }
        composable("mine") {
            MineScreen()
        }
        composable("add_bill") {
            AddBillScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { _, _, _, _, _, _, _ ->
                    navController.popBackStack()
                },
                billViewModel = billViewModel
            )
        }
    }
}
