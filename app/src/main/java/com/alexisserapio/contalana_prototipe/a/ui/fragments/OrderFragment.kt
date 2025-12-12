package com.alexisserapio.contalana_prototipe.a.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.databinding.FragmentAddProductBinding
import com.alexisserapio.contalana_prototipe.databinding.FragmentCreateOrderBinding
import com.alexisserapio.contalana_prototipe.databinding.FragmentOrderBinding

class OrderFragment(
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
    )
) : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvProductName.text = product.productName
        binding.tvProductDesc.text = product.productDescription
    }

}