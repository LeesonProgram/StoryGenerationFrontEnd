package com.example.storygeneration.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.storygeneration.data.model.Shot
import com.example.storygeneration.data.model.Style
import com.example.storygeneration.viewmodel.StoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun CreateScreen(navController: NavController, viewModel: StoryViewModel = viewModel()) {
    val (title, setTitle) = remember { mutableStateOf("") }
    val (content, setContent) = remember { mutableStateOf("") }
    val (style, setStyle) = remember { mutableStateOf<Style>(Style.Movie) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Create",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = content,
            onValueChange = setContent,
            placeholder = { Text("Write your story...") },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
            singleLine = false
        )

        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val styleOptions = listOf(Style.Movie, Style.Animation, Style.Realistic)
            styleOptions.forEach { styleOption ->
                Button(
                    onClick = { setStyle(styleOption) },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (style == styleOption) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        }
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        styleOption.name,
                        color = if (style == styleOption) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        softWrap = false
                    )
                }
            }
        }

        Button(
            onClick = {
                // 使用 OkHttp 发送 POST 请求到后端，携带 JSON 请求体 { prompt, style }
                viewModel.isLoading.value = true

                MainScope().launch {
                    try {
                        val client = OkHttpClient()

                        val jsonBody = JSONObject().apply {
                            put("prompt", content)
                            put("style", style.name)
                        }.toString()

                        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                        val body = jsonBody.toRequestBody(mediaType)

                        val request = Request.Builder()
                            .url("http://120.26.74.235/api/story/generate")
                            .post(body)
                            .build()

                        val response = withContext(Dispatchers.IO) {
                            client.newCall(request).execute()
                        }

                        if (response.isSuccessful) {
                            val respStr = response.body?.string()
                            if (!respStr.isNullOrEmpty()) {
                                val jo = JSONObject(respStr)
                                val code = jo.optInt("code", -1)
                                if (code == 200) {
                                    val data = jo.optJSONObject("data")
                                    val shotList = mutableListOf<Shot>()
                                    if (data != null) {
                                        val arr = data.optJSONArray("shotList")
                                        if (arr != null) {
                                            for (i in 0 until arr.length()) {
                                                val item = arr.optJSONObject(i)
                                                if (item != null) {
                                                    val shot = Shot(
                                                        scence_title = item.optString("scence_title"),
                                                        prompt = item.optString("prompt"),
                                                        narration = item.optString("narration"),
                                                        bgm_suggestion = item.optString("bgm_suggestion")
                                                    )
                                                    shotList.add(shot)
                                                }
                                            }
                                        }
                                    }

                                    // 更新 ViewModel 的场景数据（在主线程）
                                    withContext(Dispatchers.Main) {
                                        viewModel.scenes.clear()
                                        viewModel.scenes.addAll(shotList)
                                        viewModel.isLoading.value = false
                                        navController.navigate("storyboard")
                                    }
                                } else {
                                    Log.e("CreateScreen", "server returned code=$code")
                                    withContext(Dispatchers.Main) {
                                        viewModel.isLoading.value = false
                                    }
                                }
                            } else {
                                Log.e("CreateScreen", "empty response body")
                                withContext(Dispatchers.Main) {
                                    viewModel.isLoading.value = false
                                }
                            }
                        } else {
                            Log.e("CreateScreen", "request failed: ${response.code}")
                            withContext(Dispatchers.Main) {
                                viewModel.isLoading.value = false
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("CreateScreen", "network error", e)
                        viewModel.isLoading.value = false
                    }
                }
            },
            enabled = !viewModel.isLoading.value,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                if (viewModel.isLoading.value) "Generating..." else "Generate Storyboard",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            "The storyboard will open automatically after generation.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
            softWrap = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateScreenPreview() {
    val navController = rememberNavController()
    val viewModel = StoryViewModel()
    CreateScreen(navController = navController, viewModel = viewModel)
}