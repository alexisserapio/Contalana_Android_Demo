package com.alexisserapio.contalana_prototipe.a.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexisserapio.contalana_prototipe.a.utils.Constants

@Entity(tableName = Constants.DATABASE_ORDERS_TABLE)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "order_id")
    var id: Long = 0,

    @ColumnInfo(name = "product_id")
    var productId: Long,

    @ColumnInfo(name = "price_per_unit")
    var pricePerUnit: Double,

    @ColumnInfo(name = "total")
    var total: Double?,

)
