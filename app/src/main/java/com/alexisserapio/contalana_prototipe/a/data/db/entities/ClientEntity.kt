package com.alexisserapio.contalana_prototipe.a.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexisserapio.contalana_prototipe.a.utils.Constants

@Entity(tableName = Constants.DATABASE_CLIENTS_TABLE)

data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "client_id")
    var id: Int,
    @ColumnInfo(name = "client_name")
    var clientName: String,
    @ColumnInfo(name = "avg_ticket")
    var avgTicket: Double,
    @ColumnInfo(name = "principal_channel")
    var principalChannel: String,
)
