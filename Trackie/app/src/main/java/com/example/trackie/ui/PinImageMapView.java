package com.example.trackie.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;
import com.example.trackie.Utils;
import com.example.trackie.database.MapData;

import java.util.ArrayList;
import java.util.List;

public class PinImageMapView extends SubsamplingScaleImageView {
    private final Paint paint = new Paint();
    private List<PointF> mapPoints;

    public PinImageMapView(Context context) {
        this(context, null);
    }

    public PinImageMapView(Context context, AttributeSet attr) {
        super(context, attr);
        mapPoints = new ArrayList<>();
        initTouchListener();
    }

    public void addPoint(PointF point) {
        mapPoints.add(point);
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
        System.out.println("getSHeight: " + getSHeight());
        System.out.println("getHeight: " + getHeight());
        System.out.println("getSWidth: " + getSWidth());
        System.out.println("getWidth: " + getWidth());
        paint.setAntiAlias(true);
        for (PointF point : mapPoints) {
            Drawable pinDrawable = getContext().getDrawable(R.drawable.ic_location_on_24px);
            Bitmap pinBitmap = Utils.drawableToBitmap(pinDrawable);
            PointF vPoint = sourceToViewCoord(point);
            PointF sourceBound = sourceToViewCoord(0,0);
            float vX = vPoint.x - (pinBitmap.getWidth()/2);
            float vY = vPoint.y- pinBitmap.getHeight();
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
                        if (tappedCoordinate.x >= pointX - 30 / 2
                                && tappedCoordinate.x <= pointX + 30 / 2
                                && tappedCoordinate.y >= pointY - 30 / 2
                                && tappedCoordinate.y <= pointY + 30 / 2) {
                            Toast.makeText(getContext(), "Tapped point " + tappedCoordinate.toString(), Toast.LENGTH_SHORT).show();
                            isPointTapped = true;
                            break;
                        }
                    }
                }
                if (!isPointTapped) {
                    addPoint(tappedCoordinate);
                    Toast.makeText(getContext(), "Made point " + tappedCoordinate.toString(), Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });
        setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }


}
