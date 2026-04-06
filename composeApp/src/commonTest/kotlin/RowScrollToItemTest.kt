import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.CallCounter
import kotlin.test.*

class RowScrollToItemTest {

    private val tagOfList = "TagOfList"
    private val visibleItemsCount = 10
    private val totalItemsCount = 5000
    private val itemWidth = 50

    @Test
    fun scrollToItemInTheMiddleTest() {
        scrollToItemTest(100, 100)
    }

    @Test
    fun scrollToLastItemTest() {
        scrollToItemTest(totalItemsCount - 1, totalItemsCount - visibleItemsCount)
    }

    @Composable
    private fun createLazyRow(listState: LazyListState, callCounter: CallCounter) {
        LazyRow(
            modifier = Modifier
                .fillMaxHeight()
                .width((visibleItemsCount * itemWidth).dp)
                .testTag(tagOfList),
            state = listState,
        ) {
            items(totalItemsCount) { index ->
                ++callCounter.value
                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(itemWidth.dp),
                    text = "Item: $index",
                )
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    private fun scrollToItemTest(indexToScroll: Int, expectedFirstVisibleItem: Int) = runComposeUiTest {
        var listState: LazyListState? = null
        val callCounter = CallCounter()

        setContent {
            listState = rememberLazyListState()
            createLazyRow(listState, callCounter)
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        val state = assertNotNull(listState)

        assertVisibleWindow(0, state)
        assertEquals(visibleItemsCount, callCounter.value)

        state.scrollToItem(indexToScroll)

        assertVisibleWindow(expectedFirstVisibleItem, state)
        assertEquals(2 * visibleItemsCount, callCounter.value)
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun scrollBackwardToTopItemTest() = runComposeUiTest {
        var listState: LazyListState? = null
        val callCounter = CallCounter()

        setContent {
            listState = rememberLazyListState()
            createLazyRow(listState, callCounter)
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        val state = assertNotNull(listState)

        assertVisibleWindow(0, state)
        assertTrue(state.canScrollForward)
        assertFalse(state.canScrollBackward)
        assertEquals(visibleItemsCount, callCounter.value,
            "Expected $visibleItemsCount item creation counts, but was $callCounter.value")

        state.scrollToItem(totalItemsCount - 1)

        assertVisibleWindow(totalItemsCount - visibleItemsCount, state)
        assertFalse(state.canScrollForward)
        assertTrue(state.canScrollBackward)
        assertEquals(2 * visibleItemsCount, callCounter.value,
            "Expected ${2 * visibleItemsCount} item creation counts, but was $callCounter.value")


        state.scrollToItem(0)

        assertVisibleWindow(0, state)
        assertTrue(state.canScrollForward)
        assertFalse(state.canScrollBackward)
        assertEquals(3 * visibleItemsCount, callCounter.value,
            "Expected ${3 * visibleItemsCount}  item creation counts, but was $callCounter.value")
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun animatedScrollToLastItemTest() = runComposeUiTest {
        var listState: LazyListState? = null
        var scrollScope: CoroutineScope? = null
        val callCounter = CallCounter()

        setContent {
            scrollScope = rememberCoroutineScope()
            listState = rememberLazyListState()
            createLazyRow(listState, callCounter)
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        val state = assertNotNull(listState)
        val scope = assertNotNull(scrollScope)

        assertVisibleWindow(0, state)

        mainClock.autoAdvance = false
        runOnIdle {
            scope.launch {
                state.animateScrollToItem(totalItemsCount - 1)
            }
        }

        mainClock.advanceTimeUntil(5_000) {
            state.firstVisibleItemIndex == totalItemsCount - visibleItemsCount
        }
        mainClock.advanceTimeUntil(5_000) {
            !state.isScrollInProgress
        }
        waitForIdle()

        assertVisibleWindow(totalItemsCount - visibleItemsCount, state)
        assertFalse(state.isScrollInProgress)
        assertTrue(
            callCounter.value in 140..160,
            "Expected from 140 to 160 item creation counts, but was: ${callCounter.value}"
        )
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.assertVisibleWindow(firstVisibleIndex: Int, listState: LazyListState) {
        assertEquals(firstVisibleIndex, listState.firstVisibleItemIndex)
        assertEquals(visibleItemsCount, listState.layoutInfo.visibleItemsInfo.size)
        assertEquals(
            (firstVisibleIndex until firstVisibleIndex + visibleItemsCount).toList(),
            listState.layoutInfo.visibleItemsInfo.map { it.index }
        )

        for (index in firstVisibleIndex until firstVisibleIndex + visibleItemsCount) {
            onNodeWithText("Item: $index").assertExists()
        }
    }
}