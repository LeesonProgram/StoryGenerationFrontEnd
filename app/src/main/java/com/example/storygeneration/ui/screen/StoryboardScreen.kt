package com.example.storygeneration.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 场景项数据类 - 用于表示故事板中的单个场景
 * @param id 场景唯一标识符，用于区分不同场景
 * @param title 场景标题，简要描述场景内容
 * @param description 场景详细描述，提供更丰富的场景信息
 * @param status 场景状态，表示场景的生成状态（如Generated已生成, Not Generated未生成, Completed已完成）
 * @param thumbnailUrl 场景缩略图URL，用于显示场景预览图，默认为空字符串
 */
data class SceneItem(
    val id: String,          // 场景唯一标识符
    val title: String,       // 场景标题
    val description: String, // 场景描述
    val status: String,      // 场景状态（如Generated已生成, Not Generated未生成, Completed已完成）
    val thumbnailUrl: String = "" // 场景缩略图URL（可选）
)

/**
 * 故事板界面可组合函数 - 主界面，展示所有分镜场景卡片列表
 * @param navController 导航控制器，用于页面之间的跳转和导航
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun StoryboardScreen(navController: NavController) {
    // 状态变量：场景列表 - 使用remember和mutableStateOf创建可观察的场景列表状态
    // 初始包含2个示例场景，用于演示界面效果
    val (scenes, setScenes) = remember {
        mutableStateOf(
            listOf(
                SceneItem(
                    id = "1",
                    title = "营地晨雾...",
                    description = "清晨薄雾笼罩着山间营地",
                    status = "Generated",
                    thumbnailUrl = "https://via.placeholder.com/200"
                ), SceneItem(
                    id = "2",
                    title = "徒步者...",
                    description = "徒步者正在穿越湍急的河流",
                    status = "Not Generated",
                    thumbnailUrl = "https://via.placeholder.com/200"
                )
            )
        )
    }

    // 状态变量：生成状态 - 控制视频生成按钮的状态，防止重复点击
    // true表示正在生成，false表示未在生成
    val (generating, setGenerating) = remember { mutableStateOf(false) }

    // 主布局容器 - 使用Column垂直排列所有界面元素，填充整个屏幕
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部导航栏区域 - 使用Surface创建带阴影效果的顶部栏
        Surface(
            color = TopAppBarDefaults.topAppBarColors().containerColor, // 设置背景色
            tonalElevation = 3.dp, // 设置阴影高度
            modifier = Modifier.fillMaxWidth() // 填充整个宽度
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp) // 设置内边距
                    .fillMaxWidth(), // 填充整个宽度
                horizontalAlignment = Alignment.Start // 内容左对齐
            ) {
                // 返回按钮 - 点击后返回上一个页面
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // 返回箭头图标
                        contentDescription = "返回" // 无障碍访问描述
                    )
                }
                Spacer(modifier = Modifier.height(8.dp)) // 添加垂直间距
                // 页面标题 - 显示"故事板"标题
                Text(
                    "故事板", style = MaterialTheme.typography.headlineMedium // 使用大号标题样式
                )
            }
        }

        // 水平滚动场景卡片列表 - 使用LazyRow实现水平滚动的列表
        LazyRow(
            modifier = Modifier.padding(16.dp), // 设置外边距
            horizontalArrangement = Arrangement.spacedBy(16.dp) // 设置项目间的水平间距
        ) {
            // 遍历场景列表并为每个场景创建卡片组件
            items(scenes) { scene ->
                StoryboardCard(scene, navController)
            }
        }

        // 视频生成按钮 - 根据生成状态控制启用状态和显示文本
        Button(
            onClick = {
                // 设置生成状态为true，禁用按钮防止重复点击
                setGenerating(true)

                // 使用协程模拟视频生成过程（耗时操作）
                MainScope().launch {
                    delay(3000) // 模拟3秒的生成时间

                    // 更新所有场景状态为已完成
                    val updatedScenes = scenes.map { it.copy(status = "Completed") }
                    setScenes(updatedScenes)

                    // 重置生成状态为false，重新启用按钮
                    setGenerating(false)

                    // 跳转到预览页面
                    navController.navigate("preview")
                }
            }, modifier = Modifier
                .padding(16.dp) // 设置外边距
                .fillMaxWidth(), // 填充整个宽度
            enabled = !generating, // 根据生成状态控制按钮是否可用
            shape = MaterialTheme.shapes.large
        ) {
            // 根据生成状态显示不同文本："正在生成..."或"生成视频"
            Text(if (generating) "正在生成..." else "生成视频")
        }
    }
}

/**
 * 故事板卡片可组合函数 - 用于展示单个场景的信息
 * @param scene 场景项数据，包含要显示的场景信息
 * @param navController 导航控制器，用于页面跳转
 */
