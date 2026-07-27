package com.arturojas32.todoapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.arturojas32.todoapp.data.local.repository.TaskRepositoryImpl
import com.arturojas32.todoapp.domain.repository.AuthRepository
import com.arturojas32.todoapp.domain.repository.RemoteDbRepository
import com.arturojas32.todoapp.domain.model.Task
import com.arturojas32.todoapp.utils.SyncManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class TaskFeaturesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repo: TaskRepositoryImpl
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var remoteDbRepo: RemoteDbRepository
    private lateinit var syncManager: SyncManager
    private lateinit var authRepo: AuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle()
        remoteDbRepo = mockk(relaxed = true)
        syncManager = mockk(relaxed = true)
        authRepo = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = TaskFeaturesViewModel(
        repo = repo,
        savedStateHandle = savedStateHandle,
        remoteDbRepo = remoteDbRepo,
        syncManager = syncManager,
        authRepo = authRepo
    )

    // --- Initial state ---

    @Test
    fun `initial state is new task mode`() {
        val vm = createViewModel()
        val state = vm.taskState.value

        assertEquals("New task", state.scaffoldTitle)
        assertEquals("Save task", state.submitButtonText)
        assertFalse(state.saveButtonEnabled)
        assertEquals("", state.task.title)
        assertNull(state.task.desc)
        assertNull(state.task.deadLine)
        assertFalse(state.task.isDone)
        assertNull(state.deletedTask)
    }

    // --- Title ---

    @Test
    fun `title updates state`() {
        val vm = createViewModel()
        vm.onTitleTextFieldValueChange("Buy groceries")

        assertEquals("Buy groceries", vm.taskState.value.task.title)
    }

    @Test
    fun `non-blank title enables save button`() {
        val vm = createViewModel()
        vm.onTitleTextFieldValueChange("Task")

        assertTrue(vm.taskState.value.saveButtonEnabled)
    }

    @Test
    fun `blank title disables save button`() {
        val vm = createViewModel()
        vm.onTitleTextFieldValueChange("Task")
        assertTrue(vm.taskState.value.saveButtonEnabled)

        vm.onTitleTextFieldValueChange("")
        assertFalse(vm.taskState.value.saveButtonEnabled)
    }

    @Test
    fun `blank-only-spaces title disables save button`() {
        val vm = createViewModel()
        vm.onTitleTextFieldValueChange("   ")

        assertFalse(vm.taskState.value.saveButtonEnabled)
    }

    // --- Description ---

    @Test
    fun `description updates state`() {
        val vm = createViewModel()
        vm.onDescTextFieldValueChange("Milk, eggs, bread")

        assertEquals("Milk, eggs, bread", vm.taskState.value.task.desc)
    }

    @Test
    fun `description can be set to null`() {
        val vm = createViewModel()
        vm.onDescTextFieldValueChange("Some desc")
        vm.onDescTextFieldValueChange("")

        assertEquals("", vm.taskState.value.task.desc)
    }

    // --- Deadline ---

    @Test
    fun `deadline updates state`() {
        val vm = createViewModel()
        vm.onSetDeadLineClick("25/12/2024")

        assertEquals("25/12/2024", vm.taskState.value.task.deadLine)
    }

    // --- Save ---

    @Test
    fun `save task inserts into repo and triggers sync`() = runTest {
        coEvery { authRepo.currentUser() } returns mockk { every { uId } returns "user123" }
        val vm = createViewModel()

        vm.onTitleTextFieldValueChange("New task")
        vm.onSaveTaskClick()

        coVerify {
            repo.insertTask(withArg { task ->
                assertEquals("New task", task.title)
                assertEquals("user123", task.uId)
                assertEquals(false, task.isSynced)
            })
        }
        verify { syncManager.startSync() }
    }

    @Test
    fun `save task sets isSynced to false and updates lastModified`() = runTest {
        coEvery { authRepo.currentUser() } returns mockk { every { uId } returns "user123" }
        val vm = createViewModel()

        val before = System.currentTimeMillis()
        vm.onTitleTextFieldValueChange("Task")
        vm.onSaveTaskClick()

        coVerify {
            repo.insertTask(withArg { task ->
                assertFalse(task.isSynced)
                assertTrue(task.lastModified >= before)
            })
        }
    }

    // --- Toggle done ---

    @Test
    fun `toggle done flips isDone and saves`() = runTest {
        val task = Task(
            id = 1, uId = "user123", title = "Task",
            isDone = false, createdDate = "01/01/2024", lastModified = 0L
        )
        coEvery { repo.getTaskById(1) } returns task

        val vm = createViewModel()
        vm.onIsDoneCheckedChange(1)

        coVerify {
            repo.insertTask(withArg { savedTask ->
                assertTrue(savedTask.isDone)
                assertEquals(false, savedTask.isSynced)
            })
        }
    }

    @Test
    fun `toggle done on already done task sets isDone to false`() = runTest {
        val task = Task(
            id = 1, uId = "user123", title = "Task",
            isDone = true, createdDate = "01/01/2024", lastModified = 0L
        )
        coEvery { repo.getTaskById(1) } returns task

        val vm = createViewModel()
        vm.onIsDoneCheckedChange(1)

        coVerify {
            repo.insertTask(withArg { savedTask ->
                assertFalse(savedTask.isDone)
            })
        }
    }

    // --- Delete ---

    @Test
    fun `delete soft deletes task and triggers sync`() = runTest {
        val task = Task(
            id = 1, uId = "user123", title = "Task",
            isDone = false, createdDate = "01/01/2024", lastModified = 0L
        )
        coEvery { repo.getTaskById(1) } returns task

        val vm = createViewModel()
        vm.onDeleteClick(1)

        assertNotNull(vm.taskState.value.deletedTask)
        assertTrue(vm.taskState.value.deletedTask!!.isDeleted)

        coVerify {
            repo.insertTask(withArg { savedTask ->
                assertTrue(savedTask.isDeleted)
                assertEquals(false, savedTask.isSynced)
            })
        }
        verify { syncManager.startSync() }
    }

    // --- Restore ---

    @Test
    fun `restore re inserts deleted task and clears deletedTask`() = runTest {
        val task = Task(
            id = 1, uId = "user123", title = "Task",
            isDone = false, createdDate = "01/01/2024", lastModified = 0L
        )
        coEvery { repo.getTaskById(1) } returns task

        val vm = createViewModel()
        vm.onDeleteClick(1)
        val deletedTask = vm.taskState.value.deletedTask
        assertNotNull(deletedTask)

        vm.restoreTask()

        coVerify { repo.insertTask(deletedTask!!) }
        assertNull(vm.taskState.value.deletedTask)
    }

    @Test
    fun `restore with no deleted task does nothing`() = runTest {
        val vm = createViewModel()
        vm.restoreTask()

        coVerify(exactly = 0) { repo.insertTask(any()) }
        assertNull(vm.taskState.value.deletedTask)
    }
}
