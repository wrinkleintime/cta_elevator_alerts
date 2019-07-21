package com.example.elevator_app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.RelativeLayout.LayoutParams;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elevator_app.R;
import com.example.elevator_app.activities.AddFavoriteActivity;
import com.example.elevator_app.activities.DisplayAlertActivity;
import com.example.elevator_app.activities.SpecificLineActivity;

public class SpecificLineAdapter extends RecyclerView.Adapter<SpecificLineAdapter.SpecificLineAdapterViewHolder> {

    class SpecificLineAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView specificLineTextView;
        private final View verticalBar;
        private final GradientDrawable circle;
        private final ImageView adaImageView;
        private final ImageView statusImageView;

        private SpecificLineAdapterViewHolder(View itemView) {
            super(itemView);
            specificLineTextView = itemView.findViewById(R.id.txt_line_station);
            verticalBar = itemView.findViewById(R.id.view_vertical_bar);
            circle = (GradientDrawable)itemView.findViewById(R.id.view_circle).getBackground();
            adaImageView = itemView.findViewById(R.id.img_ada);
            statusImageView = itemView.findViewById(R.id.img_status);
        }

        private void setUI(String lineName, View verticalBar, GradientDrawable circle){
            switch(lineName){
                case("Red Line"):
                    verticalBar.setBackgroundResource(R.color.colorRedLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorRedLine));
                    break;
                case("Blue Line"):
                    verticalBar.setBackgroundResource(R.color.colorBlueLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorBlueLine));
                    break;
                case("Brown Line"):
                    verticalBar.setBackgroundResource(R.color.colorBrownLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorBrownLine));
                    break;
                case("Green Line"):
                    verticalBar.setBackgroundResource(R.color.colorGreenLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorGreenLine));
                    break;
                case("Orange Line"):
                    verticalBar.setBackgroundResource(R.color.colorOrangeLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorOrangeLine));
                    break;
                case("Pink Line"):
                    verticalBar.setBackgroundResource(R.color.colorPinkLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorPinkLine));
                    break;
                case("Purple Line"):
                    verticalBar.setBackgroundResource(R.color.colorPurpleLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorPurpleLine));
                    break;
                case("Yellow Line"):
                    verticalBar.setBackgroundResource(R.color.colorYellowLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorYellowLine));
                    break;
            }
        }
    }

    private final LayoutInflater mInflater;
    private final Context context;
    private final String[] lineStations;
    private final Toolbar toolbar;
    private final TextView toolbarTextView;

    public SpecificLineAdapter(Context context, String[] lineStations){
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.lineStations = lineStations;
        toolbar = ((Activity)context).findViewById(R.id.toolbar_dynamic);
        toolbarTextView = ((Activity)context).findViewById(R.id.txt_toolbar_title);
    }

    @Override
    public SpecificLineAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = mInflater.inflate(R.layout.specific_line_station, parent, false);
        return new SpecificLineAdapter.SpecificLineAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SpecificLineAdapter.SpecificLineAdapterViewHolder holder, int position){
        //TODO add '+' icon next to station name when coming from 'add favorite' activity
        String currStationID = lineStations[position];
        String currStationName = ((SpecificLineActivity)context).getStationName(currStationID);

        holder.setUI(toolbarTextView.getText().toString(), holder.verticalBar, holder.circle);
        if(position == 0 || position == lineStations.length - 1){
            holder.verticalBar.getLayoutParams().height = 60;
            if(position == lineStations.length - 1){
                LayoutParams lp = (LayoutParams)holder.verticalBar.getLayoutParams();
                lp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                holder.verticalBar.setLayoutParams(lp);
            }
        }

        holder.specificLineTextView.setText(currStationName);
        if(!((SpecificLineActivity)context).getHasElevator(currStationID)){
            holder.adaImageView.setImageResource(android.R.color.transparent);
        }
        if(!((SpecificLineActivity) context).getHasElevatorAlert(currStationID)){
            holder.statusImageView.setImageResource(android.R.color.transparent);
        }

        ((View)holder.specificLineTextView.getParent()).setOnClickListener(v -> {
            boolean fromFavorites = ((Activity)context).getIntent().getBooleanExtra("fromFavorites", false);

            Intent intent;

            if (fromFavorites) {
                if (!((SpecificLineActivity) context).getHasElevator(currStationID)) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("No elevator!");
                    alert.setMessage("No elevator is present at this station. Please choose a favorite station with an elevator.");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                } else {
                    intent = new Intent(context, AddFavoriteActivity.class);
                    intent.putExtra("stationID", currStationID);
                    intent.putExtra("stationName", currStationName);
                    intent.putExtra("nickname", ((Activity) context).getIntent().getStringExtra("nickname"));
                    context.startActivity(intent);
                }
            } else{
                intent = new Intent(context, DisplayAlertActivity.class);
                intent.putExtra("stationID", currStationID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        if (lineStations != null) return lineStations.length;
        else return 0;
    }

    public void setToolbar(String lineName){
        int colorID;
        toolbarTextView.setText(lineName);
        switch(lineName){
            case("Red Line"):
                toolbar.setBackgroundResource(R.color.colorRedLine);
                break;
            case("Blue Line"):
                toolbar.setBackgroundResource(R.color.colorBlueLine);
                break;
            case("Brown Line"):
                toolbar.setBackgroundResource(R.color.colorBrownLine);
                break;
            case("Green Line"):
                toolbar.setBackgroundResource(R.color.colorGreenLine);
                break;
            case("Orange Line"):
                toolbar.setBackgroundResource(R.color.colorOrangeLine);
                break;
            case("Pink Line"):
                toolbar.setBackgroundResource(R.color.colorPinkLine);
                break;
            case("Purple Line"):
                toolbar.setBackgroundResource(R.color.colorPurpleLine);
                break;
            case("Yellow Line"):
                TextView cancelText = ((Activity)context).findViewById(R.id.txt_cancel);
                colorID = context.getResources().getColor(R.color.colorPrimaryDark);
                cancelText.setTextColor(colorID);
                toolbar.setBackgroundResource(R.color.colorYellowLine);
                toolbarTextView.setTextColor(colorID);
                break;
        }
    }
}
