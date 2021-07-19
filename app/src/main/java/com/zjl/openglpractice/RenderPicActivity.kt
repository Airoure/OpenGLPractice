package com.zjl.openglpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjl.openglpractice.render.SimpleRender1
import kotlinx.android.synthetic.main.activity_render_pic.*

class RenderPicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render_pic)
        render.apply {
            setEGLContextClientVersion(2)
            val shader = SimpleRender1(this@RenderPicActivity)
            setRenderer(shader)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}