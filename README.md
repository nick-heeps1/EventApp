EventApp 📅
A Personal Event Planner Android app built with Kotlin. Users can create, view, edit and delete upcoming events, with all data stored locally on the device.

Features

Add, view, edit and delete events
Events sorted automatically by date
Input validation with Snackbar feedback
Past date prevention when creating new events
Data persists after app closure and device restarts


Tech Stack

Language — Kotlin
Database — Room Persistence Library
Navigation — Jetpack Navigation Component
Architecture — MVVM
UI — Material Design Components
Async — Kotlin Coroutines + LiveData


Project Structure
data/           — Room database, DAO, and repository
viewmodel/      — EventViewModel
ui/             — Fragments and RecyclerView adapter
res/layout/     — XML layouts
res/navigation/ — Navigation graph

Setup

Open project in Android Studio
Let Gradle sync
Run on a device or emulator (API 24+)
