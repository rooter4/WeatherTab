package com.example.weathertab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weathertab.databinding.UnitsBinding;

public class UnitsSelector extends Fragment {


    UnitsBinding binding;
    Switch tempSwitch;
    Switch pxSwitch;
    Switch distSwitch;
    public UnitsSelector(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = UnitsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tempSwitch = view.findViewById(R.id.temp_switch);
        pxSwitch = view.findViewById(R.id.px_switch);
        distSwitch = view.findViewById(R.id.dist_switch);

        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    UNITS.TEMP = UNITS.temp_f;
                }
                else
                    UNITS.TEMP = UNITS.temp_c;
            }
        });
        pxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    UNITS.PX = UNITS.px_mb;
                else
                    UNITS.PX = UNITS.px_in;
            }
        });
        distSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    UNITS.DIST = UNITS.KM;
                else
                    UNITS.DIST = UNITS.NM;
            }
        });

    }
}
