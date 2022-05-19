package com.example.pomodorotechnique.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pomodorotechnique.MainActivity
import com.example.pomodorotechnique.R

// Notification ID.
private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0
private const val FLAGS = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context){

    //Intent to open MainActivity when the user clicks the notification
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    //this to come back to the current Screen when notification is clicked
    contentIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

    val contentPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_IMMUTABLE)
    } else {
        TODO("VERSION.SDK_INT < M")
    }

    val alarmSound : Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val builder = NotificationCompat.Builder(
        applicationContext, applicationContext.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.tomato)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setOnlyAlertOnce(true)
        //cancel when user clicks
        .setAutoCancel(true)
        .setSound(alarmSound)
        //adding the button
        .addAction(
            R.drawable.tomato,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications(){
    cancelAll()
}

