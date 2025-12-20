package com.alexisserapio.contalana_prototipe.a.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.application.ContalanaApp
import com.alexisserapio.contalana_prototipe.a.data.ProductsRepository
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.a.utils.Constants.CAMERA_PERMISSION
import com.alexisserapio.contalana_prototipe.databinding.FragmentAddProductBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

    val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedPhotoUri ->
            val savedFileUri = saveImageToInternalStorage(selectedPhotoUri)

            Glide.with(this)
                .load(selectedPhotoUri)
                .centerCrop()
                .into(binding.ivTakenPhoto)

            photoToSave = savedFileUri.toString()
        }
    }
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private var cameraPermissionGranted = false
    private var isCameraActive = false
    private var currentPhotoPath: String? = null
    private var photoToSave: String? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

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

        binding.cameraButton.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            // Creamos una vista sencilla inflada desde un pequeño layout o creada por código
            val view = layoutInflater.inflate(R.xml.bottom_sheet_dialog, null)

            // Configuramos los clics de las opciones
            view.findViewById<LinearLayout>(R.id.option_camera).setOnClickListener {
                updateOrRequestCameraPermission()
                dialog.dismiss()
            }

            view.findViewById<LinearLayout>(R.id.option_gallery).setOnClickListener {
                galleryLauncher.launch("image/*")
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        }

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            currentPhotoPath?.let{ path ->
                val file = File(path)

                if(result.resultCode == Activity.RESULT_OK){
                    if(file.length() > 0L){
                        photoToSave = currentPhotoPath

                        Glide.with(this)
                            .load(file)
                            .into(binding.ivTakenPhoto)

                        binding.cameraButton.isVisible = false

                    }else{
                        file.delete()
                    }
                }else{
                    file.delete()
                }
            }
            isCameraActive = false
        }

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

    private fun updateOrRequestCameraPermission(){
        cameraPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val permissionsToRequest = mutableListOf<String>()

        if(!cameraPermissionGranted)
            permissionsToRequest.add(Manifest.permission.CAMERA)

        if(permissionsToRequest.isNotEmpty()){
            //Tenemos que pedir el permiso
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                permissionsToRequest.toTypedArray(),
                CAMERA_PERMISSION
            )
        }else{
            //Tenemos el permiso!
            actionPermissionGranted()
        }
    }

    private fun actionPermissionGranted(){
        if(!isCameraActive)
            startIntentCamera()
    }

    private fun startIntentCamera(){
        try {
            //Generamos un contenedor para el archivo
            val imageFile = File.createTempFile(
                "photo",
                ".jpg",
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )

            currentPhotoPath = imageFile.absolutePath

            //Con el archivo generamos un URi con la authority correspondiente
            val imageUri = FileProvider.getUriForFile(
                requireContext(),
                "com.alexisserapio.contalana_prototipe.fileprovider",
                imageFile
            )

            //Generamos el intent hacia la camara
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            }

            //Mandamos el intent
            resultLauncher.launch(intent)

            isCameraActive = true

        }catch (e: IOException){
            //Manejamos la excepción
        }
    }

    fun rotateImageIfRequired(img: Bitmap, selectedImagePath: String): Bitmap {
        val ei = ExifInterface(selectedImagePath)
        val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
            else -> img
        }
    }

    fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    private fun updateButtonState(){

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
        product.image = photoToSave
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
                    Log.e("APPSLOG", product.toString())
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

    private fun saveImageToInternalStorage(uri: Uri): Uri? {
        return try {

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_GALLERY_$timeStamp.jpg"

            // Buscamos la carpeta de archivos de tu app
            val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

            // Copiamos los datos
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // Devolvemos el URI del nuevo archivo local
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}