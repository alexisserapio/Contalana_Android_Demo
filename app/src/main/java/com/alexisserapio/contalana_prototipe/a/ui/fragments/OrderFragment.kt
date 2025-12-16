package com.alexisserapio.contalana_prototipe.a.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.application.ContalanaApp
import com.alexisserapio.contalana_prototipe.a.data.OrdersRepository
import com.alexisserapio.contalana_prototipe.a.data.ProductsRepository
import com.alexisserapio.contalana_prototipe.a.data.db.entities.OrderEntity
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.a.utils.toFormattedDateString
import com.alexisserapio.contalana_prototipe.databinding.FragmentOrderBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date

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
    ),
    private var order: OrderEntity = OrderEntity(
        productId = 0,
        pricePerUnit = 0.0,
        total = 0.0,
        saleDate = null
    )
) : DialogFragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private var amountOfProducts: Int = 0
    private var totalPrice: Double = 0.0
    private val currencyFormatter = NumberFormat.getCurrencyInstance()
    private lateinit var ordersRepository: OrdersRepository
    private lateinit var productsRepository: ProductsRepository

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

        var finalTotalPrice: Double = 0.0

        binding.apply {
            tvProductName.text = product.productName
            tvProductDesc.text = product.productDescription
            tvAmountNumber.text = amountOfProducts.toString()

            totalPrice = product.price

            fun updateUI() {
                finalTotalPrice = totalPrice * amountOfProducts
                tvTotalNumber.text = currencyFormatter.format(finalTotalPrice).trim()

                saveOrderButton.isEnabled = amountOfProducts > 0
            }

            updateUI()

            minusButton.setOnClickListener {
                if (amountOfProducts > 0) {
                    amountOfProducts -= 1
                    tvAmountNumber.text = amountOfProducts.toString()

                    updateUI()
                }
            }

            sumButton.setOnClickListener {
                if (amountOfProducts < product.stock) {
                    amountOfProducts++
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.createOrder_order_amountLimit),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                tvAmountNumber.text = amountOfProducts.toString()


                updateUI()
            }

            saveOrderButton.setOnClickListener {
                addOrderToDB(product, finalTotalPrice)
            }

        }
    }

    private fun addOrderToDB(productInOrder: ProductEntity, orderTotal: Double){
        productsRepository =(requireContext().applicationContext as ContalanaApp).productsRepository
        ordersRepository =(requireContext().applicationContext as ContalanaApp).ordersRepository

        val fecha = Date().time
        val fechaConvertida = fecha.toFormattedDateString(DateFormat.MEDIUM)


        Log.e("APPLOGS", "Order Total: $orderTotal, fecha: $fechaConvertida")
        order.productId = productInOrder.id
        order.pricePerUnit = productInOrder.price
        order.total = orderTotal

        order.saleDate = Date().time

        product.stock = productInOrder.stock - amountOfProducts


        try{
            lifecycleScope.launch {
                val orderResult = async{
                    ordersRepository.insertOrder(order)
                }

                val productResult = async {
                    productsRepository.updateProduct(product)
                }

                orderResult.await()
                productResult.await()

                Toast.makeText(
                    requireContext(),
                    getString(R.string.createOrder_order_createdSuccessful),
                    Toast.LENGTH_SHORT
                ).show()

                (parentFragment as? DialogFragment)?.dismiss()
            }

        }catch (e: IOException){
            e.printStackTrace()
            Toast.makeText(
                context,
                getString(R.string.createOrder_order_createdError),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}