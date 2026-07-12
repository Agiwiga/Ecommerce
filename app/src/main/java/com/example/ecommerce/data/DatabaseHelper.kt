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
        db.execSQL(CREATE_SUPPLIERS_TABLE)
        db.execSQL(CREATE_RESTOCK_TABLE)
        db.execSQL(CREATE_CART_TABLE)
        db.execSQL(CREATE_ORDERS_TABLE)
        db.execSQL(CREATE_ORDER_ITEMS_TABLE)
        insertDefaultAdmin(db)
        insertSampleProducts(db)
        insertDefaultSuppliers(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                "ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_USER_ROLE TEXT NOT NULL DEFAULT '$ROLE_CUSTOMER'"
            )
            insertDefaultAdmin(db)
        }

        if (oldVersion < 3) {
            db.execSQL(
                "ALTER TABLE $TABLE_PRODUCTS ADD COLUMN $COLUMN_PRODUCT_CATEGORY TEXT NOT NULL DEFAULT 'Pakan'"
            )
            db.execSQL(
                "ALTER TABLE $TABLE_PRODUCTS ADD COLUMN $COLUMN_PRODUCT_SALE_TYPE TEXT NOT NULL DEFAULT 'weight'"
            )
            db.execSQL(
                "ALTER TABLE $TABLE_PRODUCTS ADD COLUMN $COLUMN_PRODUCT_PACKAGE_QUANTITY REAL NOT NULL DEFAULT 50"
            )
        }

        if (oldVersion < 4) {
            db.execSQL(
                "ALTER TABLE $TABLE_CART ADD COLUMN $COLUMN_CART_PURCHASE_TYPE TEXT NOT NULL DEFAULT 'Kg'"
            )

            db.execSQL(
                "ALTER TABLE $TABLE_CART ADD COLUMN $COLUMN_CART_ACTUAL_WEIGHT REAL NOT NULL DEFAULT 0"
            )

            db.execSQL(
                "ALTER TABLE $TABLE_CART ADD COLUMN $COLUMN_CART_TOTAL_PRICE REAL NOT NULL DEFAULT 0"
            )
        }

        if (oldVersion < 5) {
            db.execSQL(
                "UPDATE $TABLE_PRODUCTS SET $COLUMN_PRODUCT_SALE_TYPE = 'Berat' WHERE $COLUMN_PRODUCT_SALE_TYPE = 'weight'"
            )
            db.execSQL(
                "ALTER TABLE $TABLE_CART ADD COLUMN $COLUMN_CART_INPUT_QUANTITY REAL NOT NULL DEFAULT 0"
            )
            db.execSQL(
                "ALTER TABLE $TABLE_CART ADD COLUMN $COLUMN_CART_ACTUAL_QUANTITY REAL NOT NULL DEFAULT 0"
            )
            db.execSQL(CREATE_ORDER_ITEMS_TABLE)
        }

        if (oldVersion < 6) {
            db.execSQL(CREATE_SUPPLIERS_TABLE)
            insertDefaultSuppliers(db)
        }

        if (oldVersion < 7) {
            db.execSQL(CREATE_RESTOCK_TABLE)
        }
    }

    private fun insertDefaultAdmin(db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, "Admin")
            put(COLUMN_USER_EMAIL, "admin@gmail.com")
            put(COLUMN_USER_PASSWORD, "123")
            put(COLUMN_USER_ROLE, ROLE_ADMIN)
        }
        db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    private fun insertSampleProducts(db: SQLiteDatabase) {
        val sampleProducts = listOf(
            SampleProduct(
                name = "Pakan Ayam Layer",
                description = "Pakan ayam petelur dengan nutrisi lengkap untuk produksi harian.",
                price = 12000.0,
                stock = 15.0,
                category = "Pakan",
                saleType = "Berat",
                packageQuantity = 50.0
            ),
            SampleProduct(
                name = "Pakan Kambing Fermentasi",
                description = "Pakan fermentasi siap pakai untuk kambing dan domba.",
                price = 9000.0,
                stock = 10.0,
                category = "Pakan",
                saleType = "Berat",
                packageQuantity = 25.0
            ),
            SampleProduct(
                name = "Vitamin Unggas",
                description = "Vitamin tambahan untuk menjaga daya tahan tubuh unggas.",
                price = 2500.0,
                stock = 25.0,
                category = "Vitamin",
                saleType = "Satuan",
                packageQuantity = 12.0
            ),
            SampleProduct(
                name = "Mineral Ternak",
                description = "Suplemen mineral untuk sapi, kambing, dan domba.",
                price = 3500.0,
                stock = 12.0,
                category = "Vitamin",
                saleType = "Satuan",
                packageQuantity = 10.0
            )
        )

        sampleProducts.forEach { product ->
            val values = ContentValues().apply {
                put(COLUMN_PRODUCT_NAME, product.name)
                put(COLUMN_PRODUCT_DESCRIPTION, product.description)
                put(COLUMN_PRODUCT_PRICE, product.price)
                put(COLUMN_PRODUCT_IMAGE_URL, "")
                put(COLUMN_PRODUCT_STOCK, product.stock)
                put(COLUMN_PRODUCT_CATEGORY, product.category)
                put(COLUMN_PRODUCT_SALE_TYPE, product.saleType)
                put(COLUMN_PRODUCT_PACKAGE_QUANTITY, product.packageQuantity)
            }
            db.insert(TABLE_PRODUCTS, null, values)
        }
    }
    private fun insertDefaultSuppliers(db: SQLiteDatabase) {
        val suppliers = listOf(
            Triple("Supplier A", "Kota A", "081111111111"),
            Triple("Supplier B", "Kota B", "082222222222"),
            Triple("Supplier C", "Kota C", "083333333333"),
            Triple("Supplier D", "Kota D", "084444444444")
        )

        suppliers.forEach {
            val values = ContentValues().apply {
                put(COLUMN_SUPPLIER_NAME, it.first)
                put(COLUMN_SUPPLIER_ADDRESS, it.second)
                put(COLUMN_SUPPLIER_PHONE, it.third)
            }
            db.insert(TABLE_SUPPLIERS, null, values)
        }
    }

    private data class SampleProduct(
        val name: String,
        val description: String,
        val price: Double,
        val stock: Double,
        val category: String,
        val saleType: String,
        val packageQuantity: Double
    )

    fun getTotalProducts(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_PRODUCTS",
            null
        )

        cursor.moveToFirst()
        val total = cursor.getInt(0)
        cursor.close()
        return total
    }

    fun getAllCategories(): List<String> {

        val categories = mutableListOf<String>()

        categories.add("Semua")

        val database = readableDatabase

        val cursor = database.rawQuery(
            """
        SELECT DISTINCT $COLUMN_PRODUCT_CATEGORY
        FROM $TABLE_PRODUCTS
        ORDER BY $COLUMN_PRODUCT_CATEGORY
        """.trimIndent(),
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                categories.add(
                    it.getString(0)
                )

            }

        }

        return categories
    }

    fun getTotalOrders(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_ORDERS",
            null
        )

        cursor.moveToFirst()
        val total = cursor.getInt(0)
        cursor.close()
        return total
    }

    fun getTotalOrdersByStatus(status: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_ORDERS WHERE $COLUMN_ORDER_STATUS = ?",
            arrayOf(status)
        )

        cursor.moveToFirst()
        val total = cursor.getInt(0)
        cursor.close()
        return total
    }

    fun isEmailExists(email: String): Boolean {

        val database = readableDatabase

        val cursor = database.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_USER_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    fun updatePassword(
        email: String,
        newPassword: String
    ): Boolean {

        val values = android.content.ContentValues().apply {
            put(COLUMN_USER_PASSWORD, newPassword)
        }

        return writableDatabase.update(
            TABLE_USERS,
            values,
            "$COLUMN_USER_EMAIL = ?",
            arrayOf(email)
        ) > 0
    }

    companion object {
        private const val DATABASE_NAME = "ecommerce.db"
        private const val DATABASE_VERSION = 8

        const val TABLE_USERS = "users"
        const val TABLE_PRODUCTS = "products"
        const val TABLE_SUPPLIERS = "suppliers"
        const val TABLE_RESTOCK = "restock"
        const val TABLE_CART = "cart"
        const val TABLE_ORDERS = "orders"

        const val COLUMN_ID = "id"

        const val COLUMN_USER_NAME = "name"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"
        const val COLUMN_USER_ROLE = "role"

        const val ROLE_ADMIN = "admin"
        const val ROLE_CUSTOMER = "customer"

        const val COLUMN_PRODUCT_NAME = "name"
        const val COLUMN_PRODUCT_DESCRIPTION = "description"
        const val COLUMN_PRODUCT_PRICE = "price"
        const val COLUMN_PRODUCT_IMAGE_URL = "image_url"
        const val COLUMN_PRODUCT_STOCK = "stock"
        const val COLUMN_PRODUCT_CATEGORY = "category"
        const val COLUMN_PRODUCT_SALE_TYPE = "sale_type"
        const val COLUMN_PRODUCT_PACKAGE_QUANTITY = "package_quantity"
        const val COLUMN_SUPPLIER_NAME = "supplier_name"
        const val COLUMN_SUPPLIER_ADDRESS = "supplier_address"
        const val COLUMN_SUPPLIER_PHONE = "supplier_phone"
        const val COLUMN_RESTOCK_PRODUCT_ID = "product_id"
        const val COLUMN_RESTOCK_SUPPLIER_ID = "supplier_id"
        const val COLUMN_RESTOCK_QUANTITY = "quantity"
        const val COLUMN_RESTOCK_PURCHASE_PRICE = "purchase_price"
        const val COLUMN_RESTOCK_TOTAL_COST = "total_cost"
        const val COLUMN_RESTOCK_CREATED_AT = "created_at"
        const val COLUMN_CART_USER_ID = "user_id"
        const val COLUMN_CART_PRODUCT_ID = "product_id"
        const val COLUMN_CART_QUANTITY = "quantity"
        const val COLUMN_CART_PURCHASE_TYPE = "purchase_type"
        const val COLUMN_CART_ACTUAL_WEIGHT = "actual_weight"
        const val COLUMN_CART_INPUT_QUANTITY = "input_quantity"
        const val COLUMN_CART_ACTUAL_QUANTITY = "actual_quantity"
        const val COLUMN_CART_TOTAL_PRICE = "total_price"
        const val COLUMN_ORDER_USER_ID = "user_id"
        const val COLUMN_ORDER_TOTAL_PRICE = "total_price"
        const val COLUMN_ORDER_CREATED_AT = "created_at"
        const val COLUMN_ORDER_STATUS = "status"
        const val COLUMN_ORDER_PAYMENT_METHOD = "payment_method"
        const val TABLE_ORDER_ITEMS = "order_items"
        const val COLUMN_ORDER_ITEM_ORDER_ID = "order_id"
        const val COLUMN_ORDER_ITEM_PRODUCT_ID = "product_id"
        const val COLUMN_ORDER_ITEM_PURCHASE_TYPE = "purchase_type"
        const val COLUMN_ORDER_ITEM_INPUT_QUANTITY = "input_quantity"
        const val COLUMN_ORDER_ITEM_ACTUAL_QUANTITY = "actual_quantity"
        const val COLUMN_ORDER_ITEM_TOTAL_PRICE = "total_price"

        private const val CREATE_USERS_TABLE = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_NAME TEXT NOT NULL,
                $COLUMN_USER_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_USER_PASSWORD TEXT NOT NULL,
                $COLUMN_USER_ROLE TEXT NOT NULL
            )
        """

        private const val CREATE_PRODUCTS_TABLE = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUCT_NAME TEXT NOT NULL,
                $COLUMN_PRODUCT_DESCRIPTION TEXT NOT NULL,
                $COLUMN_PRODUCT_PRICE REAL NOT NULL,
                $COLUMN_PRODUCT_IMAGE_URL TEXT,
                $COLUMN_PRODUCT_STOCK INTEGER NOT NULL,
                $COLUMN_PRODUCT_CATEGORY TEXT NOT NULL,
                $COLUMN_PRODUCT_SALE_TYPE TEXT NOT NULL,
                $COLUMN_PRODUCT_PACKAGE_QUANTITY REAL NOT NULL
            )
        """
        private const val CREATE_SUPPLIERS_TABLE = """
            CREATE TABLE $TABLE_SUPPLIERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SUPPLIER_NAME TEXT NOT NULL,
                $COLUMN_SUPPLIER_ADDRESS TEXT NOT NULL,
                $COLUMN_SUPPLIER_PHONE TEXT NOT NULL
             )
        """

        private const val CREATE_RESTOCK_TABLE = """
            CREATE TABLE $TABLE_RESTOCK (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RESTOCK_PRODUCT_ID INTEGER NOT NULL,
                $COLUMN_RESTOCK_SUPPLIER_ID INTEGER NOT NULL,
                $COLUMN_RESTOCK_QUANTITY REAL NOT NULL,
                $COLUMN_RESTOCK_PURCHASE_PRICE REAL NOT NULL,
                $COLUMN_RESTOCK_TOTAL_COST REAL NOT NULL,
                $COLUMN_RESTOCK_CREATED_AT INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_RESTOCK_PRODUCT_ID)
                    REFERENCES $TABLE_PRODUCTS($COLUMN_ID),
                FOREIGN KEY($COLUMN_RESTOCK_SUPPLIER_ID)
                    REFERENCES $TABLE_SUPPLIERS($COLUMN_ID)
            )
            """
        private const val CREATE_CART_TABLE = """
            CREATE TABLE $TABLE_CART (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CART_USER_ID INTEGER NOT NULL,
                $COLUMN_CART_PRODUCT_ID INTEGER NOT NULL,
                $COLUMN_CART_PURCHASE_TYPE TEXT NOT NULL,
                $COLUMN_CART_QUANTITY REAL NOT NULL,
                $COLUMN_CART_ACTUAL_WEIGHT REAL NOT NULL,
                $COLUMN_CART_INPUT_QUANTITY REAL NOT NULL,
                $COLUMN_CART_ACTUAL_QUANTITY REAL NOT NULL,
                $COLUMN_CART_TOTAL_PRICE REAL NOT NULL,
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
                payment_method TEXT NOT NULL,
                FOREIGN KEY($COLUMN_ORDER_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """

        private const val CREATE_ORDER_ITEMS_TABLE = """
            CREATE TABLE $TABLE_ORDER_ITEMS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ORDER_ITEM_ORDER_ID INTEGER NOT NULL,
                $COLUMN_ORDER_ITEM_PRODUCT_ID INTEGER NOT NULL,
                $COLUMN_ORDER_ITEM_PURCHASE_TYPE TEXT NOT NULL,
                $COLUMN_ORDER_ITEM_INPUT_QUANTITY REAL NOT NULL,
                $COLUMN_ORDER_ITEM_ACTUAL_QUANTITY REAL NOT NULL,
                $COLUMN_ORDER_ITEM_TOTAL_PRICE REAL NOT NULL,
                FOREIGN KEY($COLUMN_ORDER_ITEM_ORDER_ID) REFERENCES $TABLE_ORDERS($COLUMN_ID),
                FOREIGN KEY($COLUMN_ORDER_ITEM_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_ID)
            )
        """
    }

}
