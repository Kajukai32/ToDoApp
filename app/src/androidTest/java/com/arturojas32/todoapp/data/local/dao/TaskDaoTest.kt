package com.arturojas32.todoapp.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arturojas32.todoapp.data.local.database.TaskDataBase
import com.arturojas32.todoapp.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: TaskDataBase
    private lateinit var dao: TaskDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TaskDataBase::class.java
        ).allowMainThreadQueries().build()
        dao = database.taskDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createEntity(
        id: Int = 0,
        uId: String = "user1",
        title: String = "Task",
        desc: String? = null,
        isDone: Boolean = false,
        remoteId: String? = null,
        isSynced: Boolean = false,
        isDeleted: Boolean = false,
        deadLine: String? = null
    ) = TaskEntity(
        id = id,
        uId = uId,
        title = title,
        desc = desc,
        isDone = isDone,
        createdDate = "01/01/2024",
        lastModified = 0L,
        remoteId = remoteId,
        isSynced = isSynced,
        isDeleted = isDeleted,
        deadLine = deadLine
    )

    // --- Insert ---

    @Test
    fun insertTask_andGetById() = runTest {
        val task = createEntity(title = "Buy milk")
        dao.insertTask(task)

        val allTasks = dao.getAllTasks("user1").first()
        assertEquals(1, allTasks.size)
        assertEquals("Buy milk", allTasks[0].title)
    }

    @Test
    fun insertTask_replaces_on_conflict() = runTest {
        val task = createEntity(id = 1, title = "Original")
        dao.insertTask(task)

        val updated = createEntity(id = 1, title = "Updated")
        dao.insertTask(updated)

        val result = dao.getTaskById(1)
        assertEquals("Updated", result!!.title)
    }

    // --- getAllTasks ---

    @Test
    fun getAllTasks_filters_by_uId() = runTest {
        dao.insertTask(createEntity(uId = "user1", title = "User1 task"))
        dao.insertTask(createEntity(uId = "user2", title = "User2 task"))

        val user1Tasks = dao.getAllTasks("user1").first()
        assertEquals(1, user1Tasks.size)
        assertEquals("User1 task", user1Tasks[0].title)
    }

    @Test
    fun getAllTasks_excludes_deleted_tasks() = runTest {
        dao.insertTask(createEntity(title = "Active"))
        dao.insertTask(createEntity(title = "Deleted", isDeleted = true))

        val tasks = dao.getAllTasks("user1").first()
        assertEquals(1, tasks.size)
        assertEquals("Active", tasks[0].title)
    }

    @Test
    fun getAllTasks_orders_by_id_desc() = runTest {
        dao.insertTask(createEntity(title = "First"))
        dao.insertTask(createEntity(title = "Second"))
        dao.insertTask(createEntity(title = "Third"))

        val tasks = dao.getAllTasks("user1").first()
        assertEquals("Third", tasks[0].title)
        assertEquals("Second", tasks[1].title)
        assertEquals("First", tasks[2].title)
    }

    @Test
    fun getAllTasks_returns_empty_when_no_tasks() = runTest {
        val tasks = dao.getAllTasks("user1").first()
        assertTrue(tasks.isEmpty())
    }

    // --- deleteTask ---

    @Test
    fun deleteTask_removes_entity() = runTest {
        val task = createEntity(id = 1, title = "To delete")
        dao.insertTask(task)

        dao.deleteTask(task)

        val result = dao.getTaskById(1)
        assertNull(result)
    }

    // --- deleteAllTasks ---

    @Test
    fun deleteAllTasks_removes_everything() = runTest {
        dao.insertTask(createEntity(title = "A"))
        dao.insertTask(createEntity(title = "B"))

        dao.deleteAllTasks()

        val tasks = dao.getAllTasks("user1").first()
        assertTrue(tasks.isEmpty())
    }

    // --- getTaskById ---

    @Test
    fun getTaskById_returns_task() = runTest {
        dao.insertTask(createEntity(id = 1, title = "Find me"))

        val result = dao.getTaskById(1)
        assertEquals("Find me", result!!.title)
    }

    @Test
    fun getTaskById_returns_null_when_not_found() = runTest {
        val result = dao.getTaskById(999)
        assertNull(result)
    }

    // --- deleteTaskById ---

    @Test
    fun deleteTaskById_removes_task() = runTest {
        dao.insertTask(createEntity(id = 1, title = "Delete me"))
        dao.insertTask(createEntity(id = 2, title = "Keep me"))

        dao.deleteTaskById(1)

        assertNull(dao.getTaskById(1))
        assertEquals("Keep me", dao.getTaskById(2)!!.title)
    }

    // --- getUnsyncedTasks ---

    @Test
    fun getUnsyncedTasks_returns_only_unsynced() = runTest {
        dao.insertTask(createEntity(title = "Synced", isSynced = true))
        dao.insertTask(createEntity(title = "Unsynced", isSynced = false))

        val unsynced = dao.getUnsyncedTasks()
        assertEquals(1, unsynced.size)
        assertEquals("Unsynced", unsynced[0].title)
    }

    @Test
    fun getUnsyncedTasks_returns_empty_when_all_synced() = runTest {
        dao.insertTask(createEntity(title = "Synced", isSynced = true))
        dao.insertTask(createEntity(title = "Also synced", isSynced = true))

        val unsynced = dao.getUnsyncedTasks()
        assertTrue(unsynced.isEmpty())
    }

    // --- getTaskByRemoteId ---

    @Test
    fun getTaskByRemoteId_returns_matching_task() = runTest {
        dao.insertTask(createEntity(title = "Remote task", remoteId = "firebase_abc"))

        val result = dao.getTaskByRemoteId("firebase_abc")
        assertEquals("Remote task", result!!.title)
    }

    @Test
    fun getTaskByRemoteId_returns_null_when_not_found() = runTest {
        val result = dao.getTaskByRemoteId("nonexistent")
        assertNull(result)
    }

    // --- getTasksByTitleOrAsc (search) ---

    @Test
    fun searchByTitle_finds_match() = runTest {
        dao.insertTask(createEntity(title = "Buy groceries"))
        dao.insertTask(createEntity(title = "Walk the dog"))

        val results = dao.getTasksByTitleOrAsc("groceries")
        assertEquals(1, results.size)
        assertEquals("Buy groceries", results[0].title)
    }

    @Test
    fun searchByTitle_is_case_insensitive() = runTest {
        dao.insertTask(createEntity(title = "Buy Groceries"))

        val results = dao.getTasksByTitleOrAsc("groceries")
        assertEquals(1, results.size)
    }

    @Test
    fun searchByDescription_finds_match() = runTest {
        dao.insertTask(createEntity(title = "Task A", desc = "Buy milk"))
        dao.insertTask(createEntity(title = "Task B", desc = "Walk the dog"))

        val results = dao.getTasksByTitleOrAsc("milk")
        assertEquals(1, results.size)
        assertEquals("Task A", results[0].title)
    }

    @Test
    fun searchMatches_both_title_and_description() = runTest {
        dao.insertTask(createEntity(title = "Buy Milk", desc = "from the store"))
        dao.insertTask(createEntity(title = "Meditate", desc = "morning routine"))
        dao.insertTask(createEntity(title = "Cook", desc = "Buy ingredients"))

        val results = dao.getTasksByTitleOrAsc("buy")
        assertEquals(2, results.size)
    }

    @Test
    fun searchReturns_empty_when_no_match() = runTest {
        dao.insertTask(createEntity(title = "Buy groceries"))

        val results = dao.getTasksByTitleOrAsc("zzz no match")
        assertTrue(results.isEmpty())
    }
}
