package br.com.listennow.preferences

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "credentials")
val userKey = stringPreferencesKey("userKey")

