package com.diarybook.ui.navigation

sealed class NavRoutes(val route: String) {
    object Detail : NavRoutes("detail")
    object Calendar : NavRoutes("calendar")
    object Statistics : NavRoutes("statistics")
    object Asset : NavRoutes("asset")
    object Mine : NavRoutes("mine")
    object AddBill : NavRoutes("add_bill")
    object EditBill : NavRoutes("edit_bill/{billId}") {
        fun createRoute(billId: Long) = "edit_bill/$billId"
    }
    object OcrRecognition : NavRoutes("ocr_recognition")
    object CategoryManagement : NavRoutes("category_management")
    object BookManagement : NavRoutes("book_management")
}
