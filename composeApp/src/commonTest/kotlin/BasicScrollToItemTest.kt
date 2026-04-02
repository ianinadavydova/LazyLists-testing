import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import org.example.project.CallCounter
import org.example.project.TotalRowsCount
import org.example.project.createScrollToItemList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BasicScrollToItemTest {

    private val tagOfList = "TagOfList";

    @Test
    fun scrollToItemInTheMiddleTest() {
        scrollToItemTest(100, 100)
    }

    @Test
    fun scrollToLastItemTest() {
        scrollToItemTest(TotalRowsCount, TotalRowsCount - 10)
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
        assertNotNull(listState)

        assertEquals(0, listState.firstVisibleItemIndex)
        assertEquals(10, callCounter.value)

        listState.scrollToItem(indexToScroll)

        assertEquals(expectedFirstVisibleItem, listState.firstVisibleItemIndex)
        assertEquals(20, callCounter.value)
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
            assertNotNull(listState)

            assertEquals(0, listState.firstVisibleItemIndex)
            assertEquals(10, callCounter.value)

            listState.scrollToItem(TotalRowsCount)

            assertEquals(TotalRowsCount - 10, listState.firstVisibleItemIndex)
            assertEquals(20, callCounter.value)

            listState.scrollToItem(0)
            assertEquals(0, listState.firstVisibleItemIndex)
            assertEquals(30, callCounter.value)
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun animatedScrollToLastItemTest() = runComposeUiTest {
        var listState: LazyListState? = null
        val callCounter = CallCounter()

        setContent {
            listState = rememberLazyListState()
            createScrollToItemList(listState, callCounter, tagOfList)
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        assertNotNull(listState)

        listState.animateScrollToItem(TotalRowsCount)
        waitUntil { listState.firstVisibleItemIndex == TotalRowsCount - 10}
        assertEquals(TotalRowsCount - 10, listState.firstVisibleItemIndex)
        assertTrue(callCounter.value in 195..200)
    }
}