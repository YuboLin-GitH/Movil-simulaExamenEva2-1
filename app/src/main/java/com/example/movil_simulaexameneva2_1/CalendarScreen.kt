package com.example.movil_simulaexameneva2_1

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// --- 主屏幕 ---le
@Composable
fun CalendarScreen(navController: NavController) {
    // 状态管理
    var matches by remember { mutableStateOf(DataProvider.matches) }
    var selectedJornada by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedMatchForEdit by remember { mutableStateOf<Match?>(null) }

    // 过滤逻辑
    val filteredMatches = matches.filter { match ->
        if (selectedJornada == 0) true else match.matchDay == selectedJornada
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 1. 顶部筛选
        JornadaSelector(
            currentJornada = selectedJornada,
            onJornadaSelected = { newJornada -> selectedJornada = newJornada }
        )

        // 2. 列表
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            items(filteredMatches) { match ->
                val localTeam = DataProvider.getTeamById(match.localTeamId)
                val visitorTeam = DataProvider.getTeamById(match.visitorTeamId)

                if (localTeam != null && visitorTeam != null) {
                    MatchItem(
                        match = match,
                        teamLocal = localTeam,
                        teamVisitor = visitorTeam,
                        onClick = {
                            selectedMatchForEdit = match
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    // 弹窗逻辑
    if (showDialog && selectedMatchForEdit != null) {
        EditMatchDialog(
            match = selectedMatchForEdit!!,
            onDismiss = { showDialog = false },
            onConfirm = { localGoals, visitorGoals ->
                // 更新列表数据 (创建新列表以触发重组)
                matches = matches.map {
                    if (it.id == selectedMatchForEdit!!.id) {
                        it.copy(localScore = localGoals, visitorScore = visitorGoals)
                    } else {
                        it
                    }
                }.toMutableList()
                showDialog = false
            }
        )
    }
}

// --- 子组件: 单行比赛 ---
@Composable
fun MatchItem(match: Match, teamLocal: Team, teamVisitor: Team, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 主队
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = teamLocal.logoResId),
                    contentDescription = teamLocal.name,
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Fit
                )
                Text(text = teamLocal.name, fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 1)
            }
            // 比分/时间
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1.2f)) {
                if (match.localScore != null && match.visitorScore != null) {
                    Text(text = "${match.localScore} - ${match.visitorScore}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Finalizado", fontSize = 10.sp)
                } else {
                    Text(text = "vs", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = formatMatchDate(match.matchDate), fontSize = 10.sp)
                }
            }
            // 客队
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = teamVisitor.logoResId),
                    contentDescription = teamVisitor.name,
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Fit
                )
                Text(text = teamVisitor.name, fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 1)
            }
        }
    }
}

// --- 子组件: 编辑弹窗 ---
@Composable
fun EditMatchDialog(match: Match, onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    var localScoreStr by remember { mutableStateOf(match.localScore?.toString() ?: "") }
    var visitorScoreStr by remember { mutableStateOf(match.visitorScore?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Resultado") },
        text = {
            Column {
                Text("Introduce el resultado final:")
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = localScoreStr, onValueChange = { localScoreStr = it },
                        label = { Text("Local") }, modifier = Modifier.weight(1f), singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("-", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = visitorScoreStr, onValueChange = { visitorScoreStr = it },
                        label = { Text("Visitante") }, modifier = Modifier.weight(1f), singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val l = localScoreStr.toIntOrNull()
                val v = visitorScoreStr.toIntOrNull()
                if (l != null && v != null) onConfirm(l, v)
            }) { Text("GUARDAR") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR") }
        }
    )
}

// --- 子组件: 轮次选择器 ---
@Composable
fun JornadaSelector(currentJornada: Int, onJornadaSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val jornadas = (0..38).toList()

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(text = "JORNADA: $currentJornada ${if(currentJornada==0) "(TODAS)" else ""}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            jornadas.forEach { jornada ->
                DropdownMenuItem(
                    text = { Text("Jornada $jornada") },
                    onClick = {
                        onJornadaSelected(jornada)
                        expanded = false
                    }
                )
            }
        }
    }
}