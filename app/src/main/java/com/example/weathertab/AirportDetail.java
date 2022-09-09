package com.example.weathertab;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertab.databinding.AirportDetailBinding;

import org.w3c.dom.Document;

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


    public AirportDetail(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AirportDetailBinding.inflate(inflater,container,false);
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


        tafRv = view.findViewById(R.id.taf_rv);
        tafAdapter = new TAFAdapter(aName);
        tafRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        tafRv.setAdapter(tafAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));


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
                    WXParser.setMetar(aName);
                    rawMetar.setText(Html.fromHtml("<b> METAR: </b>") +" \n" + WXParser.getRawMetar());
                    WXParser.setTaf(aName,0);
                    rawTaf.setText("TAF: \n" + WXParser.getRawMetar());

                }
                else
                {
                    tafRv.setVisibility(View.VISIBLE);
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
    }
}
