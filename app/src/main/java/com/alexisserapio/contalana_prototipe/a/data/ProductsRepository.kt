package com.alexisserapio.contalana_prototipe.a.data

import com.alexisserapio.contalana_prototipe.a.data.db.ProductDAO
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity

class ProductsRepository(
    private val productDAO: ProductDAO
) {
    suspend fun insertProduct(product: ProductEntity) {
        productDAO.insertProduct(product)
    }

    suspend fun getAllProducts(): MutableList<ProductEntity> = productDAO.getAllProducts()
    fun getAllProductsFlow() = productDAO.getAllProductsFlow()

    suspend fun updateProduct(product: ProductEntity) {
        productDAO.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDAO.deleteProduct(product)
    }

}