package com.example.gesturecontrol;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

//3d视图容器类
class MyGLSurfaceView extends GLSurfaceView {

    private static final String TAG = "MyGLSurfaceView";

    private final MyGLRenderer renderer;

    //触摸移动比例系数
//    private static float TOUCH_SCALE_FACTOR;
    private static float TOUCH_SCALE_FACTOR = 180.0f / 1000;
    private float previousX;        //之前的x坐标
    private float previousY;        //之前的y坐标

    private float mPreviousX;
    private float mPreviousY;



    //构造函数
    public MyGLSurfaceView(Context context){
        super(context);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);  //设置使用的OpenGL ES版本。
        renderer = new MyGLRenderer();
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);          //设置SurfaceView渲染器
    }


    //触摸事件处理函数
    @Override
    public boolean onTouchEvent(MotionEvent e){
        float x = e.getX(); //触摸坐标 x
        float y = e.getY(); //触摸坐标 y
        getRate();

        switch (e.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                previousX = x;
//                previousY = y;
            case MotionEvent.ACTION_UP:
                float[] axis = new float[] {0, 0, 1};
//                float angle = 0f;
                renderer.setAxis(axis);
                break;
            case MotionEvent.ACTION_DOWN:
                mPreviousX = x;
                mPreviousY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                requestRender(x, y);
                break;
        }
        previousX = x;
        previousY = y;
        return true;
    }


    private void requestRender(float x, float y){
        float mdx = x - mPreviousX;
        float mdy = mPreviousY - y; //屏幕坐标Y轴向下，模型坐标Y轴向上

        float[] axis = getRotationAxis(mdx ,mdy);
        float dx = x - previousX;
        float dy = previousY - y;
        float angle = getRotationAngle(dx ,dy);
//        float angle = getRotationAngle(x ,y);
        renderer.setAngle(angle);
        renderer.setAxis(axis);
        requestRender();
    }

    //获取旋转角度
    private float getRotationAngle(float dx, float dy) {

//        float dx = x - previousX;
//        float dy = y - previousY;
//
//        Log.i(TAG,"dx:"+dx);
//        Log.i(TAG,"dy:"+dy);
//
//        Log.i(TAG,"getHeight():"+getHeight());
//        Log.i(TAG,"getWidth():"+getWidth());
//
//        if (y > getHeight() / 2){
//            dx = dx * -1;
//        }
//
//        Log.i(TAG, "触发onTouchEvent");
//        if (x < getWidth() / 2){
//            dy = dy * -1;
//        }
//
//        return renderer.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR);

        float d = (float) Math.sqrt(dx * dx + dy * dy);
        float angle = d * TOUCH_SCALE_FACTOR;
        Log.i("renderer.getAngle() + angle:",renderer.getAngle() + angle+"");
        return renderer.getAngle() + angle;
    }

    //获取旋转轴
    private float[] getRotationAxis(float dx, float dy) {
        float[] axis = new float[] {0, 0, 1};
        float dis = (float) Math.sqrt(dx * dx + dy * dy);
        if (dis < 0.000001f) {
            return axis;
        }
        // axis = (0, 0, 1) X (dx, dy, 0)
        float[] axis1 = renderer.getAxis();
        axis[0] = -dy;// + axis1[0];
        axis[1] = dx; //+ axis1[1];
        axis[2] = 0;

        Log.i("-dy:",-dy+"");
        Log.i("dx:",dx+"");
        return axis;
    }

    private void getRate() {
        float w = getWidth();
        float h = getHeight();
        TOUCH_SCALE_FACTOR = 360 / (float) Math.sqrt(w * w + h * h);
    }


//    //触摸事件处理函数
//    @Override
//    public boolean onTouchEvent(MotionEvent e){
//        float x = e.getX(); //触摸坐标 x
//        float y = e.getY(); //触摸坐标 y
//
//        Log.i(TAG,"x:"+x);
//        Log.i(TAG,"y:"+y);
//        Log.i(TAG,"e.getAction():"+e.getAction());
//
//        Log.i(TAG,"previousX:"+previousX);
//        Log.i(TAG,"previousY:"+previousY);
//
//        switch (e.getAction()){
//            case MotionEvent.ACTION_MOVE:
//                //获取要移动的距离
//                float dx = x - previousX;
//                float dy = y - previousY;
//
//                Log.i(TAG,"dx:"+dx);
//                Log.i(TAG,"dy:"+dy);
//
//                Log.i(TAG,"getHeight():"+getHeight());
//                Log.i(TAG,"getWidth():"+getWidth());
//
//                if (y > getHeight() / 2){
//                    dx = dx * -1;
//                }
//
//                Log.i(TAG, "触发onTouchEvent");
//                if (x < getWidth() / 2){
//                    dy = dy * -1;
//                }
//
//                renderer.setAngle(
//                        renderer.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR)
//                );
//                requestRender();
//        }
//        previousX = x;
//        previousY = y;
//        return true;
//    }
}