package com.alexisserapio.contalana_prototipe.a.data

import com.alexisserapio.contalana_prototipe.a.data.db.OrderDAO
import com.alexisserapio.contalana_prototipe.a.data.db.entities.OrderEntity
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.a.data.model.DailyIncome
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class OrdersRepository(
    private val orderDAO: OrderDAO
) {
    suspend fun insertOrder(order: OrderEntity) {
        orderDAO.insertOrder(order)
    }

    suspend fun getAllOrders(): MutableList<OrderEntity> = orderDAO.getAllOrders()

    fun getTotalOfAllOrdersFlow() = orderDAO.getTotalOfAllOrders()

    fun getDailyIncome(): Flow<List<DailyIncome>> {
        return orderDAO.getDailyIncome()
    }

    fun getAverageTicket(): Double {
        // 1. Obtenemos el inicio y fin del día actual
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        // 2. Llamamos al DAO
        return orderDAO.getAverageTicket(startOfDay, endOfDay)
    }

    suspend fun updateOrder(order: OrderEntity) {
        orderDAO.updateOrder(order)
    }

    suspend fun deleteOrder(order: OrderEntity) {
        orderDAO.deleteOrder(order)
    }

    fun getAllOrdersFlow() = orderDAO.getAllOrdersFlow()
}