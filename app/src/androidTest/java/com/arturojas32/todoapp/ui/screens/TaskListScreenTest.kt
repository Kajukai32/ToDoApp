package com.arturojas32.todoapp.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.arturojas32.todoapp.domain.model.Task
import com.arturojas32.todoapp.ui.FakeAuthRepository
import com.arturojas32.todoapp.ui.FakeRemoteDbRepository
import com.arturojas32.todoapp.ui.FakeTaskRepository
import com.arturojas32.todoapp.ui.viewmodels.SortedBy
import com.arturojas32.todoapp.ui.viewmodels.TaskFeaturesViewModel
import com.arturojas32.todoapp.ui.viewmodels.TaskListViewModel
import com.arturojas32.todoapp.utils.SyncManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeTaskRepo: FakeTaskRepository
    private lateinit var fakeAuthRepo: FakeAuthRepository
    private lateinit var fakeRemoteDbRepo: FakeRemoteDbRepository
    private lateinit var syncManager: SyncManager

    @Before
    fun setUp() {
        fakeTaskRepo = FakeTaskRepository()
        fakeAuthRepo = FakeAuthRepository()
        fakeAuthRepo.fakeUid = "test-uid"
        fakeRemoteDbRepo = FakeRemoteDbRepository()
        syncManager = SyncManager(ApplicationProvider.getApplicationContext())
    }

    private fun setContent(
        onBackClick: () -> Unit = {},
        onAddTaskClick: () -> Unit = {},
        onResetPasswordClick: () -> Unit = {},
        onLogOutClick: () -> Unit = {},
        onChangePasswordClick: () -> Unit = {},
        onTaskItemClick: (Int) -> Unit = {}
    ): Pair<TaskListViewModel, TaskFeaturesViewModel> {
        val taskListViewModel = TaskListViewModel(fakeTaskRepo, fakeAuthRepo, syncManager)
        val taskFeaturesViewModel = TaskFeaturesViewModel(
            repo = fakeTaskRepo,
            savedStateHandle = androidx.lifecycle.SavedStateHandle(),
            remoteDbRepo = fakeRemoteDbRepo,
            syncManager = syncManager,
            authRepo = fakeAuthRepo
        )
        composeTestRule.setContent {
            TaskListScreen(
                taskListViewModel = taskListViewModel,
                taskFeaturesViewModel = taskFeaturesViewModel,
                onBackClick = onBackClick,
                onAddTaskClick = onAddTaskClick,
                onResetPasswordClick = onResetPasswordClick,
                onLogOutClick = onLogOutClick,
                onChangePasswordClick = onChangePasswordClick,
                onTaskItemClick = onTaskItemClick
            )
        }
        return Pair(taskListViewModel, taskFeaturesViewModel)
    }

    private fun sampleTask(
        id: Int = 1,
        title: String = "Test Task",
        desc: String = "Test Description",
        isDone: Boolean = false
    ) = Task(
        id = id,
        title = title,
        desc = desc,
        isDone = isDone,
        createdDate = "2026-01-01"
    )

    // --- Empty state ---

    @Test
    fun menuIcon_opensDrawer() {
        setContent()

        composeTestRule.onNodeWithContentDescription("menu icon")
            .assertExists()
    }

    @Test
    fun emptyTaskList_showsEmptyStateText() {
        setContent()

        composeTestRule.onNodeWithText("You haven't added any task yet, try adding via '+'")
            .assertExists()
    }

    // --- Task list displays tasks ---

    @Test
    fun withTasks_showsTaskTitles() {
        fakeTaskRepo.emitTasks(
            listOf(
                sampleTask(id = 1, title = "First Task"),
                sampleTask(id = 2, title = "Second Task")
            )
        )
        setContent()

        composeTestRule.onNodeWithText("First Task").assertExists()
        composeTestRule.onNodeWithText("Second Task").assertExists()
    }

    // --- Search bar toggle ---

    @Test
    fun searchIcon_click_showsSearchBar() {
        fakeTaskRepo.emitTasks(listOf(sampleTask()))
        setContent()

        composeTestRule.onNodeWithContentDescription("clickable search icon")
            .performClick()

        composeTestRule.onNodeWithText("Title or description reference")
            .assertExists()
    }

    @Test
    fun searchBar_cancelIcon_hidesSearchBar() {
        fakeTaskRepo.emitTasks(listOf(sampleTask()))
        setContent()

        composeTestRule.onNodeWithContentDescription("clickable search icon")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("clickable cancel operation icon")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Title or description reference")
            .fetchSemanticsNodes()
            .isEmpty().let { assert(it) }
    }

    // --- Search filters tasks ---

    @Test
    fun searchFiltersTasks_byTitle() {
        fakeTaskRepo.emitTasks(
            listOf(
                sampleTask(id = 1, title = "Buy groceries"),
                sampleTask(id = 2, title = "Read book")
            )
        )
        val (vm, _) = setContent()

        composeTestRule.onNodeWithContentDescription("clickable search icon")
            .performClick()
        composeTestRule.waitForIdle()

        vm.onSearchFieldValueChange("groceries")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Buy groceries").assertExists()
        composeTestRule.onAllNodesWithText("Read book").fetchSemanticsNodes()
            .isEmpty().let { assert(it) }
    }

    @Test
    fun searchFiltersTasks_byDescription() {
        fakeTaskRepo.emitTasks(
            listOf(
                sampleTask(id = 1, title = "Task A", desc = "Important notes"),
                sampleTask(id = 2, title = "Task B", desc = "Random stuff")
            )
        )
        val (vm, _) = setContent()

        composeTestRule.onNodeWithContentDescription("clickable search icon")
            .performClick()
        composeTestRule.waitForIdle()

        vm.onSearchFieldValueChange("Important")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Task A").assertExists()
        composeTestRule.onAllNodesWithText("Task B").fetchSemanticsNodes()
            .isEmpty().let { assert(it) }
    }

    // --- Sort ---

    @Test
    fun sortByCompleted_sortsTasks() {
        fakeTaskRepo.emitTasks(
            listOf(
                sampleTask(id = 1, title = "Undone task", isDone = false),
                sampleTask(id = 2, title = "Done task", isDone = true)
            )
        )
        val (vm, _) = setContent()

        vm.onSortedByChange(SortedBy.COMPLETED)
        composeTestRule.waitForIdle()

        assert(vm.taskListUiState.value.sortedBy == SortedBy.COMPLETED)
        assert(vm.taskListUiState.value.tasksState.first().isDone)
    }

    @Test
    fun sortByDefault_resetsSort() {
        fakeTaskRepo.emitTasks(
            listOf(
                sampleTask(id = 2, title = "Second"),
                sampleTask(id = 1, title = "First")
            )
        )
        val (vm, _) = setContent()

        vm.onSortedByChange(SortedBy.COMPLETED)
        composeTestRule.waitForIdle()
        assert(vm.taskListUiState.value.sortedBy == SortedBy.COMPLETED)

        vm.onSortedByChange(SortedBy.DEFAULT)
        composeTestRule.waitForIdle()

        assert(vm.taskListUiState.value.sortedBy == SortedBy.DEFAULT)
    }

    // --- FAB click ---

    @Test
    fun fabClick_invokesOnAddTaskClick() {
        var addTaskClicked = false
        setContent(onAddTaskClick = { addTaskClicked = true })

        composeTestRule.onNodeWithContentDescription("add task icon")
            .performClick()

        assert(addTaskClicked)
    }

    // --- Task item click ---

    @Test
    fun taskItemClick_invokesOnTaskItemClick() {
        fakeTaskRepo.emitTasks(listOf(sampleTask(id = 42, title = "Clickable task")))
        var clickedTaskId: Int? = null
        setContent(onTaskItemClick = { clickedTaskId = it })

        composeTestRule.onNodeWithText("Clickable task")
            .performClick()

        assert(clickedTaskId == 42)
    }

    // --- More options menu ---

    @Test
    fun moreOptionsIcon_exists() {
        setContent()

        composeTestRule.onNodeWithContentDescription("More options")
            .assertExists()
    }

    // --- Log out ---

    @Test
    fun logOutOption_clearsUser() {
        val (vm, _) = setContent()

        assert(fakeAuthRepo.currentUser() != null)

        vm.onLogOutOptionClick()
        composeTestRule.waitForIdle()

        assert(fakeAuthRepo.currentUser() == null)
    }

    // --- Checkbox toggle ---
    @Test
    fun ff() {
        fakeTaskRepo.emitTasks(listOf(sampleTask(id = 1, title = "My task", isDone = false)))
        val (_, featuresVm) = setContent()

        featuresVm.onIsDoneCheckedChange(1)
        composeTestRule.waitForIdle()

        assert(fakeTaskRepo.lastInsertedTask?.isDone == true)
    }

    // --- Delete ---

    @Test
    fun deleteClick_marksTaskAsDeleted() {
        fakeTaskRepo.emitTasks(listOf(sampleTask(id = 1, title = "Delete me")))
        val (_, featuresVm) = setContent()

        featuresVm.onDeleteClick(1)
        composeTestRule.waitForIdle()

        assert(featuresVm.taskState.value.deletedTask != null)
        assert(featuresVm.taskState.value.deletedTask?.isDeleted == true)
    }

    @Test
    fun undoClick_restoresTask() {
        fakeTaskRepo.emitTasks(listOf(sampleTask(id = 1, title = "Restore me")))
        val (_, featuresVm) = setContent()

        featuresVm.onDeleteClick(1)
        composeTestRule.waitForIdle()
        assert(featuresVm.taskState.value.deletedTask != null)

        featuresVm.restoreTask()
        composeTestRule.waitForIdle()

        assert(featuresVm.taskState.value.deletedTask == null)
    }
}
