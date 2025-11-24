package com.example.gopayurself.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gopayurself.MainActivity
import com.example.gopayurself.R

object NotificationHelper {
    private const val CHANNEL_ID = "gopayurself_expenses"
    private const val CHANNEL_NAME = "Expense Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications for new expenses and payment reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showExpenseNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun showNewExpenseNotification(context: Context, groupName: String, amount: Double, paidBy: String) {
        val title = "New Expense in $groupName"
        val message = "$paidBy added an expense of $${String.format("%.2f", amount)}"
        showExpenseNotification(context, title, message)
    }

    fun showPaymentReminderNotification(context: Context, groupName: String, amount: Double, owedTo: String) {
        val title = "Payment Reminder"
        val message = "You owe $${String.format("%.2f", amount)} to $owedTo in $groupName"
        showExpenseNotification(context, title, message)
    }

    fun showPaymentReceivedNotification(context: Context, groupName: String, amount: Double, paidBy: String) {
        val title = "Payment Received"
        val message = "$paidBy paid you $${String.format("%.2f", amount)} in $groupName"
        showExpenseNotification(context, title, message)
    }

    fun sendReminderToDebtor(context: Context, groupName: String, debtorName: String, amount: Double) {
        val title = "Reminder Sent"
        val message = "Reminder sent to $debtorName for $${String.format("%.2f", amount)} in $groupName"
        showExpenseNotification(context, title, message)
    }
}