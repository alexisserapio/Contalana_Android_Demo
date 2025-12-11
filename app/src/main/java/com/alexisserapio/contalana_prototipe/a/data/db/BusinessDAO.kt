package com.alexisserapio.contalana_prototipe.a.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.alexisserapio.contalana_prototipe.a.data.db.entities.BusinessEntity
import com.alexisserapio.contalana_prototipe.a.utils.Constants

@Dao
interface BusinessDAO {

    //Create
    @Insert
    suspend fun insertBusiness(business: BusinessEntity)

    //Read
    @Query("SELECT * FROM ${Constants.DATABASE_BUSINESS_TABLE}")
    suspend fun getAllBusiness(): MutableList<BusinessEntity>

    //Upate
    @Update
    suspend fun updateBusiness(business: BusinessEntity)

    //Delete
    @Delete
    suspend fun deleteBusiness(business: BusinessEntity)

}