package com.alexisserapio.contalana_prototipe.a.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexisserapio.contalana_prototipe.a.utils.Constants

@Entity(tableName = Constants.DATABASE_BUSINESS_TABLE)
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "business_id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo("industry")
    var industry: String,

    @ColumnInfo("country")
    var country: String,

    @ColumnInfo("longitude")
    var longitude: Double,

    @ColumnInfo("latitude")
    var latitude: Double,

    @ColumnInfo("timestamp")
    var timestamp: String,

    @ColumnInfo("employees_num")
    var employeesNum: Int,

    @ColumnInfo("branch")
    var branch: Int,

    @ColumnInfo("horizontal_accuracy")
    var horizontalAccuracy: Double,

    @ColumnInfo("best_selling_product")
    var bestProduct: Int,

    @ColumnInfo("social_media")
    var socialMedia: String
)
