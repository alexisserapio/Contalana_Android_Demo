package com.alexisserapio.contalana_prototipe.a.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.data.model.informativePage
import com.alexisserapio.contalana_prototipe.databinding.ActivityInformativeBinding
import com.google.android.material.tabs.TabLayout

class InformativeActivity : AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetector
    lateinit var binding: ActivityInformativeBinding

    val informativeData by lazy {
        listOf(
            informativePage(
                getString(R.string.infoPoster_title_one),
                getString(R.string.infoPoster_description_one),
                R.drawable.mockup
            ),
            informativePage(
                getString(R.string.infoPoster_title_two),
                getString(R.string.infoPoster_description_two),
                R.drawable.mockup
            ),
            informativePage(
                getString(R.string.infoPoster_title_three),
                getString(R.string.infoPoster_description_three),
                R.drawable.mockup
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformativeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        var currentPage = 0

        // Inicializar contenido
        updatePage(currentPage)

        binding.informativeButton.setOnClickListener {
            if (currentPage < informativeData.lastIndex) {
                val oldPage = currentPage
                currentPage++
                updatePage(currentPage)
                animateDots(oldPage, currentPage)
            }else{
                val segueToBNameActivity = Intent(this, BNameActivity::class.java)
                startActivity(segueToBNameActivity)
                finish()
            }
        }

        // Crear tabs manualmente
        binding.tabLayout.removeAllTabs()
        for (i in informativeData.indices) {
            val isSelected = i == currentPage
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setIcon(createDotDrawable(isSelected))
            )
        }



        val tabStrip = binding.tabLayout.getChildAt(0) as ViewGroup
        for (i in 0 until tabStrip.childCount) {
            val tab = tabStrip.getChildAt(i)
            val params = tab.layoutParams as ViewGroup.MarginLayoutParams
            params.marginEnd = 18
            if (i == 0) {
                params.marginStart = 20 // margen al inicio del primer punto
            }
            tab.layoutParams = params

            tabStrip.let { strip ->
                for (i in 0 until strip.childCount) {
                    val tabView = strip.getChildAt(i)
                    tabView.isClickable = false
                    tabView.isEnabled = false
                    tabView.setOnTouchListener { _, _ -> true } // consume el toque en cada tab
                }
            }
        }


        // Listener de cambio de tab manual (clic en un dot)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentPage = tab.position
                updatePage(currentPage)
                updateDots(currentPage)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Swipes
        val onSwipeRight = {
            if (currentPage > 0) {
                val oldPage = currentPage
                currentPage--
                updatePage(currentPage)
                animateDots(oldPage, currentPage)
            }
        }

        val onSwipeLeft = {
            if (currentPage < informativeData.lastIndex) {
                val oldPage = currentPage
                currentPage++
                updatePage(currentPage)
                animateDots(oldPage, currentPage)
            }
        }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null || e2 == null) return false
                val diffX = e2.x - e1.x
                return if (Math.abs(diffX) > Math.abs(e2.y - e1.y)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) onSwipeRight() else onSwipeLeft()
                        true
                    } else false
                } else false
            }
        })

        binding.informative.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

    }

    /**
     * Función que crea un drawable de forma programática para el punto.
     * @param isSelected Define si el punto debe ser el de color seleccionado o no.
     */
    private fun createDotDrawable(isSelected: Boolean): GradientDrawable {
        val dotColor = if (isSelected) {
            getColor(R.color.darkestGreen) // El color del punto seleccionado
        } else {
            getColor(R.color.backgroundGreen) // El color del punto no seleccionado
        }

        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(dotColor)
            setSize(25, 25)
        }
    }

    // Actualiza contenido
    private fun updatePage(page: Int) {
        crossDissolve(binding.topLabel) {
            binding.topLabel.text = informativeData[page].title
        }

        crossDissolve(binding.bottomLabel) {
            binding.bottomLabel.text = informativeData[page].description
        }

        flipImageView(binding.ivMockup, informativeData[page].imageId)
    }


    // Actualiza dots
    private fun updateDots(selectedIndex: Int) {
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            tab?.icon = createDotDrawable(i == selectedIndex)
        }
    }

    private fun animateDots(fromIndex: Int, toIndex: Int) {
        val selectedColor = getColor(R.color.darkestGreen)
        val unselectedColor = getColor(R.color.backgroundGreen)

        val evaluator = ArgbEvaluator()

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 300 // duración de la animación
        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float

            val fromTab = binding.tabLayout.getTabAt(fromIndex)
            val toTab = binding.tabLayout.getTabAt(toIndex)

            val fromDot = fromTab?.icon as? GradientDrawable
            val toDot = toTab?.icon as? GradientDrawable

            fromDot?.setColor(evaluator.evaluate(fraction, selectedColor, unselectedColor) as Int)
            toDot?.setColor(evaluator.evaluate(fraction, unselectedColor, selectedColor) as Int)
        }
        animator.start()
    }

    private fun crossDissolve(view: View, updateAction: () -> Unit) {
        val duration = 300L

        view.animate()
            .alpha(0f)
            .setDuration(duration / 2)
            .withEndAction {
                updateAction() // aquí actualizas el contenido
                view.animate()
                    .alpha(1f)
                    .setDuration(duration / 2)
                    .start()
            }
            .start()
    }

    private fun flipImageView(imageView: ImageView, newImageRes: Int) {
        val scale = resources.displayMetrics.density
        imageView.cameraDistance = 10000 * scale // aumenta la distancia de cámara para efecto 3D

        val duration = 400L

        // Paso 1: rotar de 0° a 90° + escalar ligeramente (zoom out)
        imageView.animate()
            .rotationY(90f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(duration / 2)
            .withEndAction {
                // Cambiar la imagen en medio del flip
                imageView.setImageResource(newImageRes)
                imageView.rotationY = -90f

                // Paso 2: rotar de -90° a 0° + escalar de vuelta a tamaño original
                imageView.animate()
                    .rotationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration / 2)
                    .start()
            }.start()
    }
}