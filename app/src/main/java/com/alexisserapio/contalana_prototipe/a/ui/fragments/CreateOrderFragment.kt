package com.alexisserapio.contalana_prototipe.a.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.application.ContalanaApp
import com.alexisserapio.contalana_prototipe.a.data.ProductsRepository
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.a.ui.adapters.ProductAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.alexisserapio.contalana_prototipe.databinding.FragmentCreateOrderBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch


class CreateOrderFragment(): BottomSheetDialogFragment() {

    private var _binding: FragmentCreateOrderBinding? = null
    private val binding get() = _binding!!
    private var products = mutableListOf<ProductEntity>()
    private lateinit var productsRepository: ProductsRepository
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsRepository =(requireContext().applicationContext as ContalanaApp).productsRepository

        binding.backButton.setOnClickListener {
            dialog?.dismiss()
        }

        productAdapter = ProductAdapter{ selectedProduct ->
            //Aqui va el clic al producto
            showOrderFragment(selectedProduct)
        }

        binding.createOrderRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        updateUI()

    }

    private fun showOrderFragment(product: ProductEntity){
        binding.createOrderRV.visibility = View.GONE
        binding.tvCreateOrderSubtitle.visibility = View.GONE
        binding.fragmentContainer.visibility = View.VISIBLE

        val fragment = OrderFragment(
            product = product,
        )

        childFragmentManager.beginTransaction()
            .replace(
                binding.fragmentContainer.id,
                fragment
            )
            .addToBackStack(null)
            .commit()
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

    private fun updateUI(){

        lifecycleScope.launch {
            products = productsRepository.getAllProducts()

            binding.tvCreateOrderSubtitle.visibility =
                if(products.isNotEmpty()) View.VISIBLE else View.INVISIBLE

            binding.tvNoProducts.visibility =
                if(products.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            productAdapter.updateList(products)
        }
    }

}