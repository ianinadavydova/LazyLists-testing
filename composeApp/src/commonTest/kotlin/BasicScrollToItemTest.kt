import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import org.example.project.CallCounter
import org.example.project.TotalRowsCount
import org.example.project.createScrollToItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BasicScrollToItemTest {

    private val tagOfList = "TagOfList";
    private val visibleRowCount = 10

    @Test
    fun scrollToItemInTheMiddleTest() {
        scrollToItemTest(100, 100)
    }

    @Test
    fun scrollToLastItemTest() {
        scrollToItemTest(TotalRowsCount - 1, TotalRowsCount - visibleRowCount)
    }

    @OptIn(ExperimentalTestApi::class)
    private fun scrollToItemTest(indexToScroll: Int, expectedFirstVisibleItem: Int) = runComposeUiTest {
        var listState: LazyListState? = null
        val callCounter = CallCounter()

        setContent {
            listState = rememberLazyListState()
            createScrollToItemList(listState, callCounter, tagOfList)
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        val state = assertNotNull(listState)

        assertVisibleWindow(0, state)

        state.scrollToItem(indexToScroll)

        assertVisibleWindow(expectedFirstVisibleItem, state)
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun scrollBackwardToTopItemTest() = runComposeUiTest {
        var listState: LazyListState? = null
        val callCounter = CallCounter()

        setContent {
            listState = rememberLazyListState()
            createScrollToItemList(listState, callCounter, tagOfList)
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        val state = assertNotNull(listState)

        assertVisibleWindow(0, state)
        assertTrue(state.canScrollForward)
        assertFalse(state.canScrollBackward)

        state.scrollToItem(TotalRowsCount - 1)

        assertVisibleWindow(TotalRowsCount - visibleRowCount, state)
        assertFalse(state.canScrollForward)
        assertTrue(state.canScrollBackward)

        state.scrollToItem(0)

        assertVisibleWindow(0, state)
        assertTrue(state.canScrollForward)
        assertFalse(state.canScrollBackward)
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
            createScrollToItemList(listState, callCounter, tagOfList)
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        val state = assertNotNull(listState)
        val scope = assertNotNull(scrollScope)

        assertVisibleWindow(0, state)

        mainClock.autoAdvance = false
        runOnIdle {
            scope.launch {
                state.animateScrollToItem(TotalRowsCount - 1)
            }
        }

        mainClock.advanceTimeUntil(5_000) {
            state.firstVisibleItemIndex == TotalRowsCount - visibleRowCount
        }
        mainClock.advanceTimeUntil(5_000) {
            !state.isScrollInProgress
        }
        waitForIdle()

        assertVisibleWindow(TotalRowsCount - visibleRowCount, state)
        assertFalse(state.isScrollInProgress)
        assertTrue(
            callCounter.value in 185..205,
            "Create rows call count out of range: ${callCounter.value}"
        )
        //assertTrue(callCounter.value in 180..181)
    }

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.assertVisibleWindow(firstVisibleIndex: Int, listState: LazyListState) {
        assertEquals(firstVisibleIndex, listState.firstVisibleItemIndex)
        assertEquals(visibleRowCount, listState.layoutInfo.visibleItemsInfo.size)
        assertEquals(
            (firstVisibleIndex until firstVisibleIndex + visibleRowCount).toList(),
            listState.layoutInfo.visibleItemsInfo.map { it.index }
        )

        for (index in firstVisibleIndex until firstVisibleIndex + visibleRowCount) {
            onNodeWithText("Item: $index").assertExists()
        }
    }
}
