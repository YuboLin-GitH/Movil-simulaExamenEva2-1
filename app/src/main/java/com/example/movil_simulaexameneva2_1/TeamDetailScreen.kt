package com.example.movil_simulaexameneva2_1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
// 如果你有添加 Coil 依赖，可以使用 AsyncImage
// import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(teamId: String?, navController: NavController) {
    // 1. 获取数据
    val team = teamId?.let { DataProvider.getTeamById(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(team?.name ?: "Detalle del Equipo") },
                navigationIcon = {
                    // 题目要求：返回按钮 [cite: 133]
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (team == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Equipo no encontrado")
            }
        } else {
            // 2. 页面布局
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // 允许滚动
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- 顶部背景 + 队徽 ---
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    // 背景图 (模拟球场图)
                    // 题目要求：从 URL 加载球场图片 [cite: 132]
                    // 注意：真实开发需使用 Coil: AsyncImage(model = team.stadiumImageUrl, ...)
                    // 这里用一个灰色方块或本地资源代替，防止报错
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray)
                    ) {
                        Text(
                            text = "Foto del Estadio: ${team.stadium}\n(URL: ${team.stadiumImageUrl})",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // 队徽 (叠加在背景下部) [cite: 127]
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .offset(y = 50.dp) // 让它突出来一点
                            .size(120.dp)
                    ) {
                        Image(
                            painter = painterResource(id = team.logoResId),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(60.dp)) // 为突出的队徽留位置

                // --- 球队名称 ---
                Text(
                    text = team.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- 详细信息卡片 [cite: 129, 130, 131] ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow(label = "Presidente", value = team.president)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color
                        )
                        DetailRow(label = "Año Fundación", value = team.foundationYear)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color
                        )
                        DetailRow(label = "Estadio", value = team.stadium)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color
                        )
                        // 这里只是模拟数据，数据类里没有 ligasGanadas 字段，你可以加上或硬编码演示
                        DetailRow(label = "Ligas Ganadas", value = "35 (Simulado)")
                    }
                }
            }
        }
    }
}

// --- 辅助组件：信息行 ---
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}