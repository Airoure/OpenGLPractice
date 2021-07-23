package com.zjl.openglpractice.render2

import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.Surface
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: OpenGLPractice
 * ClassName:    GaussicRender
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年07月22日 17:33
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
class GaussicRender : TestSimpleBaseRender() {
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

    private var mSurfaceTexture: SurfaceTexture? = null

    protected var mGLSurfaceView: GLSurfaceView? = null

    init {
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.size * 4).order(
            ByteOrder.nativeOrder()
        ).asFloatBuffer()
        mTriangleVertices.put(mTriangleVerticesData).position(0)

        Matrix.setIdentityM(mSTMatrix, 0)
        Matrix.setIdentityM(mMVPMatrix, 0)

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)

        curProgram = createProgram(getVertexShader(), getGaussicFragmentShader())
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
        mSurfaceTexture = SurfaceTexture(mpTextureID[0])
        val videoSurface = Surface(mSurfaceTexture)
        IjkMediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setSurface(videoSurface)
            dataSource =
                "/storage/emulated/0/Android/data/com.zjl.openglpractice/files/Video/test.mp4"
            prepareAsync()
            setOnPreparedListener {
                start()
            }
        }

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        //Matrix.setRotateM(mMVPMatrix, 0, 180f, 0.0f, 0.0f, 1.0f)
        //调整大小比例
        Matrix.scaleM(mMVPMatrix, 0, 2f, 2.0f, 1f)
        Matrix.translateM(mMVPMatrix, 0, -0.5f, -0.5f, 0f)
    }


    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)

        GLES20.glUseProgram(curProgram)
        mSurfaceTexture?.updateTexImage()
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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        val transform = FloatArray(16)
        Matrix.setIdentityM(transform,0)
        Matrix.scaleM(transform,0,0.5f,1f,0f)
        GLES20.glUniformMatrix4fv(muSTMatrixHandler, 1, false, mSTMatrix, 0)
        GLES20.glUniformMatrix4fv(muMVPMatrixHandler, 1, false, transform, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        super.onFrameAvailable(surfaceTexture)
    }

    private fun getGaussicFragmentShader(): String {
        val radius = 6.0f
        val blurTypeString = "vec2(1.0,0.0)"
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform sampler2D sTexture;\n" + //samplerExternalOES 和 sampler2D有什么区别
                "const float resolution=1024.0;\n" +
                "const float radius = " + radius + ";\n" +
                "vec2 dir =" + blurTypeString + "; //若为x模糊，可传入（1.0,0.0）  y模糊  （0.0,1.0）\n" +
                "\n" +
                "void main() {\n" +
                "    //this will be our RGBA sum\n" +
                "    vec4 sum = vec4(0.0);\n" +
                "    \n" +
                "    //our original texcoord for this fragment\n" +
                "    vec2 tc = vTextureCoord;\n" +
                "    \n" +
                "    //the amount to blur, i.e. how far off center to sample from \n" +
                "    //1.0 -> blur by one pixel\n" +
                "    //2.0 -> blur by two pixels, etc.\n" +
                "    float blur = radius/resolution; \n" +
                "    \n" +
                "    //the direction of our blur\n" +
                "    //(1.0, 0.0) -> x-axis blur\n" +
                "    //(0.0, 1.0) -> y-axis blur\n" +
                "    float hstep = dir.x;\n" +
                "    float vstep = dir.y;\n" +
                "    \n" +
                "    \n" +
                "    //apply blurring, using a 9-tap filter with predefined gaussian weights\n" +
                "    \n" +
                "    sum += texture2D(sTexture, vec2(tc.x - 4.0*blur*hstep, tc.y - 4.0*blur*vstep)) * 0.0162162162;\n" +
                "    sum += texture2D(sTexture, vec2(tc.x - 3.0*blur*hstep, tc.y - 3.0*blur*vstep)) * 0.0540540541;\n" +
                "    sum += texture2D(sTexture, vec2(tc.x - 2.0*blur*hstep, tc.y - 2.0*blur*vstep)) * 0.1216216216;\n" +
                "    sum += texture2D(sTexture, vec2(tc.x - 1.0*blur*hstep, tc.y - 1.0*blur*vstep)) * 0.1945945946;\n" +
                "    \n" +
                "    sum += texture2D(sTexture, vec2(tc.x, tc.y)) * 0.2270270270;\n" +
                "    \n" +
                "    sum += texture2D(sTexture, vec2(tc.x + 1.0*blur*hstep, tc.y + 1.0*blur*vstep)) * 0.1945945946;\n" +
                "    sum += texture2D(sTexture, vec2(tc.x + 2.0*blur*hstep, tc.y + 2.0*blur*vstep)) * 0.1216216216;\n" +
                "    sum += texture2D(sTexture, vec2(tc.x + 3.0*blur*hstep, tc.y + 3.0*blur*vstep)) * 0.0540540541;\n" +
                "    sum += texture2D(sTexture, vec2(tc.x + 4.0*blur*hstep, tc.y + 4.0*blur*vstep)) * 0.0162162162;\n" +
                "\n" +
                "    vec4 cc= texture2D(sTexture,vTextureCoord );\n" +
                "\n" +
                "    //discard alpha for our simple demo, multiply by vertex color and return\n" +
                "    gl_FragColor =vec4(sum.rgb, cc.a);\n" +
                "}";
    }

    private fun getFragmentShader(): String {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform sampler2D sTexture;\n" +
                "void main() {\n" +
                "  vec4 c1 = texture2D(sTexture, vTextureCoord);\n" +
                "  gl_FragColor = c1;\n" +
                "}\n";
    }
}