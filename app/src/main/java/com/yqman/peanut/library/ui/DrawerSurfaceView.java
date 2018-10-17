/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yqman.peanut.library.ui;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by yqman on 2016/9/3.
 * SurfaceView 测试UI控件
 * 需要注意的是SurfaceView 它有front canvas 和 back canvas的区别。
 * 两个canvas交替返回给用户，因此为了避免闪烁，要求每次使用canvas的时候都使用类似setColor的方法对背景进行一次绘制，然后操作。
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DrawerSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private UpdateThread mUpdateThread;
    private Paint mPaint = new Paint();
    private ConcurrentLinkedQueue<Node> mPositions = new ConcurrentLinkedQueue<>();
    private long mLastTime = 0;

    public DrawerSurfaceView(Context context) {
        this(context, null);
    }

    public DrawerSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        mHolder = this.getHolder();
        mHolder.addCallback(this);
        mUpdateThread = new UpdateThread();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(30);
        mPaint.setStrokeWidth(5);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mUpdateThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mUpdateThread.exitThread();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - mLastTime < 300) {//0.3秒间隙
                mPositions.clear();
            }
            mLastTime = System.currentTimeMillis();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Node node = new Node(event.getX(), event.getY(), System.currentTimeMillis());
            mPositions.add(node);
        }

        return true;
    }

    public void myDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);  //这里涉及到font canvas和back canvas的内容，为了避免闪烁必须进行这样的操作
        Node beforeNode = null;
        if (!mPositions.isEmpty()) {
            beforeNode = mPositions.peek();
        }
        for (Node node : mPositions) {
            if (drawLineJudge(beforeNode, node)) {
                canvas.drawLine(beforeNode.x, beforeNode.y, node.x, node.y, mPaint);
            }
            beforeNode = node;
        }
    }

    private boolean drawLineJudge(Node firstNode, Node secondNode) {
        return !(firstNode == null || secondNode == null) && secondNode.time - firstNode.time < 80;
    }

    private class UpdateThread extends Thread {
        private boolean mWorkFlag = false;
        private int mSlotTimeMS = 20; //帧数 ms

        private UpdateThread() {

        }

        @Override
        public synchronized void start() {
            mWorkFlag = true;
            super.start();
        }

        @Override
        public void run() {
            while (mWorkFlag) {
                try {
                    Canvas canvas = mHolder.lockCanvas();
                    if (canvas != null) {
                        myDraw(canvas);
                    }
                    if (!mWorkFlag) {
                        break;
                    }
                    mHolder.unlockCanvasAndPost(canvas);
                    Thread.sleep(mSlotTimeMS);
                } catch (InterruptedException e) {
                    //  do nothing
                }
            }
        }

        private void exitThread() {
            mWorkFlag = false;
        }
    }

    class Node {
        float x;
        float y;
        long time;

        Node(float x, float y, long time) {
            this.x = x;
            this.y = y;
            this.time = time;
        }
    }
}