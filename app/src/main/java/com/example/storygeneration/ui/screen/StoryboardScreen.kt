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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SceneItem(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val thumbnailUrl: String = ""
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun StoryboardScreen(navController: NavController) {
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
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(scenes) { scene ->

                Box(modifier = Modifier.clickable { navController.navigate("shotDetail/${scene.id}") }) {
                    StoryboardCard(scene, navController)
                }
            }
        }

        Button(
            onClick = {
                setGenerating(true)

                MainScope().launch {
                    delay(3000)
                    val updatedScenes = scenes.map { it.copy(status = "Completed") }
                    setScenes(updatedScenes)
                    setGenerating(false)
                    navController.navigate("preview")
                }
            }, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            enabled = !generating
        ) {
            Text(if (generating) "Generating..." else "Generate Video")
        }
    }
}


@Composable
fun StoryboardCard(scene: SceneItem, navController: NavController) {

    Card(
        modifier = Modifier.size(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            AsyncImage(
                model = scene.thumbnailUrl,
                contentDescription = scene.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            val statusColor = when (scene.status) {
                "Generated" -> Color(0xFF4CAF50)
                "Not Generated" -> Color(0xFF9E9E9E)
                "Completed" -> Color(0xFF2196F3)
                else -> Color(0xFF9E9E9E)
            }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(statusColor, shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    scene.status, style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }

            Text(
                scene.title, style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )

            Text(
                scene.description, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )

            Button(
                onClick = { navController.navigate("shotDetail/${scene.id}") },
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
    StoryboardScreen(navController = navController)
}