package com.arturojas32.todoapp.ui.viewmodels

import com.arturojas32.todoapp.domain.repository.AuthRepository
import com.arturojas32.todoapp.domain.model.Task
import com.arturojas32.todoapp.domain.repository.TaskRepository
import com.arturojas32.todoapp.utils.SyncManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repo: TaskRepository
    private lateinit var authRepo: AuthRepository
    private lateinit var syncManager: SyncManager
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk(relaxed = true)
        authRepo = mockk(relaxed = true)
        syncManager = mockk(relaxed = true)

        every { authRepo.currentUser() } returns mockk { every { uId } returns "test_user" }
        every { repo.getAllTasks("test_user") } returns tasksFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTask(
        id: Int,
        title: String,
        desc: String? = null,
        isDone: Boolean = false
    ) = Task(
        id = id,
        uId = "test_user",
        title = title,
        desc = desc,
        isDone = isDone,
        createdDate = "01/01/2024",
        lastModified = 0L
    )

    private fun createViewModel() = TaskListViewModel(repo, authRepo, syncManager)

    // --- Init / Collection ---

    @Test
    fun `init collects tasks from repository`() {
        val tasks = listOf(createTask(1, "Task A"), createTask(2, "Task B"))
        tasksFlow.value = tasks

        val vm = createViewModel()
        val state = vm.taskListUiState.value

        assertEquals(2, state.tasksState.size)
        assertEquals(2, state.rawTaskList.size)
    }

    @Test
    fun `init starts with empty state when no tasks`() {
        val vm = createViewModel()
        val state = vm.taskListUiState.value

        assertEquals(0, state.tasksState.size)
        assertEquals(0, state.rawTaskList.size)
    }

    // --- Search ---

    @Test
    fun `search by title is case insensitive`() {
        tasksFlow.value = listOf(
            createTask(1, "Buy Groceries"),
            createTask(2, "Walk the Dog"),
            createTask(3, "Read a Book")
        )
        val vm = createViewModel()

        vm.onSearchFieldValueChange("groceries")

        val state = vm.taskListUiState.value
        assertEquals(1, state.tasksState.size)
        assertEquals("Buy Groceries", state.tasksState[0].title)
    }

    @Test
    fun `search by description`() {
        tasksFlow.value = listOf(
            createTask(1, "Task A", desc = "Buy milk"),
            createTask(2, "Task B", desc = "Walk the dog"),
            createTask(3, "Task C", desc = null)
        )
        val vm = createViewModel()

        vm.onSearchFieldValueChange("milk")

        val state = vm.taskListUiState.value
        assertEquals(1, state.tasksState.size)
        assertEquals("Task A", state.tasksState[0].title)
    }

    @Test
    fun `search by description is case insensitive`() {
        tasksFlow.value = listOf(
            createTask(1, "Task A", desc = "Buy Milk"),
            createTask(2, "Task B", desc = "Walk the dog")
        )
        val vm = createViewModel()

        vm.onSearchFieldValueChange("milk")

        val state = vm.taskListUiState.value
        assertEquals(1, state.tasksState.size)
        assertEquals("Task A", state.tasksState[0].title)
    }

    @Test
    fun `search matches both title and description`() {
        tasksFlow.value = listOf(
            createTask(1, "Buy Milk", desc = "from the store"),
            createTask(2, "Meditate", desc = "morning routine"),
            createTask(3, "Cook", desc = "Buy ingredients")
        )
        val vm = createViewModel()

        vm.onSearchFieldValueChange("buy")

        val state = vm.taskListUiState.value
        assertEquals(2, state.tasksState.size)
    }

    @Test
    fun `search with no matches returns empty list`() {
        tasksFlow.value = listOf(
            createTask(1, "Buy Groceries"),
            createTask(2, "Walk the Dog")
        )
        val vm = createViewModel()

        vm.onSearchFieldValueChange("zzz no match")

        val state = vm.taskListUiState.value
        assertEquals(0, state.tasksState.size)
    }

    @Test
    fun `clearing search restores all tasks`() {
        tasksFlow.value = listOf(
            createTask(1, "Buy Groceries"),
            createTask(2, "Walk the Dog")
        )
        val vm = createViewModel()

        vm.onSearchFieldValueChange("groceries")
        assertEquals(1, vm.taskListUiState.value.tasksState.size)

        vm.onSearchFieldValueChange("")
        assertEquals(2, vm.taskListUiState.value.tasksState.size)
    }

    @Test
    fun `search ignores tasks with null description`() {
        tasksFlow.value = listOf(
            createTask(1, "Task A", desc = null),
            createTask(2, "Task B", desc = "some text")
        )
        val vm = createViewModel()

        vm.onSearchFieldValueChange("some")

        val state = vm.taskListUiState.value
        assertEquals(1, state.tasksState.size)
        assertEquals("Task B", state.tasksState[0].title)
    }

    // --- Sorting ---

    @Test
    fun `default sort preserves repository order (id descending`() {
        tasksFlow.value = listOf(
            createTask(3, "Third"),
            createTask(2, "Second"),
            createTask(1, "First")
        )
        val vm = createViewModel()

        val state = vm.taskListUiState.value
        assertEquals(3, state.tasksState[0].id)
        assertEquals(2, state.tasksState[1].id)
        assertEquals(1, state.tasksState[2].id)
    }

    @Test
    fun `sort by completed puts done tasks first`() {
        tasksFlow.value = listOf(
            createTask(1, "Not done", isDone = false),
            createTask(2, "Done", isDone = true),
            createTask(3, "Also not done", isDone = false)
        )
        val vm = createViewModel()

        vm.onSortedByChange(SortedBy.COMPLETED)

        val state = vm.taskListUiState.value
        assertEquals(true, state.tasksState[0].isDone)
        assertEquals(false, state.tasksState[1].isDone)
        assertEquals(false, state.tasksState[2].isDone)
    }

    @Test
    fun `switch back to default sort`() {
        tasksFlow.value = listOf(
            createTask(3, "Third"),
            createTask(2, "Second"),
            createTask(1, "First")
        )
        val vm = createViewModel()

        vm.onSortedByChange(SortedBy.COMPLETED)
        vm.onSortedByChange(SortedBy.DEFAULT)

        val state = vm.taskListUiState.value
        assertEquals(SortedBy.DEFAULT, state.sortedBy)
        assertEquals(3, state.tasksState[0].id)
        assertEquals(2, state.tasksState[1].id)
        assertEquals(1, state.tasksState[2].id)
    }

    @Test
    fun `sort change is reflected in state`() {
        tasksFlow.value = listOf(createTask(1, "A"))
        val vm = createViewModel()

        vm.onSortedByChange(SortedBy.COMPLETED)

        assertEquals(SortedBy.COMPLETED, vm.taskListUiState.value.sortedBy)
    }

    // --- Search + Sort interaction ---

    @Test
    fun `search after sort filters sorted results`() {
        tasksFlow.value = listOf(
            createTask(1, "Buy Milk", isDone = true),
            createTask(2, "Buy Eggs", isDone = false),
            createTask(3, "Walk Dog", isDone = true)
        )
        val vm = createViewModel()

        vm.onSortedByChange(SortedBy.COMPLETED)
        vm.onSearchFieldValueChange("Buy")

        val state = vm.taskListUiState.value
        assertEquals(2, state.tasksState.size)
        assertEquals("Buy Milk", state.tasksState[0].title)
        assertEquals("Buy Eggs", state.tasksState[1].title)
    }

    @Test
    fun `new emission from repo updates task list`() {
        tasksFlow.value = listOf(createTask(1, "Initial"))
        val vm = createViewModel()
        assertEquals(1, vm.taskListUiState.value.tasksState.size)

        tasksFlow.value = listOf(createTask(1, "Initial"), createTask(2, "New task"))
        assertEquals(2, vm.taskListUiState.value.tasksState.size)
    }
}
