package com.diarybook.util

/**
 * 分类图标映射工具类
 * 
 * 功能：将旧数据中的 Material Icons 名称转换为 Emoji 表情符号
 * 用于兼容数据库升级前的旧分类数据
 * 
 * @param iconValue 图标值，可能是 Material Icons 名称或 Emoji
 * @return 对应的 Emoji 图标，如果无法识别则返回默认图标 "📌"
 */
fun mapCategoryIcon(iconValue: String?): String {
    if (iconValue.isNullOrEmpty()) {
        return "📌" // 默认图标
    }
    
    // 如果已经是 Emoji（包含非 ASCII 字符），直接返回
    if (iconValue.any { it.code > 127 }) {
        return iconValue
    }
    
    // Material Icons 名称到 Emoji 的映射表
    return materialIconToEmoji[iconValue.lowercase()] ?: "📌"
}

/**
 * Material Icons 名称到 Emoji 的映射表
 * 覆盖所有默认分类和常见自定义分类
 */
private val materialIconToEmoji = mapOf(
    // 支出分类
    "restaurant" to "🍽️",
    "directions_car" to "🚗",
    "shopping_cart" to "🛒",
    "movie" to "🎬",
    "local_hospital" to "🏥",
    "school" to "📚",
    "home" to "🏠",
    "more_horiz" to "📌",
    "sports_esports" to "🎮",
    "flight" to "✈️",
    "local_cafe" to "☕",
    "fitness_center" to "💪",
    "pets" to "🐾",
    "local_grocery_store" to "🛍️",
    "phone_android" to "📱",
    "computer" to "💻",
    "electric_bolt" to "⚡",
    "water_drop" to "💧",
    "local_phone" to "📞",
    "subscriptions" to "📺",
    
    // 收入分类
    "attach_money" to "💰",
    "card_giftcard" to "🎁",
    "trending_up" to "📈",
    "work" to "💼",
    "account_balance" to "🏦",
    "savings" to "💵",
    "redeem" to "🎟️",
    "handshake" to "🤝",
    "emoji_events" to "🏆",
    "stock_market" to "📊",
    
    // 其他常见图标
    "star" to "⭐",
    "favorite" to "❤️",
    "bookmark" to "🔖",
    "calendar_today" to "📅",
    "notifications" to "🔔",
    "settings" to "⚙️",
    "help" to "❓",
    "info" to "ℹ️",
    "warning" to "⚠️",
    "error" to "❌",
    "check_circle" to "✅",
    "add_circle" to "➕",
    "remove_circle" to "➖",
    "close" to "❎",
    "search" to "🔍",
    "edit" to "✏️",
    "delete" to "🗑️",
    "share" to "📤",
    "download" to "📥",
    "upload" to "📤",
    "camera" to "📷",
    "image" to "🖼️",
    "music_note" to "🎵",
    "videocam" to "📹",
    "mic" to "🎤",
    "headset" to "🎧",
    "gamepad" to "🎮",
    "sports_soccer" to "⚽",
    "sports_basketball" to "🏀",
    "directions_walk" to "🚶",
    "directions_bus" to "🚌",
    "directions_railway" to "🚆",
    "local_taxi" to "🚕",
    "two_wheeler" to "🏍️",
    "pedal_bike" to "🚲",
    "local_parking" to "🅿️",
    "ev_station" to "🔌",
    "fastfood" to "🍔",
    "local_pizza" to "🍕",
    "icecream" to "🍦",
    "cake" to "🎂",
    "local_bar" to "🍺",
    "wine_bar" to "🍷",
    "brunch_dining" to "🥞",
    "ramen_dining" to "🍜",
    "bakery_dining" to "🥐",
    "egg_alt" to "🥚",
    "kebab_dining" to "🍢",
    "no_meals" to "🚫",
    "outdoor_grill" to "🍖",
    "dinner_dining" to "🍽️",
    "lunch_dining" to "🍱",
    "breakfast_dining" to "🍳",
    "coffee" to "☕",
    "tea" to "🍵",
    "sports_bar" to "🍺",
    "liquor" to "🥃",
    "blender" to "🥤",
    "emoji_food_beverage" to "🧃",
    "ice_skating" to "⛸️",
    "kayaking" to "🛶",
    "paragliding" to "🪂",
    "surfing" to "🏄",
    "hiking" to "🥾",
    "snowboarding" to "🏂",
    "downhill_skiing" to "⛷️",
    "horseback_riding" to "🏇",
    "rowing" to "🚣",
    "swimming" to "🏊",
    "self_improvement" to "🧘",
    "spa" to "💆",
    "salinity" to "🧂",
    "medical_services" to "🏥",
    "dentistry" to "🦷",
    "vaccines" to "💉",
    "health_and_safety" to "🛡️",
    "psychology" to "🧠",
    "diversity_3" to "👥",
    "groups" to "👨‍👩‍👧‍👦",
    "family_restroom" to "👪",
    "pregnant_woman" to "🤰",
    "baby_changing_station" to "👶",
    "elderly" to "👴",
    "child_care" to "👼",
    "boy" to "👦",
    "girl" to "👧",
    "man" to "👨",
    "woman" to "👩",
    "person" to "🧑",
    "face" to "😊",
    "thumb_up" to "👍",
    "thumb_down" to "👎",
    "clap" to "👏",
    "pray" to "🙏",
    "raised_hand" to "✋",
    "victory_hand" to "✌️",
    "crossed_fingers" to "🤞",
    "love_you_gesture" to "🤟",
    "ok_hand" to "👌",
    "pinching_hand" to "🤏",
    "call_me_hand" to "🤙",
    "point_left" to "👈",
    "point_right" to "👉",
    "point_up" to "👆",
    "point_down" to "👇",
    "backhand_index_pointing_up" to "👆",
    "middle_finger" to "🖕",
    "fist_raised" to "✊",
    "fist_oncoming" to "👊",
    "fist_left" to "🤛",
    "fist_right" to "🤜",
    "wave" to "👋",
    "open_hands" to "👐",
    "palms_up_together" to "🤲",
    "handshake" to "🤝",
    "nail_care" to "💅",
    "ear" to "👂",
    "nose" to "👃",
    "footprints" to "👣",
    "eyes" to "👀",
    "eye" to "👁️",
    "brain" to "🧠",
    "anatomical_heart" to "🫀",
    "lungs" to "🫁",
    "tooth" to "🦷",
    "bone" to "🦴",
    "speaking_head" to "🗣️",
    "bust_in_silhouette" to "👤",
    "busts_in_silhouette" to "👥",
    "people_holding_hands" to "🧑‍🤝‍🧑",
    "family" to "👨‍👩‍👧",
    "couple" to "👫",
    "two_men_holding_hands" to "👬",
    "two_women_holding_hands" to "👭",
    "kiss" to "💏",
    "couple_with_heart" to "💑",
    "wedding" to "💒",
    "broken_heart" to "💔",
    "heart" to "❤️",
    "orange_heart" to "🧡",
    "yellow_heart" to "💛",
    "green_heart" to "💚",
    "blue_heart" to "💙",
    "purple_heart" to "💜",
    "black_heart" to "🖤",
    "white_heart" to "🤍",
    "brown_heart" to "🤎",
    "sparkling_heart" to "💖",
    "growing_heart" to "💗",
    "beating_heart" to "💓",
    "revolving_hearts" to "💞",
    "two_hearts" to "💕",
    "heart_decoration" to "💟",
    "heavy_heart_exclamation" to "❣️",
    "love_letter" to "💌",
    "cupid" to "💘",
    "gift_heart" to "💝",
    "heart_on_fire" to "❤️‍🔥",
    "mending_heart" to "❤️‍🩹",
    "peace_symbol" to "☮️",
    "latin_cross" to "✝️",
    "star_and_crescent" to "☪️",
    "om_symbol" to "🕉️",
    "wheel_of_dharma" to "☸️",
    "star_of_david" to "✡️",
    "six_pointed_star" to "🔯",
    "menorah" to "🕎",
    "yin_yang" to "☯️",
    "orthodox_cross" to "☦️",
    "place_of_worship" to "🛐",
    "ophiuchus" to "⛎",
    "aries" to "♈",
    "taurus" to "♉",
    "gemini" to "♊",
    "cancer" to "♋",
    "leo" to "♌",
    "virgo" to "♍",
    "libra" to "♎",
    "scorpius" to "♏",
    "sagittarius" to "♐",
    "capricorn" to "♑",
    "aquarius" to "♒",
    "pisces" to "♓",
    "id" to "🆔",
    "atom_symbol" to "⚛️",
    "accept" to "🉑",
    "radioactive" to "☢️",
    "biohazard" to "☣️",
    "mobile_phone_off" to "📴",
    "vibration_mode" to "📳",
    "u6709" to "🈶",
    "u7121" to "🈚",
    "u7533" to "🈸",
    "u55b6" to "🈺",
    "u6708" to "🈷️",
    "eight_spoked_asterisk" to "✳️",
    "vs" to "🆚",
    "white_flower" to "💮",
    "ideograph_advantage" to "🉐",
    "secret" to "㊙️",
    "congratulations" to "㊗️",
    "u5408" to "🈴",
    "u6e80" to "🈵",
    "u5272" to "🈹",
    "u7981" to "🈲",
    "a" to "🅰️",
    "b" to "🅱️",
    "ab" to "🆎",
    "cl" to "🆑",
    "o2" to "🅾️",
    "sos" to "🆘",
    "x" to "❌",
    "o" to "⭕",
    "stop_sign" to "🛑",
    "no_entry" to "⛔",
    "name_badge" to "📛",
    "no_entry_sign" to "🚫",
)

