package com.example.gesturecontrol;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {

    //顶点着色器
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 aColor;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +    //顺序不能反。OpenGL中使用的是列向量，如[xyzx]T，所以与矩阵相乘时，矩阵在前，向量在后
                    "   vColor = aColor;" +
                    "}";
//                "uniform mat4 uMVPMatrix;"+
//                "attribute vec4 vPosition;" +
//                "attribute vec4 aColor;"+
//                "varying  vec4 vColor;"+
//                "void main() {" +
//                "  gl_Position = uMVPMatrix*vPosition;" +
//                "  vColor=aColor;"+
//                "}";

    //片段着色器
    private final String fragmentShaderCode =
//                "precision mediump float;" +
//                "varying vec4 vColor;" +
//                "void main() {" +
//                "   gl_FragColor = vColor;" +
//                "}";
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";


    //顶点数据和颜色数据
    private FloatBuffer vertexBuffer, colorBuffer;
    //顶点绘制顺序数据
    private ShortBuffer indexBuffer;

    //着色器成员句柄
    private int positionHandle, colorHandle, mvpMatrixHandle;

    //程序对象
    private final int mProgram;

    //每个顶点坐标的数据个数
    static final int COORDINATE_PER_VERTEX = 3;

    //每个顶点颜色的数据个数
    static final int COORDINATE_PER_COLOR = 4;

    private final int vertexCount = cubeVertexCoordinate.length / COORDINATE_PER_VERTEX;

    //从缓冲区取一个顶点坐标的步长
    private final int vertexCoordinateStride = COORDINATE_PER_VERTEX * 4;

    //从缓冲区取一个顶点颜色的步长
    private final int vertexColorStride = COORDINATE_PER_COLOR * 4;

    //正方体坐标数据
    static float cubeVertexCoordinate[]={
            1.0f, 1.0f, 1.0f, //0
            -1.0f, 1.0f, 1.0f, //1
            -1.0f, -1.0f, 1.0f, //2
            1.0f, -1.0f, 1.0f, //3
            1.0f, 1.0f, -1.0f, //4
            -1.0f, 1.0f, -1.0f, //5
            -1.0f, -1.0f, -1.0f, //6
            1.0f, -1.0f, -1.0f //7
    };

    //正方体坐标颜色数据
    static float cubeVertexColor[]={
            1.0f, 1.0f, 1.0f, 1,
            0, 1.0f, 1.0f, 1,
            0, 0, 1.0f, 1,
            1.0f, 0, 1.0f, 1,
            1.0f, 1.0f, 0, 1,
            0, 1.0f, 0, 1,
            0, 0, 0, 1,
            1.0f, 0, 0, 1
    };

    //正方体坐标绘制顺序数据
    static short cubeVertexIndex[]={
            0, 2, 1, 0, 2, 3, //前面
            0, 5, 1, 0, 5, 4, //上面
            0, 7, 3, 0, 7, 4, //右面
            6, 4, 5, 6, 4, 7, //后面
            6, 3, 2, 6, 3, 7, //下面
            6, 1, 2, 6, 1, 5 //左面
    };

    public Cube(){
        //将顶点坐标数据放到缓冲区
        vertexBuffer = ByteBuffer.allocateDirect(cubeVertexCoordinate.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(cubeVertexCoordinate);
        vertexBuffer.position(0);
        //将顶点颜色数据放到缓冲区
        colorBuffer = ByteBuffer.allocateDirect(cubeVertexColor.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(cubeVertexColor);
        colorBuffer.position(0);
        //将顶点顺序数据放到缓冲区
        indexBuffer = ByteBuffer.allocateDirect(cubeVertexIndex.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBuffer.put(cubeVertexIndex);
        indexBuffer.position(0);


        //编译着色器代码
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        //创着色器建程序对象
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram,vertexShader);
        GLES20.glAttachShader(mProgram,fragmentShader);
        //链接着色器对象。
        GLES20.glLinkProgram(mProgram);
    }

    //绘制
    public void draw(float[] mvpMatrix){
        //激活着色器程序
        GLES20.glUseProgram(mProgram);

        //获取着色器变量句柄，准备顶点坐标数据。
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDINATE_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        //获取着色器变量句柄，准备顶点颜色数据。
        colorHandle = GLES20.glGetAttribLocation(mProgram,"aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, COORDINATE_PER_COLOR, GLES20.GL_FLOAT, false, 0, colorBuffer);

        mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

//        GLES20.glDrawArrays();
        //绘制正方体的表面（6个面，每面2个三角形，每个三角形3个顶点）
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, cubeVertexIndex.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
