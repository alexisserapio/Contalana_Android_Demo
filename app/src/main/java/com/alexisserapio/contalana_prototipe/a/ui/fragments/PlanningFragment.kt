package com.alexisserapio.contalana_prototipe.a.ui.fragments

import android.graphics.drawable.ColorDrawable
import android.icu.text.DateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.application.ContalanaApp
import com.alexisserapio.contalana_prototipe.a.data.DataStoreManager
import com.alexisserapio.contalana_prototipe.a.data.OrdersRepository
import com.alexisserapio.contalana_prototipe.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import com.alexisserapio.contalana_prototipe.a.data.dataStore
import com.alexisserapio.contalana_prototipe.a.data.model.DailyIncome
import com.alexisserapio.contalana_prototipe.a.utils.toFormattedDateString
import com.alexisserapio.contalana_prototipe.databinding.FragmentInventoryBinding
import com.alexisserapio.contalana_prototipe.databinding.FragmentPlanningBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class PlanningFragment : Fragment() {
    private var _binding: FragmentPlanningBinding? = null
    private val binding get() = _binding!!

    private lateinit var ordersRepository: OrdersRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPlanningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val preferences = requireContext().dataStore.data.first() // suspende hasta obtener el primer valor
            val businessName = preferences[DataStoreManager.BUSINESS_NAME] ?: getString(R.string.defaultBusinessName)
            binding.tvPlanningTitle.text = getString(R.string.planningScene_title, businessName)
        }

        ordersRepository = (requireContext().applicationContext as ContalanaApp).ordersRepository

        collectDailyIncome()
        checkTicket()

    }

    override fun onResume() {
        super.onResume()
        collectDailyIncome()
        checkTicket()
    }

    private fun collectDailyIncome() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                ordersRepository.getDailyIncome().collect { data ->
                    if (data.isEmpty()){
                        binding.barChar.isVisible = false
                    }else{
                        setupBarChart(data)

                        binding.apply {
                            tvNoData.visibility = View.INVISIBLE
                            ivNoData.visibility = View.INVISIBLE
                            barChar.visibility = View.VISIBLE
                        }
                    }

                }
            }
        }
    }

    private fun setupBarChart(data: List<DailyIncome>) {

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        data.forEachIndexed { index, item ->
            entries.add(
                BarEntry(index.toFloat(), item.totalIncome.toFloat())
            )
            labels.add(item.day.toFormattedDateString(DateFormat.MEDIUM))
        }

        val dataSet = BarDataSet(entries, "Ganancias por día")
        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        binding.apply {

            barChar.data = barData

            barChar.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }

            barChar.axisRight.isEnabled = false
            barChar.description.isEnabled = false
            barChar.setFitBars(true)

            barChar.invalidate()
        }
    }

    private fun checkTicket(){
        lifecycleScope.launch(Dispatchers.IO) {
            val avgTicket = ordersRepository.getAverageTicket()

            withContext(Dispatchers.Main){
                if(avgTicket == 0.0){
                    binding.tvTicket.visibility = View.INVISIBLE
                    binding.tvNoTicket.visibility = View.VISIBLE
                }else{
                    binding.tvTicket.visibility = View.VISIBLE
                    binding.tvTicket.text = getString(R.string.planningScene_ticket, String.format("%.2f", avgTicket))
                    binding.tvNoTicket.visibility = View.INVISIBLE
                }
            }
        }
    }

}