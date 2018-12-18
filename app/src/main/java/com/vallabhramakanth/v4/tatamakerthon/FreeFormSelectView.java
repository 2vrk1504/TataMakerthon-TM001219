package com.vallabhramakanth.v4.tatamakerthon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class FreeFormSelectView extends View {
    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path    mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
    private Paint mPaint;

    public FreeFormSelectView(Context c) {
        super(c);
        context=c;
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getResources().getColor(R.color.colorSelect));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath,  mPaint);
        canvas.drawPath(circlePath,  circlePaint);
    }

    private float mX, mY;
    private float minX, minY, maxX, maxY;
    private float startX, startY;
    private boolean hasMoved, completed;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        startX = x;
        startY = y;
        minX = maxX = x;
        minY = maxY = y;
        hasMoved = false;
        completed = false;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if(dist(startX, startY, x, y) <= 20 && hasMoved){
            mPath.quadTo(mX, mY, (startX + mX)/2, (startY + mY)/2);
            completed = true;
        }
        if ( (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) && !completed) {
            if(x > maxX) maxX = x;
            if(x < minX) minX = x;
            if(y > maxY) maxY = y;
            if(y < minY) minY = y;

            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
        if(dist(startX, startY, x, y) >= 20 && !hasMoved){
            hasMoved = true;
        }

    }

    private float dist(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
    private void touch_up() {
        if(completed){
            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            mPath.reset();
            circlePath.reset();
            Paint p = new Paint();
            p.setColor(Color.argb(50, 0x3e, 0x6a, 0x42));
            mCanvas.drawRect(getRect(), p);
            p.setColor(getResources().getColor(R.color.colorSelect));
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(10);
            mCanvas.drawRect(getRect(), p);
        }
        else {
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
    public Rect getRect(){
        String s = Float.toString(minX) + " " + Float.toString(minY) + " " +Float.toString(maxX) + " " +Float.toString(maxY) + " " ;
        String s1 = Integer.toString(getWidth()) + Integer.toString(getHeight());
        Log.d("RECTANGLE: ", s);
        Log.d("RECTANGLE ffsv: ", s1);
        Rect rect = new Rect();
        rect.left = (int) (minX);
        rect.top = (int) (minY);
        rect.right = (int) (maxX);
        rect.bottom = (int) (maxY);
        return rect;
    }
    public boolean isCompleted(){return completed;}
}
