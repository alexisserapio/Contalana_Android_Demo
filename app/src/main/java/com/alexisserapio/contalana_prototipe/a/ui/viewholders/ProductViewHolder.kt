package com.alexisserapio.contalana_prototipe.a.ui.viewholders


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.alexisserapio.contalana_prototipe.R
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.databinding.ProductElementBinding
import com.bumptech.glide.Glide
import java.text.NumberFormat

class ProductViewHolder(
    private val binding: ProductElementBinding,
    private val onProductClick: (ProductEntity) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    private var currentItem: ProductEntity? = null
    private val currencyFormatter = NumberFormat.getCurrencyInstance()

    private val context: Context = itemView.context

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
            tvProductId.text = context.getString(R.string.product_label_id, product.id.toInt())
            tvProductName.text = product.productName
            tvDescription.text = product.productDescription
            tvPrice.text = currencyFormatter.format(product.price).trim()
            tvUnits.text = context.getString(R.string.product_label_units, product.stock)

            val imagePath = product.image // Este es el String que guardaste

            if (!imagePath.isNullOrEmpty()) {
                Glide.with(productImage)
                    .load(imagePath)
                    .centerCrop()
                    .error(R.drawable.ic_missing_product) // Si el archivo fue borrado
                    .into(binding.productImage)
            } else {
                binding.productImage.setImageResource(R.drawable.ic_missing_product)
            }


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