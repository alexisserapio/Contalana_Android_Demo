package com.alexisserapio.contalana_prototipe.a.ui.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.data.DataStoreManager
import com.alexisserapio.contalana_prototipe.a.application.ContalanaApp
import kotlinx.coroutines.launch
import com.alexisserapio.contalana_prototipe.a.data.dataStore
import com.alexisserapio.contalana_prototipe.a.data.ProductsRepository
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.a.ui.adapters.ProductAdapter
import com.alexisserapio.contalana_prototipe.databinding.FragmentInventoryBinding
import kotlinx.coroutines.flow.first

class InventoryFragment : Fragment() {
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private var products = mutableListOf<ProductEntity>()
    private lateinit var productsRepository: ProductsRepository
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsRepository =(requireContext().applicationContext as ContalanaApp).productsRepository

        lifecycleScope.launch {
            val preferences = requireContext().dataStore.data.first() // suspende hasta obtener el primer valor
            val businessName = preferences[DataStoreManager.BUSINESS_NAME] ?: ""
            binding.tvInventoryTitle.text = getString(R.string.inventoryScene_title, businessName)

        }

        binding.addFirstProductButton.setOnClickListener {
            val fragmentAddProduct = AddProductFragment{
                updateUI()
            }
            //val fragmentAddProduct = AddProductFragment.newInstance()

            fragmentAddProduct.show(childFragmentManager, "addProductFragment")

        }

        binding.addButton.setOnClickListener {
            val fragmentAddProduct = AddProductFragment{
                updateUI()
            }
            //val fragmentAddProduct = AddProductFragment.newInstance()

            fragmentAddProduct.show(childFragmentManager, "addProductFragment")
        }

        productAdapter = ProductAdapter{ selectedProduct ->
            //Aqui va el clic al producto
        }

        binding.rvInventory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        updateUI()
    }

    private fun updateUI(){

        lifecycleScope.launch {
            products = productsRepository.getAllProducts()

            binding.tvNoProducts.visibility =
                if(products.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            binding.addFirstProductButton.isEnabled =
                products.isEmpty()

            binding.addFirstProductButton.visibility =
                if(products.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            productAdapter.updateList(products)
        }
    }
}