package com.example.ecommerce.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.ProfitRepository
import java.text.NumberFormat
import java.util.Locale

class ProfitReportActivity : AppCompatActivity() {

    private lateinit var profitRepository: ProfitRepository

    private lateinit var salesTextView: TextView
    private lateinit var capitalTextView: TextView
    private lateinit var profitTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profit_report)

        profitRepository = ProfitRepository(this)

        salesTextView = findViewById(R.id.textViewTotalSales)
        capitalTextView = findViewById(R.id.textViewTotalCapital)
        profitTextView = findViewById(R.id.textViewTotalProfit)

        loadReport()
    }

    private fun loadReport() {

        val report = profitRepository.getProfitReport()

        salesTextView.text =
            formatCurrency(report.totalSales)

        capitalTextView.text =
            formatCurrency(report.totalCapital)

        profitTextView.text =
            formatCurrency(report.totalProfit)

    }

    private fun formatCurrency(value: Double): String {

        return NumberFormat
            .getCurrencyInstance(Locale("in", "ID"))
            .format(value)

    }

}