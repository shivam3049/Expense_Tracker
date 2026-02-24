package com.example.expense_tracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expense_tracker.databinding.ExpenseItemBinding
import java.text.SimpleDateFormat
import java.util.*

class DetailedHistoryAdapter : ListAdapter<ExpenseEntity, DetailedHistoryAdapter.DetailViewHolder>(DiffCallback) {

    class DetailViewHolder(val binding: ExpenseItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val expense = getItem(position)

        holder.binding.apply {
            // Category Name
            tvCategory.text = expense.category

            tvDate.visibility = View.VISIBLE // Yahan hum date ko dikhayenge

            val sdfDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val dateStr = sdfDate.format(Date(expense.date))
            val timeStr = sdfTime.format(Date(expense.date))

            tvDate.text = "$dateStr | $timeStr"
            tvDate.setTextColor(Color.GRAY)

            // Amount logic
            val amountText = if (expense.isDebit) "- ₹${expense.amount}" else "+ ₹${expense.amount}"
            tvAmount.text = amountText
            tvAmount.setTextColor(if (expense.isDebit) Color.RED else Color.parseColor("#4CAF50"))
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ExpenseEntity>() {
        override fun areItemsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity) = oldItem == newItem
    }
}