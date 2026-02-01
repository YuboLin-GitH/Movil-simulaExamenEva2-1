package com.example.movil_simulaexameneva2_1

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// --- 辅助数据类：用于存储计算后的积分 ---
data class TeamStats(
    val team: Team,
    var pj: Int = 0,
    var pg: Int = 0,
    var pe: Int = 0,
    var pp: Int = 0,
    var points: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassificationScreen(navController: NavController) {
    val context = LocalContext.current

    // 1. 获取所有比赛和球队
    // 注意：在实际应用中，如果数据会变动（比如刚才编辑了比分），这里应该使用 viewModel 或 observe 状态
    // 为了简单演示，我们重新计算一次
    val matches = DataProvider.matches
    val teams = DataProvider.teams

    // 2. 计算积分逻辑
    val classificationList = remember(matches) {
        calculateLeagueStats(teams, matches)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clasificación") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    // 题目要求：分享按钮
                    IconButton(onClick = {
                        shareClassification(context, classificationList)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // 3. 表头 (Header)
            ClassificationHeader()

            // 4. 列表 (RecyclerView)
            LazyColumn {
                itemsIndexed(classificationList) { index, stats ->
                    ClassificationItem(
                        position = index + 1,
                        stats = stats,
                        onClick = {
                            // 题目要求：点击跳转到详情页
                            // 路由格式: "team_detail/{teamId}"
                            navController.navigate("team_detail/${stats.team.id}")
                        }
                    )
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
    }
}

// --- 核心逻辑：积分计算 ---
fun calculateLeagueStats(teams: List<Team>, matches: List<Match>): List<TeamStats> {
    // 初始化每个球队的统计数据
    val statsMap = teams.associate { it.id to TeamStats(it) }

    matches.forEach { match ->
        // 只统计已结束的比赛 (比分不为null)
        if (match.localScore != null && match.visitorScore != null) {
            val localStats = statsMap[match.localTeamId]
            val visitorStats = statsMap[match.visitorTeamId]

            if (localStats != null && visitorStats != null) {
                // 增加已赛场次 (PJ)
                localStats.pj++
                visitorStats.pj++

                when {
                    match.localScore > match.visitorScore -> { // 主胜
                        localStats.pg++
                        localStats.points += 3
                        visitorStats.pp++
                    }
                    match.visitorScore > match.localScore -> { // 客胜
                        visitorStats.pg++
                        visitorStats.points += 3
                        localStats.pp++
                    }
                    else -> { // 平局
                        localStats.pe++
                        localStats.points += 1
                        visitorStats.pe++
                        visitorStats.points += 1
                    }
                }
            }
        }
    }

    // 题目要求：按积分排序
    return statsMap.values.sortedByDescending { it.points }
}

// --- UI 组件：表头 ---
@Composable
fun ClassificationHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Pos", Modifier.weight(0.8f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Text("Equipo", Modifier.weight(2.5f), fontWeight = FontWeight.Bold) // 名字留宽一点
        Text("PJ", Modifier.weight(0.8f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Text("PG", Modifier.weight(0.8f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Text("PE", Modifier.weight(0.8f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Text("PP", Modifier.weight(0.8f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Text("Pts", Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
    }
}

// --- UI 组件：单行数据 ---
@Composable
fun ClassificationItem(position: Int, stats: TeamStats, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 排名
        Text("$position", Modifier.weight(0.8f), textAlign = TextAlign.Center)

        // 队徽 + 队名
        Row(Modifier.weight(2.5f), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = stats.team.logoResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(stats.team.name, fontSize = 14.sp, maxLines = 1)
        }

        // 数据列
        Text("${stats.pj}", Modifier.weight(0.8f), textAlign = TextAlign.Center)
        Text("${stats.pg}", Modifier.weight(0.8f), textAlign = TextAlign.Center)
        Text("${stats.pe}", Modifier.weight(0.8f), textAlign = TextAlign.Center)
        Text("${stats.pp}", Modifier.weight(0.8f), textAlign = TextAlign.Center)

        // 积分 (加粗)
        Text("${stats.points}", Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

// --- 辅助函数：分享逻辑 ---
fun shareClassification(context: android.content.Context, list: List<TeamStats>) {
    val sb = StringBuilder()
    sb.append("Clasificación de La Liga:\n\n")
    list.forEachIndexed { index, stats ->
        sb.append("${index + 1}. ${stats.team.name} - ${stats.points} pts\n")
    }

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Compartir clasificación")
    context.startActivity(shareIntent)
}