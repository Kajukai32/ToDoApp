package com.arturojas32.todoapp.ui.screens

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arturojas32.todoapp.ui.FakeAuthRepository
import com.arturojas32.todoapp.ui.viewmodels.RegisterViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeAuthRepo: FakeAuthRepository

    @Before
    fun setUp() {
        fakeAuthRepo = FakeAuthRepository()
    }

    private fun setContent(
        onBackClick: () -> Unit = {},
        onRegistered: () -> Unit = {}
    ): RegisterViewModel {
        val viewModel = RegisterViewModel(fakeAuthRepo)
        composeTestRule.setContent {
            RegisterScreen(
                registerViewModel = viewModel,
                onBackClick = onBackClick,
                onRegistered = onRegistered
            )
        }
        return viewModel
    }

    // --- Input flows to ViewModel ---

    @Test
    fun typingInEmailField_updatesViewModelState() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("new@example.com")

        assert(vm.registerUiState.value.newUserEmail == "new@example.com")
    }

    @Test
    fun typingInPasswordField_updatesViewModelState() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Password")
            .performTextInput("mypassword123")

        assert(vm.registerUiState.value.newUserPassword == "mypassword123")
    }

    // --- Button state reflects validation ---

    @Test
    fun registerButton_disabled_initially() {
         val vm = setContent()
        assert(!vm.registerUiState.value.isRegisterButtonEnabled)
    }

    @Test
    fun registerButton_enabled_after_valid_input() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("new@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule.onNodeWithText("Create new user")
            .assertIsEnabled()
    }

    @Test
    fun registerButton_disabled_after_clearing_email() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("new@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule.waitForIdle()
        assert(vm.registerUiState.value.isRegisterButtonEnabled)

        vm.onEmailValueChange("")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Create new user")
            .assertIsNotEnabled()
    }

    // --- Register button click ---

    @Test
    fun registerButton_click_triggersRegister() {
        fakeAuthRepo.registerResult = Result.success(Unit)
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("new@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule.onNodeWithText("Create new user")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            fakeAuthRepo.registerCalled
        }
        assert(fakeAuthRepo.lastEmail == "new@example.com")
        assert(fakeAuthRepo.lastPassword == "password123")
    }

    // --- Navigation ---

    @Test
    fun successfulRegister_invokesOnRegistered() {
        fakeAuthRepo.registerResult = Result.success(Unit)
        var registered = false
        setContent(onRegistered = { registered = true })

        composeTestRule.onNodeWithText("Email")
            .performTextInput("new@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")
        composeTestRule.onNodeWithText("Create new user")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) { registered }
        assert(registered)
    }

    @Test
    fun backButton_invokesOnBackClick() {
        var backClicked = false
        setContent(onBackClick = { backClicked = true })

        composeTestRule.onNodeWithText("Go back")
            .performClick()

        assert(backClicked)
    }

    // --- Password visibility ---

    @Test
    fun passwordVisibility_icon_togglesState() {
        val vm = setContent()

        assert(vm.registerUiState.value.passwordVisibility)

        composeTestRule.onNodeWithContentDescription("password visibility clickable icon")
            .performClick()

        assert(!vm.registerUiState.value.passwordVisibility)

        composeTestRule.onNodeWithContentDescription("password visibility clickable icon")
            .performClick()

        assert(vm.registerUiState.value.passwordVisibility)
    }

    // --- Error propagation ---

    @Test
    fun failedRegister_showsErrorText() {
        fakeAuthRepo.registerResult = Result.failure(Exception("Email already in use"))
        val vm = setContent()

        vm.onEmailValueChange("new@example.com")
        vm.onPasswordValueChange("password123")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Create new user").assertIsEnabled()
        composeTestRule.onNodeWithText("Create new user")
            .performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Email already in use").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    // --- Fields disabled during loading ---

    @Test
    fun fields_disabled_during_loading() {
        fakeAuthRepo.registerDelay = 2000
        fakeAuthRepo.registerResult = Result.success(Unit)
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("new@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule.onNodeWithText("Create new user")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            vm.registerUiState.value.loading
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Email")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithText("Password")
            .assertIsNotEnabled()
    }
}
