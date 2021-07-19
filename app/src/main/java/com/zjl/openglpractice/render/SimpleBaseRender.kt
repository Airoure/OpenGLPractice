package com.zjl.openglpractice.render

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: OpenGLPractice
 * ClassName:    SimpleBaseRender
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年07月19日 19:18
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
class SimpleBaseRender : BaseRender() {

    private var mTextureId = IntArray(2)

    private var mProgram = 0

    private var maPositionHandler = 0

    private var maTextureHandle = 0

    private var muMVPMatrixHandle = 0

    private var muSTMatrixHandle = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mProgram = createProgram(getVertexShader(), getFragmentShader())
        if (mProgram != 0) {
            maPositionHandler = GLES20.glGetAttribLocation(mProgram, "aPosition")
            maTextureHandle = GLES20.glGetAttribLocation(mProgram,"aTextureCoord")
            muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix")
            muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uSTMatrix")
            GLES20.glGenTextures(2,mTextureId,0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureId[0])

            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
            )
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        initDrawFrame()

        bindDrawFrameTexture()

        initPointerAndDraw()
    }

    private fun initPointerAndDraw() {


    }

    private fun bindDrawFrameTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            mTextureId[0]
        )

    }

    private fun initDrawFrame() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(
            GLES20.GL_DEPTH_BUFFER_BIT
                    or GLES20.GL_COLOR_BUFFER_BIT
        )

        GLES20.glUseProgram(mProgram)

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
                "  gl_Position = uMVPMatrix * aPosition;\n" +
                "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                "}\n";
    }

    protected fun getFragmentShader(): String {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" + "void main() {\n" +
                "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                "}\n";
    }
}