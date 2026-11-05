
package com.whosthekiller.game;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameRenderer implements GLSurfaceView.Renderer {

    private FloatBuffer vertexBuffer;
    private int program;
    private int positionHandle;
    private int colorHandle;
    private int mvpMatrixHandle;

    private final float[] projection = new float[16];
    private final float[] view = new float[16];
    private final float[] model = new float[16];
    private final float[] mvp = new float[16];

    private final float[] vertices = {
        -1, -1, 0,
         1, -1, 0,
         0,  1, 0
    };

    private final String vertexShader =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 aPosition;" +
        "void main() {" +
        "  gl_Position = uMVPMatrix * aPosition;" +
        "}";

    private final String fragmentShader =
        "precision mediump float;" +
        "uniform vec4 uColor;" +
        "void main() {" +
        "  gl_FragColor = uColor;" +
        "}";

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.35f, 0.65f, 0.9f, 1f);

        ByteBuffer buffer = ByteBuffer.allocateDirect(
            vertices.length * 4
        );

        buffer.order(ByteOrder.nativeOrder());

        vertexBuffer = buffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        int vertex = loadShader(
            GLES20.GL_VERTEX_SHADER,
            vertexShader
        );

        int fragment = loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            fragmentShader
        );

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertex);
        GLES20.glAttachShader(program, fragment);
        GLES20.glLinkProgram(program);

        positionHandle = GLES20.glGetAttribLocation(
            program, "aPosition"
        );

        colorHandle = GLES20.glGetUniformLocation(
            program, "uColor"
        );

        mvpMatrixHandle = GLES20.glGetUniformLocation(
            program, "uMVPMatrix"
        );
    }

    @Override
    public void onSurfaceChanged(
        GL10 gl, int width, int height
    ) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        Matrix.frustumM(
            projection, 0,
            -ratio, ratio,
            -1, 1,
            1, 20
        );

        Matrix.setLookAtM(
            view, 0,
            0, 0, 5,
            0, 0, 0,
            0, 1, 0
        );
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(
            GLES20.GL_COLOR_BUFFER_BIT |
            GLES20.GL_DEPTH_BUFFER_BIT
        );

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glUseProgram(program);

        Matrix.setIdentityM(model, 0);

        Matrix.multiplyMM(
            mvp, 0, view, 0, model, 0
        );

        Matrix.multiplyMM(
            mvp, 0, projection, 0, mvp, 0
        );

        GLES20.glUniformMatrix4fv(
            mvpMatrixHandle, 1, false, mvp, 0
        );

        GLES20.glUniform4f(
            colorHandle,
            0.9f, 0.1f, 0.1f, 1f
        );

        vertexBuffer.position(0);

        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
            positionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            12,
            vertexBuffer
        );

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLES,
            0,
            3
        );

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private int loadShader(int type, String code) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        return shader;
    }
  }
          
