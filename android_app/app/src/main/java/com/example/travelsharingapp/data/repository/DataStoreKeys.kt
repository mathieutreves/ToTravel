package com.example.travelsharingapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

internal val Context.dataStoreInstance: DataStore<Preferences> by preferencesDataStore(name = "settings")

object AuthPreferenceKeys {
    fun profileExistsKey(userId: String): Preferences.Key<Boolean> =
        booleanPreferencesKey("profile_exists_$userId")

    val LOGGED_IN_USER_ID = stringPreferencesKey("logged_in_user_id")
}

object ThemePreferenceKeys {
    val THEME_KEY = stringPreferencesKey("theme_preference")
}

object NotificationPreferenceKeys {
    val MASTER_NOTIFICATIONS_ENABLED = booleanPreferencesKey("pref_master_notifications_enabled")
    val HAS_SEEN_SWIPE_GUIDE = booleanPreferencesKey("has_seen_swipe_guide")
}

object WidgetPreferenceKeys {
    val UPCOMING_TRAVELS_JSON = stringPreferencesKey("widget_upcoming_travels_json")
}