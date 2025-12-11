package com.alexisserapio.contalana_prototipe.a.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexisserapio.contalana_prototipe.a.utils.Constants

@Entity(tableName = Constants.DATABASE_PRODUCT_TABLE)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    var id: Int = 0,

    @ColumnInfo(name = "product_name")
    var productName: String,

    @ColumnInfo(name = "product_description")
    var productDescription: String,

    @ColumnInfo(name = "category")
    var category: String,

    @ColumnInfo(name = "stock")
    var stock: Int,

    @ColumnInfo(name = "price")
    var price: Double,

    @ColumnInfo(name = "supplier")
    var supplier: Int,

    @ColumnInfo(name = "location")
    var location: String,

    @ColumnInfo(name = "added_product_date")
    var addedProductDate: String,

    @ColumnInfo(name = "branch")
    var branch: String,

    @ColumnInfo(name = "brand")
    var brand: String,

    @ColumnInfo(name = "code")
    var code: String,

    @ColumnInfo(name = "last_purchase_date")
    var lastPurchaseDate: String,

    @ColumnInfo(name = "max_price")
    var maxPrice: Double,

    @ColumnInfo(name = "min_price")
    var minPrice: Double,

    @ColumnInfo(name = "image")
    var image: String
)
