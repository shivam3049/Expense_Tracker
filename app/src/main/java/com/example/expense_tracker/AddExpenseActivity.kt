package com.example.expense_tracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expense_tracker.databinding.ActivityAddExpenseBinding // Check karein aapka binding name sahi hai
import kotlinx.coroutines.launch
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var database: ExpenseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Database initialize karein
        database = ExpenseDatabase.getDatabase(this)

        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun saveExpense() {
        val amountString = binding.etAmount.text.toString()
        val category = binding.etCategory.text.toString()

        if (amountString.isNotEmpty() && category.isNotEmpty()) {
            val amount = amountString.toInt()

            lifecycleScope.launch {
                val newExpense = ExpenseEntity(
                    amount = amount,
                    category = category,
                    date = System.currentTimeMillis(),
                    isDebit = true // <--- YE LINE ADD KARNA ZAROORI HAI
                )

                database.expenseDao().insertExpense(newExpense)

                runOnUiThread {
                    Toast.makeText(this@AddExpenseActivity, "Expense Added!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show()
        }
    }
}