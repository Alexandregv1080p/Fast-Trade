package com.fasttrade.android.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "fasttrade_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_NAME  = stringPreferencesKey("user_name")
        private val KEY_EMAIL = stringPreferencesKey("user_email")
        private val KEY_ROLE  = stringPreferencesKey("user_role")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val userName: Flow<String?> = context.dataStore.data.map { it[KEY_NAME] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[KEY_EMAIL] }

    /**
     * Cópia do token em memória, lida de forma síncrona pelo interceptor do OkHttp —
     * assim ele não faz `runBlocking` numa leitura de DataStore a cada request.
     * ponytail: preenchido com um runBlocking único na construção do singleton.
     */
    @Volatile
    var cachedToken: String? = null
        private set

    init {
        cachedToken = runBlocking { getTokenOnce() }
    }

    suspend fun saveSession(token: String, name: String, email: String, role: String) {
        context.dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_NAME]  = name
            it[KEY_EMAIL] = email
            it[KEY_ROLE]  = role
        }
        cachedToken = token
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
        cachedToken = null
    }

    suspend fun getTokenOnce(): String? {
        var result: String? = null
        context.dataStore.edit { prefs ->
            result = prefs[KEY_TOKEN]
        }
        return result
    }
}
