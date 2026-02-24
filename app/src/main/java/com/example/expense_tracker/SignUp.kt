package com.example.expense_tracker

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.expense_tracker.databinding.ActivitySignupBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Google Sign In Setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.my_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    hideLoader() // ERROR: Hide loader
                    Toast.makeText(this, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                hideLoader()
            }
        }

        binding.btnGoogle.setOnClickListener {
            showLoader()
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        binding.registerbutton.setOnClickListener {
            registerUserWithEmail()
        }

        binding.Loginbutton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        setupTextStyles()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val userMap = hashMapOf(
                    "user" to (user?.displayName ?: "Google User"),
                    "email" to (user?.email ?: ""),
                    "uid" to (user?.uid ?: "")
                )

                db.collection("users").document(user!!.uid).set(userMap)
                    .addOnSuccessListener {
                        hideLoader() // SUCCESS: Hide loader
                        Toast.makeText(this, "Google Sign-Up Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        hideLoader()
                        Toast.makeText(this, "Firestore Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                hideLoader() // FAILED: Hide loader
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUserWithEmail() {
        val email = binding.etemail.text.toString().trim()
        val user = binding.etUsername.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val repeatPassword = binding.repeatpassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || user.isEmpty()) {
            Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != repeatPassword) {
            Toast.makeText(this, "Password must match", Toast.LENGTH_SHORT).show()
            return
        }
        showLoader()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                val userMap = hashMapOf("user" to user, "email" to email, "uid" to uid)
                db.collection("users").document(uid).set(userMap).addOnSuccessListener {
                    hideLoader()
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                hideLoader()
                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnGoogle.isEnabled = false
        binding.registerbutton.isEnabled = false
    }

    private fun hideLoader() {
        binding.progressBar.visibility = View.GONE
        binding.btnGoogle.isEnabled = true
        binding.registerbutton.isEnabled = true
    }

    private fun setupTextStyles() {
        val text = "ExpenseTracker"
        val span = SpannableString(text)
        span.setSpan(ForegroundColorSpan(Color.parseColor("#FDD702")), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(ForegroundColorSpan(Color.parseColor("#FFFFFF")), 7, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.Expense.text = span
        binding.Loginbutton.paintFlags = binding.Loginbutton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}