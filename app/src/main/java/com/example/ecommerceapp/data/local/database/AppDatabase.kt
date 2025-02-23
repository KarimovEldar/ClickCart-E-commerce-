package com.example.ecommerceapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ecommerceapp.data.local.dao.ProductDao
import com.example.ecommerceapp.data.local.entities.ProductModel
import com.example.ecommerceapp.utils.Constants.Companion.DATABASE_NAME

@Database(entities = [ProductModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {

        @Volatile
        var database: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {

            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

            }
            return database!!
        }

    }

}