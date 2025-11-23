package com.example.storygeneration.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBarScreen(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(initialPage = selectedItem) { 2 }
    val coroutineScope = rememberCoroutineScope()

    // 监听页面变化并更新选中的底部导航项
    remember(pagerState) {
        coroutineScope.launch {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                selectedItem = page
            }
        }
    }

    // 当底部导航项被点击时，切换到相应的页面
    fun onBottomNavItemClicked(index: Int) {
        selectedItem = index
        coroutineScope.launch {
            pagerState.animateScrollToPage(index)
        }
    }

    // 定义底部导航项
    val items = listOf(
        BottomNavItem("Create", Icons.Default.Add),
        BottomNavItem("Assets", Icons.Default.Collections)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(56.dp)
                    .clip(MaterialTheme.shapes.large)
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = { onBottomNavItemClicked(index) })
                }
            }
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState, modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> CreateScreen(navController)
                    1 -> AssetsScreen(navController)
                }
            }
        }
    }
}

data class BottomNavItem(val title: String, val icon: ImageVector)

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarScreenPreview() {
    val navController = rememberNavController()
    BottomNavigationBarScreen(navController = navController)
}
