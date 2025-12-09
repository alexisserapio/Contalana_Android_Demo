package com.alexisserapio.contalana_prototipe.a.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.data.DataStoreManager
import com.alexisserapio.contalana_prototipe.a.data.dataStore
import com.alexisserapio.contalana_prototipe.a.encryption.EncryptedPrefs
import com.alexisserapio.contalana_prototipe.databinding.ActivityBnameBinding
import com.google.crypto.tink.Aead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBnameBinding
    //Para las shared Preferences
    private lateinit var sp: SharedPreferences

    //Para el cifrado
    private lateinit var aead: Aead
    private lateinit var encryptedPrefs: EncryptedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBnameBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar el EditText para mostrar el botón "Done"
        binding.etBName.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.etBName.setSingleLine(true)
        binding.bnameButton.isEnabled = false

        // Detectar cuando el usuario presiona Done
        binding.etBName.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Ocultar el teclado
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else {
                false
            }
        }

        binding.etBName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.bnameButton.isEnabled = !s.isNullOrBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })


        binding.bnameButton.setOnClickListener {
            val bNameRegex = "^[A-Z0-9a-z._$&/\"']{1}[A-Z0-9a-z._$&/\"' ]{3,30}[A-Z0-9a-z._$&/\"']{1}$"

            if (!binding.etBName.text.matches(bNameRegex.toRegex())){
                mostrarAlertaPersonalizada(getString(R.string.bName_alert_notValid))
                binding.etBName.setText(null)
            }
            else if (binding.etBName.text.length>5 && binding.etBName.text.length>32){

                mostrarAlertaPersonalizada(getString(R.string.bName_alert_notValid))

            }else{

                lifecycleScope.launch (Dispatchers.IO){

                    saveValues(binding.etBName.text.toString())
                    val segueToSignInActivity = Intent(this@BNameActivity, SigninActivity::class.java)
                    startActivity(segueToSignInActivity)
                    finish()
                }



            }
        }
    }

    fun mostrarAlertaPersonalizada(errorName: String) {
        // 1. Inflar el diseño personalizado
        val dialogView = LayoutInflater.from(this).inflate(R.layout.b_name_dialog, null)

        // 2. Crear el AlertDialog
        val builder = AlertDialog.Builder(this, R.style.DialogFadeAnimation)
            .setView(dialogView)

        // 3. Crear el diálogo y obtener referencia a los elementos del diseño
        val alertDialog = builder.create()

        // 4. Configurar el comportamiento (texto y click del botón)

        val titleTextView = dialogView.findViewById<TextView>(R.id.alertTitle)
        val messageTextView = dialogView.findViewById<TextView>(R.id.alertMessage)
        val positiveButton = dialogView.findViewById<Button>(R.id.alertPositiveButton)

        titleTextView.text = getString(R.string.bName_alert_title)
        messageTextView.text = errorName

        positiveButton.setOnClickListener {
            // Lógica a ejecutar al hacer clic en 'Aceptar'
            alertDialog.dismiss() // Cierra el diálogo
        }

        // Opcional: Remover el fondo por defecto para ver mejor el diseño personalizado
        // y darle un toque más limpio, similar a iOS
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 5. Mostrar el diálogo
        alertDialog.show()
    }

    private suspend fun saveValues(businessName: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreManager.BUSINESS_NAME] = businessName
            preferences[DataStoreManager.BUSINESS_EXISTS] = true
        }
    }

}
