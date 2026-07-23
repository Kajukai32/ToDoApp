package com.arturojas32.todoapp.ui.viewmodels

import app.cash.turbine.test
import com.arturojas32.todoapp.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class RegisterViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var authRepo: AuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepo = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = RegisterViewModel(authRepo)

    // --- Initial state ---

    @Test
    fun `initial state has empty fields and disabled button`() {
        val vm = createViewModel()
        val state = vm.registerUiState.value

        assertEquals("", state.newUserEmail)
        assertEquals("", state.newUserPassword)
        assertFalse(state.isRegisterButtonEnabled)
        assertFalse(state.loading)
        assertNull(state.error)
        assertTrue(state.passwordVisibility)
    }

    // --- Email field ---

    @Test
    fun `email field updates state`() {
        val vm = createViewModel()
        vm.onEmailValueChange("new@example.com")

        assertEquals("new@example.com", vm.registerUiState.value.newUserEmail)
    }

    @Test
    fun `email change alone does not update button state`() {
        val vm = createViewModel()
        vm.onEmailValueChange("test@example.com")

        assertFalse(vm.registerUiState.value.isRegisterButtonEnabled)
    }

    // --- Password field ---

    @Test
    fun `password field updates state`() {
        val vm = createViewModel()
        vm.onPasswordValueChange("mypassword")

        assertEquals("mypassword", vm.registerUiState.value.newUserPassword)
    }

    // --- Validation ---

    @Test
    fun `button disabled with empty fields`() {
        val vm = createViewModel()
        assertFalse(vm.registerUiState.value.isRegisterButtonEnabled)
    }

    @Test
    fun `button disabled with invalid email`() {
        val vm = createViewModel()
        vm.onEmailValueChange("not-an-email")
        vm.onPasswordValueChange("password123")

        assertFalse(vm.registerUiState.value.isRegisterButtonEnabled)
    }

    @Test
    fun `button disabled with short password`() {
        val vm = createViewModel()
        vm.onEmailValueChange("test@example.com")
        vm.onPasswordValueChange("short")

        assertFalse(vm.registerUiState.value.isRegisterButtonEnabled)
    }

    @Test
    fun `button enabled with valid email and long password`() {
        val vm = createViewModel()
        vm.onEmailValueChange("test@example.com")
        vm.onPasswordValueChange("password123")

        assertTrue(vm.registerUiState.value.isRegisterButtonEnabled)
    }

    @Test
    fun `button disabled again when password becomes too short`() {
        val vm = createViewModel()
        vm.onEmailValueChange("test@example.com")
        vm.onPasswordValueChange("password123")
        assertTrue(vm.registerUiState.value.isRegisterButtonEnabled)

        vm.onPasswordValueChange("short")
        assertFalse(vm.registerUiState.value.isRegisterButtonEnabled)
    }

    // --- Password visibility ---

    @Test
    fun `password visibility toggles`() {
        val vm = createViewModel()
        assertTrue(vm.registerUiState.value.passwordVisibility)

        vm.onPasswordVisibilityChange()
        assertFalse(vm.registerUiState.value.passwordVisibility)

        vm.onPasswordVisibilityChange()
        assertTrue(vm.registerUiState.value.passwordVisibility)
    }

    // --- Register ---

    @Test
    fun `successful register emits Success event`() = runTest {
        coEvery { authRepo.register(any(), any()) } returns Result.success(Unit)
        val vm = createViewModel()

        vm.event.test {
            vm.onEmailValueChange("test@example.com")
            vm.onPasswordValueChange("password123")
            vm.register()

            assertEquals(RegisterEvent.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(vm.registerUiState.value.loading)
        assertNull(vm.registerUiState.value.error)
    }

    @Test
    fun `failed register sets error message`() = runTest {
        coEvery { authRepo.register(any(), any()) } returns Result.failure(
            Exception("Email already in use")
        )
        val vm = createViewModel()

        vm.onEmailValueChange("test@example.com")
        vm.onPasswordValueChange("password123")
        vm.register()

        val state = vm.registerUiState.value
        assertEquals("Email already in use", state.error)
        assertFalse(state.loading)
    }

    @Test
    fun `failed register with null message shows default error`() = runTest {
        coEvery { authRepo.register(any(), any()) } returns Result.failure(Exception())
        val vm = createViewModel()

        vm.onEmailValueChange("test@example.com")
        vm.onPasswordValueChange("password123")
        vm.register()

        assertEquals("Unexpected error. Try again later", vm.registerUiState.value.error)
    }

    @Test
    fun `register clears previous error`() = runTest {
        coEvery { authRepo.register(any(), any()) } returns Result.failure(
            Exception("First error")
        )
        val vm = createViewModel()

        vm.onEmailValueChange("test@example.com")
        vm.onPasswordValueChange("password123")
        vm.register()
        assertEquals("First error", vm.registerUiState.value.error)

        coEvery { authRepo.register(any(), any()) } returns Result.success(Unit)
        vm.register()
        assertNull(vm.registerUiState.value.error)
    }
}
