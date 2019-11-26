package com.example.mplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.Log;

import com.example.mplayer.BuildConfig;

public class FaceCropper {

    String TAG = "CropFace";

    private static final String LOG_TAG = FaceCropper.class.getSimpleName();

    public enum SizeMode { FaceMarginPx, EyeDistanceFactorMargin };

    private static final int MAX_FACES = 8;
    private static final int MIN_FACE_SIZE = 200;

    private int mFaceMinSize = MIN_FACE_SIZE;
    private int mFaceMarginPx = 100;
    private float mEyeDistanceFactorMargin = 2f;
    private int mMaxFaces = MAX_FACES;
    private SizeMode mSizeMode = SizeMode.EyeDistanceFactorMargin;
    private boolean mDebug;
    private Paint mDebugPainter;
    private Paint mDebugAreaPainter;

    public FaceCropper() {
        initPaints();
    }

    public FaceCropper(int faceMarginPx) {
        setFaceMarginPx(faceMarginPx);
        initPaints();
    }

    public FaceCropper(float eyesDistanceFactorMargin) {
        setEyeDistanceFactorMargin(eyesDistanceFactorMargin);
        initPaints();
    }

    private void initPaints() {
        mDebugPainter = new Paint();
        mDebugPainter.setColor(Color.RED);
        mDebugPainter.setAlpha(80);

        mDebugAreaPainter = new Paint();
        mDebugAreaPainter.setColor(Color.GREEN);
        mDebugAreaPainter.setAlpha(80);
    }

    public int getMaxFaces() {
        return mMaxFaces;
    }

    public void setMaxFaces(int maxFaces) {
        this.mMaxFaces = maxFaces;
    }

    public int getFaceMinSize() {
        return mFaceMinSize;
    }

    public void setFaceMinSize(int faceMinSize) {
        mFaceMinSize = faceMinSize;
    }

    public int getFaceMarginPx() {
        return mFaceMarginPx;
    }

    public void setFaceMarginPx(int faceMarginPx) {
        mFaceMarginPx = faceMarginPx;
        mSizeMode = SizeMode.FaceMarginPx;
    }

    public SizeMode getSizeMode() {
        return mSizeMode;
    }

    public float getEyeDistanceFactorMargin() {
        return mEyeDistanceFactorMargin;
    }

    public void setEyeDistanceFactorMargin(float eyeDistanceFactorMargin) {
        mEyeDistanceFactorMargin = eyeDistanceFactorMargin;
        mSizeMode = SizeMode.EyeDistanceFactorMargin;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    protected CropResult cropFace(Bitmap original, boolean debug) {
        Bitmap fixedBitmap = BitmapUtils.forceEvenBitmapSize(original);
        fixedBitmap = BitmapUtils.forceConfig565(fixedBitmap);
        Bitmap mutableBitmap = fixedBitmap.copy(Bitmap.Config.RGB_565, true);

        if (fixedBitmap != mutableBitmap) {
            fixedBitmap.recycle();
        }

        FaceDetector faceDetector = new FaceDetector(
                mutableBitmap.getWidth(), mutableBitmap.getHeight(),
                mMaxFaces);

        FaceDetector.Face[] faces = new FaceDetector.Face[mMaxFaces];

        // The bitmap must be in 565 format (for now).
        int faceCount = faceDetector.findFaces(mutableBitmap, faces);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, faceCount + " faces found");
        }

        if (faceCount == 0) {
            Log.d(TAG, "cropFace: No face found");
            return new CropResult(mutableBitmap);
        }

        int initX = mutableBitmap.getWidth();
        int initY = mutableBitmap.getHeight();
        int endX = 0;
        int endY = 0;
        Log.d(TAG, "cropFace: Start "+initX+","+initY+" End"+endX+","+endY);

