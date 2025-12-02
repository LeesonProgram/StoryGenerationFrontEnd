package com.example.storygeneration.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.storygeneration.data.model.Shot
import com.example.storygeneration.viewmodel.StoryViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun StoryboardScreen(navController: NavController, viewModel: StoryViewModel = viewModel()) {
    // 使用ViewModel中的场景数据
    val scenes = viewModel.scenes

    val (generating, setGenerating) = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    "Storyboard", style = MaterialTheme.typography.headlineMedium
                )
            }, navigationIcon = {

                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back", modifier = Modifier.size(24.dp)
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), modifier = Modifier.statusBarsPadding() // 添加状态栏填充，避免与系统状态栏重叠
        )

        LazyRow(
            modifier = Modifier
                .padding(16.dp)
                .height(300.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(scenes) { shot ->
                Box(modifier = Modifier.clickable {
                    navController.navigate(
                        "shotDetail/${
                            scenes.indexOf(
                                shot
                            ) + 1
                        }"
                    )
                }) {
                    StoryboardCard(shot, scenes.indexOf(shot), navController)
                }
            }
        }

        Button(
            onClick = {
                // 更新场景状态为"Completed"
                viewModel.scenes.forEach { scene ->
                    // 在实际应用中，这里可能需要更新每个场景的状态
                }

                // 模拟视频生成过程
                MainScope().launch {
                    delay(3000)
                    navController.navigate("preview")
                }
            }, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Generate Video")
        }
    }
}


@Composable
fun StoryboardCard(shot: Shot, index: Int, navController: NavController) {

    Card(
        modifier = Modifier.size(250.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9)), // 设置绿色背景
            horizontalAlignment = Alignment.Start
        ) {
            // 使用绿色背景图代替占位图
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFF4CAF50)), // 深绿色背景
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "场景 ${index + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }

            // 因为Shot类中没有status字段，所以使用固定值
            val statusColor = Color(0xFF4CAF50) // 默认使用绿色表示已生成

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(statusColor, shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    "Generated", style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }

            Text(
                shot.scence_title, style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )

            Text(
                shot.narration, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )

            // 显示prompt信息
            Text(
                shot.prompt,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                maxLines = 2,
                softWrap = true
            )

            // 显示bgm_suggestion信息
            Text(
                "BGM: ${shot.bgm_suggestion}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = { navController.navigate("shotDetail/${index + 1}") },
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                contentPadding = ButtonDefaults.ContentPadding
            ) {
                Text("Details")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun StoryboardScreenPreview() {
    val navController = rememberNavController()
    // 创建一个带有模拟数据的ViewModel
    val viewModel = StoryViewModel()

    // 添加一些模拟的场景数据用于预览
    val mockScenes = listOf(
        Shot(
            scence_title = "小猫清晨醒来",
            prompt = "一只毛茸茸的小猫伸懒腰，从温暖的被窝中爬出来，阳光透过窗户洒在它身上。它好奇地四处张望，似乎在寻找早餐。",
            narration = "小猫睁开眼睛，金色的眼睛闪烁着好奇与活力。它轻轻地舔了舔嘴巴，似乎刚刚睡醒。",
            bgm_suggestion = "轻柔的钢琴曲"
        ),
        Shot(
            scence_title = "小猫探索花园",
            prompt = "小猫小心翼翼地走进花园，花朵的香气让它陶醉。蝴蝶在它周围飞舞，小猫好奇地追逐着。",
            narration = "阳光明媚的早晨，小猫第一次来到了美丽的花园。这里的一切对它来说都是那么新奇。",
            bgm_suggestion = "轻快的小提琴曲"
        )
    )

    // 直接填充模拟数据（在实际应用中，这些数据会通过API获取）
    viewModel.scenes.clear()
    viewModel.scenes.addAll(mockScenes)

    StoryboardScreen(navController = navController, viewModel = viewModel)
}