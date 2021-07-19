package com.zjl.openglpractice.render

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: OpenGLPractice
 * ClassName:    BaseRender
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年07月19日 14:01
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
abstract class BaseRender : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    protected fun loadShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader,source)
        GLES20.glCompileShader(shader)
        return shader
    }

    protected fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexSource)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource)
        val program = GLES20.glCreateProgram()
        if (program != 0) {
            GLES20.glAttachShader(program,vertexShader)
            GLES20.glAttachShader(program,fragmentShader)
            GLES20.glLinkProgram(program)
        }
        return program
    }

}