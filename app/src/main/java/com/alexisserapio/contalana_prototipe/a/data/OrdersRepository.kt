package com.alexisserapio.contalana_prototipe.a.data

import com.alexisserapio.contalana_prototipe.a.data.db.OrderDAO
import com.alexisserapio.contalana_prototipe.a.data.db.entities.OrderEntity
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity

class OrdersRepository(
    private val orderDAO: OrderDAO
) {
    suspend fun insertOrder(order: OrderEntity) {
        orderDAO.insertOrder(order)
    }

    suspend fun getAllOrders(): MutableList<OrderEntity> = orderDAO.getAllOrders()

    suspend fun getTotalOfAllOrders(): Double {
        return orderDAO.getTotalOfAllOrders() ?: 0.0
    }

    suspend fun updateOrder(order: OrderEntity) {
        orderDAO.updateOrder(order)
    }

    suspend fun deleteOrder(order: OrderEntity) {
        orderDAO.deleteOrder(order)
    }

    fun getAllOrdersFlow() = orderDAO.getAllOrdersFlow()
}