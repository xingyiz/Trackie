package com.example.trackie.ui.testmode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;
import com.example.trackie.Utils;

import java.util.ArrayList;

public class TestImageMapView extends SubsamplingScaleImageView {
    private final Paint paint = new Paint();
    private PointF currentUserLocation;
    private ArrayList<PointF> errorPoints = new ArrayList<>();
    private Bitmap pinBitmap;
    private Bitmap errorBitmap;
    private final static int ANIMATION_FRAMERATE = 50;

    public TestImageMapView(Context context, AttributeSet attr) {
        super(context, attr);

        pinBitmap = setUpPinBitmap(R.drawable.ic_my_location_24px);
        errorBitmap = setUpPinBitmap(R.drawable.ic_error_indicator_24dp);
    }

    public TestImageMapView(Context context) {
        super(context);

        pinBitmap = setUpPinBitmap(R.drawable.ic_my_location_24px);
        errorBitmap = setUpPinBitmap(R.drawable.ic_error_indicator_24dp);
    }

    public PointF getCurrentUserLocation() {
        return currentUserLocation;
    }

    public void updateCurrentUserLocation(PointF newLocation) {
        if (newLocation == null) {
            System.out.println("Could not get prediction result. Maybe try looking at ModelPrediction if this persists.");
        }
        System.out.println("Got new location: " + newLocation);
        if (currentUserLocation == null) {
            currentUserLocation = new PointF(newLocation.x, newLocation.y);
            setMaxScale(5f);
            animateScaleAndCenter(4f, currentUserLocation)
                    .withDuration(500)
                    .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                    .withInterruptible(false)
                    .start();
            invalidate();
            return;
        }
        PointThread pointThread = new PointThread(newLocation, this.getWidth() / ANIMATION_FRAMERATE);
        pointThread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isReady()) return;
        paint.setAntiAlias(true);
        if (currentUserLocation == null) return;

        // draw user's current location
        drawPoint(currentUserLocation, canvas, pinBitmap);

        // draw all indicated error points by user
        for (PointF errorPoint: errorPoints) {
            System.out.println("Error point");
           drawPoint(errorPoint, canvas, errorBitmap);
        }
    }

    private void drawPoint(PointF point, Canvas canvas, Bitmap bitmap) {
        PointF vPoint = sourceToViewCoord(point);
        float vX = vPoint.x - (bitmap.getWidth()/2);
        float vY = vPoint.y - bitmap.getHeight();
        canvas.drawBitmap(bitmap, vX, vY, paint);
    }

    private Bitmap setUpPinBitmap(int drawableResource) {
        Drawable pinDrawable = getContext().getDrawable(drawableResource);
        Bitmap tempBitmap = Utils.drawableToBitmap(pinDrawable);
        float density = getResources().getDisplayMetrics().densityDpi;
        float width = (density / 420f) * tempBitmap.getWidth();
        float height = (density / 420f) * tempBitmap.getHeight();
        return Bitmap.createScaledBitmap(tempBitmap, (int) width, (int) height, true);

    }

    protected void indicatePositionError(PointF errorPoint) {
        if (errorPoints.contains(errorPoint)) return;
        errorPoints.add(errorPoint);
        invalidate();
    }

    protected ArrayList<PointF> getErrorPoints() {
        return errorPoints;
    }

    private class PointThread extends Thread {
        PointF endPoint;
        int smoothness; // lower value = more fluid movement, but may take up more resources

        public PointThread(PointF endPoint, int smoothness) {
            this.endPoint = endPoint;
            this.smoothness = smoothness;
        }

        @Override
        public void run() {
            float deltaX = endPoint.x - currentUserLocation.x;
            float deltaY = endPoint.y - currentUserLocation.y;
            float angle = (float) Math.atan2(deltaY, deltaX);

            while (!currentUserLocation.equals(endPoint)) {
                float newX = (float) (currentUserLocation.x + smoothness * Math.cos(angle));
                float newY = (float) (currentUserLocation.y + smoothness * Math.sin(angle));

                if (Math.abs(newX - endPoint.x) < 20 && Math.abs(newY - endPoint.y) < 20) {
                    currentUserLocation.set(endPoint);
                    break;
                }
                else currentUserLocation.set(newX, newY);
                postInvalidate();

                try {
                    Thread.sleep(smoothness);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            animateCenter(currentUserLocation)
                    .withDuration(100)
                    .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                    .withInterruptible(false)
                    .start();
        }
    }
}
