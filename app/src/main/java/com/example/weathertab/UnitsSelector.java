package com.example.weathertab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weathertab.databinding.UnitsBinding;

import java.util.Arrays;
import java.util.List;

public class UnitsSelector extends Fragment {


    UnitsBinding binding;
    Switch tempSwitch;
    Switch pxSwitch;
    Spinner distSwitch;
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
        distSwitch = view.findViewById(R.id.spinner);
        if(UNITS.TEMP == UNITS.temp_f)
            tempSwitch.setChecked(true);
        if(UNITS.PX == UNITS.px_mb)
            pxSwitch.setChecked(true);
        List<String> temp = Arrays.asList(getResources().getStringArray(R.array.dropDown));
        distSwitch.setSelection(temp.indexOf(UNITS.DIST));


        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    UNITS.TEMP = UNITS.temp_f;
                    UNITS.change("TEMP", UNITS.temp_f);
                }
                else {
                    UNITS.TEMP = UNITS.temp_c;
                    UNITS.change("TEMP", UNITS.temp_c);
                }
            }
        });
        pxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    UNITS.PX = UNITS.px_mb;
                    UNITS.change("PX", UNITS.px_mb);
                }
                else {
                    UNITS.PX = UNITS.px_in;
                    UNITS.change("PX", UNITS.px_in);
                }
            }
        });
        distSwitch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] temp = getResources().getStringArray(R.array.dropDown);
                UNITS.change("DIST", temp[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }
}
