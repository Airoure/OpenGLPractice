package com.zjl.openglpractice

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
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
    renderer: GLSurfaceView.Renderer,
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : TextureView(context,attributeSet,defStyle), TextureView.SurfaceTextureListener {
    private var mPlayer: IjkMediaPlayer? = null
    init {
        surfaceTextureListener = this
    }
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        val videoSurface = Surface(surface)
        mPlayer = IjkMediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setSurface(videoSurface)
            dataSource = "/storage/emulated/0/Android/data/com.zjl.openglpractice/files/Video/test.mp4"
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


}

class GLThread(surfaceTexture: SurfaceTexture,renderer: GLSurfaceView.Renderer) : Thread() {
    private var mSurfaceTexture: SurfaceTexture
    private var mRenderer: GLSurfaceView.Renderer

    init {
        mSurfaceTexture = surfaceTexture
        mRenderer = renderer
    }

    override fun run() {
        mRenderer.onSurfaceCreated()
    }
}