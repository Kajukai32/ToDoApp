package com.arturojas32.todoapp.ui.viewmodels

import com.arturojas32.todoapp.domain.repository.AuthRepository
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
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
class ChangePasswordViewModelTest {

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

    private fun createViewModel() = ChangePasswordViewModel(authRepo)

    // --- Initial state ---

    @Test
    fun `initial state has RESET mode and empty fields`() {
        val vm = createViewModel()
        val state = vm.forgotPasswordUiState.value

        assertEquals(PasswordMode.RESET, state.mode)
        assertEquals("", state.email)
        assertEquals("", state.currentPasswordField)
        assertEquals("", state.newPasswordField)
        assertFalse(state.enableConfirmButton)
        assertFalse(state.loading)
        assertNull(state.error)
        assertFalse(state.currentPasswordFieldVisibility)
        assertFalse(state.newPasswordFieldVisibility)
    }

    // --- Mode ---

    @Test
    fun `setMode updates mode`() {
        val vm = createViewModel()

        vm.setMode(PasswordMode.CHANGE)
        assertEquals(PasswordMode.CHANGE, vm.forgotPasswordUiState.value.mode)

        vm.setMode(PasswordMode.RESET)
        assertEquals(PasswordMode.RESET, vm.forgotPasswordUiState.value.mode)
    }

