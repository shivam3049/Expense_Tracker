package com.example.expense_tracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expense_table ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expense_table WHERE date >= :startTime AND date <= :endTime ORDER BY date DESC")
    fun getExpensesBetween(startTime: Long, endTime: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expense_table WHERE category = :catName AND isDebit = 1")
    suspend fun getTotalSpentByCategory(catName: String): Int
}