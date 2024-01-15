package br.com.listennow.preferences

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "credentials")
val userKey = longPreferencesKey("userKey")