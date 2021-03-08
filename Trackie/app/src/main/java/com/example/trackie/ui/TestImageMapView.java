package com.example.trackie.ui;

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

public class TestImageMapView extends SubsamplingScaleImageView {
    private final Paint paint = new Paint();
    private PointF currentUserLocation;
    private Bitmap pinBitmap;

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

    public void updateCurrentUserLocation(PointF currentUserLocation) {
        this.currentUserLocation = currentUserLocation;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isReady()) return;
        paint.setAntiAlias(true);

        PointF vPoint = sourceToViewCoord(currentUserLocation);
        float vX = vPoint.x - (pinBitmap.getWidth()/2);
        float vY = vPoint.y - pinBitmap.getHeight();

        canvas.drawBitmap(pinBitmap, vX, vY, paint);
    }

    private void setUpPinBitmap() {
        Drawable pinDrawable = getContext().getDrawable(R.drawable.confirmed_pin_marker_24px);
        Bitmap tempBitmap = Utils.drawableToBitmap(pinDrawable);
        float density = getResources().getDisplayMetrics().densityDpi;
        float pinW = (density / 420f) * tempBitmap.getWidth();
        float pinH = (density / 420f) * tempBitmap.getHeight();
        pinBitmap =  Bitmap.createScaledBitmap(tempBitmap, (int) pinW, (int) pinH, true);
    }
}
