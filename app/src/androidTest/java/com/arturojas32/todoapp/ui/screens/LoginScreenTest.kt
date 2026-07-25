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
import com.arturojas32.todoapp.ui.viewmodels.LoginViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeAuthRepo: FakeAuthRepository

    @Before
    fun setUp() {
        fakeAuthRepo = FakeAuthRepository()
    }

    private fun setContent(
        onLoginClick: () -> Unit = {},
        onGoToRegisterScreen: () -> Unit = {}
    ): LoginViewModel {
        val viewModel = LoginViewModel(fakeAuthRepo)
        composeTestRule.setContent {
            LoginScreen(
                loginViewModel = viewModel,
                onLoginClick = onLoginClick,
                onGoToRegisterScreen = onGoToRegisterScreen
            )
        }
        return viewModel
    }

    // --- Input flows to ViewModel ---

    @Test
    fun typingInEmailField_updatesViewModelState() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")

        assert(vm.loginScreenUiState.value.email == "test@example.com")
    }

    @Test
    fun typingInPasswordField_updatesViewModelState() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Password")
            .performTextInput("mypassword123")

        assert(vm.loginScreenUiState.value.password == "mypassword123")
    }

    // --- Button state reflects validation ---

    @Test
    fun loginButton_disabled_initially() {
        val vm = setContent()

        assert(!vm.loginScreenUiState.value.isLoginButtonEnabled)

        composeTestRule.onNodeWithText("Log in")
            .assertIsNotEnabled()
    }

    @Test
    fun loginButton_enabled_after_valid_input() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule.onNodeWithText("Log in")
            .assertIsEnabled()
    }

    @Test
    fun loginButton_disabled_after_clearing_email() {
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule.waitForIdle()
        assert(vm.loginScreenUiState.value.isLoginButtonEnabled)

        vm.onUserTextFieldValueChange("")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Log in")
            .assertIsNotEnabled()
    }

    // --- Login button click ---

    @Test
    fun loginButton_click_triggersSignIn() {
        fakeAuthRepo.signInResult = Result.success(Unit)
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule.onNodeWithText("Log in")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            fakeAuthRepo.signInCalled
        }
        assert(fakeAuthRepo.lastEmail == "test@example.com")
        assert(fakeAuthRepo.lastPassword == "password123")
    }

    // --- Navigation ---

    @Test
    fun successfulLogin_invokesOnLoginClick() {
        fakeAuthRepo.signInResult = Result.success(Unit)
        var loginClicked = false
        setContent(onLoginClick = { loginClicked = true })

        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")
        composeTestRule.onNodeWithText("Log in")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) { loginClicked }
        assert(loginClicked)
    }

    @Test
    fun registerButton_invokesOnGoToRegisterScreen() {
        var registerClicked = false
        setContent(onGoToRegisterScreen = { registerClicked = true })

        composeTestRule.onNodeWithText("Register")
            .performClick()

        assert(registerClicked)
    }

    // --- Password visibility ---

    @Test
    fun passwordVisibility_icon_togglesState() {
        val vm = setContent()

        assert(!vm.loginScreenUiState.value.passwordVisibility)

        composeTestRule.onNodeWithContentDescription("password visibility clickable icon")
            .performClick()

        assert(vm.loginScreenUiState.value.passwordVisibility)

        composeTestRule.onNodeWithContentDescription("password visibility clickable icon")
            .performClick()

        assert(!vm.loginScreenUiState.value.passwordVisibility)
    }

    // --- Error propagation ---

    @Test
    fun failedLogin_showsErrorText() {
        fakeAuthRepo.signInResult = Result.failure(Exception("Invalid credentials"))
        setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")
        composeTestRule.onNodeWithText("Log in")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Invalid credentials").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    // --- Fields disabled during loading ---

    @Test
    fun fields_disabled_during_loading() {
        fakeAuthRepo.signInDelay = 2000
        fakeAuthRepo.signInResult = Result.success(Unit)
        val vm = setContent()

        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule.onNodeWithText("Log in")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            vm.loginScreenUiState.value.loading
        }

        composeTestRule.onNodeWithText("Email")
            .assertIsNotEnabled()
        composeTestRule.onNodeWithText("Password")
            .assertIsNotEnabled()
    }
}