        PointF centerFace = new PointF();

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mutableBitmap, new Matrix(), null);

        // Calculates minimum box to fit all detected faces
        for (int i = 0; i < faceCount; i++) {
            FaceDetector.Face face = faces[i];

            // Eyes distance * 3 usually fits an entire face
            int faceSize = (int) (face.eyesDistance() * 3);
            Log.d(TAG, "cropFace: Face size is:"+faceSize);

            if (SizeMode.FaceMarginPx.equals(mSizeMode)) {
                faceSize += mFaceMarginPx * 2; // *2 for top and down/right and left effect
            }
            else if (SizeMode.EyeDistanceFactorMargin.equals(mSizeMode)) {
                faceSize += face.eyesDistance() * mEyeDistanceFactorMargin;
            }

            faceSize = Math.max(faceSize, mFaceMinSize);
            Log.d(TAG, "cropFace: Face size is:"+faceSize+ "and mFaceMinSize is:"+mFaceMinSize);

            face.getMidPoint(centerFace);

            if (debug) {
                canvas.drawPoint(centerFace.x, centerFace.y, mDebugPainter);
                canvas.drawCircle(centerFace.x, centerFace.y, face.eyesDistance() * 1.5f, mDebugPainter);
            }

            int tInitX = (int) (centerFace.x - faceSize / 2);
            int tInitY = (int) (centerFace.y - faceSize / 2);
            tInitX = Math.max(0, tInitX);
            tInitY = Math.max(0, tInitY);

            int tEndX = tInitX + faceSize;
            int tEndY = tInitY + faceSize;
            tEndX = Math.min(tEndX, mutableBitmap.getWidth());
            tEndY = Math.min(tEndY, mutableBitmap.getHeight());

            initX = Math.min(initX, tInitX);
            initY = Math.min(initY, tInitY);
            endX = Math.max(endX, tEndX);
            endY = Math.max(endY, tEndY);
        }

        int sizeX = endX - initX;
        int sizeY = endY - initY;

        if (sizeX + initX > mutableBitmap.getWidth()) {
            sizeX = mutableBitmap.getWidth() - initX;
        }
        if (sizeY + initY > mutableBitmap.getHeight()) {
            sizeY = mutableBitmap.getHeight() - initY;
        }

        Point init = new Point(initX, initY);
        Point end = new Point(initX + sizeX, initY + sizeY);

        Log.d(TAG, "cropFace: Start "+initX+","+initY+"Size"+sizeX+","+sizeY);

        return new CropResult(mutableBitmap, init, end);

    }

    public Bitmap cropFace(Context ctx, int resDrawable) {
        return getCroppedImage(ctx, resDrawable);
    }

    public Bitmap cropFace(Bitmap bitmap) {
        return getCroppedImage(bitmap);
    }

    public Bitmap getFullDebugImage(Context ctx, int resDrawable) {
        // Set internal configuration to RGB_565
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        return getFullDebugImage(BitmapFactory.decodeResource(ctx.getResources(), resDrawable, bitmapOptions));
    }

    public Bitmap getFullDebugImage(Bitmap bitmap) {
        CropResult result = cropFace(bitmap, true);
        Canvas canvas = new Canvas(result.getBitmap());

        canvas.drawBitmap(result.getBitmap(), new Matrix(), null);
        canvas.drawRect(result.getInit().x,
                result.getInit().y,
                result.getEnd().x,
                result.getEnd().y,
                mDebugAreaPainter);

        return result.getBitmap();
    }

    public Bitmap getCroppedImage(Context ctx, int resDrawable) {
        // Set internal configuration to RGB_565
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        return getCroppedImage(BitmapFactory.decodeResource(ctx.getResources(), resDrawable, bitmapOptions));
    }

    public Bitmap getCroppedImage(Bitmap bitmap) {
        CropResult result = cropFace(bitmap, mDebug);
//        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap,
//                result.getInit().x,
//                result.getInit().y,
//                result.getEnd().x - result.getInit().x,
//                result.getEnd().y - result.getInit().y);

        Bitmap croppedBitmap = Bitmap.createBitmap(result.getBitmap(),
                result.getInit().x,
                result.getInit().y,
                result.getEnd().x - result.getInit().x,
                result.getEnd().y - result.getInit().y);

        if (result.getBitmap() != croppedBitmap) {
            result.getBitmap().recycle();
        }

        return croppedBitmap;
    }

    protected class CropResult {
        Bitmap mBitmap;
        Point mInit;
        Point mEnd;

        public CropResult(Bitmap bitmap, Point init, Point end) {
            mBitmap = bitmap;
            mInit = init;
            mEnd = end;
        }

        public CropResult(Bitmap bitmap) {
            mBitmap = bitmap;
            mInit = new Point(0, 0);
            mEnd = new Point(bitmap.getWidth(), bitmap.getHeight());
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public Point getInit() {
            return mInit;
        }

        public Point getEnd() {
            return mEnd;
        }
    }
}