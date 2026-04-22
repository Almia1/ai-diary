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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiaryBookTheme {
                val billViewModel: BillViewModel = viewModel(
                    factory = BillViewModelFactory(application)
                )
                MainApp(billViewModel = billViewModel)
            }
        }
    }
}

@Composable
fun MainApp(billViewModel: BillViewModel) {
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
            modifier = Modifier.padding(innerPadding)
        )
    }
}


