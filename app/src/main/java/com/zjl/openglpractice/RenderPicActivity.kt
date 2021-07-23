package com.zjl.openglpractice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zjl.openglpractice.render2.TestSimpleRender
import kotlinx.android.synthetic.main.activity_render_pic.*

class RenderPicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render_pic)
        render.apply {
            setEGLContextClientVersion(2)
            val shader = TestSimpleRender(this@RenderPicActivity,1.0f,this)
            setRenderer(shader)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}