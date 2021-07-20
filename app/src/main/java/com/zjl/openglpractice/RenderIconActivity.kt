package com.zjl.openglpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjl.openglpractice.render.SimpleRender2
import com.zjl.openglpractice.render.TempRender2
import kotlinx.android.synthetic.main.activity_render_icon.*

class RenderIconActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render_icon)
        render.apply {
            setEGLContextClientVersion(2)
            val shader = TempRender2(this@RenderIconActivity,width,height)
            setRenderer(shader)
        }
    }
}