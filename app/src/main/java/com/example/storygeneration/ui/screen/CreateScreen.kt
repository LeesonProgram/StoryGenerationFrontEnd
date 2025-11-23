package com.example.storygeneration.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.storygeneration.data.model.Style

@Composable
fun CreateScreen(navController: NavController) {
    val (title, setTitle) = remember { mutableStateOf("") }
    val (content, setContent) = remember { mutableStateOf("") }
    val (style, setStyle) = remember { mutableStateOf<Style>(Style.Movie) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题
        Text(
            "Create",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(top = 16.dp)
        )

        // 内容输入框
        OutlinedTextField(
            value = content,
            onValueChange = setContent,
            placeholder = { Text("Write your story...") },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            singleLine = false,
            shape = MaterialTheme.shapes.medium
        )

        // 水平排列的样式选择按钮
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(Style.Movie, Style.Animation, Style.Realistic).forEach { styleOption ->
                Button(
                    modifier = Modifier
                        .weight(1f),
                    onClick = { setStyle(styleOption) },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (style == styleOption) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        }
                    )
                ) {
                    Text(
                        styleOption.name,
                        color = if (style == styleOption) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        },
                        modifier = Modifier.padding(8.dp),
                        maxLines = 2,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // 生成故事板按钮
        Button(
            onClick = { navController.navigate("storyboard") },
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Generate Storyboard", style = MaterialTheme.typography.bodyLarge)
        }

        // 底部提示文本
        Text(
            "The storyboard will open automatically after generation.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateScreenPreview() {
    val navController = rememberNavController()
    CreateScreen(navController = navController)
}
