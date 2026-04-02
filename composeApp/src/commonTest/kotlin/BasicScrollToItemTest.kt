import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import org.example.project.TotalRowsCount
import org.example.project.createScrollToItemList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
        var seenLazyIndexes: HashSet<Int>? = null

        setContent {
            listState = rememberLazyListState()

            LaunchedEffect(listState) {
                seenLazyIndexes = hashSetOf()

                snapshotFlow {
                    listState.isScrollInProgress to listState.layoutInfo.visibleItemsInfo.map { it.index}
                }.collect { (isScrolling, visibleIndexes) ->
                    if (!isScrolling) {
                        visibleIndexes.forEach { index ->
                            seenLazyIndexes.add(index)
                        }
                    }
                }
            }

            createScrollToItemList(listState, tagOfList)
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithTag(tagOfList).assertExists()
        assertNotNull(listState)
        assertNotNull(seenLazyIndexes)

        assertEquals(0, listState.firstVisibleItemIndex)
        assertEquals(10, seenLazyIndexes.size)

        listState.scrollToItem(indexToScroll)

        assertEquals(expectedFirstVisibleItem, listState.firstVisibleItemIndex)
        waitForIdle() // we should wait until LaunchedEffect will be applied
        assertEquals(20, seenLazyIndexes.size)
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun scrollBackWardToTopItemTest() = runComposeUiTest {
            var listState: LazyListState? = null
            var seenLazyIndexes: HashSet<Int>? = null

            setContent {
                listState = rememberLazyListState()

                LaunchedEffect(listState) {
                    seenLazyIndexes = hashSetOf()

                    snapshotFlow {
                        listState.isScrollInProgress to listState.layoutInfo.visibleItemsInfo.map { it.index }
                    }.collect { (isScrolling, visibleIndexes) ->
                        if (!isScrolling) {
                            visibleIndexes.forEach { index ->
                                seenLazyIndexes.add(index)
                            }
                        }
                    }
                }

                createScrollToItemList(listState, tagOfList)
            }

            // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
            onNodeWithTag(tagOfList).assertExists()
            assertNotNull(listState)
            assertNotNull(seenLazyIndexes)

            assertEquals(0, listState.firstVisibleItemIndex)
            assertEquals(10, seenLazyIndexes.size)

            listState.scrollToItem(TotalRowsCount)

            assertEquals(TotalRowsCount - 10, listState.firstVisibleItemIndex)
            waitForIdle() // we should wait until LaunchedEffect will be applied
            assertEquals(20, seenLazyIndexes.size)

            listState.scrollToItem(0)
            assertEquals(0, listState.firstVisibleItemIndex)
            waitForIdle() // we should wait until LaunchedEffect will be applied
            assertEquals(20, seenLazyIndexes.size)
    }
}