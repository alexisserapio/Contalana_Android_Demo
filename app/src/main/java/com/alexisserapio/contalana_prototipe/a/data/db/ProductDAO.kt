package com.alexisserapio.contalana_prototipe.a.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.a.utils.Constants

@Dao
interface ProductDAO {

    //Create
    @Insert
    suspend fun insertProduct(product: ProductEntity)

    //Read
    @Query("SELECT * FROM ${Constants.DATABASE_PRODUCT_TABLE}")
    suspend fun getAllProducts(): MutableList<ProductEntity>

    @Query("SELECT * FROM ${Constants.DATABASE_PRODUCT_TABLE} WHERE product_id=:productId")
    suspend fun getProductById(productId: Int): ProductEntity?

    //Upate
    @Update
    suspend fun updateProduct(product: ProductEntity)

    //Delete
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}