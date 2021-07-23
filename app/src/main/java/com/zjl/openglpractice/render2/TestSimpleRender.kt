package com.zjl.openglpractice.render2

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.zjl.openglpractice.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: OpenGLPractice
 * ClassName:    TestSimpleRender
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年07月22日 9:59
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
open class TestSimpleRender(private val context: Context, private val mAlpha: Float, glSurfaceView: GLSurfaceView) :
    TestSimpleBaseRender() {

    private var mTriangleVerticesData = floatArrayOf(
        -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f, 1.0f, 0.0f
    )

    private var mTriangleVertices: FloatBuffer

    private var mMVPMatrix = FloatArray(16)

    private var mSTMatrix = FloatArray(16)

    protected var mpTextureID = IntArray(2)

    private var maPositionHandler = 0

    private var maTextureHandler = 0

    private var muMVPMatrixHandler = 0

    private var muSTMatrixHandler = 0

    private var curProgram = 0

    private val mBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.secret)

    protected var mGLSurfaceView: GLSurfaceView? = null

    init {
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.size * 4).order(
            ByteOrder.nativeOrder()
        ).asFloatBuffer()
        mTriangleVertices.put(mTriangleVerticesData).position(0)

        Matrix.setIdentityM(mSTMatrix, 0)
        Matrix.setIdentityM(mMVPMatrix, 0)

        this.mGLSurfaceView = glSurfaceView
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)

        curProgram = createProgram(getVertexShader(), getFragmentShader())
        maPositionHandler = GLES20.glGetAttribLocation(curProgram, "aPosition")
        maTextureHandler = GLES20.glGetAttribLocation(curProgram, "aTextureCoord")
        muMVPMatrixHandler = GLES20.glGetUniformLocation(curProgram, "uMVPMatrix")
        muSTMatrixHandler = GLES20.glGetUniformLocation(curProgram, "uSTMatrix")

        GLES20.glGenTextures(1, mpTextureID, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mpTextureID[0]);
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST.toFloat()
        );
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat()
        );
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat()
        );
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat()
        );

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        //Matrix.setRotateM(mMVPMatrix, 0, 180f, 0.0f, 0.0f, 1.0f)
        //调整大小比例
        Matrix.scaleM(mMVPMatrix, 0, 2f, 2.0f,1f)
        Matrix.translateM(mMVPMatrix, 0, -0.5f, -0.5f, 0f)
    }


    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)

        GLES20.glUseProgram(curProgram)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0)
        //绑定注入bitmap
        val mFilterInputTextureUniform2 = GLES20.glGetUniformLocation(curProgram, "sTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mpTextureID[0])
        GLES20.glUniform1i(mFilterInputTextureUniform2, mpTextureID[0])

        mTriangleVertices.position(0)
        GLES20.glVertexAttribPointer(
            maPositionHandler,
            3,
            GLES20.GL_FLOAT,
            false,
            20,
            mTriangleVertices
        )
        GLES20.glEnableVertexAttribArray(maPositionHandler)

        mTriangleVertices.position(0)
        GLES20.glVertexAttribPointer(
            maTextureHandler,
            3,
            GLES20.GL_FLOAT,
            false,
            20,
            mTriangleVertices
        )
        GLES20.glEnableVertexAttribArray(maTextureHandler)

        GLES20.glUniformMatrix4fv(muMVPMatrixHandler, 1, false, mMVPMatrix, 0)
        GLES20.glUniformMatrix4fv(muSTMatrixHandler, 1, false, mSTMatrix, 0)

        //水印透明
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glFinish()

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        super.onFrameAvailable(surfaceTexture)
    }

    private fun getFragmentShader(): String {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform sampler2D sTexture;\n" +
                "void main() {\n" +
                "  vec4 c1 = texture2D(sTexture, vTextureCoord);\n" +
                "  gl_FragColor = vec4(c1.rgb, c1.a *" + mAlpha + ");\n" +
                "}\n";
    }

}