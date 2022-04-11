package com.example.gesturecontrol;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";

    //背景颜色
    private float[] colorArr;

    //要绘制的图形对象
    private Cube cube;

    private final float[] vPMatrix = new float[16];

    //投影矩阵
    private final float[] projectionMatrix = new float[16];

    //相机矩阵、观测变换矩阵
    private final float[] viewMatrix = new float[16];

    //旋转矩阵
    private float[] rotationMatrix = new float[16];

    private int mRotateAgree = 0;

    public volatile float[] mAxis = new float[] {-1.0f, -1.0f, -1.0f};

    public float[] getAxis() {
        return mAxis;
    }

    public void setAxis(float[] axis) {
        this.mAxis = axis;
    }

    //旋转角度参数
    public volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        this.mAngle = angle;
    }


    /**
     * 加载着色器
     * @param type
     * @param shaderCode
     * @return
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER) or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        /**
         * 创建着色器
         */
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        /**
         * 添加
         */
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    //设置视图的 OpenGL ES 环境
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 设置背景颜色，v3为不透明度。
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearColor(0.1f, 0.2f, 0.3f, 0.4f);
        //启动深度测试
        gl10.glEnable(GLES20.GL_DEPTH_TEST);
        cube = new Cube();
//        mTriangle = new Triangle();
    }

    //视图的形状改变时调用，例如 屏幕方向发生变化。
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        Log.i(TAG, "触发onSurfaceChanged");
        //视图发生变化时设置视图的宽和高。
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        /**
         * 透视投影
         *
         * 相对画布宽度和高度的比值。一般而言，画布宽度和高度不相等，造成同样数值的 x 和 y，对应的 dp 值不相等。
         *
         * m:结果
         * offset:偏移量
         *
         * 图形上下和左右的缩放比例
         *  left:-ratio
         *  right:
         *  bottom:
         *  top:
         *
         * near:物体前面到摄像机的位置
         * far:物体的后面
         */
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
    }

    /**
     * 视图：
     *  将所绘制的物体的三位坐标转换为屏幕上的二维坐标。
     *
     * @param gl10
     */

    //每次重新绘制图形时调用
    @Override
    public void onDrawFrame(GL10 gl10) {

        float[] scratch = new float[16];
        // 清空背景颜色，把整个窗口清除为黑色。
        // 清空之前绘制的图形。
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //long time = SystemClock.uptimeMillis() % 4000L;   //自启动以来的非睡眠的正常运行时间的毫秒数。
        //float angle = 0.090f * ((int) time);              //旋转角度
        /**
         * 相机视图
         * 设置相机位置
         * 设置视角、坐标系位置、竖直向上的坐标轴。
         * 结果保存到viewMatrix中。
         *
         * upX、upY、upZ为向量（向量有三个属性，起始点、方向、量）
         */
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0f);

        /**
         * 相机视图矩阵 * 透视投影矩阵
         * 将两个4*4矩阵相乘，结果保存到vPMatrix
         */
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//        Matrix.setRotateM(rotationMatrix, 0, angle, 0, 0, -1.0f);
//        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);

        //创建旋转矩阵 z为-1则反转，为1则正转。
        //mAngle为旋转的角度。
//        Matrix.setRotateM(rotationMatrix, 0, mAngle, -1.0f, -1.0f, -1.0f);
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, mAngle, mAxis[0], mAxis[1], mAxis[2]);
//        mRotateAgree = (mRotateAgree + 2) % 360;
//        Matrix.rotateM(rotationMatrix, 0, mRotateAgree, 1,1,1);
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);
        cube.draw(scratch);
    }
}