package com.zjl.openglpractice

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.zjl.openglpractice.render.SimpleRender2
import com.zjl.openglpractice.render.TempRender2
import kotlinx.android.synthetic.main.activity_render_icon.*

class RenderIconActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render_icon)
        render.apply {
            setEGLContextClientVersion(2)
            val shader = TempRender2(this@RenderIconActivity,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50f,resources.displayMetrics).toInt(),TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50f,resources.displayMetrics).toInt())
            shader.setGLSurfaceView(this)
            setRenderer(shader)
        }
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}