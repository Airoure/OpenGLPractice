package com.zjl.openglpractice

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import com.zjl.openglpractice.utils.TextureEGLHelper
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * Project Name: OpenGLPractice
 * ClassName:    MyTextureView
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年07月20日 18:43
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
class MyTextureView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : TextureView(context, attributeSet, defStyle), TextureView.SurfaceTextureListener {
    private var mPlayer: IjkMediaPlayer? = null
    private var mTextureEGLHelper: TextureEGLHelper? = null

    init {
        surfaceTextureListener = this
        mTextureEGLHelper = TextureEGLHelper(context, width, height)
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
//        val textureID = loadOESTexture()
//        mTextureEGLHelper?.initEGL(this,textureID)
//        val surfaceTexture = mTextureEGLHelper?.loadTexture()
//        setSurfaceTexture(surfaceTexture!!)

        val videoSurface = Surface(surfaceTexture)
        mPlayer = IjkMediaPlayer().apply {
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

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    fun loadOESTexture(): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0])
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        return textureIds[0]
    }
}
