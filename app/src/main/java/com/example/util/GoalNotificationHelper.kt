package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object GoalNotificationHelper {

    const val CHANNEL_ID = "goal_milestones_channel"
    private const val CHANNEL_NAME = "Goal Milestone Alerts"
    private const val CHANNEL_DESC = "Notifications when savings and financial goals reach key milestones (50%, 75%, 100%)"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = 0xFF10B981.toInt() // Emerald Green
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showMilestoneNotification(
        context: Context,
        goalId: Long,
        goalTitle: String,
        milestonePercent: Int,
        currentAmount: Double,
        targetAmount: Double,
        currencySymbol: String
    ) {
        // Check permission on Android 13+ (Tiramisu, API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                return
            }
        }

        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            (goalId * 10 + milestonePercent).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, body) = when (milestonePercent) {
            100 -> Pair(
                "🎉 100% Goal Completed: $goalTitle!",
                "Incredible milestone! You have fully hit $currencySymbol${String.format("%,.2f", targetAmount)} for $goalTitle."
            )
            75 -> Pair(
                "🔥 75% Milestone Reached: $goalTitle!",
                "You're in the home stretch! $currencySymbol${String.format("%,.2f", currentAmount)} saved out of $currencySymbol${String.format("%,.2f", targetAmount)} (75% achieved)."
            )
            50 -> Pair(
                "⚡ Halfway There: $goalTitle is 50% Funded!",
                "Halfway mark reached! You've accumulated $currencySymbol${String.format("%,.2f", currentAmount)} toward your $currencySymbol${String.format("%,.2f", targetAmount)} target."
            )
            else -> Pair(
                "🎯 $milestonePercent% Milestone: $goalTitle",
                "Progress update: $currencySymbol${String.format("%,.2f", currentAmount)} saved out of $currencySymbol${String.format("%,.2f", targetAmount)}."
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_goal)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        // Distinct notification ID per goal and milestone tier
        val notificationId = (goalId * 1000 + milestonePercent).toInt()
        notificationManager?.notify(notificationId, notification)
    }
}
