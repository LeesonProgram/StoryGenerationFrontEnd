// 预览界面 - 用于展示和播放生成的视频
package com.example.storygeneration.ui.screen

// 导入必要的Android和Jetpack Compose库
// 导入ExoPlayer相关库，用于视频播放
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

// 使用不稳定的API注解，因为ExoPlayer的一些API可能还在变化中
@OptIn(UnstableApi::class)
// 预览界面的主要可组合函数
@Composable
fun PreviewScreen(navController: NavController) {
    // 获取当前上下文
    val context = LocalContext.current
    // 定义播放状态变量
    val (isPlaying, setIsPlaying) = remember { mutableStateOf(false) }
    // 定义当前播放时间变量
    val (currentTime, setCurrentTime) = remember { mutableStateOf(0L) }
    // 定义视频总时长变量
    val (duration, setDuration) = remember { mutableStateOf(0L) }
    // 定义音量控制变量
    var volume by remember { mutableFloatStateOf(1.0f) }
    // 定义ExoPlayer实例变量
    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    // 定义导出状态变量
    var isExporting by remember { mutableStateOf(false) }
    // 定义导出状态消息变量
    var exportStatus by remember { mutableStateOf<String?>(null) }
    // 定义Snackbar宿主状态，用于显示消息
    val snackbarHostState = remember { SnackbarHostState() }
    // 定义协程作用域
    val coroutineScope = rememberCoroutineScope()
    // 获取当前生命周期所有者
    val lifecycleOwner = LocalLifecycleOwner.current

    // 导出视频功能函数
    fun exportVideo() {
        // 在IO线程中启动协程执行导出操作
        coroutineScope.launch(Dispatchers.IO) {
            // 设置导出状态为正在进行
            isExporting = true
            exportStatus = "正在导出视频..."

            try {
                // 示例视频URL - 实际应用中应替换为生成的视频路径
                val videoUri =
                    Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                // 生成视频文件名
                val videoTitle = "story_generation_video_${System.currentTimeMillis()}.mp4"
                // 视频描述
                val videoDescription = "生成的故事视频"

                // 创建媒体存储的内容值
                val contentValues = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, videoTitle)
                    put(MediaStore.Video.Media.DESCRIPTION, videoDescription)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/StoryGeneration")
                    // 标记为待处理状态
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }

                // 获取内容解析器并插入视频条目
                val contentResolver = context.contentResolver
                val uri = contentResolver.insert(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues
                ) ?: throw IOException("创建媒体存储条目失败")

                // 下载并保存视频
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val inputStream = URL(videoUri.toString()).openStream()
                    inputStream.copyTo(outputStream)
                } ?: throw IOException("打开输出流失败")

                // 更新媒体存储使视频可用
                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)

                // 显示成功消息
                withContext(Dispatchers.Main) {
                    exportStatus = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "视频导出成功", duration = SnackbarDuration.Short
                        )
                    }
                }
            } catch (e: Exception) {
                // 处理导出失败的情况
                withContext(Dispatchers.Main) {
                    exportStatus = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "导出失败: ${e.message}", duration = SnackbarDuration.Short
                        )
                    }
                }
            } finally {
                // 最终重置导出状态
                isExporting = false
            }
        }
    }

    // 权限处理 - 请求存储权限以导出视频
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = { permissions ->
            // 检查是否所有权限都被授予
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                // 如果权限被授予，则执行导出视频操作
                exportVideo()
            } else {
                // 如果权限被拒绝，显示提示消息
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "需要存储权限才能导出视频", duration = SnackbarDuration.Short
                    )
                }
            }
        })

    // 初始化ExoPlayer
    DisposableEffect(Unit) {
        // 创建ExoPlayer实例
        val exoPlayer = ExoPlayer.Builder(context).build()
        player = exoPlayer

        // 示例视频URL - 实际应用中应替换为生成的视频路径
        val mediaItem =
            MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        // 设置媒体项并准备播放
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()

        // 设置播放器监听器以跟踪状态变化（可选，用于外部使用）
        val listener = object : Player.Listener {
            // 当播放准备状态改变时调用
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                setIsPlaying(playWhenReady)
            }

            // 当播放位置发生不连续变化时调用
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int
            ) {
                setCurrentTime(exoPlayer.currentPosition)
            }

            // 当媒体项转换时调用
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                setDuration(exoPlayer.duration)
            }

            // 当播放状态改变时调用
            override fun onPlaybackStateChanged(playbackState: Int) {
                // 当播放器准备好时设置视频时长
                if (playbackState == Player.STATE_READY) {
                    setDuration(exoPlayer.duration)
                }
            }
        }

        // 添加监听器到播放器
        exoPlayer.addListener(listener)

        // 生命周期观察者，用于在屏幕暂停/恢复时控制播放器
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    // 屏幕暂停时暂停播放
                    exoPlayer.playWhenReady = false
                }

                Lifecycle.Event.ON_RESUME -> {
                    // 屏幕恢复时根据之前状态决定是否播放
                    exoPlayer.playWhenReady = isPlaying
                }

                Lifecycle.Event.ON_DESTROY -> {
                    // 屏幕销毁时释放播放器资源
                    exoPlayer.release()
                    player = null
                }

                else -> { /* 其他事件不做处理 */
                }
            }
        }

        // 添加生命周期观察者
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        // 组件销毁时的清理工作
        onDispose {
            exoPlayer.removeListener(listener)
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            exoPlayer.release()
            player = null
        }
    }

    // 使用Scaffold布局，包含Snackbar显示区域
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        // 主内容列布局
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 返回按钮行
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("返回")
                }
            }

            // 预览标题
            Text(
                "预览", fontSize = 24.sp, modifier = Modifier.align(Alignment.Start)
            )

            // 使用AndroidView包装PlayerView实现视频播放
            // ExoPlayer原生控件已包含：播放/暂停按钮、进度条、时间显示、音量控制等功能
            val localPlayer = player
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = localPlayer
                        useController = true  // 启用原生控制器
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        )
                        // 可选：自定义控制器行为
                        controllerShowTimeoutMs = 3000  // 3秒后自动隐藏控制器
                        controllerAutoShow = true       // 自动显示控制器
                        // showBuffering设置需要通过方法而不是直接赋值
                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
                    .padding(vertical = 16.dp)
            )

            // 导出视频按钮
            Button(
                onClick = {
                    // 请求权限并导出视频
                    storagePermissionLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }, enabled = !isExporting, // 导出过程中禁用按钮
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isExporting) {
                    // 导出过程中显示加载指示器
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text("导出中...", modifier = Modifier.padding(start = 8.dp))
                } else {
                    Text("导出视频")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreenPreview() {
    val navController = rememberNavController()
    PreviewScreen(navController = navController)
}
