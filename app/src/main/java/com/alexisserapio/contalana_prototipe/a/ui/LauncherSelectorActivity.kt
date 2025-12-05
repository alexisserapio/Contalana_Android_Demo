package com.alexisserapio.contalana_prototipe.a.ui

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.lifecycle.lifecycleScope
import com.alexisserapio.contalana_prototipe.a.data.DataStoreManager
import com.alexisserapio.contalana_prototipe.a.data.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LauncherSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        lifecycleScope.launch {
            // Leemos las preferencias guardadas
            val preferences = dataStore.data.first()

            val businessExists = preferences[DataStoreManager.BUSINESS_EXISTS] ?: false
            val formAnswered = preferences[DataStoreManager.FORM_ANSWERED] ?: false
            val userExists = preferences[DataStoreManager.USER_EXISTS] ?: false

            if(!businessExists){
                startActivity(Intent(this@LauncherSelectorActivity, WelcomeActivity::class.java))

            } else if (!userExists){
                startActivity(Intent(this@LauncherSelectorActivity, SigninActivity::class.java))

            } else if (!formAnswered){
                startActivity(Intent(this@LauncherSelectorActivity, FormActivity::class.java))

            }else{
                startActivity(Intent(this@LauncherSelectorActivity, LoginActivity::class.java))

            }

            finish() // Cerramos esta activity para no volver a ella
        }
    }
}
