package com.example.movil_simulaexameneva2_1

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- 1. 数据模型 ---
data class Team(
    val id: String,
    val name: String,
    val logoResId: Int,
    val foundationYear: String,
    val president: String,
    val stadium: String,
    val stadiumImageUrl: String
)

data class Match(
    val id: String,
    val localTeamId: String,
    val visitorTeamId: String,
    val localScore: Int?,
    val visitorScore: Int?,
    val matchDate: Long,
    val matchDay: Int
)

// --- 2. 数据提供者 (单例) ---
object DataProvider {
    // ⚠️ 注意：请确保你的 res/drawable 文件夹里真的有 a173, a175 这些图片
    // 如果没有，暂时改回 R.drawable.ic_launcher_foreground 测试
    val teams = listOf(
        Team("1", "Real Madrid", R.drawable.ic_launcher_foreground, "1902", "Florentino Pérez", "Santiago Bernabéu", "https://example.com/bernabeu.jpg"),
        Team("2", "Athletic Bilbao", R.drawable.ic_launcher_foreground, "1898", "Jon Uriarte", "San Mamés", "https://example.com/sanmames.jpg"),
        Team("3", "Barcelona", R.drawable.ic_launcher_foreground, "1899", "Joan Laporta", "Camp Nou", "https://example.com/campnou.jpg")
    )

    val matches = mutableListOf(
        Match("101", "2", "1", null, null, 1735689600L, 10),
        Match("102", "2", "3", 1, 1, 1735689600L, 10)
    )

    fun getTeamById(id: String): Team? = teams.find { it.id == id }
}

// --- 3. 工具函数 ---
fun formatMatchDate(timestampSecs: Long): String {
    val date = Date(timestampSecs * 1000)
    val formatter = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
    return formatter.format(date)
}