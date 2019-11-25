package com.example.mplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;

public class SquareImageView extends AppCompatImageView {

    private static final int MAX_FACES = 1;
    private RectF[] rects = new RectF[MAX_FACES];
    private Bitmap image;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();



        canvas.drawBitmap(image, 0, 0, p);
        Paint rectPaint = new Paint();
        rectPaint.setStrokeWidth(2);
        rectPaint.setColor(Color.BLUE);
        rectPaint.setStyle(Paint.Style.STROKE);


        for (int i=0; i < MAX_FACES; i++) {
            RectF r = rects[i];
            if (r != null)
                canvas.drawRect(r, rectPaint);
        }
    }

    public void detectFaces() {
        Log.d("FaceDet", "Detecting faces....");
        // Convert bitmap in 556

        Bitmap tmpBmp = image.copy(Bitmap.Config.RGB_565, true);



        FaceDetector faceDet = new FaceDetector(tmpBmp.getWidth(), tmpBmp.getHeight(), MAX_FACES);

        FaceDetector.Face[] faceList = new FaceDetector.Face[MAX_FACES];

        faceDet.findFaces(tmpBmp, faceList);

        // Log the result
        for (int i=0; i < faceList.length; i++) {
            FaceDetector.Face face = faceList[i];
            Log.d("FaceDet", "Face ["+face+"]");
            if (face != null) {
                Log.d("FaceDet", "Face ["+i+"] - Confidence ["+face.confidence()+"]");
                PointF pf = new PointF();
                face.getMidPoint(pf);
                Log.d("FaceDet", "\t Eyes distance ["+face.eyesDistance()+"] - Face midpoint ["+pf+"]");
                RectF r = new RectF();
                r.left = pf.x - face.eyesDistance() / 2;
                r.right = pf.x + face.eyesDistance() / 2;
                r.top = pf.y - face.eyesDistance() / 2;
                r.bottom = pf.y + face.eyesDistance() / 2;
                rects[i] = r;
            }
        }

        this.invalidate();
    }
}
