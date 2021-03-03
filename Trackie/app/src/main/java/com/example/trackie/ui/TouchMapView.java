package com.example.trackie.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;
import com.example.trackie.database.MapData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Observable;

// wrapper class for the map image, which is to be used in both map and test mode in the app
// builds the existing TouchImageView class by Mike Ortiz
// inlcudes more functionality such

public class TouchMapView extends Observable {
    private Context context;
    private int mode;
    public final static int TEST_MODE = 0;
    public final static int MAP_MODE = 1;
    private SubsamplingScaleImageView mapImageView;
    private Bitmap mMapBitmap;
    private Canvas mapCanvas;
    private Bitmap previousBitmap;
    private PointF currentSelectedPoint;
    private boolean hasUnconfirmedPoint;

    public TouchMapView(Context context, int mode, SubsamplingScaleImageView mapImageView, Bitmap mapBitmap) {
        this.context = context;
        this.mode = mode;
        this.mapImageView = mapImageView;
        this.mMapBitmap = mapBitmap.copy(mapBitmap.getConfig(), true);
        this.mapCanvas = new Canvas(mMapBitmap);
        mapImageView.setImage(ImageSource.bitmap(mapBitmap));
        mapImageView.setOnTouchListener(createMappingTouchListener());
        hasUnconfirmedPoint = false;
    }

    // create touch listener for mapping of points. Main use - create mapping of point when user long presses on the map
    private View.OnTouchListener createMappingTouchListener() {
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (mapImageView.isReady()) {
                    if (hasUnconfirmedPoint && previousBitmap != null) {
                        mapCanvas = new Canvas(previousBitmap);

                        mMapBitmap = previousBitmap.copy(previousBitmap.getConfig(), true);
                        mapImageView.setImage(ImageSource.bitmap(mMapBitmap));
                    }
                    hasUnconfirmedPoint = true;
                    currentSelectedPoint = mapImageView.viewToSourceCoord(e.getX(), e.getY());
                    String s = "Pressed - x: " + currentSelectedPoint.x + ", y: " + currentSelectedPoint.y;
                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    setMarker(currentSelectedPoint);
                }
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

    }

    private void setMarker(PointF coords) {
        mapCanvas.save();
        Drawable marker = context.getDrawable(R.drawable.ic_location_on_24px);
        marker.setBounds((int) (coords.x - 45.0),
                         (int) (coords.y - 45.0),
                         (int) (coords.x + 45.0),
                         (int) (coords.y + 45.0));
        marker.draw(mapCanvas);
        float currentScale = mapImageView.getScale();
        PointF currentCenter = mapImageView.getCenter();
        mapImageView.setImage(ImageSource.bitmap(mMapBitmap));
        mapImageView.setScaleAndCenter(currentScale, currentCenter);
    }

    public Bitmap getImmutableMapBitmap() {
        return mMapBitmap.copy(mMapBitmap.getConfig(), false);
    }


    public void setHasUnconfirmedPoint(boolean hasUnconfirmedPoint) {
        this.hasUnconfirmedPoint = hasUnconfirmedPoint;
        previousBitmap = mMapBitmap.copy(mMapBitmap.getConfig(), true);
    }
}
