package com.example.pomodorotechnique.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Task(var name: String,
                var dateCreated: String,
                var cyclesCompleted: Int,
                var focusedTime: Long,
                var restTime: Long) : Parcelable