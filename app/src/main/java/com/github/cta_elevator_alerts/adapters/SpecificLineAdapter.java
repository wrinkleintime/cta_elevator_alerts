package com.github.cta_elevator_alerts.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cta_elevator_alerts.R;
import com.github.cta_elevator_alerts.activities.DisplayAlertActivity;
import com.github.cta_elevator_alerts.activities.SpecificLineActivity;

import java.util.List;

/**
 * Adapter for all stations with SpecificLineActivity (RecyclerView)
 *
 * @author Southport Developers (Sam Siner & Tyler Arndt)
 *
 */

public class SpecificLineAdapter extends RecyclerView.Adapter<SpecificLineAdapter.SpecificLineAdapterViewHolder> {

    class SpecificLineAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView specificLineTextView;
        private final View verticalBarTop;
        private final View verticalBarBottom;
        private final GradientDrawable circleDrawable;
        private final View circle;
        private final ImageView adaImageView;
        private final ImageView statusImageView;
        private final ImageView star_icon;
//        private final ImageView rightArrow;

        private SpecificLineAdapterViewHolder(View itemView) {
            super(itemView);
            specificLineTextView = itemView.findViewById(R.id.txt_line_station);
            verticalBarTop = itemView.findViewById(R.id.view_vertical_bar_top);
            verticalBarBottom = itemView.findViewById(R.id.view_vertical_bar_bottom);
            circle = itemView.findViewById(R.id.view_circle);
            circleDrawable = (GradientDrawable)circle.getBackground();
            adaImageView = itemView.findViewById(R.id.img_ada);
            statusImageView = itemView.findViewById(R.id.img_status);
            star_icon = itemView.findViewById(R.id.img_star_icon);
        }

        private void setUI(String lineName, View verticalBarTop, View verticalBarBottom, GradientDrawable circle){
            switch(lineName){
                case("Red Line"):
                    verticalBarTop.setBackgroundResource(R.color.colorRedLine);
                    verticalBarBottom.setBackgroundResource(R.color.colorRedLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorRedLine));
                    break;
                case("Blue Line"):
                    verticalBarTop.setBackgroundResource(R.color.colorBlueLine);
                    verticalBarBottom.setBackgroundResource(R.color.colorBlueLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorBlueLine));
                    break;
                case("Brown Line"):
                    verticalBarTop.setBackgroundResource(R.color.colorBrownLine);
                    verticalBarBottom.setBackgroundResource(R.color.colorBrownLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorBrownLine));
                    break;
                case("Green Line"):
                    verticalBarTop.setBackgroundResource(R.color.colorGreenLine);
                    verticalBarBottom.setBackgroundResource(R.color.colorGreenLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorGreenLine));
                    break;
                case("Orange Line"):
                    verticalBarTop.setBackgroundResource(R.color.colorOrangeLine);
                    verticalBarBottom.setBackgroundResource(R.color.colorOrangeLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorOrangeLine));
                    break;
                case("Pink Line"):
                    verticalBarTop.setBackgroundResource(R.color.colorPinkLine);
                    verticalBarBottom.setBackgroundResource(R.color.colorPinkLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorPinkLine));
                    break;
                case("Purple Line"):
                    verticalBarTop.setBackgroundResource(R.color.colorPurpleLine);
                    verticalBarBottom.setBackgroundResource(R.color.colorPurpleLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorPurpleLine));
                    break;
                case("Yellow Line"):
                    verticalBarTop.setBackgroundResource(R.color.colorYellowLine);
                    verticalBarBottom.setBackgroundResource(R.color.colorYellowLine);
                    circle.setStroke(5, context.getResources().getColor(R.color.colorYellowLine));
                    break;
            }
        }
    }

    private final LayoutInflater mInflater;
    private final Context context;
    private final List<String> lineStations;
    private final androidx.appcompat.widget.Toolbar toolbar;
    private final TextView toolbarTextView;
    private final ImageView back_arrow;
    private final ImageView home_icon;

    public SpecificLineAdapter(Context context, List<String> lineStations){
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.lineStations = lineStations;
        toolbar = ((Activity)context).findViewById(R.id.toolbar);
        toolbarTextView = ((Activity)context).findViewById(R.id.txt_toolbar);
        back_arrow = ((Activity)context).findViewById(R.id.img_back_arrow);
        home_icon = ((Activity)context).findViewById(R.id.img_home_icon);
    }

    @Override
    @NonNull
    public SpecificLineAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = mInflater.inflate(R.layout.specific_line_station, parent, false);
        return new SpecificLineAdapter.SpecificLineAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecificLineAdapter.SpecificLineAdapterViewHolder holder, int position){
        String currStationID = lineStations.get(position);
        String currStationName = ((SpecificLineActivity)context).getStationName(currStationID);
        int transparentColor = context.getResources().getColor(R.color.colorTransparent);
        boolean hasElevator = ((SpecificLineActivity)context).getHasElevator(currStationID);
        boolean isFavorite = ((SpecificLineActivity)context).getIsFavorite(currStationID);

        holder.setUI(toolbarTextView.getText().toString(), holder.verticalBarTop, holder.verticalBarBottom, holder.circleDrawable);
        if(position == 0){
            holder.verticalBarTop.setBackgroundColor(transparentColor);
        }
        if(position == lineStations.size() - 1){
            holder.verticalBarBottom.setBackgroundColor(transparentColor);
        }

        holder.specificLineTextView.setText(currStationName);
        holder.adaImageView.setVisibility(View.VISIBLE);
        if(!hasElevator) holder.adaImageView.setVisibility(View.INVISIBLE);

        holder.statusImageView.setVisibility(View.VISIBLE);
        holder.circle.setVisibility(View.VISIBLE);
        if(!((SpecificLineActivity) context).getHasElevatorAlert(currStationID)){
            holder.statusImageView.setVisibility(View.INVISIBLE);
        } else{
            holder.circle.setVisibility(View.INVISIBLE);
        }

        if(isFavorite){
            holder.star_icon.setVisibility(View.VISIBLE);
        } else {
            holder.star_icon.setVisibility(View.INVISIBLE);
        }

        ((View)holder.specificLineTextView.getParent()).setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(context, DisplayAlertActivity.class);
            intent.putExtra("stationID", currStationID);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount(){
        if (lineStations != null) return lineStations.size();
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
                colorID = context.getResources().getColor(R.color.colorPrimaryDark);
                toolbar.setBackgroundResource(R.color.colorYellowLine);
                toolbarTextView.setTextColor(colorID);
                back_arrow.setColorFilter(colorID);
                home_icon.setColorFilter(colorID);
                break;
        }
    }
}
