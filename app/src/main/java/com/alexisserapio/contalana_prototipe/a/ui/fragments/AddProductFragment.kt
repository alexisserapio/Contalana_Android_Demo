package com.alexisserapio.contalana_prototipe.a.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.application.ContalanaApp
import com.alexisserapio.contalana_prototipe.a.data.ProductsRepository
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.databinding.FragmentAddProductBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

class AddProductFragment(
    private var product: ProductEntity = ProductEntity(
        productName = "",
        productDescription = "",
        category = null,
        stock = 0,
        price = 0.0,
        supplier = null,
        location = null,
        addedProductDate = null,
        branch = null,
        brand = null,
        code = null,
        lastPurchaseDate = null,
        maxPrice = 0.0,
        minPrice = 0.0,
        image = null
    ),
    private val updateUI: () -> Unit

) : BottomSheetDialogFragment() {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private lateinit var productsRepository: ProductsRepository
    private val maxLength = 26
    private val minLength = 5
    private var nameValidationTimer: CountDownTimer? = null
    private var descValidationTimer: CountDownTimer? = null
    private var priceValidationTimer: CountDownTimer? = null
    private var unitsValidationTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            dialog?.dismiss()
        }

        binding.etProductName.addTextChangedListener(object : TextWatcher{
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
                isEditTextChanged(s.toString(), binding.etProductName, "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ._\$&/\\\\\\\"'“”]+\$".toRegex())
            }

        })

        binding.etProductDesc.addTextChangedListener(object : TextWatcher{
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
                isEditTextChanged(s.toString(), binding.etProductDesc, "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ._\$&/\\\\\\\"'“”]+\$".toRegex())
            }

        })

        binding.etProductPrice.addTextChangedListener(object : TextWatcher{
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
                isEditTextChanged(s.toString(), binding.etProductPrice, "^[1-9]\\d*(\\.\\d+)?$".toRegex())
            }

        })

        binding.etProductStock.addTextChangedListener(object : TextWatcher{
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
                isEditTextChanged(s.toString(), binding.etProductStock, "^[1-9]\\d*$".toRegex())
            }

        })




    }

    override fun onStart() {
        super.onStart()

        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return

        bottomSheet.layoutParams.height =
            (resources.displayMetrics.heightPixels * 0.95).toInt()

        val behavior = BottomSheetBehavior.from(bottomSheet)

        // Forzar que se expanda completamente
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
    }

    private fun updateButtonState(){

        /*val parsedNumber = try {
            currencyFormatter.parse(priceText) // Esto devuelve un Number
        } catch (e: Exception) {
            // Manda error si el texto no es un formato de número/moneda válido
            null
        }*/
        val priceText = binding.etProductPrice.text.toString().trim()
        val unitsText = binding.etProductStock.text.toString().trim()

        val finalProductName = binding.etProductName.text.toString().trim()
        val finalProductDesc = binding.etProductDesc.text.toString().trim()

        val nameCorrect = finalProductName.count() in minLength..maxLength
        val descCorrect = finalProductDesc.count() in minLength..maxLength
        val priceCorrect = priceText.count() in 2 ..maxLength
        val unitsCorrect = unitsText.count() in 1 ..maxLength

        if(nameCorrect && descCorrect && priceCorrect && unitsCorrect){

            binding.addProductButton.isEnabled = true

            val finalProductPrice = binding.etProductPrice.text.toString().trim().toDoubleOrNull()
            val finalProductUnits = binding.etProductStock.text.toString().trim().toIntOrNull()

            binding.addProductButton.setOnClickListener {
                addProductToDB(finalProductName, finalProductDesc, finalProductPrice, finalProductUnits)
            }
        }else{
            binding.addProductButton.isEnabled = false
        }

        //TDO("Manejar errores generales de longitud y regex, comprobar que no haya 0 unidades")

    }

    private fun addProductToDB(productName: String, productDesc: String, productPrice: Double?, productUnits: Int?){

        productsRepository =(requireContext().applicationContext as ContalanaApp).productsRepository

        product.productName = productName
        product.productDescription = productDesc
        if (productPrice != null) {
            product.price = productPrice
        }
        if (productUnits != null) {
            product.stock = productUnits
        }

        try{
            lifecycleScope.launch {
                val result = async{
                    productsRepository.insertProduct(product)
                }

                result.await()

                updateUI()

                Toast.makeText(
                    requireContext(),
                    getString(R.string.product_success),
                    Toast.LENGTH_SHORT
                ).show()

                dismiss()
            }

        }catch (e: IOException){
            e.printStackTrace()
            Toast.makeText(
                context,
                getString(R.string.product_error),
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    private fun isEditTextChanged(etText: String, editText: EditText, regex: Regex){

        updateButtonState()

        val currentTimer = when (editText) {
            binding.etProductName -> {
                nameValidationTimer
            }
            binding.etProductDesc -> {
                descValidationTimer
            }
            binding.etProductPrice -> {
                priceValidationTimer
            }
            else -> {
                unitsValidationTimer
            }
        }

        currentTimer?.cancel()

        if(etText.count()>0){
            val timer = object : CountDownTimer(2000,2000){
                override fun onFinish() {
                    showError(etText, editText, regex)
                }

                override fun onTick(millisUntilFinished: Long) {}
            }

            when (editText) {
                binding.etProductName -> {
                    nameValidationTimer = timer
                }
                binding.etProductDesc -> {
                    descValidationTimer = timer
                }
                binding.etProductPrice -> {
                    priceValidationTimer = timer
                }
                else -> {
                    unitsValidationTimer = timer
                }
            }

            timer.start()

        }else{
            editText.error = null
        }

    }

    private fun showError(etText: String, editText: EditText, regex: Regex) {

        editText.error = null

        if (etText.count()<minLength && editText != binding.etProductStock){
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


}