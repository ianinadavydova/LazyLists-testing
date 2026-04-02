package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val TotalRowsCount = 20
private const val ItemHeight = 20

@Composable
@Preview
fun App() {
    val listState = rememberLazyListState()

    val visibleRowIndices by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.map { it.index }
        }
    }

    val visibleRangeText by remember {
        derivedStateOf {
            if (visibleRowIndices.isEmpty()) {
                "none"
            } else {
                "Item ${visibleRowIndices.first() + 1}..Item ${visibleRowIndices.last() + 1}"
            }
        }
    }

    var settledSeenRowsCount by remember { mutableStateOf(0) }

    LaunchedEffect(listState) {
        val seenLazyIndexes = hashSetOf<Int>()

        snapshotFlow {
            listState.isScrollInProgress to listState.layoutInfo.visibleItemsInfo.map { it.index }
        }.collect { (isScrolling, visibleIndexes) ->
            if (!isScrolling) {
                visibleIndexes.forEach { index ->
                    seenLazyIndexes.add(index)
                }
                settledSeenRowsCount = seenLazyIndexes.size
            }
        }
    }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Visible row count: ${visibleRowIndices.size}",
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
                    .height((TotalRowsCount / 2 * ItemHeight).dp)
                    .border(width = 1.dp, color = Color.Red)
                    .background(Color(0x1AFF0000)),
                state = listState
            ) {
                items(TotalRowsCount) { index ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ItemHeight.dp)
                            .background(if (index % 2 == 0) Color.LightGray else Color.Gray)
                            .border(width = 0.1.dp, color = Color.Gray),
                        text = "Item: ${index + 1}"
                    )
                }
            }
        }
    }
}
