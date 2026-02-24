package com.example.expense_tracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.expense_tracker.databinding.ActivityLoginBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)

        Handler(Looper.getMainLooper()).postDelayed({
            val anim = AnimationUtils.loadAnimation(this, R.anim.text_fade_slide)
            tvTitle.startAnimation(anim)
            tvTitle.alpha = 1f
        }, 600)



        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }, 2500)
    }
}
