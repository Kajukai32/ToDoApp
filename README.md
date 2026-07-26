# ToDoApp

A task management Android app built with Jetpack Compose, featuring user authentication, real-time cross-device sync, and offline support.

## Features

- **User Authentication** — Sign up, log in, log out, stay logged in, and forgot/change password via Firebase Auth
- **Task Management** — Create, update, delete, and mark tasks as done with deadlines
- **Search & Sort** — Filter tasks by title or description; sort by title, deadline, or completion status
- **Cross-device Sync** — Real-time synchronization with Firebase Realtime Database with offline-first Room database
- **Side Menu Drawer** — Navigation drawer with reset password, change password, and log out options
- **Material 3 Design** — Modern UI with animated components, custom top bars, and responsive layouts

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose, Material 3, Compose Navigation (type-safe) |
| Architecture | MVVM, Clean Architecture (data / domain / ui) |
| Local DB | Room |
| DI | Hilt |
| Auth | Firebase Authentication |
| Remote DB | Firebase Realtime Database |
| Background Sync | WorkManager |
| Async | Kotlin Coroutines + Flow |
| Serialization | Kotlinx Serialization |
| Testing | JUnit, MockK, Turbine, Robolectric, Compose Testing |

> **Note on offline support:** Firebase RTDB has built-in offline persistence, but this project uses Room as the source of truth for offline-first access. Tasks are always read from/written to Room first, then synced to Firebase in the background via WorkManager. This gives full control over conflict resolution, sync flags, and data filtering (e.g. per-user queries) that RTDB's automatic persistence doesn't easily support.

## Project Structure

```
app/src/main/java/com/arturojas32/todoapp/
├── data/
│   ├── di/                          # Hilt modules (AuthModule, PersistenceModule)
│   ├── local/
│   │   ├── dao/                     # Room DAOs
│   │   ├── database/                # Room DB, DataStore
│   │   ├── entities/                # Room entities
│   │   └── repository/              # TaskRepositoryImpl
│   ├── mappers/                     # Entity <-> Domain <-> Remote mappers
│   └── network/
│       ├── auth/data/               # AuthRepositoryImpl (Firebase Auth)
│       └── remotedb/                # RemoteDbRepositoryImpl (Firebase RTDB), RemoteTask DTO
├── domain/
│   ├── model/                       # Task, AuthUser
│   └── repository/                  # TaskRepository, AuthRepository, RemoteDbRepository interfaces
├── navigation/                      # NavWrapper, route definitions
├── ui/
│   ├── components/                  # Reusable composables (MyTopBar, MyEmailTextField, etc.)
│   ├── screens/                     # Screen composables
│   └── viewmodels/                  # ViewModels
└── utils/                           # Validators, date helpers, SyncManager
```

## Screens

| Screen | Description |
|--------|-------------|
| LoginScreen | Email/password login with stay-logged-in option, forgot password link |
| RegisterScreen | New account creation with stay-logged-in option |
| TaskListScreen | Task list with search bar, sort options, side menu drawer, FAB to add tasks |
| AddTaskScreen | Create or edit tasks with title, description, and deadline picker |
| ChangePasswordScreen | Dual-mode: reset password (from login) or change password (logged in) |

## Testing

The project includes **190+ tests** across unit and instrumented test suites:

### Unit Tests (src/test)

| Test Class | Tests | What it covers |
|-----------|-------|---------------|
| LoginViewModelTest | 17 | Login flow, validation, events, auth state |
| RegisterViewModelTest | 14 | Register flow, validation, stay logged in |
| TaskListViewModelTest | 15 | Task list, search, sort, log out |
| TaskFeaturesViewModelTest | 15 | Task CRUD, deadline, isDone toggle |
| ChangePasswordViewModelTest | 24 | Reset + change password, mode switching, validation |
| TaskMapperTest | 14 | Entity/domain/remote mappers, round-trip mappings |
| UtilsTest | 13 | Validators, date formatting, toReadable |

### Instrumented Tests (src/androidTest)

| Test Class | Tests | What it covers |
|-----------|-------|---------------|
| LoginScreenTest | 12 | Text input, button states, login, navigation, errors, forgot password |
| RegisterScreenTest | 13 | Text input, button states, register, navigation, stay logged in toggle |
| TaskListScreenTest | 16 | Task display, search, sort, FAB, task click, drawer, checkbox, delete/undo |
| AddTaskScreenTest | 10 | Text input, save, back navigation, update flow, default state |
| ChangePasswordScreenTest | 18 | Reset/change modes, field visibility, email pre-fill, navigation, errors |
| TaskDaoTest | 20 | Room DAO CRUD, filtering, sync flags, conflicts |

### Running Tests

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (requires connected device/emulator)
./gradlew connectedDebugAndroidTest

# Single test class
./gradlew testDebugUnitTest --tests "com.arturojas32.todoapp.ui.viewmodels.LoginViewModelTest"
```

### Test Philosophy

Instrumented tests focus on **Compose-ViewModel integration logic** — verifying that user interactions update VM state and trigger navigation callbacks. They do **not** assert visual properties like text content or button enabled states, since those are driven by VM state that is already covered in unit tests.

Shared fakes (`FakeAuthRepository`, `FakeTaskRepository`, `FakeRemoteDbRepository`) are used instead of MockK in instrumented tests to avoid JVMTI agent issues on Android.

## Getting Started

1. Clone the repository
2. Add your `google-services.json` from Firebase Console to `app/`
3. Enable Firebase Authentication (Email/Password) and Realtime Database in your Firebase project
4. Build and run:
   ```bash
   ./gradlew assembleDebug
   ```

## Branch Strategy

- `develop` — default branch, integration target
- `master` — production releases
- Feature branches (`feature/*`) — created from `develop`, merged via PR
