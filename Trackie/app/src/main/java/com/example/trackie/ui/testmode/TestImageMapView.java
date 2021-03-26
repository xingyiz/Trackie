package com.example.trackie.ui.testmode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.core.widgets.Rectangle;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;
import com.example.trackie.Utils;

import java.util.concurrent.TimeUnit;

public class TestImageMapView extends SubsamplingScaleImageView {
    private final Paint paint = new Paint();
    private PointF currentUserLocation;
    private Bitmap pinBitmap;
    private final static int ANIMATION_FRAMERATE = 8;


    public TestImageMapView(Context context, AttributeSet attr) {
        super(context, attr);

        setUpPinBitmap();
    }

    public TestImageMapView(Context context) {
        super(context);
        setUpPinBitmap();
    }

    public PointF getCurrentUserLocation() {
        return currentUserLocation;
    }

    public void updateCurrentUserLocation(PointF newLocation) {
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

        PointThread pointThread = new PointThread(newLocation, ANIMATION_FRAMERATE);
        pointThread.start();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isReady()) return;
        paint.setAntiAlias(true);
        if (currentUserLocation == null) return;
        PointF vPoint = sourceToViewCoord(currentUserLocation);

        float vX = vPoint.x - (pinBitmap.getWidth()/2);
        float vY = vPoint.y - pinBitmap.getHeight();

        canvas.drawBitmap(pinBitmap, vX, vY, paint);

    }

    private void setUpPinBitmap() {
        Drawable pinDrawable = getContext().getDrawable(R.drawable.ic_my_location_24px);
        Bitmap tempBitmap = Utils.drawableToBitmap(pinDrawable);
        float density = getResources().getDisplayMetrics().densityDpi;
        float pinW = (density / 420f) * tempBitmap.getWidth();
        float pinH = (density / 420f) * tempBitmap.getHeight();
        pinBitmap =  Bitmap.createScaledBitmap(tempBitmap, (int) pinW, (int) pinH, true);
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