    @Test
    fun `setMode pre-fills email when user is logged in`() {
        val fakeUser = com.arturojas32.todoapp.domain.model.AuthUser(
            uId = "test-uid",
            email = "user@example.com"
        )
        every { authRepo.currentUser() } returns fakeUser
        val vm = createViewModel()

        vm.setMode(PasswordMode.RESET)

        assertEquals("user@example.com", vm.forgotPasswordUiState.value.email)
        assertTrue(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    @Test
    fun `setMode keeps email empty when no user logged in`() {
        every { authRepo.currentUser() } returns null
        val vm = createViewModel()

        vm.setMode(PasswordMode.RESET)

        assertEquals("", vm.forgotPasswordUiState.value.email)
        assertFalse(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    // --- Email field ---

    @Test
    fun `onEmailValueChange updates email`() {
        val vm = createViewModel()
        vm.onEmailValueChange("test@example.com")

        assertEquals("test@example.com", vm.forgotPasswordUiState.value.email)
    }

    @Test
    fun `onEmailValueChange clears error`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.CHANGE)
        vm.onNewPasswordFieldValueChange("password123")
        vm.onCurrentPasswordFieldValueChange("password123")
        vm.onEmailValueChange("invalid")
        vm.sendReset()
        assertFalse(vm.forgotPasswordUiState.value.error.isNullOrEmpty() || vm.forgotPasswordUiState.value.error == null)

        vm.onEmailValueChange("test@example.com")
        assertNull(vm.forgotPasswordUiState.value.error)
    }

    // --- Password fields ---

    @Test
    fun `onCurrentPasswordFieldValueChange updates current password`() {
        val vm = createViewModel()
        vm.onCurrentPasswordFieldValueChange("password123")

        assertEquals("password123", vm.forgotPasswordUiState.value.currentPasswordField)
    }

    @Test
    fun `onNewPasswordFieldValueChange updates new password`() {
        val vm = createViewModel()
        vm.onNewPasswordFieldValueChange("newpassword123")

        assertEquals("newpassword123", vm.forgotPasswordUiState.value.newPasswordField)
    }

    // --- Password visibility ---

    @Test
    fun `current password visibility toggles`() {
        val vm = createViewModel()
        assertFalse(vm.forgotPasswordUiState.value.currentPasswordFieldVisibility)

        vm.onCurrentPasswordVisibilityChange(true)
        assertTrue(vm.forgotPasswordUiState.value.currentPasswordFieldVisibility)

        vm.onCurrentPasswordVisibilityChange(false)
        assertFalse(vm.forgotPasswordUiState.value.currentPasswordFieldVisibility)
    }

    @Test
    fun `new password visibility toggles`() {
        val vm = createViewModel()
        assertFalse(vm.forgotPasswordUiState.value.newPasswordFieldVisibility)

        vm.onNewPasswordVisibilityChange(true)
        assertTrue(vm.forgotPasswordUiState.value.newPasswordFieldVisibility)

        vm.onNewPasswordVisibilityChange(false)
        assertFalse(vm.forgotPasswordUiState.value.newPasswordFieldVisibility)
    }

    // --- Button validation in RESET mode ---

    @Test
    fun `button disabled with empty email in RESET mode`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)

        assertFalse(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    @Test
    fun `button disabled with invalid email in RESET mode`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("not-an-email")

        assertFalse(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    @Test
    fun `button enabled with valid email in RESET mode`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("test@example.com")

        assertTrue(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    @Test
    fun `button does not require passwords in RESET mode`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("test@example.com")

        assertTrue(vm.forgotPasswordUiState.value.enableConfirmButton)
        assertEquals("", vm.forgotPasswordUiState.value.currentPasswordField)
        assertEquals("", vm.forgotPasswordUiState.value.newPasswordField)
    }

    // --- Button validation in CHANGE mode ---

    @Test
    fun `button disabled with only valid email in CHANGE mode`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.CHANGE)
        vm.onEmailValueChange("test@example.com")

        assertFalse(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    @Test
    fun `button disabled with missing current password in CHANGE mode`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.CHANGE)
        vm.onEmailValueChange("test@example.com")
        vm.onNewPasswordFieldValueChange("newpassword123")

        assertFalse(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    @Test
    fun `button disabled with missing new password in CHANGE mode`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.CHANGE)
        vm.onEmailValueChange("test@example.com")
        vm.onCurrentPasswordFieldValueChange("password123")

        assertFalse(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    @Test
    fun `button disabled with short passwords in CHANGE mode`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.CHANGE)
        vm.onEmailValueChange("test@example.com")
        vm.onCurrentPasswordFieldValueChange("short")
        vm.onNewPasswordFieldValueChange("short")

        assertFalse(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    @Test
    fun `button enabled with all valid fields in CHANGE mode`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.CHANGE)
        vm.onEmailValueChange("test@example.com")
        vm.onCurrentPasswordFieldValueChange("password123")
        vm.onNewPasswordFieldValueChange("newpassword123")

        assertTrue(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    @Test
    fun `button disabled again when email becomes invalid in CHANGE mode`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.CHANGE)
        vm.onEmailValueChange("test@example.com")
        vm.onCurrentPasswordFieldValueChange("password123")
        vm.onNewPasswordFieldValueChange("newpassword123")
        assertTrue(vm.forgotPasswordUiState.value.enableConfirmButton)

        vm.onEmailValueChange("invalid")
        assertFalse(vm.forgotPasswordUiState.value.enableConfirmButton)
    }

    // --- sendReset ---

    @Test
    fun `sendReset with invalid email shows error`() {
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("invalid")
        vm.sendReset()

        assertEquals("Invalid email", vm.forgotPasswordUiState.value.error)
        assertFalse(vm.forgotPasswordUiState.value.loading)
    }

    @Test
    fun `sendReset calls repository with trimmed lowercase email`() = runTest {
        coEvery { authRepo.sendPassword(any()) } returns Result.success(Unit)
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("  TEST@Example.COM  ")
        vm.sendReset()

        coVerify { authRepo.sendPassword("test@example.com") }
    }

    @Test
    fun `sendReset shows loading during request`() = runTest {
        coEvery { authRepo.sendPassword(any()) } returns Result.success(Unit)
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("test@example.com")

        vm.sendReset()

        assertFalse(vm.forgotPasswordUiState.value.loading)
        assertNull(vm.forgotPasswordUiState.value.error)
    }

    @Test
    fun `sendReset emits ResetSent on success`() = runTest {
        coEvery { authRepo.sendPassword(any()) } returns Result.success(Unit)
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("test@example.com")

        vm.events.test {
            vm.sendReset()

            val msg = awaitItem()
            assertTrue(msg is ForgotPasswordEvent.ShowMessage)

            val resetEvent = awaitItem()
            assertTrue(resetEvent is ForgotPasswordEvent.ResetSent)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendReset sets error on failure`() = runTest {
        coEvery { authRepo.sendPassword(any()) } returns Result.failure(
            Exception("Network error")
        )
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("test@example.com")
        vm.sendReset()

        assertEquals("Network error", vm.forgotPasswordUiState.value.error)
        assertFalse(vm.forgotPasswordUiState.value.loading)
    }

    @Test
    fun `sendReset with null exception message shows default error`() = runTest {
        coEvery { authRepo.sendPassword(any()) } returns Result.failure(Exception())
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("test@example.com")
        vm.sendReset()

        assertEquals("Unexpected error. Try again later", vm.forgotPasswordUiState.value.error)
    }

    @Test
    fun `sendReset does nothing when already loading`() = runTest {
        val deferred = CompletableDeferred<Result<Unit>>()
        coEvery { authRepo.sendPassword(any()) } coAnswers { deferred.await() }
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("test@example.com")

        vm.sendReset()
        assertTrue(vm.forgotPasswordUiState.value.loading)

        vm.sendReset()

        coVerify(exactly = 1) { authRepo.sendPassword(any()) }
        deferred.complete(Result.success(Unit))
    }

    @Test
    fun `sendReset clears previous error`() = runTest {
        coEvery { authRepo.sendPassword(any()) } returns Result.failure(Exception("First error"))
        val vm = createViewModel()
        vm.setMode(PasswordMode.RESET)
        vm.onEmailValueChange("test@example.com")
        vm.sendReset()
        assertEquals("First error", vm.forgotPasswordUiState.value.error)

        coEvery { authRepo.sendPassword(any()) } returns Result.success(Unit)
        vm.sendReset()
        assertNull(vm.forgotPasswordUiState.value.error)
    }
}
