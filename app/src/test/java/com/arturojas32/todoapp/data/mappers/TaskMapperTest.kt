package com.arturojas32.todoapp.data.mappers

import com.arturojas32.todoapp.data.local.entities.TaskEntity
import com.arturojas32.todoapp.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TaskMapperTest {

    private val fullTask = Task(
        id = 1,
        uId = "user123",
        remoteId = "remote_abc",
        title = "Buy groceries",
        desc = "Milk, eggs, bread",
        isDone = true,
        createdDate = "15/01/2024",
        deadLine = "20/01/2024",
        lastModified = 1705276800000L,
        isSynced = true,
        isDeleted = false
    )

    private val fullEntity = TaskEntity(
        id = 1,
        uId = "user123",
        remoteId = "remote_abc",
        title = "Buy groceries",
        desc = "Milk, eggs, bread",
        isDone = true,
        createdDate = "15/01/2024",
        deadLine = "20/01/2024",
        lastModified = 1705276800000L,
        isSynced = true,
        isDeleted = false
    )

    // --- Task.toEntity ---

    @Test
    fun `toEntity maps all fields correctly`() {
        val entity = fullTask.toEntity()

        assertEquals(1, entity.id)
        assertEquals("user123", entity.uId)
        assertEquals("remote_abc", entity.remoteId)
        assertEquals("Buy groceries", entity.title)
        assertEquals("Milk, eggs, bread", entity.desc)
        assertEquals(true, entity.isDone)
        assertEquals("15/01/2024", entity.createdDate)
        assertEquals("20/01/2024", entity.deadLine)
        assertEquals(1705276800000L, entity.lastModified)
        assertEquals(true, entity.isSynced)
        assertEquals(false, entity.isDeleted)
    }

    @Test
    fun `toEntity maps nullable fields as null`() {
        val task = Task(
            id = 2,
            uId = "user456",
            title = "Clean house",
            desc = null,
            deadLine = null,
            remoteId = null,
            createdDate = "01/01/2024",
            lastModified = 0L
        )

        val entity = task.toEntity()

        assertNull(entity.desc)
        assertNull(entity.deadLine)
        assertNull(entity.remoteId)
    }

    // --- TaskEntity.toDomain ---

    @Test
    fun `toDomain maps all fields correctly`() {
        val task = fullEntity.toDomain()

        assertEquals(1, task.id)
        assertEquals("user123", task.uId)
        assertEquals("remote_abc", task.remoteId)
        assertEquals("Buy groceries", task.title)
        assertEquals("Milk, eggs, bread", task.desc)
        assertEquals(true, task.isDone)
        assertEquals("15/01/2024", task.createdDate)
        assertEquals("20/01/2024", task.deadLine)
        assertEquals(1705276800000L, task.lastModified)
        assertEquals(true, task.isSynced)
        assertEquals(false, task.isDeleted)
    }

    @Test
    fun `toDomain maps nullable fields as null`() {
        val entity = TaskEntity(
            id = 2,
            uId = "user456",
            title = "Clean house",
            desc = null,
            deadLine = null,
            remoteId = null,
            createdDate = "01/01/2024",
            lastModified = 0L
        )

        val task = entity.toDomain()

        assertNull(task.desc)
        assertNull(task.deadLine)
        assertNull(task.remoteId)
    }

    // --- Round-trip ---

    @Test
    fun `round trip Task to Entity to Task preserves all fields`() {
        val result = fullTask.toEntity().toDomain()
        assertEquals(fullTask, result)
    }

    @Test
    fun `round trip TaskEntity to Task to TaskEntity preserves all fields`() {
        val result = fullEntity.toDomain().toEntity()
        assertEquals(fullEntity, result)
    }

    @Test
    fun `round trip with null fields preserves all fields`() {
        val task = Task(
            id = 3,
            uId = "user789",
            title = "Write tests",
            desc = null,
            deadLine = null,
            remoteId = null,
            createdDate = "10/06/2024",
            lastModified = 1718006400000L
        )

        val result = task.toEntity().toDomain()
        assertEquals(task, result)
    }

    @Test
    fun `round trip with all booleans false preserves state`() {
        val task = Task(
            id = 4,
            uId = "user",
            title = "Task",
            createdDate = "01/01/2024",
            lastModified = 0L,
            isDone = false,
            isSynced = false,
            isDeleted = false
        )

        val result = task.toEntity().toDomain()
        assertEquals(false, result.isDone)
        assertEquals(false, result.isSynced)
        assertEquals(false, result.isDeleted)
    }
}
