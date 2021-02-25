package com.rs132studio.multiplerecycler.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.rs132studio.multiplerecycler.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var topAnim : Animation
    private lateinit var bottomAnim : Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        topAnim = AnimationUtils.loadAnimation(this,
            R.anim.top_anim
        )
        bottomAnim = AnimationUtils.loadAnimation(this,
            R.anim.bottom_anim
        )

        logo_bg.animation = topAnim
        loginTextView.animation = bottomAnim

        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}