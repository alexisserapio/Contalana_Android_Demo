package com.alexisserapio.contalana_prototipe.a.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.alexisserapio.contalana_prototipe.a.data.db.entities.BusinessEntity
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ClientEntity
import com.alexisserapio.contalana_prototipe.a.utils.Constants

@Dao
interface ClientDAO {

    //Create
    @Insert
    suspend fun insertClient(client: ClientEntity)

    //Read
    @Query("SELECT * FROM ${Constants.DATABASE_CLIENTS_TABLE}")
    suspend fun getAllClients(): MutableList<ClientEntity>

    //Upate
    @Update
    suspend fun updateClient(client: ClientEntity)

    //Delete
    @Delete
    suspend fun deleteClient(client: ClientEntity)
}