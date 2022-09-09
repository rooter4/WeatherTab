package com.example.weathertab;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SmallRVAdapter extends RecyclerView.Adapter<SmallRVAdapter.AirportViewHolder> {

    List<String> data;
    Context context;
    NavController navController;
    boolean clickable;
    SmallRVAdapter(Context context, List<String> iData, boolean clickable) {
        data = iData;
        updateData(data);
        this.context = context;
        this.clickable = clickable;

    }
    @Override
    public AirportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_view, parent, false);
        return new AirportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AirportViewHolder holder, int position) {
        String name = data.get(position);
        WXParser.setMetar(name);
        holder.id.setText(name);
        holder.flight.setText(WXParser.getSky());
        holder.wind.setText(WXParser.getWind());
        String cat = WXParser.getCondition();
        holder.category.setText(cat);
        GradientDrawable gd = (GradientDrawable) holder.category.getBackground();
        switch (cat){
            case "VFR" : gd.setColor(ContextCompat.getColor(context,R.color.VFR)); break;
            case "MVFR": gd.setColor(ContextCompat.getColor(context,R.color.MVFR)); break;
            case "IFR": gd.setColor(ContextCompat.getColor(context,R.color.error)); break;
            case "LIFR": gd.setColor(ContextCompat.getColor(context,R.color.LIFR));break;
            default: gd.setColor(ContextCompat.getColor(context,R.color.grey));
        }
        holder.category.setBackground(gd);
        holder.name.setText(AirportData.getName(data.get(position)));
        holder.wx.setText(WXParser.getWx());
        holder.pa.setText(WXParser.getPa());
        holder.da.setText(WXParser.getDa());

        int time = WXParser.getTime();
        holder.time.setText(time + " min");
        if(time < 60)
            holder.time.setTextColor(ContextCompat.getColor(context,R.color.VFR));
        else
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.error));
        //holder.temp_dp.setText(parser.getTemp_Dp(name));
        holder.temp_dp.setText(WXParser.getTemp_Dp());
        holder.px.setText(WXParser.getPx());
        if(WXParser.hasTaf())
            holder.hasTaf.setVisibility(View.VISIBLE);
        else
            holder.hasTaf.setVisibility(View.INVISIBLE);

        if(clickable) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                    Bundle bundle = new Bundle();
                    bundle.putString("airport", AirportData.getName(name));
                    bundle.putString("id", name);
                    bundle.putString("tafTitle",name);
                    navController.navigate(R.id.action_FirstFragment_to_Detail, bundle);


                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class AirportViewHolder extends RecyclerView.ViewHolder{
       TextView id;
       TextView flight;
       TextView category;
       TextView wind;
       TextView name;
       TextView wx;
       TextView time;
       TextView temp_dp;
       TextView px;
       TextView hasTaf;
       TextView pa;
       TextView da;
        public AirportViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.airport_name);
            id = itemView.findViewById(R.id.airport_nid);
            flight = itemView.findViewById(R.id.flight_condition);
            category = itemView.findViewById(R.id.category);
            wind = itemView.findViewById(R.id.wind);
            wx = itemView.findViewById(R.id.wx_string);
            time = itemView.findViewById(R.id.time);
            temp_dp = itemView.findViewById(R.id.temp_dp);
            px = itemView.findViewById(R.id.alt);
            hasTaf = itemView.findViewById(R.id.taf);
            pa = itemView.findViewById(R.id.pa);
            da = itemView.findViewById(R.id.da);

        }
    }
    public void updateData(List<String> uData){

       data = uData;

    }
}
