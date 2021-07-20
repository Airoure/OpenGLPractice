package com.zjl.openglpractice.render

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import com.zjl.openglpractice.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: OpenGLPractice
 * ClassName:    SimpleBaseRender2
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年07月20日 9:32
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
class SimpleRender2(private val context: Context,private val width: Int, private val height: Int) : SimpleRender1(context) {

    private var curProgram = 0

    private var mAlpha = 0.5f

    private var maPositionHandler = 0

    private var maTextureHandler = 0

    private var muMVPMatrixHandler = 0

    private var muSTMatrixHandler = 0

    private var mMVPMatrix = FloatArray(16)

    private var mSTMatrix = FloatArray(16)

    private var mTextureID = IntArray(2)

    private val mVertexCoors = floatArrayOf(
        0f, 0f,
        1f, 0f,
        0f, 1f,
        1f ,1f
    )

    private val mTextureCoors = floatArrayOf(
        0f, 1f,
        1f, 1f,
        0f, 0f,
        1f, 0f
    )

    private var mVertexBuffer: FloatBuffer

    private var mTextureBuffer: FloatBuffer

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
        super.onSurfaceCreated(gl, config)

        curProgram = createProgram(getVertexShader(), getFragmentShader())
        if (curProgram != 0) {
            maPositionHandler = GLES20.glGetAttribLocation(curProgram, "aPosition")
            maTextureHandler = GLES20.glGetAttribLocation(curProgram, "aTextureCoord")
            val mTextureUniform2 = GLES20.glGetUniformLocation(curProgram,"uTexture")
            GLES20.glUniform1i(mTextureUniform2,0)
//            muMVPMatrixHandler = GLES20.glGetUniformLocation(curProgram, "uMVPMatrix")
//            muSTMatrixHandler = GLES20.glGetUniformLocation(curProgram, "uSTMatrix")

            GLES20.glGenTextures(1, mTextureID, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID[0])
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )

        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        //旋转到正常角度
//        Matrix.setRotateM(mMVPMatrix, 0, 180f, 0.0f, 0f, 1.0f)
//        //调整大小比例
//        Matrix.scaleM(mMVPMatrix, 0, (50/width).toFloat(), (50/height).toFloat(), 1f)
//        //调整位置
//        Matrix.translateM(
//            mMVPMatrix,
//            0,
//            -(width/ (50) - 1.0f),
//            -(height / (50) - 1.0f),
//            0f
//        )
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        GLES20.glUseProgram(curProgram)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureID[0])
        GLUtils.texImage2D(
            GLES20.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(
                context.resources,
                R.drawable.mute
            ), 0
        )


        mVertexBuffer.position(0)
        GLES20.glVertexAttribPointer(maPositionHandler,2,GLES20.GL_FLOAT,false,0,mVertexBuffer)
        GLES20.glEnableVertexAttribArray(maPositionHandler)
        mTextureBuffer.position(0)
        GLES20.glVertexAttribPointer(maTextureHandler,2,GLES20.GL_FLOAT,false,0,mTextureBuffer)
        GLES20.glEnableVertexAttribArray(maTextureHandler)

//        GLES20.glUniformMatrix4fv(muSTMatrixHandler, 1, false, mSTMatrix, 0)
//
//
//        GLES20.glUniformMatrix4fv(muMVPMatrixHandler, 1, false, mMVPMatrix, 0)


        //水印透明
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glFinish()
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        super.onFrameAvailable(surfaceTexture)
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
