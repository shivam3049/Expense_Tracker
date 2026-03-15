package com.example.expense_tracker

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "expense_table")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val amount: Int,
    val note: String="",
    val isDebit: Boolean=true,
    val date: Long
)