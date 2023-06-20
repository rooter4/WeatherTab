package com.example.weathertab;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertab.Formatter.TafFormatter;
import com.example.weathertab.Formatter.WXFormatter;

public class TAFAdapter extends RecyclerView.Adapter<TAFAdapter.TAFHolder> {

    int dataSize = 0;
    String name;
    TafFormatter formatter;
    LinearLayout layout;
    LinearLayout.LayoutParams params;
    Context context;
    public static final String[] TAFINFO = {"wind_dir",
            "wind_spd",
            "wind_gst",
            "wind_shr_h",
            "wind_shr_d",
            "wind_shr_spd",
            "vis",
            "wx",
            "temp_c",
            "dp_c",
            "sky",
            "px_in",
            "px_mb",
            "cat",
            "maxT",
            "minT"
    };

    public TAFAdapter(String name, Context context){
    this.name = name;
    this.dataSize = WXParser.setTaf(name, 0);
    formatter = new TafFormatter();
    this.context = context;
    params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    @NonNull
    @Override
    public TAFHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taf_detail, parent, false);
        layout = view.findViewById(R.id.taf_layout);
        return new TAFHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TAFHolder holder, int position) {
        WXParser.setTaf(name, position);
        holder.time.setText(formatter.getTafTime(WXParser.getTafTime(), WXParser.getChange()));
        for(String s: TAFINFO){
            String temp = WXParser.airport.fields.get(s);
            if(temp !=null && !temp.isEmpty()) {
                TextView t = new TextView(context);
                t.setLayoutParams(params);
                t.setText(formatter.COMMON.get(s) + temp);
                layout.addView(t);

            }
        }

        /*
        holder.wind.setText(formatter.getWind(WXParser.getWind()[0],WXParser.getWind()[1],WXParser.getWind()[2]));
        holder.vis.setText(formatter.getVis(WXParser.getVis()));
        holder.sky.setText(WXParser.getSky());
        holder.wx.setText(formatter.getWX(WXParser.getWx()));
         */
    }

    @Override
    public int getItemCount() {
        return dataSize;
    }

    public class TAFHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView wind;
        TextView vis;
        TextView sky;
        TextView wx;


        public TAFHolder(@NonNull View itemView){
            super(itemView);

            time = itemView.findViewById(R.id.forcast_type_time);
            time.addTextChangedListener(new MutliTextWatcher(time));
                    }
    }
    private class MutliTextWatcher implements TextWatcher{

        TextView view;

        public MutliTextWatcher(View view){
            this.view = (TextView) view;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length() == 0)
                view.setVisibility(View.GONE);
            else
                view.setVisibility(View.VISIBLE);

        }
    }
}
