package com.example.trackie.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;
import com.example.trackie.Utils;

import java.util.ArrayList;
import java.util.List;

public class PinImageMapView extends SubsamplingScaleImageView {
    private final Paint paint = new Paint();
    private List<PointF> mapPoints;
    private Bitmap pinBitmap;
    private Bitmap unconfirmedPinBitmap;
    private boolean isConfirmedPoint;

    public PinImageMapView(Context context) {
        this(context, null);
        initialise();
    }

    public PinImageMapView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    // helper function to set up some varaibles in the class
    private void initialise() {
        mapPoints = new ArrayList<>();
        initTouchListener();

        Drawable pinDrawable = getContext().getDrawable(R.drawable.confirmed_pin_marker_24px);
        pinBitmap = setUpPinBitmap(pinDrawable);

        Drawable unconfirmedPinDrawable = getContext().getDrawable(R.drawable.unconfirmed_pin_marker_24px);
        unconfirmedPinBitmap = setUpPinBitmap(unconfirmedPinDrawable);

        isConfirmedPoint = false;
    }

    private Bitmap setUpPinBitmap(Drawable drawable) {
        Bitmap tempBitmap = Utils.drawableToBitmap(drawable);
        float density = getResources().getDisplayMetrics().densityDpi;
        float pinW = (density / 420f) * tempBitmap.getWidth();
        float pinH = (density / 420f) * tempBitmap.getHeight();
        return Bitmap.createScaledBitmap(tempBitmap, (int) pinW, (int) pinH, true);
    }

    public void addPoint(PointF point) {
        if (!isConfirmedPoint && !mapPoints.isEmpty()) mapPoints.remove(mapPoints.size() - 1);
        mapPoints.add(point);
        isConfirmedPoint = false;
        invalidate();
    }

    public void removePoint(PointF point) {
        mapPoints.remove(point);
        invalidate();
    }

    public void removeAllPoints() {
        this.mapPoints.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isReady()) {
            return;
        }
        paint.setAntiAlias(true);
        for (PointF point : mapPoints) {
            PointF vPoint = sourceToViewCoord(point);

            float vX = vPoint.x - (pinBitmap.getWidth()/2);
            float vY = vPoint.y- pinBitmap.getHeight();
            if (!isConfirmedPoint && mapPoints.indexOf(point) == mapPoints.size() - 1) {
                canvas.drawBitmap(unconfirmedPinBitmap, vX, vY, paint);
                continue;
            }
            canvas.drawBitmap(pinBitmap, vX, vY, paint);
        }
    }

    private void initTouchListener() {
        GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                boolean isPointTapped = false;
                PointF tappedCoordinate = viewToSourceCoord(new PointF(e.getX(), e.getY()));
                if (isReady() && mapPoints != null) {
                    for (PointF point : mapPoints) {
                        int pointX = (int) point.x;
                        int pointY = (int) point.y;
                        if (tappedCoordinate.x >= pointX - pinBitmap.getWidth()
                                && tappedCoordinate.x <= pointX + pinBitmap.getWidth()
                                && tappedCoordinate.y >= pointY - pinBitmap.getHeight()
                                && tappedCoordinate.y <= pointY + pinBitmap.getHeight()) {
                            Toast.makeText(getContext(), "Tapped point " + tappedCoordinate.toString(), Toast.LENGTH_SHORT).show();
                            isPointTapped = true;
                            break;
                        }
                    }
                }
                if (!isPointTapped) {
                    addPoint(tappedCoordinate);
                }
                return true;
            }
        });
        setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    public void setConfirmedPoint(boolean confirmedPoint) {
        isConfirmedPoint = confirmedPoint;
        invalidate();
    }
}
