package com.alexisserapio.contalana_prototipe.a.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.data.DataStoreManager
import com.alexisserapio.contalana_prototipe.a.data.dataStore
import com.alexisserapio.contalana_prototipe.a.utils.NonceUtils
import com.alexisserapio.contalana_prototipe.databinding.ActivitySigninBinding
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.material.snackbar.Snackbar
import com.alexisserapio.contalana_prototipe.BuildConfig
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.math.sign
import kotlin.toString

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding

    //Mostrar cuentas con un BottomSheet (flujo acceder con Google)
    private lateinit var googleIdOption: GetGoogleIdOption
    //Ingresar con el flujo de un botón
    private lateinit var signInWithGoogleOption: GetSignInWithGoogleOption
    //Para el Credential manager
    private lateinit var credentialManager: CredentialManager
    //Para la autenticación con Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    private var userName = ""
    private var email = ""
    private var pswd = ""
    private val minLength = 8
    private val maxLength = 26

    private var userNameValidatorTimer: CountDownTimer? = null
    private var emailValidationTimer: CountDownTimer? = null
    private var passwordValidationTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        lifecycleScope.launch {
            val preferences = dataStore.data.first()
            val bName = preferences[DataStoreManager.BUSINESS_NAME]

            binding.tvBname.text = "$bName."
        }

        firebaseAuth = FirebaseAuth.getInstance()

        //Instanciamos el credential manager
        credentialManager = CredentialManager.create(this)

        //Generamos un nonce de 32 caracteres
        val nonce = NonceUtils.generateNonce(32)

        //Para mostrar las cuentas con un BottomSheet
        googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            //.setServerClientId()
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .setAutoSelectEnabled(false)
            .setNonce(nonce)
            .build()

        //Para el flujo de un botón
        signInWithGoogleOption = GetSignInWithGoogleOption.Builder(BuildConfig.WEB_CLIENT_ID)
            .setNonce(nonce)
            .build()

        binding.btnSignInGoogle.setOnClickListener {
            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                //.addCredentialOption(googleIdOption)  //BottomSheet
                .addCredentialOption(signInWithGoogleOption) //Flujo de un botón
                .build()

            lifecycleScope.launch {
                try{
                    val result = credentialManager.getCredential(
                        this@SigninActivity,
                        request
                    )
                    handleSignIn(result)

                }catch (e: Exception){
                    //Manejamos el error
                    if(e.message.equals("No credentials available")){
                        AlertDialog.Builder(this@SigninActivity)
                            .setTitle(getString(R.string.signIn_register_error))
                            .setMessage(getString(R.string.signIn_register_message))
                            .setNeutralButton(getString(R.string.signIn_register_accept)){ dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    }
                }
            }
        }

        if(firebaseAuth.currentUser != null)
            actionLoginSuccessful()

        binding.etUsername.addTextChangedListener(object : TextWatcher{
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
                isEditTextChanged(s.toString(), binding.etUsername, "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ._\$&/\\\\\\\"'“”]+\$".toRegex())
            }

        })

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
        lifecycleScope.launch {
            val preferences = dataStore.data.first()
            val formAnswered = preferences[DataStoreManager.FORM_ANSWERED] ?: false

            if(!formAnswered){
                withContext(Dispatchers.Main) {
                    val segueToTabBarActivity =
                        Intent(this@SigninActivity, FormActivity::class.java)
                    startActivity(segueToTabBarActivity)
                    finish()
                }
            }else{
                withContext(Dispatchers.Main) {
                    val segueToTabBarActivity =
                        Intent(this@SigninActivity, TabBarActivity::class.java)
                    startActivity(segueToTabBarActivity)
                    finish()
                }
            }

        }
    }

    private fun isEditTextChanged(etText: String, editText: android.widget.EditText, regex: Regex){

        updateButtonState()

        val currentTimer = if (editText == binding.etUsername){
            userNameValidatorTimer
        } else if (editText == binding.etMail){
            emailValidationTimer
        }else{
            passwordValidationTimer
        }

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
            }else if (editText == binding.etPassword){
                passwordValidationTimer = timer
            }else{
                userNameValidatorTimer = timer
            }

            timer.start()

        }else{
            editText.error = null
        }

    }

    private fun updateButtonState() {
        val finalClientName = binding.etUsername.text.toString().trim()
        val finalEmail = binding.etMail.text.toString().trim()
        val finalPassword = binding.etPassword.text.toString()

        userName = finalClientName

        val clientCorrect = finalEmail.count() in minLength..maxLength &&
                finalClientName.matches(
                    "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ._$&/\\\\\"'“”]+$".toRegex()
                )

        val emailCorrect = finalEmail.count() in minLength..maxLength &&
                Patterns.EMAIL_ADDRESS.matcher(finalEmail).matches()

        val passwordCorrect = finalPassword.count() in minLength..maxLength &&
                finalPassword.matches(
                    """^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&ÁÉÍÓÚáéíóúÑñ ._$&/"'“”]*$""".toRegex()
                )

        if(emailCorrect && passwordCorrect && clientCorrect){
            binding.apply {
                //Login Button
                signinButton.isEnabled = true

                signinButton.setOnClickListener {
                    progressBar.visibility = View.VISIBLE

                    tvUsername.visibility = View.INVISIBLE
                    etUsername.visibility = View.INVISIBLE
                    tvMail.visibility = View.INVISIBLE
                    etMail.visibility = View.INVISIBLE
                    tvPassword.visibility = View.INVISIBLE
                    etPassword.visibility = View.INVISIBLE

                    createUser(finalEmail, finalPassword, binding.signinButton)

                    lifecycleScope.launch(Dispatchers.IO) {
                        dataStore.edit { preferences ->
                            preferences[DataStoreManager.USER_NAME] = finalClientName
                        }
                    }
                }

            }
        }else{
            binding.apply {
                //UI
                progressBar.visibility = View.INVISIBLE

                tvUsername.visibility = View.VISIBLE
                etUsername.visibility = View.VISIBLE
                tvMail.visibility = View.VISIBLE
                etMail.visibility = View.VISIBLE
                tvPassword.visibility = View.VISIBLE
                etPassword.visibility = View.VISIBLE

                ///SignIn Button
                signinButton.isEnabled = false
            }
        }
    }

    private fun createUser(user: String, psw: String, view:View){
        firebaseAuth.createUserWithEmailAndPassword(user,psw)
            .addOnCompleteListener { authResult ->
                if(authResult.isSuccessful){
                    firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {

                        createSnackbar(getString(R.string.signIn_confirmationMail_successful), false)

                    }?.addOnFailureListener {

                        createSnackbar(getString(R.string.signIn_confirmationMail_error), true)

                    }

                    createSnackbar(getString(R.string.signIn_register_successful, userName), false)
                    actionLoginSuccessful()

                }else{
                    handleErrors(authResult)
                    //binding.progressBar.visibility = View.GONE
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
                createSnackbar(getString(R.string.signIn_error_pswOrMailNotValid), true)
                restartUI()
                binding.etPassword.requestFocus()

            }

            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                //An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.
                createSnackbar(getString(R.string.signIn_error_accountWithOtherProvider), true)
                restartUI()

                TODO("Vincular ambos proveedores a ambos correos")

            }

            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                createSnackbar(getString(R.string.signIn_error_accountAlreadyExists), true)
                restartUI()
                binding.etMail.requestFocus()
            }

            "ERROR_USER_TOKEN_EXPIRED" -> {

                val snackbar = Snackbar.make(
                    binding.main,
                    getString(R.string.signIn_error_sessionExpired),
                    Snackbar.LENGTH_SHORT
                ).show()

                restartUI()
            }

            "ERROR_USER_NOT_FOUND" -> {
                createSnackbar(getString(R.string.signIn_error_noAccount), true)
                restartUI()
            }


            "NO_NETWORK" -> {

               Snackbar.make(
                    binding.main,
                    getString(R.string.signIn_error_noConnection),
                    Snackbar.LENGTH_SHORT
                ).show()

                restartUI()
            }

            else -> {
                createSnackbar(getString(R.string.signIn_error_generalError), true)
                restartUI()
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

    private fun handleSignIn(result: GetCredentialResponse){
        //Manejamos la credencial obtenida
        when(val credential = result.credential){
            is CustomCredential ->{
                //Verificamos si el resultado es un token de una credencial Google
                if(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    try{
                        //Usamos el googleIdTokenCredential y lo extraemos para autenticarnos en firebase

                        //Obtenemos el token de Google
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        //Usamos el token para obtener la credencial de autenticación
                        val authCredential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnSuccessListener {
                                actionLoginSuccessful()
                            }.addOnFailureListener {
                                createSnackbar(getString(R.string.signIn_register_problem), true)
                            }

                    }catch(e: GoogleIdTokenParsingException){
                        Log.e("APPLOGS", "Respuesta de token de Google inválida", e)
                    }
                }else{
                    //Capturamos el error de una credencial no reconocida
                    Log.e("APPLOGS", "Credencial no reconocida")
                }
            }
            else -> {
                //Capturamos el error de una credencial no reconocida
                Log.e("APPLOGS", "Credencial no reconocida")
            }
        }
    }

    private fun restartUI(){
        binding.apply {
            progressBar.visibility = View.INVISIBLE

            tvUsername.visibility = View.VISIBLE
            etUsername.visibility = View.VISIBLE
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
}