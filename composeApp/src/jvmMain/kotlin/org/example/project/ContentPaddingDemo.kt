package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ContentPaddingDemo() {
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

    Text(
        text = "Visible row range: $visibleRangeText",
        modifier = Modifier.padding(horizontal = 12.dp),
    )

    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch { listState.scrollToItem(5) }
        },
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text("To row index 5")
    }

    val verticalContentPadding = 5

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height((10 * ItemHeight + verticalContentPadding).dp)
            .background(Color(0x1AFF0000)),
        state = listState,
        contentPadding = PaddingValues(vertical = verticalContentPadding.dp)
    ) {
        items(20) { index ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ItemHeight.dp)
                    .background(if (index % 2 == 0) Color.LightGray else Color.Gray),
                text = "Item: $index"
            )
        }
    }
}