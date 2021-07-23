package com.zjl.openglpractice.render2

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import com.zjl.openglpractice.render.BaseRender
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: OpenGLPractice
 * ClassName:    TestSimpleRender1
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年07月22日 9:37
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
const val GL_TEXTURE_EXTERNAL_OES = 0x8D65
abstract class TestSimpleBaseRender : BaseRender() {

    private val mTriangleVerticesData = floatArrayOf(
        -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f, 1.0f, 0.0f
    )

    private var mTriangleVertices: FloatBuffer

    private var mMVPMatrix = FloatArray(16)

    private var mSTMatrix = FloatArray(16)

    private var mTextureID = IntArray(2)

    private var maPositionHandler = 0

    private var maTextureHandler = 0

    private var muMVPMatrixHandler = 0

    private var muSTMatrixHandler = 0

    private var mProgram = 0

    init {
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.size * 4).order(
            ByteOrder.nativeOrder()
        ).asFloatBuffer()
        mTriangleVertices.put(mTriangleVerticesData).position(0)

        Matrix.setIdentityM(mSTMatrix, 0)
        Matrix.setIdentityM(mMVPMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mProgram = createProgram(getVertexShader(), getFragmentShader())
        maPositionHandler = GLES20.glGetAttribLocation(mProgram, "aPosition")
        maTextureHandler = GLES20.glGetAttribLocation(mProgram, "aTextureCoord")
        muMVPMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        muSTMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uSTMatrix")

        GLES20.glGenTextures(2, mTextureID, 0);

        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //
        mProgram = createProgram(getVertexShader(), getFragmentShader())
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(mProgram)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID.get(0))

        mTriangleVertices.position(0)
        GLES20.glVertexAttribPointer(maPositionHandler,3,GLES20.GL_FLOAT,false,20,mTriangleVertices)
        GLES20.glEnableVertexAttribArray(maPositionHandler)

        mTriangleVertices.position(0)
        GLES20.glVertexAttribPointer(maTextureHandler,3,GLES20.GL_FLOAT,false,20,mTriangleVertices)
        GLES20.glEnableVertexAttribArray(maTextureHandler)

        GLES20.glUniformMatrix4fv(muMVPMatrixHandler, 1, false, mMVPMatrix, 0)
        GLES20.glUniformMatrix4fv(muSTMatrixHandler, 1, false, mSTMatrix, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glFinish()
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {

    }

    protected fun getVertexShader(): String {
        return "uniform mat4 uMVPMatrix;\n" +
                "uniform mat4 uSTMatrix;\n" +
                "attribute vec4 aPosition;\n" +
                "attribute vec4 aTextureCoord;\n" +
                "varying vec2 vTextureCoord;\n" +
                "void main() {\n" +
                "  gl_Position = (uMVPMatrix * aPosition)*vec4(1, -1, 1, 1);\n" +
                "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                "}\n";
    }

    private fun getFragmentShader(): String {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" + "void main() {\n" +
                "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                "}\n";
    }
}