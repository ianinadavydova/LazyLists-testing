package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val ZeroViewportSpacerHeight = 400.dp

@Composable
fun LazyListZeroViewportDemo(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()

    val visibleRowIndices by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.map { it.index }
        }
    }

    val viewportHeight by remember {
        derivedStateOf {
            listState.layoutInfo.viewportEndOffset - listState.layoutInfo.viewportStartOffset
        }
    }

    val visibleRowsText by remember {
        derivedStateOf {
            if (visibleRowIndices.isEmpty()) {
                "none"
            } else {
                visibleRowIndices.joinToString()
            }
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "viewportHeight=$viewportHeight",
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        Text(
            text = "visibleItemsInfo.size=${visibleRowIndices.size}",
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        Text(
            text = "visible=$visibleRowsText",
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(ZeroViewportSpacerHeight)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .border(width = 1.dp, color = Color.Red)
                .background(Color(0x1AFF0000)),
            state = listState,
        ) {
            items(TotalRowsCount) { index ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ItemHeight.dp)
                        .background(if (index % 2 == 0) Color.LightGray else Color.Gray)
                        .border(width = 0.1.dp, color = Color.Gray),
                    text = "Item: $index",
                )
            }
        }
    }
}
