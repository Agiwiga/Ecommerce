package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.model.Restock
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RestockAdapter(
    private val restockList: List<Restock>
) : RecyclerView.Adapter<RestockAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val supplier: TextView = view.findViewById(R.id.textSupplier)
        val product: TextView = view.findViewById(R.id.textProduct)
        val quantity: TextView = view.findViewById(R.id.textQuantity)
        val purchasePrice: TextView = view.findViewById(R.id.textPurchasePrice)
        val totalCost: TextView = view.findViewById(R.id.textTotalCost)
        val date: TextView = view.findViewById(R.id.textDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restock, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val restock = restockList[position]

        val rupiah =
            NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        val formatter =
            SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

        holder.supplier.text = restock.supplierName
        holder.product.text = restock.productName

        holder.quantity.text =
            "Jumlah : ${restock.quantity}"

        holder.purchasePrice.text =
            "Harga : ${rupiah.format(restock.purchasePrice)}"

        holder.totalCost.text =
            "Total : ${rupiah.format(restock.totalCost)}"

        holder.date.text =
            formatter.format(Date(restock.createdAt))
    }

    override fun getItemCount(): Int {
        return restockList.size
    }
}