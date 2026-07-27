# ToDoApp

A task management Android app built with Jetpack Compose, featuring user authentication, real-time sync with Firebase, and offline support with Room DB.

## Features

- **User Authentication** — Register, login, stay logged in, forgot password, and change password via Firebase Auth
- **Task CRUD** — Create, edit, delete tasks with title, description, and deadline
- **Task List** — Sort by default/completed, search by title or description, mark tasks as done
- **Cross-device Sync** — Background sync with Firebase Realtime Database via WorkManager
- **Offline Support** — Room local database with sync-on-reconnect
- **Side Menu Drawer** — Reset password, change password, and log out

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose, Material 3 |
| Navigation | Compose Navigation 2.9 (type-safe routes) |
| DI | Hilt |
| Local DB | Room |
| Remote DB | Firebase Realtime Database |
| Auth | Firebase Auth |
| Background | WorkManager |
| Persistence | DataStore Preferences |

## Screenshots

### Login

![Login screen](screenshots/login/Screenshot_20260726_222000.png)
![Login with credentials](screenshots/login/Screenshot_20260726_222142.png)

### Register

![Register screen](screenshots/register/Screenshot_20260726_222300.png)
![Register form filled](screenshots/register/Screenshot_20260726_222327.png)
![Register confirmation](screenshots/register/Screenshot_20260726_222341.png)

### Task List

![Task list](screenshots/tasklistscreen/Screenshot_20260726_222532.png)
![Task list populated](screenshots/tasklistscreen/Screenshot_20260726_222632.png)
![Task list sort](screenshots/tasklistscreen/Screenshot_20260726_222727.png)
![Task list search](screenshots/tasklistscreen/Screenshot_20260726_222851.png)
![Task list drawer](screenshots/tasklistscreen/Screenshot_20260726_222901.png)
![Task list completed](screenshots/tasklistscreen/Screenshot_20260726_223026.png)

### Reset Password

![Reset password screen](screenshots/resetpassword/Screenshot_20260726_223258.png)
![Reset password sent](screenshots/resetpassword/Screenshot_20260726_223516.png)

### Change Password

![Change password screen](screenshots/changepassword/Screenshot_20260726_223641.png)
![Change password filled](screenshots/changepassword/Screenshot_20260726_223741.png)

### New / Edit Task

![New task screen](screenshots/neworedittask/Screenshot_20260726_223820.png)
![New task form](screenshots/neworedittask/Screenshot_20260726_223900.png)
![New task filled](screenshots/neworedittask/Screenshot_20260726_223917.png)
![Task saved](screenshots/neworedittask/Screenshot_20260726_223930.png)
![Edit task](screenshots/neworedittask/Screenshot_20260726_223954.png)
![Edit task filled](screenshots/neworedittask/Screenshot_20260726_224000.png)
![Edit task confirmation](screenshots/neworedittask/Screenshot_20260726_224031.png)

## Project Structure

```
app/src/main/java/com/arturojas32/todoapp/
├── data/
│   ├── di/                    # Hilt modules
│   ├── local/                 # Room DB, DAO, entities
│   ├── mappers/               # Entity ↔ Domain mappers
│   └── network/               # Firebase auth & remote DB
├── domain/
│   ├── model/                 # Domain models (Task, AuthUser)
│   └── repository/            # Repository interfaces
├── navigation/                # NavWrapper, routes
├── ui/
│   ├── components/            # Reusable composables
│   ├── screens/               # Screen composables
│   ├── viewmodels/            # ViewModels
│   └── theme/                 # Material theme
└── utils/                     # Validators, SyncManager
```

## Getting Started

1. Clone the repo
2. Add your `google-services.json` in `app/`
3. Build and run on device/emulator (min SDK 26)
