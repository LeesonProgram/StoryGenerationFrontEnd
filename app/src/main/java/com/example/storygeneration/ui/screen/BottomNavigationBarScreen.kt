package com.example.storygeneration.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBarScreen(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(initialPage = selectedItem) { 2 }
    val coroutineScope = rememberCoroutineScope()

    remember(pagerState) {
        coroutineScope.launch {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                selectedItem = page
            }
        }
    }

    fun onBottomNavItemClicked(index: Int) {
        selectedItem = index
        coroutineScope.launch {
            pagerState.animateScrollToPage(index)
        }
    }

    val items = listOf(
        BottomNavItem("Create", Icons.Default.Add),
        BottomNavItem("Assets", Icons.Default.Collections)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = { onBottomNavItemClicked(index) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedItem) {
                items.forEachIndexed { index, item ->
                    Tab(
                        selected = selectedItem == index,
                        onClick = { onBottomNavItemClicked(index) },
                        text = { Text(item.title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
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