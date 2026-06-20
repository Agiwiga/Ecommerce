package com.example.ecommerce.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USERS_TABLE)
        db.execSQL(CREATE_PRODUCTS_TABLE)
        db.execSQL(CREATE_CART_TABLE)
        db.execSQL(CREATE_ORDERS_TABLE)
        insertSampleProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun insertSampleProducts(db: SQLiteDatabase) {
        val sampleProducts = listOf(
            SampleProduct(
                name = "Wireless Headphone",
                description = "Headphone Bluetooth dengan suara jernih dan baterai tahan lama.",
                price = 249000.0,
                stock = 15
            ),
            SampleProduct(
                name = "Smart Watch",
                description = "Jam tangan pintar untuk aktivitas harian dan notifikasi.",
                price = 399000.0,
                stock = 10
            ),
            SampleProduct(
                name = "USB-C Charger",
                description = "Charger cepat USB-C 30W untuk ponsel dan tablet.",
                price = 129000.0,
                stock = 25
            ),
            SampleProduct(
                name = "Laptop Stand",
                description = "Dudukan laptop aluminium yang kokoh dan mudah dilipat.",
                price = 179000.0,
                stock = 12
            )
        )

        sampleProducts.forEach { product ->
            val values = ContentValues().apply {
                put(COLUMN_PRODUCT_NAME, product.name)
                put(COLUMN_PRODUCT_DESCRIPTION, product.description)
                put(COLUMN_PRODUCT_PRICE, product.price)
                put(COLUMN_PRODUCT_IMAGE_URL, "")
                put(COLUMN_PRODUCT_STOCK, product.stock)
            }
            db.insert(TABLE_PRODUCTS, null, values)
        }
    }

    private data class SampleProduct(
        val name: String,
        val description: String,
        val price: Double,
        val stock: Int
    )

    companion object {
        private const val DATABASE_NAME = "ecommerce.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val TABLE_PRODUCTS = "products"
        const val TABLE_CART = "cart"
        const val TABLE_ORDERS = "orders"

        const val COLUMN_ID = "id"

        const val COLUMN_USER_NAME = "name"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"

        const val COLUMN_PRODUCT_NAME = "name"
        const val COLUMN_PRODUCT_DESCRIPTION = "description"
        const val COLUMN_PRODUCT_PRICE = "price"
        const val COLUMN_PRODUCT_IMAGE_URL = "image_url"
        const val COLUMN_PRODUCT_STOCK = "stock"

        const val COLUMN_CART_USER_ID = "user_id"
        const val COLUMN_CART_PRODUCT_ID = "product_id"
        const val COLUMN_CART_QUANTITY = "quantity"

        const val COLUMN_ORDER_USER_ID = "user_id"
        const val COLUMN_ORDER_TOTAL_PRICE = "total_price"
        const val COLUMN_ORDER_CREATED_AT = "created_at"
        const val COLUMN_ORDER_STATUS = "status"

        private const val CREATE_USERS_TABLE = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_NAME TEXT NOT NULL,
                $COLUMN_USER_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_USER_PASSWORD TEXT NOT NULL
            )
        """

        private const val CREATE_PRODUCTS_TABLE = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUCT_NAME TEXT NOT NULL,
                $COLUMN_PRODUCT_DESCRIPTION TEXT NOT NULL,
                $COLUMN_PRODUCT_PRICE REAL NOT NULL,
                $COLUMN_PRODUCT_IMAGE_URL TEXT,
                $COLUMN_PRODUCT_STOCK INTEGER NOT NULL
            )
        """

        private const val CREATE_CART_TABLE = """
            CREATE TABLE $TABLE_CART (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CART_USER_ID INTEGER NOT NULL,
                $COLUMN_CART_PRODUCT_ID INTEGER NOT NULL,
                $COLUMN_CART_QUANTITY INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_CART_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID),
                FOREIGN KEY($COLUMN_CART_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_ID)
            )
        """

        private const val CREATE_ORDERS_TABLE = """
            CREATE TABLE $TABLE_ORDERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ORDER_USER_ID INTEGER NOT NULL,
                $COLUMN_ORDER_TOTAL_PRICE REAL NOT NULL,
                $COLUMN_ORDER_CREATED_AT INTEGER NOT NULL,
                $COLUMN_ORDER_STATUS TEXT NOT NULL,
                FOREIGN KEY($COLUMN_ORDER_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """
    }
}
