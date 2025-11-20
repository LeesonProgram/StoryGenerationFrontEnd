// 分镜详情界面 - 展示单个分镜的详细信息，包括图像、标题和描述
package com.example.storygeneration.ui.screen

// 导入必要的Android和Jetpack Compose库
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
// 导入数据模型
import com.example.storygeneration.data.model.Transition
import kotlinx.coroutines.launch

/**
 * 分镜详情界面可组合函数
 * @param navController 导航控制器，用于页面跳转
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ShotDetailScreen(navController: NavController) {
    // 状态变量：分镜描述 - 用于输入或显示分镜的详细描述
    val (prompt, setPrompt) = remember { mutableStateOf("A misty forest at dawn with tent") }
    // 状态变量：视频过渡效果 - 控制场景之间切换时的动画效果
    val (transition, setTransition) = remember { mutableStateOf(Transition.KEN_BURNS) }
    // 状态变量：旁白文本 - 用于输入或显示场景的旁白内容
    val (narration, setNarration) = remember { mutableStateOf("") }
    // 状态变量：生成状态 - 表示当前分镜的生成状态（如"Generated"已生成、"Generating"生成中）
    val (status, setStatus) = remember { mutableStateOf("Generated") }
    // 状态变量：下拉菜单展开状态 - 控制过渡效果选择下拉菜单的显示与隐藏
    val (dropdownExpanded, setDropdownExpanded) = remember { mutableStateOf(false) }

    // 根据状态设置颜色 - 不同状态使用不同颜色标识，便于用户识别
    // Generated(已生成): 绿色 #4CAF50
    // Generating(生成中): 黄色 #FFC107
    // 其他状态: 灰色 #9E9E9E
    val statusColor = when (status) {
        "Generated" -> Color(0xFF4CAF50)
        "Generating" -> Color(0xFFFFC107)
        else -> Color(0xFF9E9E9E)
    }

    // 主内容列布局 - 垂直排列所有界面元素
    Column(
        modifier = Modifier
            .fillMaxSize() // 占满整个屏幕
            .padding(16.dp) // 设置外边距
    ) {
        // 返回按钮和标题区域 - 使用Surface组件创建顶部应用栏效果
        Surface(
            color = TopAppBarDefaults.topAppBarColors().containerColor, // 使用Material Design默认的顶部应用栏背景色
            tonalElevation = 3.dp, // 设置表面高度，产生阴影效果
            modifier = Modifier.fillMaxWidth() // 占满宽度
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp) // 内边距
                    .fillMaxWidth(), // 占满宽度
                horizontalAlignment = Alignment.Start // 水平左对齐
            ) {
                // 返回按钮 - 点击返回上一级界面
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // 使用系统返回图标
                        contentDescription = "返回" // 无障碍访问描述
                    )
                }
                Spacer(modifier = Modifier.height(8.dp)) // 添加垂直间距
                // 标题文本 - 显示当前界面标题
                Text(
                    "分镜详情",
                    style = MaterialTheme.typography.headlineMedium // 使用Material Design headline样式
                )
            }
        }

        // 图像占位符 - 用于展示分镜图像的区域，目前使用绿色占位图
        Box(
            modifier = Modifier
                .padding(top = 16.dp) // 上方间距
                .fillMaxWidth() // 占满宽度
                .height(200.dp) // 固定高度200dp
                .clip(RoundedCornerShape(8.dp)) // 圆角矩形裁剪
                .background(Color(0xFF4CAF50)) // 绿色背景占位
        ) {
            // 状态标签 - 在图像区域右上角显示当前生成状态
            Box(
                modifier = Modifier
                    .padding(8.dp) // 外边距
                    .background(statusColor, shape = RoundedCornerShape(4.dp)) // 使用状态对应的颜色作为背景
                    .padding(horizontal = 12.dp, vertical = 4.dp) // 内边距
            ) {
                Text(
                    text = status, // 显示状态文本
                    color = Color.White, // 白色文字
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall // 小号字体样式
                )
            }
        }

        // 描述输入框 - 允许用户编辑分镜描述
        OutlinedTextField(
            value = prompt, // 当前值
            onValueChange = setPrompt, // 值变化回调
            label = { Text("分镜描述") }, // 输入框标签
            modifier = Modifier
                .padding(top = 16.dp) // 上方间距
                .fillMaxWidth() // 占满宽度
        )

        // 视频过渡效果下拉选择器 - 用于选择场景间的过渡动画效果
        Box(modifier = Modifier.padding(top = 16.dp)) {
            Text("视频过渡效果") // 标题文本
            OutlinedTextField(
                value = transition.name.replace('_', ' '), // 显示当前选中的过渡效果名称（下划线替换为空格）
                onValueChange = {}, // 禁止直接编辑
                readOnly = true, // 设置为只读模式
                modifier = Modifier
                    .padding(top = 8.dp) // 上方间距
                    .fillMaxWidth(), // 占满宽度
                trailingIcon = {
                    // 下拉箭头图标 - 点击可展开下拉菜单
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown, // 下拉箭头图标
                        contentDescription = "下拉箭头", // 无障碍访问描述
                        modifier = Modifier.clickable { setDropdownExpanded(!dropdownExpanded) }) // 点击切换菜单展开状态
                })

            // 下拉菜单 - 包含所有可用的过渡效果选项
            DropdownMenu(
                expanded = dropdownExpanded, // 展开状态
                onDismissRequest = { setDropdownExpanded(false) }, // 菜单关闭回调
                modifier = Modifier.fillMaxWidth() // 占满宽度
            ) {
                // 遍历所有过渡效果枚举值并创建菜单项
                Transition.entries.forEach { transitionOption ->
                    DropdownMenuItem(
                        text = { Text(transitionOption.name.replace('_', ' ')) }, // 显示过渡效果名称
                        onClick = {
                            setTransition(transitionOption) // 选择该项时更新状态
                            setDropdownExpanded(false) // 关闭菜单
                        })
                }
            }
        }

        // 旁白输入框 - 允许用户输入场景的旁白文本
        OutlinedTextField(
            value = narration, // 当前值
            onValueChange = setNarration, // 值变化回调
            label = { Text("旁白文本") }, // 输入框标签
            modifier = Modifier
                .padding(top = 16.dp) // 上方间距
                .fillMaxWidth() // 占满宽度
        )

        // 生成图像按钮 - 点击触发图像生成流程
        Button(
            onClick = {
                setStatus("Generating") // 设置状态为生成中
                // 模拟图像生成延迟 - 使用协程模拟耗时操作
                kotlinx.coroutines.MainScope().launch {
                    kotlinx.coroutines.delay(2000) // 延迟2秒模拟处理时间
                    setStatus("Generated") // 恢复状态为已生成
                }
            }, modifier = Modifier
                .padding(top = 16.dp) // 上方间距
                .fillMaxWidth() // 占满宽度
        ) {
            Text("生成图像") // 按钮文本
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShotDetailScreenPreview() {
    val navController = rememberNavController()
    ShotDetailScreen(navController = navController)
}