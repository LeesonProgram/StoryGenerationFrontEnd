package com.example.storygeneration.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.storygeneration.data.model.Transition
import kotlinx.coroutines.launch

fun String.capitalizeWords(): String {
    return this.split(' ')
        .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ShotDetailScreen(navController: NavController, shotId: String? = null) {

    val (prompt, setPrompt) = remember { mutableStateOf("A misty forest at dawn with tent") }

    val (selectedTransition, setSelectedTransition) = remember {
        mutableStateOf<Transition>(
            Transition.Ken_Burns
        )
    }
    val (narration, setNarration) = remember { mutableStateOf("") }
    val (status, setStatus) = remember { mutableStateOf("Generated") }
    val (dropdownExpanded, setDropdownExpanded) = remember { mutableStateOf(false) }
    val statusColor = when (status) {
        "Generated" -> Color(0xFF4CAF50)
        "Generating" -> Color(0xFFFFC107)
        else -> Color(0xFF9E9E9E)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    "Shot Details",
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier.statusBarsPadding()
        )

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4CAF50)) // 绿色背景占位
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(statusColor, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = status,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text(
            "Shot Description",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        OutlinedTextField(
            value = prompt, // 当前值
            onValueChange = setPrompt, // 值变化回调
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        )

        Text(
            "Video Transition",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { setDropdownExpanded(!dropdownExpanded) },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor() // 必须添加menuAnchor修饰符
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                readOnly = true,
                value = selectedTransition.name.replace('_', ' ').capitalizeWords(),
                onValueChange = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                },
            )

            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { setDropdownExpanded(false) },
                modifier = Modifier.fillMaxWidth()
            ) {

                Transition.entries.forEach { transitionOption ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                transitionOption.name.replace('_', ' ').capitalizeWords()
                            )
                        },
                        onClick = {
                            setSelectedTransition(transitionOption)
                            setDropdownExpanded(false)
                        }
                    )
                }
            }
        }

        Text(
            "Narration Text",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        OutlinedTextField(
            value = narration,
            onValueChange = setNarration,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        )

        Button(
            onClick = {
                setStatus("Generating")
                kotlinx.coroutines.MainScope().launch {
                    kotlinx.coroutines.delay(2000)
                    setStatus("Generated")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2)
            ),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Generate Image")
        }
    }
}

@Preview
@Composable
fun ShotDetailScreenPreview() {
    ShotDetailScreen(navController = rememberNavController())
}