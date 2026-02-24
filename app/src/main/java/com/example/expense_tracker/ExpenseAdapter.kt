package com.example.expense_tracker

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expense_tracker.databinding.ExpenseItemBinding

// FIX: Constructor mein click listener add kiya gaya hai
class ExpenseAdapter(private val onItemClick: (ExpenseEntity) -> Unit) :
    ListAdapter<ExpenseEntity, ExpenseAdapter.ExpenseViewHolder>(DiffCallback) {

    class ExpenseViewHolder(val binding: ExpenseItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)

        holder.binding.apply {
            // Click handle karne ke liye logic
            root.setOnClickListener { onItemClick(expense) }

            val fullText = expense.category

            if (fullText.contains(" (")) {
                val spannable = SpannableString(fullText)
                val startIndex = fullText.lastIndexOf(" (")
                spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.setSpan(ForegroundColorSpan(Color.parseColor("#FF5252")), startIndex, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvCategory.text = spannable
            } else {
                tvCategory.text = fullText
            }

            // Wallet/Timeline view mein date hide rahegi
            tvDate.visibility = View.VISIBLE
            tvAmount.text = if (expense.isDebit) "- ₹${expense.amount}" else "+ ₹${expense.amount}"
            tvAmount.setTextColor(if (expense.isDebit) Color.RED else Color.parseColor("#4CAF50"))
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ExpenseEntity>() {
        override fun areItemsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity) = oldItem == newItem
    }
}