package com.alexisserapio.contalana_prototipe.a.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alexisserapio.contalana_prototipe.a.data.db.entities.BusinessEntity
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ClientEntity
import com.alexisserapio.contalana_prototipe.a.data.db.entities.ProductEntity
import com.alexisserapio.contalana_prototipe.a.utils.Constants

@Database(
    entities = [ProductEntity::class, BusinessEntity::class, ClientEntity::class],
    version = 1
)
abstract class ContalanaDatabase: RoomDatabase() {
    //Aquí va la función del DAO
    abstract fun productDao(): ProductDAO
   /* abstract fun businessDao(): BusinessDAO
    abstract fun clientDao(): ClientDAO */

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ContalanaDatabase? = null

        fun getDatabase(context: Context): ContalanaDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContalanaDatabase::class.java,
                    Constants.DATABASE_NAME
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}