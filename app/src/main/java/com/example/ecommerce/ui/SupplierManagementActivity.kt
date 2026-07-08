package com.example.ecommerce.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.SupplierAdapter
import com.example.ecommerce.data.SupplierRepository

class SupplierManagementActivity : AppCompatActivity() {

    private lateinit var supplierRepository: SupplierRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_management)

        supplierRepository = SupplierRepository(this)

        recyclerView = findViewById(R.id.recyclerViewSuppliers)
        addButton = findViewById(R.id.buttonAddSupplier)

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadSuppliers()

        addButton.setOnClickListener {
            startActivity(
                android.content.Intent(
                    this,
                    AddSupplierActivity::class.java
                )
            )
        }
    }
    override fun onResume() {
        super.onResume()
        loadSuppliers()
    }
    private fun loadSuppliers() {

        val suppliers = supplierRepository.getAllSuppliers()

        recyclerView.adapter = SupplierAdapter(
            suppliers,
            onEditClick = {

                val intent = android.content.Intent(
                    this,
                    EditSupplierActivity::class.java
                )

                intent.putExtra(EditSupplierActivity.EXTRA_ID, it.id)
                intent.putExtra(EditSupplierActivity.EXTRA_NAME, it.name)
                intent.putExtra(EditSupplierActivity.EXTRA_ADDRESS, it.address)
                intent.putExtra(EditSupplierActivity.EXTRA_PHONE, it.phone)

                startActivity(intent)
            },
            onDeleteClick = {

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Hapus Supplier")
                    .setMessage("Yakin ingin menghapus ${it.name}?")
                    .setPositiveButton("Ya") { _, _ ->

                        val success = supplierRepository.deleteSupplier(it.id)

                        if (success) {
                            Toast.makeText(
                                this,
                                "Supplier berhasil dihapus",
                                Toast.LENGTH_SHORT
                            ).show()

                            loadSuppliers()

                        } else {

                            Toast.makeText(
                                this,
                                "Gagal menghapus supplier",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    }
                    .setNegativeButton("Batal", null)
                    .show()

            }
        )
    }
}