package com.example.trackie.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    public void removeAllCategories() {
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

//            if (categoryPoint.getPointF() != null && categoryPoint.getImage() != null) {
//                PointF point = sourceToViewCoord(categoryPoint.getPointF());
//                float vX = point.x - (pinIcon.getWidth()/2);
//                float vY = point.y - pinIcon.getHeight();
//                canvas.drawBitmap(pinIcon, vX, vY, paint);
//            }

            Drawable drawable = getContext().getDrawable(R.drawable.ic_location_on_24px);
            float vX = point.x - (30/2);
            float vY = point.y - 30;
            drawable.setBounds((int) (point.x - 45.0),
                               (int) (point.y -  45.0),
                               (int) (point.x + 45.0),
                               (int) (point.y + 45.0));
            drawable.draw(canvas);
//            canvas.drawD(unconfirmedPointerBitmap, vX, vY, paint);
        }
    }

    private void initTouchListener() {
        GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                boolean isPointTapped = false;
                PointF tappedCoordinate = new PointF(e.getX(), e.getY());
                if (isReady() && mapPoints != null) {
//                    Bitmap clickArea = mapPoints.get(0).getImage();
//                    int clickAreaWidth = clickArea.getWidth();
//                    int clickAreaHeight = clickArea.getHeight();
//                    for (MapPoint categoryPoint : mapPoints) {
//                        PointF categoryCoordinate = sourceToViewCoord(categoryPoint.getPointF());
//                        int categoryX = (int) (categoryCoordinate.x);
//                        int categoryY = (int) (categoryCoordinate.y - clickAreaHeight / 2);
//                        if (tappedCoordinate.x >= categoryX - clickAreaWidth / 2
//                                && tappedCoordinate.x <= categoryX + clickAreaWidth / 2
//                                && tappedCoordinate.y >= categoryY - clickAreaHeight / 2
//                                && tappedCoordinate.y <= categoryY + clickAreaHeight / 2) {
//                            onPinClickListener.onPinClick(categoryPoint);
//                            break;
//                        }
//                    }
                    for (PointF point : mapPoints) {
                        int pointX = (int) point.x;
                        int pointY = (int) point.y;
                        if (tappedCoordinate.x >= pointX - 30 / 2
                                && tappedCoordinate.x <= pointX + 30 / 2
                                && tappedCoordinate.y >= pointY - 30 / 2
                                && tappedCoordinate.y <= pointY + 30 / 2) {
                            Toast.makeText(getContext(), "Tapped point", Toast.LENGTH_SHORT).show();
                            isPointTapped = true;
                            break;
                        }
                    }
                }
                if (!isPointTapped) {
                    addPoint(tappedCoordinate);
                    Toast.makeText(getContext(), "Made point", Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });
        setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }
}
