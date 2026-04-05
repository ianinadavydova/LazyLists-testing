import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import org.example.project.BasicRenderingTotalRowsCount
import org.example.project.CallCounter
import org.example.project.createBasicRenderingList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BasicTest {

    private val tagOfList = "TagOfList"

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun basicRenderingTest() = runComposeUiTest {
        var listState: LazyListState? = null
        val callCounter = CallCounter()
        // Declares a mock UI to demonstrate API calls
        //
        // Replace with your own declarations to test the code of your project
        setContent {
            listState = rememberLazyListState()

            createBasicRenderingList(listState, callCounter, tagOfList)
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        val state = assertNotNull(listState)

        assertEquals(0, state.firstVisibleItemIndex)
        assertEquals(BasicRenderingTotalRowsCount / 2, state.layoutInfo.visibleItemsInfo.size)
        assertEquals(callCounter.value, BasicRenderingTotalRowsCount / 2)
        assertEquals(
            (0 until BasicRenderingTotalRowsCount / 2).toList(),
            state.layoutInfo.visibleItemsInfo.map { it.index }
        )

        for (index in 0 until BasicRenderingTotalRowsCount / 2) {
            onNodeWithText("Item: $index").assertExists()
            onNodeWithText("Item: $index").assertIsDisplayed()
        }
        for (index in BasicRenderingTotalRowsCount / 2 until BasicRenderingTotalRowsCount) {
            onNodeWithText("Item: $index").assertDoesNotExist()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun basicRowRenderingTest() = runComposeUiTest {
        val itemWidth = 50
        val visibleItemsCount = 10
        var listState: LazyListState? = null
        val callCounter = CallCounter()
        // Declares a mock UI to demonstrate API calls
        //
        // Replace with your own declarations to test the code of your project
        setContent {
            listState = rememberLazyListState()

            LazyRow(
                modifier = Modifier
                    .fillMaxHeight()
                    .width((visibleItemsCount * itemWidth).dp)
                    .testTag(tagOfList),
                state = listState
            ) {
                items(2 * visibleItemsCount) { index ->
                    ++callCounter.value
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(itemWidth.dp),
                        text = "Item: $index"
                    )
                }
            }
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        val state = assertNotNull(listState)

        assertEquals(0, state.firstVisibleItemIndex)
        assertEquals(visibleItemsCount, state.layoutInfo.visibleItemsInfo.size)
        assertEquals(visibleItemsCount, callCounter.value)
        assertEquals(
            (0 until visibleItemsCount).toList(),
            state.layoutInfo.visibleItemsInfo.map { it.index }
        )

        for (index in 0 until visibleItemsCount) {
            onNodeWithText("Item: $index").assertExists()
            onNodeWithText("Item: $index").assertIsDisplayed()
        }
        for (index in visibleItemsCount until 2 * visibleItemsCount) {
            onNodeWithText("Item: $index").assertDoesNotExist()
        }
    }
}