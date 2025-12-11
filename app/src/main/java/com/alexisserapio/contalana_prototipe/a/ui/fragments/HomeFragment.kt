package com.alexisserapio.contalana_prototipe.a.ui.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.application.ContalanaApp
import com.alexisserapio.contalana_prototipe.a.data.DataStoreManager
import com.alexisserapio.contalana_prototipe.a.data.ProductsRepository
import com.alexisserapio.contalana_prototipe.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import com.alexisserapio.contalana_prototipe.a.data.dataStore
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import kotlinx.coroutines.flow.first

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var products = mutableListOf<ProductEntity>()
    private lateinit var productsRespository: ProductsRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsRespository =(requireContext().applicationContext as ContalanaApp).productsRepository

        lifecycleScope.launch {
            val preferences = requireContext().dataStore.data.first() // suspende hasta obtener el primer valor
            val userName = preferences[DataStoreManager.USER_NAME] ?: ""
            val businessName = preferences[DataStoreManager.BUSINESS_NAME] ?: ""

            binding.tvHomeTitle.text = getString(R.string.homeScene_welcome, userName)
            binding.tvBusinessName.text = businessName
        }

        updateUI()
    }

    override fun onResume() {
        super.onResume()
        val color = (requireView().background as ColorDrawable).color
        val isLight = ColorUtils.calculateLuminance(color) > 0.5

        WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView)
            .isAppearanceLightStatusBars = isLight
    }

    private fun updateUI(){

        lifecycleScope.launch {
            products = productsRespository.getAllProducts()

            binding.tvProductsCount.text = getString(R.string.homeScene_productsCount, products.count())

        }
    }

}