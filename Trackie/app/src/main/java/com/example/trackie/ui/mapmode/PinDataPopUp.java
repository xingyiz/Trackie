package com.example.trackie.ui.mapmode;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.example.trackie.database.MapData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Pop-up which appears when user clicks on an existing pin and selects 'View Data' option
public class PinDataPopUp {
    private MapData mapData;
    private List<String> bssidList;
    private Point pinLocation;
    private Context context;
    private final View mRSSIDataView;
    private PopupWindow popUp;


    public PinDataPopUp(Context context, MapData mapData, PointF pinLocation) {
        this.mapData = mapData;
        this.pinLocation = new Point((int)pinLocation.x, (int)pinLocation.y);
        this.context = context;

        Set<String> bssidSet = mapData.getData().keySet();
        this.bssidList = new ArrayList<>();
        bssidList.addAll(bssidSet);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRSSIDataView = inflater.inflate(R.layout.view_pin_data_popup_layout, null);
    }

    public void show() {
        bindView();
        popUp = new PopupWindow(mRSSIDataView, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        popUp.setTouchable(true);
        popUp.showAtLocation(mRSSIDataView, Gravity.CENTER, 0, 0);
    }

    public void bindView() {
        TextView pointLabelTextview = mRSSIDataView.findViewById(R.id.pin_point_label_textview);
        pointLabelTextview.setText(pinLocation.toString());

        Map<String, List<Integer>> bssidValueData = mapData.getData();
        TextView bssidCountTextview = mRSSIDataView.findViewById(R.id.mapped_bssids_count_textview);
        bssidCountTextview.setText(context.getResources().getString(R.string.bssids_count) + ": " + bssidValueData.size());

        TextView timestampTextview = mRSSIDataView.findViewById(R.id.pin_mapped_timestamp);
        Date dateRecorded = mapData.getTimestamp().toDate();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM hh:mm:ss");
        timestampTextview.setText(dateFormat.format(dateRecorded));

        RecyclerView pinDataRecycler = mRSSIDataView.findViewById(R.id.pin_data_info_recyclerview);
        pinDataRecycler.setAdapter(new PinDataAdapter());
        pinDataRecycler.setLayoutManager(new LinearLayoutManager(context));

        View exitPullUpView = mRSSIDataView.findViewById(R.id.pull_up_exit_view_pin_data_view);
        exitPullUpView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MyGestureDetector gestureDetector = new MyGestureDetector();
                return (new GestureDetector(context, gestureDetector)).onTouchEvent(event);
            }
        });

    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
                    return false;
                }

                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    popUp.dismiss();
                }

//                else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                    popUp.dismiss();
//                }
            } catch (Exception e) {

            }
            return false;
        }
    }

    private class PinDataAdapter extends RecyclerView.Adapter {


        public PinDataAdapter() {
            System.out.println(mapData);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View pinSingleDataView = layoutInflater.inflate(R.layout.pin_rssi_data_recycler_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(pinSingleDataView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            String bssid = bssidList.get(position);
            System.out.println("Bssid: " + bssid);
            List<Integer> rssiValues = mapData.getData().get(bssid);
            viewHolder.bssidTextview.setText(bssid);
            viewHolder.rssiTextview.setText(rssiValues.toString());
        }

        @Override
        public int getItemCount() {
            return mapData.getData().keySet().size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView bssidTextview;
            TextView rssiTextview;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                bssidTextview = itemView.findViewById(R.id.pin_bssid_textview);
                rssiTextview = itemView.findViewById(R.id.pin_rssi_textview);
            }
        }
    }


}
