package com.example.storygeneration.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.storygeneration.data.model.Shot
import com.example.storygeneration.data.remote.ApiService

class StoryViewModel : ViewModel() {
    private val apiService = ApiService()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val TAG = "StoryViewModel"

    var scenes = mutableStateListOf<Shot>()
        private set

    var isLoading = mutableStateOf(false)
        private set

    fun generateStory(
        prompt: String,
        style: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isLoading.value) return

        isLoading.value = true
        Log.d(TAG, "开始生成故事: prompt=$prompt, style=$style")

        apiService.generateStory(prompt, style) { result ->
            // 确保在主线程中更新UI状态
            mainHandler.post {
                result.fold(
                    onSuccess = { response ->
                        Log.d(TAG, "API响应: code=${response.code}, msg=${response.msg}")
                        if (response.code == 200 && response.data.shotList.isNotEmpty()) {
                            // 确保shotList不为空
                            Log.d(TAG, "成功生成 ${response.data.shotList.size} 个场景")
                            scenes.clear()
                            scenes.addAll(response.data.shotList)
                            isLoading.value = false
                            onSuccess()
                        } else {
                            val errorMsg = response.msg.ifEmpty { "未知错误" }
                            Log.e(TAG, "API返回错误: $errorMsg")
                            isLoading.value = false
                            onError(errorMsg)
                        }
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "生成故事失败: ${exception.message}")
                        isLoading.value = false
                        onError(exception.message ?: "未知错误")
                    }
                )
            }
        }
    }
}