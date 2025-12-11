package com.alexisserapio.contalana_prototipe.a.application

import android.app.Application
import com.alexisserapio.contalana_prototipe.a.data.ProductsRepository
import com.alexisserapio.contalana_prototipe.a.data.db.ContalanaDatabase

class ContalanaApp: Application() {

    private val database by lazy{
        ContalanaDatabase.getDatabase(this)
    }

    val productsRepository by lazy{
        ProductsRepository(database.productDao())
    }


}