package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.model.Supplier

class SupplierAdapter(
    private val suppliers: List<Supplier>,
    private val onEditClick: (Supplier) -> Unit,
    private val onDeleteClick: (Supplier) -> Unit
) : RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_supplier, parent, false)

        return SupplierViewHolder(view)
    }

    override fun getItemCount() = suppliers.size

    override fun onBindViewHolder(holder: SupplierViewHolder, position: Int) {
        holder.bind(suppliers[position])
    }

    inner class SupplierViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView = itemView.findViewById(R.id.textViewSupplierName)
        private val address: TextView = itemView.findViewById(R.id.textViewSupplierAddress)
        private val phone: TextView = itemView.findViewById(R.id.textViewSupplierPhone)

        private val edit: Button = itemView.findViewById(R.id.buttonEditSupplier)
        private val delete: Button = itemView.findViewById(R.id.buttonDeleteSupplier)

        fun bind(supplier: Supplier) {

            name.text = supplier.name
            address.text = supplier.address
            phone.text = supplier.phone

            edit.setOnClickListener {
                onEditClick(supplier)
            }

            delete.setOnClickListener {
                onDeleteClick(supplier)
            }
        }
    }
}