package com.example.pomodorotechnique.models

sealed class TimerState {

    object OnFocusRunning : TimerState()
    object OnRestRunning : TimerState()
    object OnFocusPaused : TimerState()
    object OnRestPaused : TimerState()
    object Completed : TimerState()
    object RestCompleted : TimerState()
    object NotStarted : TimerState()

}
