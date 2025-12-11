package com.alexisserapio.contalana_prototipe.a.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.databinding.FragmentAddProductBinding
import com.alexisserapio.contalana_prototipe.databinding.FragmentInventoryBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddProductFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

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
        // Lógica de tus botones/vistas aquí
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

    companion object {
        // 1. Define la constante TAG (el error en .TAG)
        // Se usa para identificar el diálogo en el FragmentManager
        const val TAG = "addProductFragmentTAG"

        // Se usa para crear una nueva instancia del diálogo
        fun newInstance(): AddProductFragment {
            return AddProductFragment()
        }

        // Si necesitas pasar argumentos (ej: un ID de producto):
        /*
        fun newInstance(productId: Int): MyBottomSheetDialog {
            val args = Bundle().apply {
                putInt("PRODUCT_ID_KEY", productId)
            }
            return MyBottomSheetDialog().apply {
                arguments = args
            }
        }
        */
    }

}