package com.example.weathertab;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertab.Formatter.TafFormatter;
import com.example.weathertab.Formatter.WXFormatter;

public class TAFAdapter extends RecyclerView.Adapter<TAFAdapter.TAFHolder> {

    int dataSize = 0;
    String name;
    TafFormatter formatter;

    public TAFAdapter(String name){
    this.name = name;
    this.dataSize = WXParser.setTaf(name, 0);
    formatter = new TafFormatter();
    }
    @NonNull
    @Override
    public TAFHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taf_detail, parent, false);
        return new TAFHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TAFHolder holder, int position) {
        WXParser.setTaf(name, position);
        holder.time.setText(formatter.getTafTime(WXParser.getTafTime(), WXParser.getChange()));
        holder.wind.setText(formatter.getWind(WXParser.getWind()[0],WXParser.getWind()[1],WXParser.getWind()[2]));
        holder.vis.setText(formatter.getVis(WXParser.getVis()));
        holder.sky.setText(WXParser.getSky());
        holder.wx.setText(formatter.getWX(WXParser.getWx()));
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
            wind = itemView.findViewById(R.id.taf_wind);
            wind.addTextChangedListener(new MutliTextWatcher(wind));
            vis = itemView.findViewById(R.id.taf_vis);
            vis.addTextChangedListener(new MutliTextWatcher(vis));

            sky = itemView.findViewById(R.id.taf_ceil);
            sky.addTextChangedListener(new MutliTextWatcher(sky));
            wx = itemView.findViewById(R.id.taf_wx);
            wx.addTextChangedListener(new MutliTextWatcher(wx));


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
