package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun BasicScrollToItemDemo(indexToScroll: Int) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

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

    Column {
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
        Button(
            onClick = {
                    scope.launch {  listState.scrollToItem(indexToScroll) }
            },
            modifier = Modifier
                .padding(horizontal = 12.dp)
        ) {
            Text("To row index $indexToScroll")
        }

        createScrollToItemList(listState)
    }
}

@Composable
fun createScrollToItemList(listState: LazyListState, tag: String? = null) {
    var modifier = Modifier
        .height((10 * ItemHeight).dp)
        .fillMaxWidth()
        .border(width = 1.dp, color = Color.Red)
        .background(Color(0x1AFF0000))
    if (tag != null) {
        modifier = modifier.testTag(tag)
    }
    LazyColumn(
        modifier = modifier,
        state = listState,
    ) {
        items(TotalRowsCount) { index ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ItemHeight.dp)
                    .background(if (index % 2 == 0) Color.LightGray else Color.Gray)
                    .border(width = 0.1.dp, color = Color.Gray),
                text = "Item: ${index}",
            )
        }
    }
}
