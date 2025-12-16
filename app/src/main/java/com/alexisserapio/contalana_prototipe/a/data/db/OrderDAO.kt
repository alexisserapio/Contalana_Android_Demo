package com.alexisserapio.contalana_prototipe.a.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.alexisserapio.contalana_prototipe.a.data.db.entities.OrderEntity
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.a.utils.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDAO {

    //Create
    @Insert
    suspend fun insertOrder(order: OrderEntity)

    //Read
    @Query("SELECT * FROM ${Constants.DATABASE_ORDERS_TABLE}")
    suspend fun getAllOrders(): MutableList<OrderEntity>

    @Query("SELECT * FROM ${Constants.DATABASE_ORDERS_TABLE} WHERE order_id=:orderId")
    suspend fun getOrderById(orderId: Int): OrderEntity?

    @Query("SELECT * FROM ${Constants.DATABASE_ORDERS_TABLE} ORDER BY order_id DESC")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT SUM(total) FROM ${Constants.DATABASE_ORDERS_TABLE}")
    fun getTotalOfAllOrders(): Flow<Double?>

    //Upate
    @Update
    suspend fun updateOrder(order: OrderEntity)

    //Delete
    @Delete
    suspend fun deleteOrder(order: OrderEntity)
}