package com.example.expense_tracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expense_tracker.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.Loginbutton.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all details ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }

        binding.signUpbutton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        val text = "ExpenseTracker"
        val span = SpannableString(text)

        span.setSpan(
            ForegroundColorSpan(Color.parseColor("#FDD702")),
            0,
            7,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        span.setSpan(
            ForegroundColorSpan(Color.parseColor("#FFFFFF")),
            7,
            14,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.Expense.text = span
    }
}


