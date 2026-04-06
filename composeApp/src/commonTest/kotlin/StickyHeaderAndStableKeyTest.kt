import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import org.example.project.ItemHeight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StickyHeaderAndStableKeyTest {
    private val tagOfList = "TagOfList"
    private val tagOfButton = "TagOfButton"
    private val stickyHeaderText = "Sticky Item"

    private val stickyHeaderKey = { stickyHeaderText }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stickyHeaderWithStableKeytest() = runComposeUiTest {
        var listState: LazyListState? = null
        // Declares a mock UI to demonstrate API calls
        //
        // Replace with your own declarations to test the code of your project
        setContent {
            listState = rememberLazyListState()
            var rows by remember {
                mutableStateOf(List(19) { Item(it) })
            }

            Column {
                Button(
                    onClick = {
                        rows = rows.drop(1)
                        println(rows)
                    },
                    modifier = Modifier.testTag(tagOfButton)
                ) {}
                LazyColumn(
                    modifier = Modifier
                        .height((10 * ItemHeight).dp)
                        .testTag(tagOfList),
                    state = listState
                ) {
                    stickyHeader(key = stickyHeaderKey) {
                        Text(
                            modifier = Modifier.height(ItemHeight.dp),
                            text = stickyHeaderText
                        )
                    }
                    items(
                        items = rows,
                        key = { it },
                    ) { item ->
                        Text(
                            modifier = Modifier.height(ItemHeight.dp),
                            text = item.getText()
                        )
                    }
                }
            }
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        val state = assertNotNull(listState)

        assertEquals(0, state.firstVisibleItemIndex)
        assertEquals(10, state.layoutInfo.visibleItemsInfo.size)
        assertEquals(
            (0 until 10).toList(),
            state.layoutInfo.visibleItemsInfo.map { it.index }
        )

        val stickyHeader = onNodeWithText(stickyHeaderText)
        stickyHeader.assertExists()
        stickyHeader.assertIsDisplayed()

        assertEquals(stickyHeaderKey, state.layoutInfo.visibleItemsInfo[0].key)

        val checkItemExistsAndIsVisible = { itemIndex: Int, expectedIndex: Int ->
            val item = state.layoutInfo.visibleItemsInfo[itemIndex].key as Item
            assertEquals(expectedIndex, item.index)
            val node = onNodeWithText(item.getText())
            node.assertExists()
            node.assertIsDisplayed()
        }

        for (index in 1 until 10) {
            checkItemExistsAndIsVisible(index, index - 1)
        }

        // remove the first non-sticky item
        onNodeWithTag(tagOfButton).performClick()

        stickyHeader.assertExists()
        stickyHeader.assertIsDisplayed()

        assertEquals(stickyHeaderKey, state.layoutInfo.visibleItemsInfo[0].key)

        onNodeWithText(Item(0).getText()).assertDoesNotExist()

        for (index in 1 until 10) {
            checkItemExistsAndIsVisible(index, index)
        }
    }

    private class Item(val index: Int) {
        fun getText() = "Item: $index"
    }
}