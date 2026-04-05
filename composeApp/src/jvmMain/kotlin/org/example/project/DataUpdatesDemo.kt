package org.example.project

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DataUpdatesDemo() {
    var nextId by remember { mutableIntStateOf(1000) }
    var rows by remember {
        mutableStateOf(List(18) { index ->
            Item(id = index, label = "Item $index", revision = 0)
        })
    }
    val listState = rememberLazyListState()

    fun newItem(label: String): Item {
        val item = Item(id = nextId, label = label, revision = 0)
        nextId++
        return item
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { rows = listOf(newItem("Inserted front")) + rows }) {
            Text("Insert front")
        }
        Button(onClick = {
            val middle = rows.size / 2
            rows = rows.toMutableList().apply { add(middle, newItem("Inserted middle")) }
        }) {
            Text("Insert middle")
        }
        Button(onClick = { rows = rows + newItem("Appended") }) {
            Text("Append")
        }
        Button(onClick = { if (rows.isNotEmpty()) rows = rows.drop(1) }) {
            Text("Remove first")
        }
        Button(onClick = {
            if (rows.isNotEmpty()) {
                val middle = rows.size / 2
                rows = rows.filterIndexed { index, _ -> index != middle }
            }
        }) {
            Text("Remove middle")
        }
        Button(onClick = { if (rows.isNotEmpty()) rows = rows.dropLast(1) }) {
            Text("Remove last")
        }
        Button(onClick = {
            rows = List(12) { index ->
                Item(id = 2000 + index, label = "Replacement $index", revision = 0)
            }
        }) {
            Text("Replace list")
        }
        Button(onClick = {
            val target = rows.size / 2
            rows = rows.mapIndexed { index, item ->
                if (index == target) {
                    item.copy(revision = item.revision + 1)
                } else {
                    item
                }
            }
        }) {
            Text("Update middle")
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(rows, key = { it.id }) { item ->
            Text(text = "${item.label} |  id=${item.id}, revision=${item.revision}")
        }
    }
}

private data class Item(
    val id: Int,
    val label: String,
    val revision: Int,
)