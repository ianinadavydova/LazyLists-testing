package org.example.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

private enum class DemoScreen {
    Basic,
    InitialScroll,
}

@Composable
@Preview
fun App() {
    var selectedDemo by remember { mutableStateOf(DemoScreen.Basic) }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            PrimaryTabRow(selectedTabIndex = selectedDemo.ordinal) {
                Tab(
                    selected = selectedDemo == DemoScreen.Basic,
                    onClick = { selectedDemo = DemoScreen.Basic },
                    text = { Text("Basic") },
                )
                Tab(
                    selected = selectedDemo == DemoScreen.InitialScroll,
                    onClick = { selectedDemo = DemoScreen.InitialScroll },
                    text = { Text("Initial Scroll") },
                )
            }

            when (selectedDemo) {
                DemoScreen.Basic -> {
                    BasicRenderingDemo()
                }
                DemoScreen.InitialScroll -> {
                    LazyListInitialScrollDemo(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