@Composable
fun StoryboardCard(scene: SceneItem, navController: NavController) {
    // 卡片组件 - 固定尺寸为200dp，使用阴影提升视觉层次感
    Card(
        modifier = Modifier.size(200.dp), // 设置固定大小
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // 设置默认阴影
        shape = MaterialTheme.shapes.medium
    ) {
        // 卡片内部垂直布局
        Column(
            modifier = Modifier.fillMaxSize(), // 填充整个卡片
            horizontalAlignment = Alignment.Start // 内容左对齐
        ) {
            // 缩略图图像 - 展示场景预览图
            AsyncImage(
                model = scene.thumbnailUrl, // 图像URL
                contentDescription = scene.title, // 无障碍访问描述
                contentScale = ContentScale.Crop, // 裁剪填充模式
                modifier = Modifier
                    .fillMaxWidth() // 填充整个宽度
                    .height(120.dp) // 固定高度为120dp
            )

            // 状态标签 - 根据不同状态显示不同颜色
            // 绿色表示已生成，灰色表示未生成，蓝色表示已完成
            val statusColor = when (scene.status) {
                "Generated" -> Color(0xFF4CAF50) // 绿色 - 已生成
                "Not Generated" -> Color(0xFF9E9E9E) // 灰色 - 未生成
                "Completed" -> Color(0xFF2196F3) // 蓝色 - 已完成
                else -> Color(0xFF9E9E9E) // 默认灰色
            }

            // 状态标签容器 - 使用圆角背景显示状态文本
            Box(
                modifier = Modifier
                    .padding(8.dp) // 外边距
                    .background(statusColor, shape = MaterialTheme.shapes.small) // 背景色和圆角形状
                    .padding(horizontal = 8.dp, vertical = 2.dp) // 内边距
            ) {
                // 状态文本 - 白色文字显示状态
                Text(
                    scene.status, style = MaterialTheme.typography.bodySmall, // 小号字体
                    color = Color.White // 白色文字
                )
            }

            // 场景标题 - 显示场景的简要标题
            Text(
                scene.title, style = MaterialTheme.typography.titleSmall, // 小号标题样式
                modifier = Modifier.padding(start = 8.dp, top = 4.dp) // 左侧和顶部内边距
            )

            // 场景描述 - 显示场景的详细描述
            Text(
                scene.description, style = MaterialTheme.typography.bodySmall, // 小号字体
                modifier = Modifier.padding(start = 8.dp, top = 4.dp) // 左侧和顶部内边距
            )

            // 详情按钮 - 点击跳转到镜头详情页
            Button(
                onClick = { navController.navigate("shotDetail/${scene.id}") }, // 导航到详情页并传递场景ID
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp) // 左侧和顶部外边距
                    .fillMaxWidth() // 填充整个宽度
                    .height(40.dp), // 固定高度
                shape = MaterialTheme.shapes.medium,
                contentPadding = ButtonDefaults.ContentPadding // 使用默认内边距
            ) {
                Text("详情") // 按钮文本
            }
        }
    }
}

/**
 * 故事板界面预览函数 - 用于在Android Studio中预览界面效果
 */
@Preview(showBackground = true) // 显示背景
@Composable
fun StoryboardScreenPreview() {
    // 创建导航控制器实例用于预览
    val navController = rememberNavController()
    // 调用故事板界面函数显示预览
    StoryboardScreen(navController = navController)
}
