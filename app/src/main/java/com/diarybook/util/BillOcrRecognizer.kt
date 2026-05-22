package com.diarybook.util

import android.graphics.Bitmap
import android.net.Uri
import com.diarybook.data.model.BillRecognitionResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.min

object BillOcrRecognizer {
    private val recognizer: TextRecognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    
    suspend fun recognizeFromBitmap(bitmap: Bitmap): BillRecognitionResult {
        val image = InputImage.fromBitmap(bitmap, 0)
        return recognizeFromImage(image)
    }
    
    suspend fun recognizeFromUri(uri: Uri, context: android.content.Context): BillRecognitionResult {
        val image = InputImage.fromFilePath(context, uri)
        return recognizeFromImage(image)
    }
    
    private suspend fun recognizeFromImage(image: InputImage): BillRecognitionResult {
        val text = suspendCancellableCoroutine<String> { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    continuation.resume(visionText.text)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
        
        return parseBillText(text)
    }
    
    private fun parseBillText(text: String): BillRecognitionResult {
        val amount = extractAmount(text)
        val date = extractDate(text)
        val merchant = extractMerchant(text)
        val type = detectTransactionType(text)
        val category = suggestCategory(text, merchant)
        
        return BillRecognitionResult(
            amount = amount,
            date = date,
            merchant = merchant,
            type = type,
            suggestedCategoryId = category.first,
            suggestedCategoryName = category.second,
            suggestedCategoryIcon = category.third,
            rawText = text
        )
    }
    
    private fun extractAmount(text: String): Double? {
        val patterns = listOf(
            Regex("""(?:¥|￥|RMB)?\s*(\d+\.?\d*)\s*元?"""),
            Regex("""(?:总金额|合计|金额|付款|消费)[:：]?\s*(?:¥|￥)?\s*(\d+\.?\d*)"""),
            Regex("""(\d+\.\d{2})"""),
            Regex("""(\d+\.\d)"""),
            Regex("""支出[:：]?\s*(?:¥|￥)?\s*(\d+\.?\d*)""", RegexOption.IGNORE_CASE),
            Regex("""收入[:：]?\s*(?:¥|￥)?\s*(\d+\.?\d*)""", RegexOption.IGNORE_CASE)
        )
        
        val amounts = mutableListOf<Double>()
        
        for (pattern in patterns) {
            pattern.findAll(text).forEach { match ->
                match.groupValues.getOrNull(1)?.toDoubleOrNull()?.let { amount ->
                    if (amount > 0 && amount < 1000000) {
                        amounts.add(amount)
                    }
                }
            }
            if (amounts.isNotEmpty()) break
        }
        
        return amounts.maxByOrNull { it }
    }
    
    private fun extractDate(text: String): String? {
        val patterns = listOf(
            Regex("""(\d{4}[-/年]\d{1,2}[-/月]\d{1,2}[日]?)"""),
            Regex("""(\d{4}\d{2}\d{2})"""),
            Regex("""(\d{2}/\d{2}/\d{4})"""),
            Regex("""(\d{4}年\d{1,2}月\d{1,2}日)""")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                val dateStr = match.groupValues[1]
                return normalizeDate(dateStr)
            }
        }
        
        return null
    }
    
    private fun normalizeDate(dateStr: String): String {
        var normalized = dateStr
            .replace("年", "-")
            .replace("月", "-")
            .replace("日", "")
            .replace("/", "-")
        
        if (normalized.length == 8 && normalized.all { it.isDigit() }) {
            normalized = "${normalized.substring(0, 4)}-${normalized.substring(4, 6)}-${normalized.substring(6, 8)}"
        }
        
        return normalized
    }
    
    private fun extractMerchant(text: String): String? {
        val patterns = listOf(
            Regex("""(?:交易对方|商户|商家|收款方|收款人|对方)[:：]\s*(.+)"""),
            Regex("""(?:商品说明|订单详情|交易说明)[:：]\s*(.+)"""),
            Regex("""(?:星巴克|麦当劳|肯德基|支付宝|微信|美团|饿了么|淘宝|京东|拼多多)[^\u4e00-\u9fa5]*([\u4e00-\u9fa5]+)?""")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.groupValues[1].trim().take(50)
            }
        }
        
        return null
    }
    
    private fun detectTransactionType(text: String): Int {
        val expenseKeywords = listOf("支出", "消费", "付款", "购买", "支出金额")
        val incomeKeywords = listOf("收入", "收款", "到账", "入账", "转账收入")
        
        val lowerText = text.lowercase()
        
        for (keyword in expenseKeywords) {
            if (lowerText.contains(keyword)) return 0
        }
        
        for (keyword in incomeKeywords) {
            if (lowerText.contains(keyword)) return 1
        }
        
        return 0
    }
    
    private fun suggestCategory(text: String, merchant: String?): Triple<Long?, String?, String?> {
        val combinedText = "$text ${merchant ?: ""}"
        
        val categoryMapping = listOf(
            Triple(listOf("餐饮", "食物", "餐饮", "吃饭", "餐厅", "美食"), "🍽️", "餐饮"),
            Triple(listOf("超市", "便利店", "商场", "购物", "商店", "商品"), "🛒", "购物"),
            Triple(listOf("交通", "打车", "地铁", "公交", "出行", "滴滴", "打车"), "🚗", "交通"),
            Triple(listOf("医疗", "医院", "药店", "医疗", "看病", "买药"), "🏥", "医疗"),
            Triple(listOf("教育", "学校", "培训", "学习", "课程", "书"), "📚", "教育"),
            Triple(listOf("娱乐", "电影", "游戏", "娱乐", "K歌", "KTV"), "🎬", "娱乐"),
            Triple(listOf("居住", "房租", "水电", "物业", "住房", "住宿"), "🏠", "居住"),
            Triple(listOf("通讯", "话费", "流量", "宽带", "通讯"), "📱", "通讯"),
            Triple(listOf("服装", "衣服", "鞋", "包", "服装", "穿戴"), "👔", "服装"),
            Triple(listOf("水果", "水果店", "水果", "鲜果"), "🍎", "水果"),
            Triple(listOf("咖啡", "奶茶", "饮料", "饮品", "咖啡", "茶"), "☕", "饮品"),
            Triple(listOf("日用品", "日用", "生活用品", "超市", "便利店"), "🧴", "日用品"),
            Triple(listOf("美容", "美发", "理发", "美容", "护肤", "化妆"), "💄", "美容"),
            Triple(listOf("运动", "健身", "运动", "体育"), "💪", "运动"),
            Triple(listOf("宠物", "宠物", "猫粮", "狗粮", "动物"), "🐾", "宠物"),
            Triple(listOf("转账", "红包", "转账", "微信转账", "支付宝转账"), "💰", "转账"),
            Triple(listOf("工资", "薪资", "工资", "奖金", "收入"), "💼", "工资"),
            Triple(listOf("理财", "投资", "基金", "股票", "理财"), "📈", "理财")
        )
        
        for ((keywords, icon, name) in categoryMapping) {
            for (keyword in keywords) {
                if (combinedText.contains(keyword)) {
                    return Triple(null, name, icon)
                }
            }
        }
        
        return Triple(null, "其他", "📌")
    }
}
