package com.example.weathertab;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertab.Formatter.WXFormatter;
import com.example.weathertab.databinding.AirportDetailBinding;


import java.util.ArrayList;
import java.util.List;

public class AirportDetail extends Fragment {

    private AirportDetailBinding binding;
    String aName;
    RecyclerView tafRv;
    TAFAdapter tafAdapter;
    RecyclerView copyRv;
    SmallRVAdapter copyAdapter;
    Switch rawSwitch;
    TextView rawMetar;
    TextView rawTaf;
    TextView issueTime;
    LinearLayout layout;
    LinearLayoutCompat.LayoutParams layoutParams;
    Button radar;
    public static final String[] AMPLIFYING = {"wind_dir",
            "wind_spd",
            "wind_gst",
            "vis",
            "wx",
            "temp_c",
            "dp_c",
            "sky",
            "px_in",
            "px_mb",
            "cat", "lat", "long", "px_chng", "maxT", "minT", "maxT24", "minT24", "precip", "pcp3", "pcp6", "pcp24", "snow"
            ,"wind_shr_h"
   ,"wind_shr_d"
    ,"wind_shr_spd"
};

    public AirportDetail(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AirportDetailBinding.inflate(inflater,container,false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        View view = toolbar.findViewById(R.id.search_view);
        view.setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> data = new ArrayList<>();

        aName = (String) getArguments().get("id");
        data.add(aName);
        rawMetar = view.findViewById(R.id.metar_raw);
        rawTaf = view.findViewById(R.id.taf_raw);
        layout = view.findViewById(R.id.info);
        radar = view.findViewById(R.id.radar_button);
        issueTime = view.findViewById(R.id.issue_time);
        radar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                Bundle bundle = new Bundle();
                bundle.putString("name", AirportData.getClosestStation(aName));
                navController.navigate(R.id.action_Detail_to_Radar, bundle);
            }
        });

        WXParser.setMetar(aName);

        for(String s: AMPLIFYING){
            String temp = WXParser.airport.fields.get(s);
            if(temp !=null && !temp.isEmpty()) {
                TextView t = new TextView(this.getContext());
                t.setText(WXFormatter.COMMON.get(s) + temp);
                layout.addView(t);
            }
        }


        tafRv = view.findViewById(R.id.taf_rv);
        tafAdapter = new TAFAdapter(aName,getContext());
        tafRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        tafRv.setAdapter(tafAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        if(WXParser.hasTaf()) {
            issueTime.setText(WXFormatter.COMMON.get("issue_time") + WXFormatter.getIssue(WXParser.getIssueTime()));
            issueTime.setBackgroundColor(getResources().getColor(R.color.primary_light));
        }


        copyRv = view.findViewById(R.id.rv_copy);
        copyAdapter = new SmallRVAdapter(view.getContext(),data,false);
        copyRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        copyRv.addItemDecoration(decoration);

        copyRv.setAdapter(copyAdapter);


        rawSwitch = view.findViewById(R.id.raw_switch);
        rawSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    tafRv.setVisibility(View.GONE);
                    issueTime.setVisibility(View.GONE);
                    WXParser.setMetar(aName);
                    rawMetar.setText(Html.fromHtml("<b> METAR: </b>") +" \n" + WXParser.getRawMetar());
                    WXParser.setTaf(aName,0);
                    rawTaf.setText("TAF: \n" + WXParser.getRawMetar());

                }
                else
                {
                    tafRv.setVisibility(View.VISIBLE);
                    issueTime.setVisibility(View.VISIBLE);
                    rawMetar.setText("");
                    rawTaf.setText("");
                }

            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        View view = toolbar.findViewById(R.id.search_view);
        view.setVisibility(View.VISIBLE);
    }

}
