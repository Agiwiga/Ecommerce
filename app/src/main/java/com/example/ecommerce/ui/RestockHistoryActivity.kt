package com.example.ecommerce.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.RestockAdapter
import com.example.ecommerce.data.RestockHistoryRepository

class RestockHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var repository: RestockHistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restock_history)

        recyclerView = findViewById(R.id.recyclerViewRestock)

        repository = RestockHistoryRepository(this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        showRestock()
    }

    override fun onResume() {
        super.onResume()

        showRestock()
    }

    private fun showRestock() {

        val adapter = RestockAdapter(
            repository.getAllRestock()
        )

        recyclerView.adapter = adapter
    }
}