/**
 * 获取分类图标的显示文本
 * 
 * 这个函数是 mapCategoryIcon 的别名，提供更语义化的 API
 */
fun getCategoryDisplayIcon(iconValue: String?): String = mapCategoryIcon(iconValue)

/**
 * 判断图标值是否已经是 Emoji
 */
fun isEmoji(iconValue: String?): Boolean {
    if (iconValue.isNullOrEmpty()) return false
    return iconValue.any { it.code > 127 }
}

/**
 * 安全解析颜色字符串为 Compose Color 对象
 * 
 * 支持格式：
 * - "#FF5722" (带 # 前缀的 6 位十六进制)
 * - "FF5722" (不带 # 前缀的 6 位十六进制)
 * - "#AARRGGBB" (带 alpha 的 8 位十六进制)
 * - 空值或无效格式返回默认浅紫色
 * 
 * @param colorString 颜色字符串，如 "#FF5722"
 * @param defaultColor 解析失败时的默认颜色，默认为浅紫色 LightPurple
 * @return Compose Color 对象
 */
fun parseColorSafely(
    colorString: String?,
    defaultColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFFF5F0FF)
): androidx.compose.ui.graphics.Color {
    if (colorString.isNullOrEmpty()) {
        return defaultColor
    }
    
    return try {
        // 移除 # 前缀（如果存在）
        val hexColor = colorString.trimStart('#')
        
        // 验证是否为有效的十六进制字符串
        if (hexColor.isEmpty() || !hexColor.all { it in '0'..'9' || it in 'A'..'F' || it in 'a'..'f' }) {
            return defaultColor
        }
        
        // 解析为 Long，支持 6 位和 8 位格式
        val colorValue = when (hexColor.length) {
            6 -> hexColor.toLong(16) or 0xFF000000 // 6 位格式，添加不透明 alpha
            8 -> hexColor.toLong(16) // 8 位格式，直接使用
            else -> return defaultColor // 其他长度，返回默认值
        }
        
        androidx.compose.ui.graphics.Color(colorValue)
    } catch (e: NumberFormatException) {
        // 解析失败，返回默认颜色
        defaultColor
    } catch (e: Exception) {
        // 其他异常，返回默认颜色
        defaultColor
    }
}

/**
 * 将颜色字符串解析为 Android 原生 Color Int（用于 android.graphics.Color）
 * 
 * @param colorString 颜色字符串，如 "#FF5722"
 * @param defaultColorInt 解析失败时的默认颜色值
 * @return Android Color Int
 */
fun parseColorToInt(
    colorString: String?,
    defaultColorInt: Int = 0xFFF5F0FF.toInt()
): Int {
    if (colorString.isNullOrEmpty()) {
        return defaultColorInt
    }
    
    return try {
        val hexColor = colorString.trimStart('#')
        
        if (hexColor.isEmpty() || !hexColor.all { it in '0'..'9' || it in 'A'..'F' || it in 'a'..'f' }) {
            return defaultColorInt
        }
        
        when (hexColor.length) {
            6 -> hexColor.toLong(16).toInt() or -0x1000000 // 添加不透明 alpha
            8 -> hexColor.toLong(16).toInt()
            else -> defaultColorInt
        }
    } catch (e: NumberFormatException) {
        defaultColorInt
    } catch (e: Exception) {
        defaultColorInt
    }
}
