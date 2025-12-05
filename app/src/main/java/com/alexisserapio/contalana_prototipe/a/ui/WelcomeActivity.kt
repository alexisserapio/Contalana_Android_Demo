package com.alexisserapio.contalana_prototipe.a.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.utils.Constants
import com.alexisserapio.contalana_prototipe.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupTermsTextLink()
        setupSignInSpan()

        val animateBlueSquare = ObjectAnimator.ofFloat(binding.welcomeSquareblue, "rotation", 0f, 25f)
        val animateGreenSquare = ObjectAnimator.ofFloat(binding.welcomeSquaregreen, "rotation", 0f, -25f)
        animateBlueSquare.duration = 800 // duración en ms
        animateGreenSquare.duration = 800 // duración en ms

        val animateTitle = ObjectAnimator.ofFloat(binding.welcomeLabel, "alpha", 0f, 1f)
        val animateSubtitle = ObjectAnimator.ofFloat(binding.welcomeSubLabel, "alpha", 0f, 1f)
        val animateButton = ObjectAnimator.ofFloat(binding.welcomeButton, "alpha", 0f, 1f)
        val animateTerms = ObjectAnimator.ofFloat(binding.welcomeTerms, "alpha", 0f, 1f)
        val animateSignIn = ObjectAnimator.ofFloat(binding.welcomeSignIn, "alpha", 0f, 1f)

        animateTitle.duration = 800 // duración en ms
        animateSubtitle.duration = 500 // duración en ms
        animateButton.duration = 500 // duración en ms
        animateTerms.duration = 500
        animateSignIn.duration = 500

        val set = AnimatorSet()
        val set2 = AnimatorSet()
        set.playSequentially(animateBlueSquare, animateTitle, animateSubtitle)
        set2.playTogether( animateButton, animateTerms)

        animateGreenSquare.start()
        set.start()
        binding.root.postDelayed({
            set2.start()
        }, 2200)
        binding.root.postDelayed({
            animateSignIn.start()
        },2700)

        binding.welcomeButton.setOnClickListener {
            val segueToInformativeAct = Intent(this, InformativeActivity::class.java)
            startActivity(segueToInformativeAct)
            finish()
        }

    }

    fun setupTermsTextLink() {
        // 1. Obtener el texto completo
        val fullText = getString(R.string.welcome_terms)

        // 2. Crea el SpannableString basado en el texto completo
        val spannableString = SpannableString(fullText)

        // 3. Implementa UN SOLO ClickableSpan (para ambos textos)
        // Primer clickable span
        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(Intent.ACTION_VIEW, Constants.TERMS_URL.toUri())
                widget.context.startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(binding.welcomeTerms.context, R.color.CL_darkBlue)
                ds.isUnderlineText = true
                ds.typeface = Typeface.create(ds.typeface, Typeface.BOLD)
            }
        }

        // --- Aplicar Span al PRIMER texto: welcome_terms_label ---
        val targetText1 = getString(R.string.welcome_terms_label)
        val startIndex1 = fullText.indexOf(targetText1)

        // Solo aplica si el texto fue encontrado (índice >= 0)
        if (startIndex1 >= 0) {
            val endIndex1 = startIndex1 + targetText1.length
            spannableString.setSpan(
                clickableSpan1,
                startIndex1,
                endIndex1,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(Intent.ACTION_VIEW, Constants.TERMS_URL.toUri())
                widget.context.startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(binding.welcomeTerms.context, R.color.CL_darkBlue)
                ds.isUnderlineText = true
                ds.typeface = Typeface.create(ds.typeface, Typeface.BOLD)
            }
        }

        // --- Aplicar Span al SEGUNDO texto: welcome_terms_policy ---
        val targetText2 = getString(R.string.welcome_terms_policy)
        val startIndex2 = fullText.indexOf(targetText2)

        // Solo aplica si el texto fue encontrado (índice >= 0)
        if (startIndex2 >= 0) { // <--- CLAVE: Asegura un índice válido
            val endIndex2 = startIndex2 + targetText2.length
            spannableString.setSpan(
                clickableSpan2,
                startIndex2,
                endIndex2,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 4. Establece el SpannableString en el TextView (SOLO UNA VEZ)
        binding.welcomeTerms.text = spannableString

        // 5. ¡CRUCIAL! Habilita los clics
        binding.welcomeTerms.movementMethod = LinkMovementMethod.getInstance()

        // Opcional: Esto ayuda a que el color del span se vea correctamente
        binding.welcomeTerms.highlightColor = ContextCompat.getColor(binding.welcomeTerms.context, android.R.color.transparent)

        // El setTextColor debe hacerse si el color por defecto del TextView está siendo afectado
        // binding.welcomeTerms.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    }

    fun setupSignInSpan(){
        val fullText = getString(R.string.welcome_signin)

        val spannableString = SpannableString(fullText)

        // 3. Implementa UN SOLO ClickableSpan (para ambos textos)
        // Primer clickable span
        val signInSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val segueToSignIn = Intent(this@WelcomeActivity, LoginActivity::class.java)
                widget.context.startActivity(segueToSignIn)

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)

                ds.color = ContextCompat.getColor(binding.welcomeSignIn.context, R.color.CL_darkBlue)
                ds.isUnderlineText = false
                ds.typeface = Typeface.create(ds.typeface, Typeface.BOLD)
            }
        }

        val targetText = getString(R.string.welcome_signin_label)

        val startIndex = fullText.indexOf(targetText)

        if (startIndex >= 0) {
            val endIndex = startIndex + targetText.length
            spannableString.setSpan(
                signInSpan,
                startIndex,
                endIndex,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 4. Establece el SpannableString en el TextView (SOLO UNA VEZ)
        binding.welcomeSignIn.text = spannableString
        // 5. ¡CRUCIAL! Habilita los clics
        binding.welcomeSignIn.movementMethod = LinkMovementMethod.getInstance()

        binding.welcomeSignIn.highlightColor = ContextCompat.getColor(binding.welcomeSignIn.context, android.R.color.transparent)

    }
}