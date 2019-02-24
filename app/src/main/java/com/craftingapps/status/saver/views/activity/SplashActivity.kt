package com.craftingapps.status.saver.views.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.craftingapps.status.saver.R
import android.content.Intent
import android.os.Handler
import android.view.Window.*
import android.view.WindowManager


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        load()
    }

    fun load() {
        Handler().postDelayed({
            val i = Intent(this@SplashActivity, DashBoardDrawerActivity::class.java)
            startActivity(i)
            finish()
        }, 2000)
    }

}
