package com.alexisserapio.contalana_prototipe.a.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "USER_PREFERENCES_BUSSINESNAME")

object DataStoreManager {
    val BUSINESS_NAME = stringPreferencesKey("businessName")
    val BUSINESS_EXISTS = booleanPreferencesKey("businessExists")
    val FORM_ANSWERED = booleanPreferencesKey("formAnswered")
    val USER_EXISTS = booleanPreferencesKey("userExists")
}