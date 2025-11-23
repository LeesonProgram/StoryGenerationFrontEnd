package com.example.storygeneration.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun AssetsScreen(navController: NavController) {
    val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }
    // 更新模拟数据以匹配UI参考图
    val mockStories = listOf(
        StoryAsset("1", "Camping Adventure", "Apr 21, 2024", "camping.jpg"),
        StoryAsset("2", "Sunset at the Summit", "Apr 21, 2024", "sunset.jpg")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部标题
        Text(
            "StoryFlow",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 搜索框
        OutlinedTextField(
            value = searchQuery,
            onValueChange = setSearchQuery,
            placeholder = { Text("Search your stories...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.colors(),
            shape = MaterialTheme.shapes.medium
        )

        // 使用LazyColumn实现单列列表
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mockStories) { story ->
                AssetCard(story, navController)
            }
        }
    }
}

@Composable
fun AssetCard(story: StoryAsset, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // 点击卡片跳转到预览页（极简模式）
                navController.navigate("preview")
            },
        shape = MaterialTheme.shapes.medium
    )
    {
        Column(modifier = Modifier.padding(12.dp)) {
            // 图片占位区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 这里应该是实际的图片，使用占位符代替
                Text("Image Placeholder")
            }

            // 标题和时间
            Text(
                story.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                story.time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

data class StoryAsset(
    val id: String,
    val title: String,
    val time: String,
    val imageUrl: String
)

@Preview(showBackground = true)
@Composable
fun AssetsScreenPreview() {
    // 在预览中我们不需要实际的导航控制器
    val navController = rememberNavController()
    AssetsScreen(navController = navController)
}
