package com.zjl.openglpractice.utils

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.*
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.TextureView
import com.zjl.openglpractice.eglrender.SimpleEGLRender2

/**
 * Project Name: OpenGLPractice
 * ClassName:    TextureEGLHelper
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年07月21日 9:35
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
const val MSG_INIT = 0
const val MSG_RENDER = 1
const val MSG_DESTORY = 2

class TextureEGLHelper(
    private val context: Context,
    private val width: Int,
    private val height: Int
) : HandlerThread("TextureEGLHelper"),
    SurfaceTexture.OnFrameAvailableListener {

    private var mTextureView: TextureView? = null

    private var mTextureID: Int = 0

    private var mHandlerThread: HandlerThread? = null

    private var mHandler: TextureHandler? = null

    private var mEGLDisplay: EGLDisplay = EGL14.EGL_NO_DISPLAY

    private var mEGLContext: EGLContext = EGL14.EGL_NO_CONTEXT

    private var mConfigs: Array<EGLConfig?> = arrayOf(null)

    private var mEGLSurface: EGLSurface? = null

    private var mTextureRenderer: SimpleEGLRender2? = null

    private var mOESSurfaceTexture: SurfaceTexture? = null

    inner class TextureHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_INIT -> initEGLContext(2)
                MSG_RENDER -> drawFrame()
            }
        }
    }

    fun initEGL(textureView: TextureView, textureId: Int) {
        mTextureView = textureView
        mTextureID = textureId
        mHandlerThread = HandlerThread("Renderer Thread")
        mHandlerThread!!.start()
        mHandler = TextureHandler(mHandlerThread!!.looper)
        mHandler?.sendEmptyMessage(MSG_INIT)
    }

    private fun initEGLContext(clientVersion: Int) {
        Log.e("INIT", "INITING")
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        version[0] = clientVersion
        EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)

        val attributes = intArrayOf(
            EGL14.EGL_BUFFER_SIZE, 32,
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, 4,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
            EGL14.EGL_NONE
        )

        val numConfigs = IntArray(1)
        EGL14.eglChooseConfig(mEGLDisplay, attributes, 0, mConfigs, 0, mConfigs.size, numConfigs, 0)

        val surfaceTexture = mTextureView!!.surfaceTexture
        val surfaceAttributes = intArrayOf(EGL14.EGL_NONE)

        mEGLSurface = EGL14.eglCreateWindowSurface(
            mEGLDisplay,
            mConfigs[0],
            surfaceTexture,
            surfaceAttributes,
            0
        )

        val contextAttributes =
            intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, clientVersion, EGL14.EGL_NONE)
        mEGLContext = EGL14.eglCreateContext(
            mEGLDisplay,
            mConfigs[0],
            EGL14.EGL_NO_CONTEXT,
            contextAttributes,
            0
        )

        mTextureRenderer = SimpleEGLRender2(context,mTextureID)
        mTextureRenderer?.onSurfaceCreated()
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        mTextureRenderer?.onSurfaceChanged(width, height)
    }

    private fun drawFrame() {
        if (mTextureRenderer != null) {
            EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)
            mTextureRenderer?.onDrawFrame(mOESSurfaceTexture!!)
            EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface)
        }
    }


    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        mHandler?.sendEmptyMessage(MSG_RENDER)
    }

    fun loadTexture(): SurfaceTexture {
        mOESSurfaceTexture = SurfaceTexture(mTextureID)
        mOESSurfaceTexture?.setOnFrameAvailableListener(this)
        return mOESSurfaceTexture!!
    }

    fun onDestroy() {
        mHandlerThread?.quitSafely()
        mHandler?.removeCallbacksAndMessages(null)
    }
}