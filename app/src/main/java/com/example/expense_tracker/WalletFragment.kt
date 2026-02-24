package com.example.expense_tracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expense_tracker.databinding.FragmentWalletBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WalletFragment : Fragment(R.layout.fragment_wallet) {

    private lateinit var binding: FragmentWalletBinding
    private lateinit var database: ExpenseDatabase
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWalletBinding.bind(view)
        database = ExpenseDatabase.getDatabase(requireContext())

        // FIX: Adapter initialize karte waqt click listener pass kiya
        expenseAdapter = ExpenseAdapter { selectedExpense ->
            val intent = Intent(requireContext(), TransactionDetailActivity::class.java)

            // Category name se brackets "(count)" hatane ke liye split kiya
            val cleanCategory = if (selectedExpense.category.contains(" (")) {
                selectedExpense.category.substringBefore(" (")
            } else {
                selectedExpense.category
            }

            intent.putExtra("CATEGORY_NAME", cleanCategory)
            startActivity(intent)
        }

        binding.rvMonthlyList.apply {
            adapter = expenseAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            database.expenseDao().getAllExpenses().collect { list ->
                val total = list.filter { it.isDebit }.sumOf { it.amount }
                binding.tvTotalExpense.text = "-₹$total"

                val groupedList = list.groupBy { it.category + it.isDebit }
                    .map { (key, originalItems) ->
                        val firstItem = originalItems[0]
                        val totalAmount = originalItems.sumOf { it.amount }
                        val count = originalItems.size

                        firstItem.copy(
                            category = if (count > 1) "${firstItem.category} ($count)" else firstItem.category,
                            amount = totalAmount
                        )
                    }.sortedByDescending { it.amount }

                expenseAdapter.submitList(groupedList)
                setupChart(list)
            }
        }
    }

    private fun setupChart(list: List<ExpenseEntity>) {
        val entries = ArrayList<Entry>()
        val monthLabels = mutableListOf<String>()
        val sdf = SimpleDateFormat("MMM", Locale.getDefault())

        for (i in 5 downTo 0) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -i)
            monthLabels.add(sdf.format(calendar.time))

            val monthlySum = list.filter {
                val expCal = Calendar.getInstance().apply { timeInMillis = it.date }
                expCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                        expCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && it.isDebit
            }.sumOf { it.amount }.toFloat()

            entries.add(Entry((5 - i).toFloat(), monthlySum))
        }

        val dataSet = LineDataSet(entries, "Monthly Expense").apply {
            color = Color.parseColor("#FF5252")
            setCircleColor(Color.parseColor("#FF5252"))
            lineWidth = 3f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.parseColor("#FF5252")
            fillAlpha = 35
        }

        binding.expenseChart.data = LineData(dataSet)
        binding.expenseChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.GRAY
            setDrawGridLines(false)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String = monthLabels.getOrNull(value.toInt()) ?: ""
            }
        }
        binding.expenseChart.axisLeft.textColor = Color.GRAY
        binding.expenseChart.axisRight.isEnabled = false
        binding.expenseChart.description.isEnabled = false
        binding.expenseChart.legend.isEnabled = false
        binding.expenseChart.invalidate()
    }
}