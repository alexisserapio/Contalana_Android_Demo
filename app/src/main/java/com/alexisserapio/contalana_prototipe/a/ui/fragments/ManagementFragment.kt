package com.alexisserapio.contalana_prototipe.a.ui.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
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
import com.alexisserapio.contalana_prototipe.databinding.FragmentInventoryBinding
import com.alexisserapio.contalana_prototipe.databinding.FragmentManagementBinding
import kotlinx.coroutines.flow.first

class ManagementFragment : Fragment() {
    private var _binding: FragmentManagementBinding? = null
    private val binding get() = _binding!!

    private var products = mutableListOf<ProductEntity>()
    private lateinit var productsRespository: ProductsRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsRespository =(requireContext().applicationContext as ContalanaApp).productsRepository

        lifecycleScope.launch {
            val preferences = requireContext().dataStore.data.first() // suspende hasta obtener el primer valor
            val businessName = preferences[DataStoreManager.BUSINESS_NAME] ?: ""
            binding.tvManagementTitle.text = getString(R.string.managementScene_title, businessName)
        }

        observeProducts()
    }

    override fun onResume() {
        super.onResume()
        val color = (requireView().background as ColorDrawable).color
        val isLight = ColorUtils.calculateLuminance(color) > 0.5

        WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView)
            .isAppearanceLightStatusBars = isLight
    }

    private fun observeProducts() {
        lifecycleScope.launch {
            productsRespository.getAllProductsFlow().collect { list ->
                products = list.toMutableList()

                binding.tvProductsCount.text = getString(
                    R.string.managementScene_productsCount,
                    products.size
                )
            }
        }
    }


}