package com.alexisserapio.contalana_prototipe.a.ui.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.databinding.ProductElementBinding
import java.text.NumberFormat

class ProductViewHolder(
    private val binding: ProductElementBinding,
    private val onProductClick: (ProductEntity) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    private var currentItem: ProductEntity? = null
    private val currencyFormatter = NumberFormat.getCurrencyInstance()

    init {
        //Click a los elementos del viewholder
        binding.root.setOnClickListener {
            currentItem?.let(onProductClick)
        }
    }

    fun bind(product: ProductEntity){
        currentItem = product
        //Aqui vinculamos las vistas

        binding.apply {
            tvProductId.text = product.id.toString()
            tvProductName.text = product.productName
            tvDescription.text = product.productDescription
            tvPrice.text = currencyFormatter.format(product.price)
            tvUnits.text = product.stock.toString()
        }
    }

    companion object{
        fun create(
            parent: ViewGroup,
            onProductClick: (ProductEntity) -> Unit
        ): ProductViewHolder {
            //Inflamos cada view holder
            val binding = ProductElementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ProductViewHolder(binding, onProductClick)
        }
    }

}