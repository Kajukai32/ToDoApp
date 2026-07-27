package com.arturojas32.todoapp.ui.screens

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.arturojas32.todoapp.ui.FakeAuthRepository
import com.arturojas32.todoapp.ui.FakeRemoteDbRepository
import com.arturojas32.todoapp.ui.FakeTaskRepository
import com.arturojas32.todoapp.ui.viewmodels.TaskFeaturesViewModel
import com.arturojas32.todoapp.utils.SyncManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddTaskScreenTest {

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
        taskId: Int? = null
    ): TaskFeaturesViewModel {
        val savedStateHandle = if (taskId != null) {
            SavedStateHandle(mapOf("taskId" to taskId))
        } else {
            SavedStateHandle()
        }
        val viewModel = TaskFeaturesViewModel(
            repo = fakeTaskRepo,
            savedStateHandle = savedStateHandle,
            remoteDbRepo = fakeRemoteDbRepo,
            syncManager = syncManager,
            authRepo = fakeAuthRepo
        )
        composeTestRule.setContent {
            AddTaskScreen(
                taskFeaturesViewModel = viewModel,
                onBackClick = onBackClick
            )
        }
        return viewModel
    }

    // --- Back navigation ---

    @Test
    fun backButton_invokesOnBackClick() {
        var backClicked = false
        setContent(onBackClick = { backClicked = true })

        composeTestRule.onNodeWithContentDescription("back icon")
            .performClick()

        assert(backClicked)
    }

    // --- Title input ---

    @Test
    fun typingTitle_updatesViewModelState() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Insert the task title")
            .performClick()
        composeTestRule.onNodeWithText("Insert the task title")
            .performTextInput("My new task")

        assert(vm.taskState.value.task.title == "My new task")
    }

    // --- Description input ---

    @Test
    fun typingDescription_updatesViewModelState() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Insert the task description")
            .performClick()
        composeTestRule.onNodeWithText("Insert the task description")
            .performTextInput("Some description")

        assert(vm.taskState.value.task.desc == "Some description")
    }

    // --- Save button state ---

    @Test
    fun saveButton_disabled_initially() {
        val vm = setContent()

        assert(!vm.taskState.value.saveButtonEnabled)
    }

    @Test
    fun saveButton_enabled_after_title() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Insert the task title")
            .performClick()
        composeTestRule.onNodeWithText("Insert the task title")
            .performTextInput("Some title")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Save task")
            .assertIsEnabled()
    }

    @Test
    fun saveButton_disabled_after_clearing_title() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Insert the task title")
            .performClick()
        composeTestRule.onNodeWithText("Insert the task title")
            .performTextInput("Some title")
        composeTestRule.waitForIdle()

        vm.onTitleTextFieldValueChange("")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Save task")
            .assertIsNotEnabled()
    }

    // --- Save click ---

    @Test
    fun saveClick_savesTask_andNavigatesBack() {
        var backClicked = false
        val vm = setContent(onBackClick = { backClicked = true })

        vm.onTitleTextFieldValueChange("New task")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Save task")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) { backClicked }
        assert(backClicked)
        assert(fakeTaskRepo.insertTaskCalled)
    }

    // --- Default screen state ---

    @Test
    fun defaultTitle_showsNewTask() {
        setContent()

        composeTestRule.onNodeWithText("New task")
            .assertExists()
    }

    @Test
    fun defaultDeadline_showsPlaceholder() {
        setContent()

        composeTestRule.onNodeWithText("Tap the calendar to set a deadline")
            .assertExists()
    }

    // --- Update flow: pre-filled fields ---

    @Test
    fun updateTask_fieldsPopulatedFromExistingTask() {
        val existingTask = com.arturojas32.todoapp.domain.model.Task(
            id = 42,
            uId = "test-uid",
            title = "Existing title",
            desc = "Existing description"
        )
        fakeTaskRepo.emitTasks(listOf(existingTask))

        setContent(taskId = 42)

        composeTestRule.onNodeWithText("Existing title").assertExists()
        composeTestRule.onNodeWithText("Existing description").assertExists()
    }
}
