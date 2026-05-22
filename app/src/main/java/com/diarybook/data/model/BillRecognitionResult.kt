package com.diarybook.data.model

data class BillRecognitionResult(
    val amount: Double? = null,
    val date: String? = null,
    val merchant: String? = null,
    val type: Int = 0,
    val suggestedCategoryId: Long? = null,
    val suggestedCategoryName: String? = null,
    val suggestedCategoryIcon: String? = null,
    val rawText: String = ""
)
