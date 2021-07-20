package com.zjl.openglpractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_render_pic.setOnClickListener {
            startActivity(Intent(this,RenderPicActivity::class.java))
        }

        btn_render_icon.setOnClickListener {
            startActivity(Intent(this,RenderIconActivity::class.java))
        }

        btn_render_video.setOnClickListener {
            startActivity(Intent(this,RenderVideoActivity::class.java))
        }
    }
}