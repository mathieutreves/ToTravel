package com.example.travelsharingapp.ui.screens.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.travelsharingapp.R
import com.example.travelsharingapp.data.model.NotificationType
import com.example.travelsharingapp.data.repository.NotificationPreferenceKeys
import com.example.travelsharingapp.data.repository.dataStoreInstance
import com.example.travelsharingapp.ui.widget.UpdateWidgetWorker
import com.example.travelsharingapp.utils.FcmTokenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger

class CustomFirebaseMessagingService: FirebaseMessagingService() {
    companion object {
        const val DEFAULT_CHANNEL_ID = "toTravel_default_channel"
    }

    private val notificationIdCounter = AtomicInteger(0)
    private lateinit var dataStore: DataStore<Preferences>

    override fun onCreate() {
        super.onCreate()
        dataStore = applicationContext.dataStoreInstance
    }

    override fun onNewToken(token: String) {
        FcmTokenManager.handleTokenRefresh(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val recipientId = data["recipientId"]
        val currentLoggedInUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentLoggedInUserId == null || recipientId != currentLoggedInUserId) {
            return
        }

        val title = data["title"] ?: "New Notification"
        val body = data["body"] ?: "You have a new message."
        val notificationTypeString = data["notificationType"]

        if (shouldShowNotification(notificationTypeString)) {
            sendNotification(title, body, data)
        }
    }

    private fun shouldShowNotification(notificationTypeKeyFromFcm: String?): Boolean {
        return runBlocking {
            try {
                val preferences = dataStore.data.first()

                val masterEnabled = preferences[NotificationPreferenceKeys.MASTER_NOTIFICATIONS_ENABLED] != false
                if (!masterEnabled) {
                    return@runBlocking false
                }

                if (notificationTypeKeyFromFcm == null) {
                    return@runBlocking true
                }

                val notificationType = NotificationType.fromKey(notificationTypeKeyFromFcm)
                if (notificationType == null) {
                    return@runBlocking true
                }

                val typeEnabled = preferences[notificationType.dataStoreKey] != false
                return@runBlocking typeEnabled
            } catch (_: Exception) {
                return@runBlocking true
            }
        }
    }

    private fun sendNotification(title: String, messageBody: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val notificationType = data["notificationType"]
        val proposalId = data["proposalId"]
        var deepLinkUriString: String? = null

        when (notificationType) {
            NotificationType.NEW_TRAVEL_REVIEW.key -> if (proposalId != null) {
                deepLinkUriString = "myapp://travelsharingapp.example.com/reviewViewAll/$proposalId"
            }
            NotificationType.NEW_USER_REVIEW.key -> {
                if (FirebaseAuth.getInstance().currentUser?.uid != null) {
                    deepLinkUriString = "myapp://travelsharingapp.example.com/userReviews/${FirebaseAuth.getInstance().currentUser!!.uid}"
                }
            }
            NotificationType.NEW_TRAVEL_APPLICATION.key -> if (proposalId != null) {
                deepLinkUriString = "myapp://travelsharingapp.example.com/manageApplications/$proposalId"
            }
            NotificationType.TRAVEL_APPLICATION_ACCEPTED.key -> if (proposalId != null) {
                deepLinkUriString = "myapp://travelsharingapp.example.com/travelProposalInfo/$proposalId"

                // Also refresh widget
                UpdateWidgetWorker.enqueueImmediateWidgetUpdate(applicationContext)
            }
            NotificationType.TRAVEL_APPLICATION_REJECTED.key -> if (proposalId != null) {
                deepLinkUriString = "myapp://travelsharingapp.example.com/travelProposalInfo/$proposalId"
            }
            NotificationType.NEW_CHAT_MESSAGE.key -> if (proposalId != null) {
                deepLinkUriString = "myapp://travelsharingapp.example.com/chat/$proposalId"
            }
            else -> {
            }
        }

        if (deepLinkUriString != null) {
            intent.action = Intent.ACTION_VIEW
            intent.data = deepLinkUriString.toUri()
        }

        val notificationId = notificationIdCounter.incrementAndGet()
        val pendingIntentFlags = PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(this, notificationId, intent, pendingIntentFlags)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_logo)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            "Travel App Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Channel for general app notifications"
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}

@Composable
fun NotificationRationaleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Enable Notifications?") },
        text = {
            Text(
                "To help you stay updated with your travel plans, we'd like to send you notifications. " +
                        "This includes alerts for:\n\n" +
                        "• New reviews on your owned travels\n" +
                        "• New reviews about you as a traveler/organizer\n" +
                        "• When someone applies to join one of your travels\n\n" +
                        "Allowing notifications ensures you don't miss these important updates."
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Allow Notifications")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No, Thanks")
            }
        }
    )
}

@Composable
fun PermissionDeniedPermanentlyDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Notifications Disabled") },
        text = {
            Text(
                "You have previously denied notification permissions, or they are restricted by system policy. " +
                        "If you want to receive updates about your travels, new reviews, and applications, " +
                        "you'll need to enable notifications for this app in your device settings."
            )
        },
        confirmButton = {
            Button(onClick = onOpenSettings) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Maybe Later")
            }
        }
    )
}