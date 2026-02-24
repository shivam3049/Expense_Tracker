package com.example.expense_tracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expense_tracker.databinding.ActivityTransactionDetailsBinding
import kotlinx.coroutines.launch

class TransactionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionDetailsBinding
    private lateinit var database: ExpenseDatabase
    private lateinit var detailAdapter: DetailedHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding setup
        binding = ActivityTransactionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = ExpenseDatabase.getDatabase(this)
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: ""

        // Adapter Setup
        detailAdapter = DetailedHistoryAdapter()
        binding.rvDetails.apply {
            adapter = detailAdapter
            layoutManager = LinearLayoutManager(this@TransactionDetailActivity)
        }

        // Data Load karna
        lifecycleScope.launch {
            database.expenseDao().getAllExpenses().collect { list ->
                val filteredList = list.filter { it.category == categoryName }
                detailAdapter.submitList(filteredList.sortedByDescending { it.date })
            }
        }
    }
}