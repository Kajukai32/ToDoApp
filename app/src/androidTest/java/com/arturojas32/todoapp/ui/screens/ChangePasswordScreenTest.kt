package com.arturojas32.todoapp.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arturojas32.todoapp.ui.FakeAuthRepository
import com.arturojas32.todoapp.ui.viewmodels.ChangePasswordViewModel
import com.arturojas32.todoapp.ui.viewmodels.PasswordMode
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChangePasswordScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeAuthRepo: FakeAuthRepository

    @Before
    fun setUp() {
        fakeAuthRepo = FakeAuthRepository()
    }

    private fun setContent(
        mode: PasswordMode = PasswordMode.RESET,
        onBackClick: () -> Unit = {},
        onNavigateToLoginAndResetBackStack: () -> Unit = {}
    ): ChangePasswordViewModel {
        val viewModel = ChangePasswordViewModel(fakeAuthRepo)
        composeTestRule.setContent {
            ChangePasswordScreen(
                vm = viewModel,
                mode = mode,
                onBackClick = onBackClick,
                onNavigateToLoginAndResetBackStack = onNavigateToLoginAndResetBackStack
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

    // --- Title based on mode ---

    @Test
    fun resetMode_showsResetTitle() {
        setContent(mode = PasswordMode.RESET)

        composeTestRule.onNodeWithText("Reset your password")
            .assertIsDisplayed()
    }

    @Test
    fun changeMode_showsChangeTitle() {
        setContent(mode = PasswordMode.CHANGE)

        composeTestRule.onNodeWithText("Change your password")
            .assertIsDisplayed()
    }

    // --- Email input ---

    @Test
    fun typingEmail_updatesViewModelState() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performClick()
        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")

        assert(vm.forgotPasswordUiState.value.email == "test@example.com")
    }

    // --- Password fields visibility by mode ---

    @Test
    fun resetMode_passwordFieldsNotShown() {
        setContent(mode = PasswordMode.RESET)

        composeTestRule.onNodeWithText("Current password")
            .assertIsNotDisplayed()
        composeTestRule.onNodeWithText("New password")
            .assertIsNotDisplayed()
    }

    @Test
    fun changeMode_passwordFieldsShown() {
        setContent(mode = PasswordMode.CHANGE)

        composeTestRule.onNodeWithText("Current password")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("New password")
            .assertIsDisplayed()
    }

    // --- Password input (CHANGE mode) ---

    @Test
    fun typingCurrentPassword_updatesViewModelState() {
        val vm = setContent(mode = PasswordMode.CHANGE)

        composeTestRule.onNodeWithText("Current password")
            .performClick()
        composeTestRule.onNodeWithText("Current password")
            .performTextInput("oldpass123")

        assert(vm.forgotPasswordUiState.value.currentPasswordField == "oldpass123")
    }

    @Test
    fun typingNewPassword_updatesViewModelState() {
        val vm = setContent(mode = PasswordMode.CHANGE)

        composeTestRule.onNodeWithText("New password")
            .performClick()
        composeTestRule.onNodeWithText("New password")
            .performTextInput("newpass123")

        assert(vm.forgotPasswordUiState.value.newPasswordField == "newpass123")
    }

    // --- Button text by mode ---

    @Test
    fun resetMode_showsResetButton() {
        setContent(mode = PasswordMode.RESET)

        composeTestRule.onNodeWithText("Reset")
            .assertIsDisplayed()
    }

    @Test
    fun changeMode_showsConfirmButton() {
        setContent(mode = PasswordMode.CHANGE)

        composeTestRule.onNodeWithText("Confirm")
            .assertIsDisplayed()
    }

    // --- Error display ---

    @Test
    fun errorDisplayed_whenVmHasError() {
        val vm = setContent()
        vm.onEmailValueChange("invalid")
        vm.sendReset()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Invalid email")
            .assertIsDisplayed()
    }

    // --- Confirm button click triggers sendReset ---

    @Test
    fun confirmButton_triggersSendReset() {
        val vm = setContent(mode = PasswordMode.RESET)
        vm.onEmailValueChange("test@example.com")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Reset")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            fakeAuthRepo.lastEmail != null
        }
        assert(fakeAuthRepo.lastEmail == "test@example.com")
    }

    // --- Navigation on reset sent ---

    @Test
    fun resetSent_navigatesBackToLogin() {
        var navigated = false
        val vm = setContent(
            onNavigateToLoginAndResetBackStack = { navigated = true }
        )

        vm.onEmailValueChange("test@example.com")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Reset")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) { navigated }
        assert(navigated)
    }

    // --- Email pre-fill for logged-in user ---

    @Test
    fun emailPreFilled_whenUserLoggedIn() {
        fakeAuthRepo.fakeUid = "test-uid"
        fakeAuthRepo.fakeEmail = "user@example.com"

        val vm = setContent(mode = PasswordMode.RESET)

        assert(vm.forgotPasswordUiState.value.email == "user@example.com")
    }

    @Test
    fun emailEmpty_whenNoUserLoggedIn() {
        val vm = setContent(mode = PasswordMode.RESET)

        assert(vm.forgotPasswordUiState.value.email == "")
    }
}
