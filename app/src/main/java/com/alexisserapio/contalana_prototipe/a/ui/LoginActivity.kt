package com.alexisserapio.contalana_prototipe.a.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sobrescribe la transición de apertura
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // OVERRIDE_TRANSITION_OPEN: Se usa al iniciar la Activity B
            // enterAnim: slide_in_right (Activity B se desliza hacia adentro)
            // exitAnim: slide_out_left (Activity A se desliza hacia afuera)
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_up_activity,
                R.anim.fade_out_activity

            );
        }



    }

    override fun finish() {
        super.finish()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // OVERRIDE_TRANSITION_CLOSE: Se aplica al finalizar
            // R.anim.fade_in: Activity A (ENTRA de nuevo)
            // R.anim.slide_down: Activity B (SALE)
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.fade_in_activity,
                R.anim.slide_down_activity
            )
        }
    }
}