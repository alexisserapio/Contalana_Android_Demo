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
import com.alexisserapio.contalana_prototipe.a.data.OrdersRepository
import com.alexisserapio.contalana_prototipe.a.data.ProductsRepository
import com.alexisserapio.contalana_prototipe.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import com.alexisserapio.contalana_prototipe.a.data.dataStore
import com.alexisserapio.contalana_prototipe.a.data.db.entities.OrderEntity
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import kotlinx.coroutines.flow.first
import java.text.NumberFormat

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var orders = mutableListOf<OrderEntity>()
    private val currencyFormatter = NumberFormat.getCurrencyInstance()
    private lateinit var orderRespository: OrdersRepository

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

        orderRespository =(requireContext().applicationContext as ContalanaApp).ordersRepository


        lifecycleScope.launch {
            val preferences = requireContext().dataStore.data.first() // suspende hasta obtener el primer valor
            val userName = preferences[DataStoreManager.USER_NAME] ?: ""
            val businessName = preferences[DataStoreManager.BUSINESS_NAME] ?: ""

            binding.tvHomeTitle.text = getString(R.string.homeScene_welcome, userName)
            binding.tvBusinessName.text = businessName
        }

        binding.createOrderButton.setOnClickListener {
            val fragmentCreateOrder = CreateOrderFragment()
            //val fragmentAddProduct = AddProductFragment.newInstance()

            fragmentCreateOrder.show(childFragmentManager, "createOrderFragment")

        }

        observe()

    }

    override fun onResume() {
        super.onResume()
        val color = (requireView().background as ColorDrawable).color
        val isLight = ColorUtils.calculateLuminance(color) > 0.5

        WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView)
            .isAppearanceLightStatusBars = isLight
    }

    private fun observe() {
        lifecycleScope.launch {
            orders = orderRespository.getAllOrders()
            val totalGains = orderRespository.getTotalOfAllOrders()

            binding.apply {
                tvNoMovements.visibility =
                    if(orders.isEmpty()) View.VISIBLE else View.INVISIBLE
                ivEmptyIcon.visibility =
                    if(orders.isEmpty()) View.VISIBLE else View.INVISIBLE

                tvTotalGains.text = getString(R.string.homeScene_totalGains, currencyFormatter.format(totalGains).trim())
                tvTotalGains.visibility =
                    if(orders.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            }
        }
    }


}