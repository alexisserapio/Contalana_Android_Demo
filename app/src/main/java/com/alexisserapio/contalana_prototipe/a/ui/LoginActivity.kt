package com.alexisserapio.contalana_prototipe.a.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Snackbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.data.DataStoreManager
import com.alexisserapio.contalana_prototipe.a.data.dataStore
import com.alexisserapio.contalana_prototipe.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.toString

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val minLength = 8
    private val maxLength = 26

    private var emailValidationTimer: CountDownTimer? = null
    private var passwordValidationTimer: CountDownTimer? = null

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

        firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser != null)
            actionLoginSuccessful()

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

        binding.tvForgottenPassword.setOnClickListener {
            resetPassword()
        }

        binding.etMail.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {}

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                isEditTextChanged(p0.toString(), binding.etMail, Patterns.EMAIL_ADDRESS.toRegex())
            }

        })

        binding.etPassword.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                isEditTextChanged(s.toString(), binding.etPassword, """^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&ÁÉÍÓÚáéíóúÑñ ._$&/"'“”]*$""".toRegex())
            }

        })

    }

    private fun actionLoginSuccessful() {

        lifecycleScope.launch(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[DataStoreManager.BUSINESS_EXISTS] = true
                preferences[DataStoreManager.FORM_ANSWERED] = true
                preferences[DataStoreManager.USER_EXISTS] = true
            }

            withContext(Dispatchers.Main){
                val segueToTabBarActivity =
                    Intent(this@LoginActivity, TabBarActivity::class.java)
                startActivity(segueToTabBarActivity)
                finish()
            }
        }



    }

    private fun authenticateUser(mail: String, psw: String, view: View) {
        //Nos autenticamos con firebase auth
        firebaseAuth.signInWithEmailAndPassword(mail, psw)
            .addOnCompleteListener { authResult ->

                if (authResult.isSuccessful) {
                    createSnackbar(getString(R.string.logIn_success), false)
                    actionLoginSuccessful()
                } else {
                    handleErrors(authResult)
                }
            }
    }

    private fun handleErrors(task: Task<AuthResult>) {
        var errorCode: String = ""
        val exception = task.exception

        // 1. Verificación de Excepción de Red y Autenticación
        if (exception is com.google.firebase.FirebaseNetworkException) {
            // Caso Específico 1: Error de Red
            errorCode = "NO_NETWORK"
            Log.d("APPLOGS", "Error de red atrapado.")

        } else if (exception is com.google.firebase.auth.FirebaseAuthException) {
            // Caso Específico 2: Error de Autenticación
            errorCode = exception.errorCode
            Log.d("APPLOGS", "Firebase Auth Error Code: $errorCode")

        } else if (exception != null) {
            // Caso Genérico: Cualquier otra excepción que no sea de Firebase Auth o Red
            Log.d("APPLOGS", "Excepción no manejada: ${exception.javaClass.simpleName}")
            errorCode = "UNEXPECTED_ERROR" // Nuevo código para el 'else'
        }

        when (errorCode) {

            "ERROR_INVALID_CREDENTIAL", "ERROR_WRONG_PASSWORD" -> {
                createSnackbar(getString(R.string.logIn_error_pswOrMailNotValid), true)
                restartUI()
                binding.etPassword.requestFocus()
                binding.etMail.requestFocus()

            }

            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                //An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.
                createSnackbar(getString(R.string.logIn_error_accountWithOtherProvider), true)
                restartUI()
                binding.etMail.requestFocus()
            }

            "ERROR_USER_TOKEN_EXPIRED" -> {
                createSnackbar(getString(R.string.logIn_error_sessionExpired), true)
                restartUI()
            }

            "ERROR_USER_NOT_FOUND" -> {
                createSnackbar(getString(R.string.logIn_error_noAccount), true)
                restartUI()
            }

            "NO_NETWORK" -> {
                Snackbar.make(
                    binding.main,
                    getString(R.string.logIn_error_noConnection),
                    Snackbar.LENGTH_SHORT
                ).show()

                restartUI()
            }

            else -> {
                createSnackbar(getString(R.string.logIn_error_generalError), true)
                restartUI()
            }
        }

    }


    private fun resetPassword(){
        val resetMail = EditText(this)
        resetMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logIn_alert_title))
            .setMessage(getString(R.string.logIn_alert_message))
            .setView(resetMail)
            .setPositiveButton(getString(R.string.logIn_alert_done)){_,_ ->

                val mail = resetMail.text.toString()

                if(mail.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                    firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener {

                        Toast.makeText(
                            this,
                            getString(R.string.logIn_alert_doneMessage),
                            Toast.LENGTH_SHORT
                        )

                    }.addOnFailureListener {

                        Toast.makeText(
                            this,
                            getString(R.string.logIn_alert_failMessage),
                            Toast.LENGTH_LONG
                        )
                    }
                }else{
                    createSnackbar(getString(R.string.logIn_alert_invalidMail),true)
                }
            }
            .setNegativeButton(getString(R.string.logIn_alert_cancel)){dialog,_ ->
                dialog.dismiss()
            }
            .create().show()
    }

    private fun isEditTextChanged(etText: String, editText: android.widget.EditText, regex: Regex){

        updateButtonState()

        val currentTimer = if (editText == binding.etMail) emailValidationTimer else passwordValidationTimer

        currentTimer?.cancel()

        if(etText.count()>0){
            val timer = object : CountDownTimer(2000,2000){
                override fun onFinish() {
                    showError(etText, editText, regex)
                }

                override fun onTick(millisUntilFinished: Long) {}
            }

            if(editText == binding.etMail){
                emailValidationTimer = timer
            }else{
                passwordValidationTimer = timer
            }

            timer.start()

        }else{
            editText.error = null
        }

    }

    private fun updateButtonState() {
        val finalEmail = binding.etMail.text.toString().trim()
        val finalPassword = binding.etPassword.text.toString()

        val emailCorrect = finalEmail.count() in minLength..maxLength &&
                Patterns.EMAIL_ADDRESS.matcher(finalEmail).matches()
        val passwordCorrect = finalPassword.count() in minLength..maxLength &&
                finalPassword.matches(
                    """^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&ÁÉÍÓÚáéíóúÑñ ._$&/"'“”]*$""".toRegex()
                )

        if(emailCorrect && passwordCorrect){
            binding.apply {
                //Login Button
                loginButton.isEnabled = true

                loginButton.setOnClickListener {
                    progressBar.visibility = View.VISIBLE

                    tvMail.visibility = View.INVISIBLE
                    etMail.visibility = View.INVISIBLE
                    tvPassword.visibility = View.INVISIBLE
                    etPassword.visibility = View.INVISIBLE
                    tvForgottenPassword.visibility = View.INVISIBLE

                    authenticateUser(finalEmail, finalPassword, binding.loginButton)
                }
            }
        }else{
            binding.apply {

                //UI
                progressBar.visibility = View.INVISIBLE

                tvMail.visibility = View.VISIBLE
                etMail.visibility = View.VISIBLE
                tvPassword.visibility = View.VISIBLE
                etPassword.visibility = View.VISIBLE
                tvForgottenPassword.visibility = View.VISIBLE

                //Login Button
                loginButton.isEnabled = false
            }
        }
    }

    private fun showError(etText: String, editText: android.widget.EditText, regex: Regex) {

        editText.error = null

        if (etText.count()<minLength){
            binding.apply {
                editText.error = getString(R.string.signIn_error_requiredLength)
                editText.requestFocus()
            }
            return
        }

        if (etText.count()>maxLength){
            binding.apply {
                editText.error = getString(R.string.signIn_error_maxLength)
                editText.requestFocus()
            }
            return
        }

        if(!etText.matches(regex)){
            binding.apply {
                editText.error = getString(R.string.signIn_error_notValid)
                editText.requestFocus()
            }
            return
        }
    }

    private fun restartUI(){
        binding.apply {
            progressBar.visibility = View.INVISIBLE

            tvMail.visibility = View.VISIBLE
            etMail.visibility = View.VISIBLE
            tvPassword.visibility = View.VISIBLE
            etPassword.visibility = View.VISIBLE

        }
    }

    private fun createSnackbar(message: String, isError: Boolean){

        if (isError){
            val snackbar = Snackbar.make(
                binding.main,
                message,
                Snackbar.LENGTH_SHORT
            )
            snackbar.setTextColor(getColor(R.color.white))
            snackbar.setBackgroundTint(getColor(R.color.errorColor))

            snackbar.show()
        }else{
            val snackbar = Snackbar.make(
                binding.main,
                message,
                Snackbar.LENGTH_SHORT
            )
            snackbar.setTextColor(getColor(R.color.white))
            snackbar.setBackgroundTint(getColor(R.color.successColor))

            snackbar.show()
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