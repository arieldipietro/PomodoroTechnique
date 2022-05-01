package com.example.pomodorotechnique.models

//making it an enum class so there's no need to add a typeConverter in the database
enum class TimerState {

    OnFocusRunning,
    OnRestRunning,
    OnFocusPaused,
    OnRestPaused,
    Completed,
    RestCompleted,
    NotStarted

}
