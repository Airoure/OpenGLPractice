package com.zjl.openglpractice.eglrender

import android.opengl.GLES20

/**
 * Project Name: OpenGLPractice
 * ClassName:    BaseEGLRender
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年07月21日 10:31
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
abstract class BaseEGLRender {
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