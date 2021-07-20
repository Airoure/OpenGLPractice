package com.zjl.openglpractice.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLUtils
import com.zjl.openglpractice.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: OpenGLPractice
 * ClassName:    SimpleShader1
 *
 * Description: 图片渲染器
 *
 * @author  zjl
 * @date    2021年07月19日 14:37
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
open class SimpleRender1(context: Context) : BaseRender() {

    private var mCanUpdate = false

    private var mProgram = 0

    private var maPositionHandler = 0

    private var maCoordinateHandler = 0

    private var muTextureHandler = 0

    private var mSurfaceTexture: SurfaceTexture? = null

    private val mTextureID = IntArray(2)

    private var mVertexBuffer: FloatBuffer

    private var mTextureBuffer: FloatBuffer

    private val mBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.secret)

    private val mVertexCoors = floatArrayOf(
        -1f, -1f,
        1f, -1f,
        -1f, 1f,
        1f ,1f
    )

    private val mTextureCoors = floatArrayOf(
        0.1f, 1f,
        0.9f, 1f,
        0.1f, -0f,
        0.9f, 0f
    )

    init {
        val bb = ByteBuffer.allocateDirect(mVertexCoors.size*4)
        bb.order(ByteOrder.nativeOrder())
        mVertexBuffer = bb.asFloatBuffer()
        mVertexBuffer.put(mVertexCoors)
        mVertexBuffer.position(0)

        val cc = ByteBuffer.allocateDirect(mTextureCoors.size*4)
        cc.order(ByteOrder.nativeOrder())
        mTextureBuffer = cc.asFloatBuffer()
        mTextureBuffer.put(mTextureCoors)
        mTextureBuffer.position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mProgram = createProgram(getVertexShader(), getFragmentShader())
        if (mProgram != 0) {
            maPositionHandler = GLES20.glGetAttribLocation(mProgram, "aPosition")
            maCoordinateHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate")
            muTextureHandler = GLES20.glGetUniformLocation(mProgram,"uTexture")
            GLES20.glUniform1i(muTextureHandler, 0)
            GLES20.glGenTextures(1, mTextureID, 0)
            //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID[0])

            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE
            )

        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        initDrawer()
        bindDrawFrameTexture()
        bindBitmapToTexture()
        initPointerAndDraw()
        GLES20.glFinish()
    }

    private fun bindBitmapToTexture() {
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,mBitmap,0)
    }

    private fun initPointerAndDraw() {
        //启用顶点的句柄
        GLES20.glEnableVertexAttribArray(maPositionHandler)
        GLES20.glEnableVertexAttribArray(maCoordinateHandler)

        GLES20.glVertexAttribPointer(maPositionHandler, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
        GLES20.glVertexAttribPointer(maCoordinateHandler, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer)
        //开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

    }

    private fun bindDrawFrameTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            0x8D65,
            mTextureID[0]
        )

    }

    private fun initDrawer() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(
            GLES20.GL_DEPTH_BUFFER_BIT
                    or GLES20.GL_COLOR_BUFFER_BIT
        )

        GLES20.glUseProgram(mProgram)

    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        mCanUpdate = true
    }

    private fun getVertexShader(): String {
        return "attribute vec4 aPosition;" +
                "attribute vec2 aCoordinate;" +
                "varying vec2 vCoordinate;" +
                "void main() {" +
                "  gl_Position = aPosition;" +
                "  vCoordinate = aCoordinate;" +
                "}"
    }

    private fun getFragmentShader(): String {
        return  "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;" +
                "uniform sampler2D uTexture;" +
                "varying vec2 vCoordinate;" +
                "void main() {" +
                "  vec4 color = texture2D(uTexture, vCoordinate);" +
                "  gl_FragColor = color;" +
                "}"
    }
}