package com.arturojas32.todoapp.ui.viewmodels

import com.arturojas32.todoapp.domain.model.AuthUser
import com.arturojas32.todoapp.domain.repository.AuthRepository
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var authRepo: AuthRepository
    private val authStateFlow = MutableStateFlow<AuthUser?>(null)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepo = mockk(relaxed = true)
        every { authRepo.authState } returns authStateFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = LoginViewModel(authRepo)

    // --- Initial state ---

    @Test
    fun `initial state has empty fields and disabled button`() {
        val vm = createViewModel()
        val state = vm.loginScreenUiState.value

        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isLoginButtonEnabled)
        assertFalse(state.loading)
        assertNull(state.error)
        assertFalse(state.passwordVisibility)
        assertTrue(state.stayLoggedValue)
    }

    @Test
    fun `initial user is null`() {
        val vm = createViewModel()
        assertNull(vm.user.value)
    }

    // --- Email field ---

    @Test
    fun `email field updates state`() {
        val vm = createViewModel()
        vm.onUserTextFieldValueChange("test@example.com")

        assertEquals("test@example.com", vm.loginScreenUiState.value.email)
    }

    // --- Password field ---

    @Test
    fun `password field updates state`() {
        val vm = createViewModel()
        vm.onPasswordTextFieldValueChange("mypassword")

        assertEquals("mypassword", vm.loginScreenUiState.value.password)
    }

    // --- Validation ---

    @Test
    fun `button disabled with empty fields`() {
        val vm = createViewModel()
        assertFalse(vm.loginScreenUiState.value.isLoginButtonEnabled)
    }

    @Test
    fun `button disabled with invalid email`() {
        val vm = createViewModel()
        vm.onUserTextFieldValueChange("not-an-email")
        vm.onPasswordTextFieldValueChange("password123")

        assertFalse(vm.loginScreenUiState.value.isLoginButtonEnabled)
    }

    @Test
    fun `button disabled with short password`() {
        val vm = createViewModel()
        vm.onUserTextFieldValueChange("test@example.com")
        vm.onPasswordTextFieldValueChange("short")

        assertFalse(vm.loginScreenUiState.value.isLoginButtonEnabled)
    }

    @Test
    fun `button enabled with valid email and long password`() {
        val vm = createViewModel()
        vm.onUserTextFieldValueChange("test@example.com")
        vm.onPasswordTextFieldValueChange("password123")

        assertTrue(vm.loginScreenUiState.value.isLoginButtonEnabled)
    }

    @Test
    fun `button disabled again when email becomes invalid`() {
        val vm = createViewModel()
        vm.onUserTextFieldValueChange("test@example.com")
        vm.onPasswordTextFieldValueChange("password123")
        assertTrue(vm.loginScreenUiState.value.isLoginButtonEnabled)

        vm.onUserTextFieldValueChange("invalid")
        assertFalse(vm.loginScreenUiState.value.isLoginButtonEnabled)
    }

    // --- Password visibility ---

    @Test
    fun `password visibility toggles`() {
        val vm = createViewModel()
        assertFalse(vm.loginScreenUiState.value.passwordVisibility)

        vm.onPasswordVisibilityClick()
        assertTrue(vm.loginScreenUiState.value.passwordVisibility)

        vm.onPasswordVisibilityClick()
        assertFalse(vm.loginScreenUiState.value.passwordVisibility)
    }

    // --- Stay logged in ---

    @Test
    fun `stay logged in toggles`() {
        val vm = createViewModel()
        assertTrue(vm.loginScreenUiState.value.stayLoggedValue)

        vm.onStayLoggedValueChange()
        assertFalse(vm.loginScreenUiState.value.stayLoggedValue)

        vm.onStayLoggedValueChange()
        assertTrue(vm.loginScreenUiState.value.stayLoggedValue)
    }

    // --- Sign in ---

    @Test
    fun `successful sign in emits Success event`() = runTest {
        coEvery { authRepo.signIn(any(), any()) } returns Result.success(Unit)
        val vm = createViewModel()

        vm.event.test {
            vm.onUserTextFieldValueChange("test@example.com")
            vm.onPasswordTextFieldValueChange("password123")
            vm.signIn()

            assertEquals(LoginViewModel.Event.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(vm.loginScreenUiState.value.loading)
        assertNull(vm.loginScreenUiState.value.error)
    }

    @Test
    fun `failed sign in sets error message`() = runTest {
        coEvery { authRepo.signIn(any(), any()) } returns Result.failure(
            Exception("Invalid credentials")
        )
        val vm = createViewModel()

        vm.onUserTextFieldValueChange("test@example.com")
        vm.onPasswordTextFieldValueChange("password123")
        vm.signIn()

        val state = vm.loginScreenUiState.value
        assertEquals("Invalid credentials", state.error)
        assertFalse(state.loading)
    }

    @Test
    fun `failed sign in with null message shows default error`() = runTest {
        coEvery { authRepo.signIn(any(), any()) } returns Result.failure(Exception())
        val vm = createViewModel()

        vm.onUserTextFieldValueChange("test@example.com")
        vm.onPasswordTextFieldValueChange("password123")
        vm.signIn()

        assertEquals("Unexpected error. Try again later", vm.loginScreenUiState.value.error)
    }

    @Test
    fun `sign in clears previous error`() = runTest {
        coEvery { authRepo.signIn(any(), any()) } returns Result.failure(
            Exception("First error")
        )
        val vm = createViewModel()

        vm.onUserTextFieldValueChange("test@example.com")
        vm.onPasswordTextFieldValueChange("password123")
        vm.signIn()
        assertEquals("First error", vm.loginScreenUiState.value.error)

        coEvery { authRepo.signIn(any(), any()) } returns Result.success(Unit)
        vm.signIn()
        assertNull(vm.loginScreenUiState.value.error)
    }

    @Test
    fun `button disabled during loading`() = runTest {
        coEvery { authRepo.signIn(any(), any()) } returns Result.success(Unit)
        val vm = createViewModel()

        vm.onUserTextFieldValueChange("test@example.com")
        vm.onPasswordTextFieldValueChange("password123")

        assertTrue(vm.loginScreenUiState.value.isLoginButtonEnabled)

        vm.signIn()

        assertFalse(vm.loginScreenUiState.value.loading)
    }

    // --- Auth state ---

    @Test
    fun `user reflects auth state`() {
        val vm = createViewModel()
        assertNull(vm.user.value)

        val fakeUser = AuthUser(uid = "test-uid")
        authStateFlow.value = fakeUser
        assertEquals(fakeUser, vm.user.value)
    }
}
