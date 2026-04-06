package org.example.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

const val InitialScrollRow = TotalRowsCount / 2

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

    val createItemCallsCount = remember { CallCounter() }
    var settledSeenRowsCount by remember { mutableStateOf(0) }

    LaunchedEffect(listState) {
        val seenLazyIndexes = hashSetOf<Int>()

        snapshotFlow {
            listState.isScrollInProgress to listState.layoutInfo.visibleItemsInfo.map { it.index }
        }.collect { (isScrolling, visibleIndexes) ->
            if (!isScrolling) {
                visibleIndexes.forEach(seenLazyIndexes::add)
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

        createScrollToItemList(listState, createItemCallsCount)
    }
}
