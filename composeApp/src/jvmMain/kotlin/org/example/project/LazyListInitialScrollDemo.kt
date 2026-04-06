package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private const val InitialScrollDemoItemCount = 200
private const val InitialScrollRow = 100
private const val InitialScrollItemHeight = 20

@Composable
fun LazyListInitialScrollDemo(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = InitialScrollRow,
    )

    val visibleRowIndices by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.map { it.index}
        }
    }

    val viewportHeight by remember {
        derivedStateOf {
            listState.layoutInfo.viewportEndOffset - listState.layoutInfo.viewportStartOffset
        }
    }

    val firstVisibleRow by remember {
        derivedStateOf { listState.firstVisibleItemIndex}
    }

    val visibleRangeText by remember {
        derivedStateOf {
            if (visibleRowIndices.isEmpty()) {
                "none"
            } else {
                "Item ${visibleRowIndices.first()}..Item ${visibleRowIndices.last()}"
            }
        }
    }

    var settledSeenRowsCount by remember { mutableStateOf(0) }

    LaunchedEffect(listState) {
        val seenLazyIndexes = hashSetOf<Int>()

        snapshotFlow {
            listState.isScrollInProgress to listState.layoutInfo.visibleItemsInfo.map { it.index}
        }.collect { (isScrolling, visibleIndexes) ->
            if (!isScrolling) {
                visibleIndexes.forEach { index ->
                    seenLazyIndexes.add(index)
                }
                settledSeenRowsCount = seenLazyIndexes.size
            }
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Initial first visible row: $InitialScrollRow",
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        Text(
            text = "Visible row count: ${visibleRowIndices.size}",
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        Text(
            text = "LazyColumn viewport height: $viewportHeight",
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        Text(
            text = "First visible row: $firstVisibleRow",
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        Text(
            text = "Visible row range: $visibleRangeText",
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        Text(
            text = "Rows seen after completed scrolls: $settledSeenRowsCount",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.Red)
                .background(Color(0x1AFF0000)),
            state = listState,
        ) {
            items(InitialScrollDemoItemCount) { index ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(InitialScrollItemHeight.dp)
                        .background(if (index % 2 == 0) Color.LightGray else Color.Gray)
                        .border(width = 0.1.dp, color = Color.Gray),
                    text = "Item: ${index}",
                )
            }
        }
    }
}
