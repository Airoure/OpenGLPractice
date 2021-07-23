package com.zjl.openglpractice

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.zjl.openglpractice.render2.TestBitmapRender2
import kotlinx.android.synthetic.main.activity_render_icon.*

class RenderIconActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render_icon)
        render.apply {
            setEGLContextClientVersion(2)
            val shader = TestBitmapRender2(this@RenderIconActivity, 0.5f,this,dp2px(100f),dp2px(100f))
            setRenderer(shader)
        }
    }

    private fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            resources.displayMetrics
        )
    }
}