package com.example.storygeneration.data.remote

import android.util.Log
import com.example.storygeneration.data.model.ApiResponse
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // 连接超时60秒
        .readTimeout(60, TimeUnit.SECONDS)    // 读取超时60秒
        .writeTimeout(60, TimeUnit.SECONDS)   // 写入超时60秒
        .build()
    private val gson = Gson()
    private val BASE_URL = "http://120.26.74.235/api/"

    private val TAG = "ApiService"
    fun generateStory(prompt: String, style: String, callback: (Result<ApiResponse>) -> Unit) {
        val url = "${BASE_URL}story/generate"

        Log.d(TAG, "开始生成故事请求: prompt=$prompt, style=$style")

        // 创建请求体
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = """
            {
                "prompt": "$prompt",
                "style": "$style"
            }
        """.trimIndent().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "网络请求失败: ${e.message}")
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    Log.d(TAG, "收到响应: code=${response.code}")

                    if (!response.isSuccessful) {
                        Log.e(TAG, "请求失败: HTTP ${response.code}")
                        callback(Result.failure(IOException("Unexpected response code: ${response.code}")))
                        return
                    }

                    val responseBody = response.body?.string()
                    if (responseBody.isNullOrEmpty()) {
                        Log.e(TAG, "响应体为空")
                        callback(Result.failure(IOException("Empty response body")))
                        return
                    }

                    Log.d(TAG, "响应内容: $responseBody")

                    try {
                        val apiResponse = gson.fromJson(responseBody, ApiResponse::class.java)
                        Log.d(TAG, "解析成功: code=${apiResponse.code}, msg=${apiResponse.msg}")
                        callback(Result.success(apiResponse))
                    } catch (e: Exception) {
                        Log.e(TAG, "JSON解析失败: ${e.message}")
                        callback(Result.failure(e))
                    }
                }
            }
        })
    }
}