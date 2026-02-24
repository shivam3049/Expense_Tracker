package com.example.expense_tracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow // Ye import zaroori hai

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    // 1. Ye function missing tha (TimelineFragment ise hi dhoond raha hai)
    @Query("SELECT * FROM expense_table ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    // 2. Filter ke liye function
    @Query("SELECT * FROM expense_table WHERE date >= :startTime AND date <= :endTime ORDER BY date DESC")
    fun getExpensesBetween(startTime: Long, endTime: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expense_table WHERE category = :catName AND isDebit = 1")
    suspend fun getTotalSpentByCategory(catName: String): Int
}