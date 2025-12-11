package com.alexisserapio.contalana_prototipe.a.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.a.ui.viewholders.ProductViewHolder

class ProductAdapter(
    private val onProductClick: (ProductEntity) -> Unit
): RecyclerView.Adapter<ProductViewHolder>() {

    private var products = mutableListOf<ProductEntity>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder =
        ProductViewHolder.create(parent, onProductClick)

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    //Funcion para actualizar el recycler view
    fun updateList(list: List<ProductEntity>){
        products.clear()
        products.addAll(list)
        notifyDataSetChanged()
    }

}