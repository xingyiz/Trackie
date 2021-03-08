package com.example.trackie.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;
import com.example.trackie.Utils;
import com.example.trackie.ui.mapmode.MapModeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/*
    TODO: rotation of image using 2 finger gesture
    TODO: pop up menu when clicking on existing point
 */
public class PinImageMapView extends SubsamplingScaleImageView {
    private final Paint paint = new Paint();
    private List<PointF> mapPoints;
    private Bitmap pinBitmap;
    private Bitmap unconfirmedPinBitmap;
    private PointF unconfirmedPoint;
    private PointF selectedPoint;

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
        setMaxScale(155);
        mapPoints = new ArrayList<>();
        initTouchListener();

        Drawable pinDrawable = getContext().getDrawable(R.drawable.confirmed_pin_marker_24px);
        pinBitmap = setUpPinBitmap(pinDrawable);

        Drawable unconfirmedPinDrawable = getContext().getDrawable(R.drawable.unconfirmed_pin_marker_24px);
        unconfirmedPinBitmap = setUpPinBitmap(unconfirmedPinDrawable);

        unconfirmedPoint = null;
    }

    private Bitmap setUpPinBitmap(Drawable drawable) {
        Bitmap tempBitmap = Utils.drawableToBitmap(drawable);
        float density = getResources().getDisplayMetrics().densityDpi;
        float pinW = (density / 420f) * tempBitmap.getWidth();
        float pinH = (density / 420f) * tempBitmap.getHeight();
        return Bitmap.createScaledBitmap(tempBitmap, (int) pinW, (int) pinH, true);
    }

    public void addPoint(PointF point) {
        if (unconfirmedPoint != null && !mapPoints.isEmpty()) mapPoints.remove(unconfirmedPoint);
        mapPoints.add(point);
        unconfirmedPoint = point;
        invalidate();
    }

    public void removeAllPoints() {
        this.mapPoints.clear();
        invalidate();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isReady()) return;
        paint.setAntiAlias(true);
        for (PointF point : mapPoints) {
            PointF vPoint = sourceToViewCoord(point);

            float vX = vPoint.x - (pinBitmap.getWidth()/2);
            float vY = vPoint.y- pinBitmap.getHeight();
            if (unconfirmedPoint != null && point == unconfirmedPoint) {
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
                PointF tappedCoordinate = new PointF(e.getX(), e.getY());
                if (isReady() && mapPoints != null) {
                    for (PointF point : mapPoints) {
                        PointF vPoint = sourceToViewCoord(point);
                        int pointX = (int) vPoint.x;
                        int pointY = (int) vPoint.y;

                        // Check if existing point is tapped
                        if (tappedCoordinate.x >= pointX - (pinBitmap.getWidth() / 1.5)
                                && tappedCoordinate.x <= pointX + (pinBitmap.getWidth() / 1.5)
                                && tappedCoordinate.y >= pointY - (pinBitmap.getHeight() / 1.5)
                                && tappedCoordinate.y <= pointY + (pinBitmap.getHeight() / 1.5)) {
                            isPointTapped = true;
                            selectedPoint = point;
                            createPinPopUpOptions(pointX, pointY);
                            break;
                        }
                    }
                }
                if (!isPointTapped) {
                    PointF sTappedCoordinate = viewToSourceCoord(tappedCoordinate);
                    addPoint(sTappedCoordinate);
                }
                return true;
            }
        });

//        RotationGestureDetector rotationGestureDetector = new RotationGestureDetector(new RotationGestureDetector.OnRotationGestureListener() {
//            @Override
//            public void OnRotation(RotationGestureDetector rotationDetector) {
//                PinImageMapView.this.setZoomEnabled(false);
//                float angle = rotationDetector.getAngle();
//                Toast.makeText(getContext(), "Angle: " + angle, Toast.LENGTH_SHORT).show();
//            }
//        });

        setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
//            rotationGestureDetector.onTouchEvent(event);
            return false;
        });
    }

    public void comfirmPoint() {
        unconfirmedPoint = null;
        invalidate();
    }

    public PointF getUnconfirmedPoint() {
        return unconfirmedPoint;
    }

    private void createPinPopUpOptions(int pointX, int pointY) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View mOptionsView = inflater.inflate(R.layout.pin_options_layout, null);
        final PopupWindow popUp = new PopupWindow(mOptionsView, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        popUp.setTouchable(true);
        popUp.setOutsideTouchable(true);

        mOptionsView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        popUp.showAtLocation((View) getParent(), Gravity.NO_GRAVITY, pointX - (mOptionsView.getMeasuredWidth() / 2),
                pointY + mOptionsView.getMeasuredHeight() - (pinBitmap.getHeight() / 2));

        FrameLayout pinDeleteSelector = (FrameLayout) mOptionsView.findViewById(R.id.pin_delete_option_view);
        pinDeleteSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPoint != null) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        mapPoints.remove(selectedPoint);
                        selectedPoint = null;
                        popUp.dismiss();
                        invalidate();
                    }, 150);
                }
            }
        });
    }
}
