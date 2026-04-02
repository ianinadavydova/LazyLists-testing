package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun BasicRenderingDemo() {
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
                "Item ${visibleRowIndices.first()}..Item ${visibleRowIndices.last()}"
            }
        }
    }

    val createItemCallsCount = remember({ CallCounter() })

    Text(
        text = "Visible row count: ${visibleRowIndices.size}",
        modifier = Modifier.padding(horizontal = 12.dp),
    )

    Text(
        text = "Visible row range: $visibleRangeText",
        modifier = Modifier.padding(horizontal = 12.dp),
    )

    Text(
        text = "Create row calls count: ${createItemCallsCount.value}",
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    )

    createBasicRenderingList(listState, createItemCallsCount)
}

const val BasicRenderingTotalRowsCount = 20

@Composable
fun createBasicRenderingList(listState: LazyListState, callCounter: CallCounter, tag: String? = null) {
    var modifier = Modifier
        .fillMaxWidth()
        .height((BasicRenderingTotalRowsCount / 2 * ItemHeight).dp)
        .border(width = 1.dp, color = Color.Red)
        .background(Color(0x1AFF0000))

    if (tag != null) {
        modifier = modifier.testTag(tag)
    }

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(BasicRenderingTotalRowsCount) { index ->
            ++callCounter.value
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ItemHeight.dp)
                    .background(if (index % 2 == 0) Color.LightGray else Color.Gray)
                    .border(width = 0.1.dp, color = Color.Gray),
                text = "Item: ${index}"
            )
        }
    }
}