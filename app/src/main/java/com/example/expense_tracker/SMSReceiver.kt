package com.example.expense_tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val body = sms.messageBody ?: continue
                Log.d("SMS_RECEIVER", "Received: $body")

                val isDebit = body.contains("debited", true) || body.contains("paid", true) || body.contains("sent", true)
                val isCredit = body.contains("credited", true) || body.contains("received", true)

                if (isDebit || isCredit) {
                    val amtRegex = Regex("(?i)(?:RS|INR|Rs)\\.?\\s*([\\d,]+\\.\\d{2}|[\\d,.]+)")
                    val amountDouble = amtRegex.find(body)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull() ?: 0.0

                    if (amountDouble > 0) {
                        val amount = amountDouble.toInt()

                        // Naam nikalne ka logic: ab uppercase aur lowercase dono dhoondega
                        val nameRegex = if (isDebit) {
                            Regex("(?i)(?:to|vpa|at|transfer to)\\s+([A-Za-z0-9\\s*]{3,25})")
                        } else {
                            Regex("(?i)(?:from|by|sender)\\s+([A-Za-z0-9\\s*]{3,25})")
                        }

                        val rawName = nameRegex.find(body)?.groupValues?.get(1)?.trim() ?: "Online Transaction"

                        // "Other" ki jagah ab rawName jayega
                        // ... baki code same rahega ...

// 3. Category/Name decide karein
                        val displayName = if (isCredit) {
                            // Agar paisa aaya hai, toh 'from' ya 'by' ke baad wala naam nikalein
                            val senderName = Regex("(?i)(?:from|by|sender|transfer by)\\s+([A-Za-z0-9\\s*]{3,25})")
                                .find(body)?.groupValues?.get(1)?.trim() ?: "Income/Received"
                            senderName
                        } else {
                            // Agar paisa gaya hai, toh purana rawName (Jo 'to' ya 'vpa' se nikala tha)
                            rawName
                        }

                        val expense = ExpenseEntity(
                            category = displayName, // Ab yahan "Income" ki jagah sender ka naam aayega
                            amount = amount,
                            note = "SMS: $rawName",
                            isDebit = isDebit,
                            date = System.currentTimeMillis()
                        )

// ... baki database save wala code same rahega ...

                        val database = ExpenseDatabase.getDatabase(context)
                        CoroutineScope(Dispatchers.IO).launch {
                            database.expenseDao().insertExpense(expense)
                            Log.d("SMS_RECEIVER", "Saved: $amount for $displayName")
                        }
                    }
                }
            }
        }
    }
}