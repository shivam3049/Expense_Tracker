package com.example.expense_tracker

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "expense_table")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val amount: Int,       // Agar ye Int hai toh SMS mein .toInt() lagana hoga
    val note: String="",     // SMSReceiver mein 'note' pass karna zaroori hai
    val isDebit: Boolean=true, // SMSReceiver mein 'isDebit' pass karna zaroori hai
    val date: Long
)