package com.alexisserapio.contalana_prototipe.a.data

import com.alexisserapio.contalana_prototipe.a.data.db.OrderDAO
import com.alexisserapio.contalana_prototipe.a.data.db.entities.OrderEntity
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.a.data.model.DailyIncome
import kotlinx.coroutines.flow.Flow

class OrdersRepository(
    private val orderDAO: OrderDAO
) {
    suspend fun insertOrder(order: OrderEntity) {
        orderDAO.insertOrder(order)
    }

    suspend fun getAllOrders(): MutableList<OrderEntity> = orderDAO.getAllOrders()

    fun getTotalOfAllOrdersFlow() = orderDAO.getTotalOfAllOrders()

    /*suspend fun getTotalOfAllOrders(): Double {
        return orderDAO.getTotalOfAllOrders() ?: 0.0
    }*/

    fun getDailyIncome(): Flow<List<DailyIncome>> {
        return orderDAO.getDailyIncome()
    }

    suspend fun updateOrder(order: OrderEntity) {
        orderDAO.updateOrder(order)
    }

    suspend fun deleteOrder(order: OrderEntity) {
        orderDAO.deleteOrder(order)
    }

    fun getAllOrdersFlow() = orderDAO.getAllOrdersFlow()
}