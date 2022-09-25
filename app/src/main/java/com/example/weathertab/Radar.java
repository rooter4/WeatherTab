package com.example.weathertab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weathertab.databinding.RadarViewBinding;

public class Radar extends Fragment {
    WebView radarView;
    RadarViewBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RadarViewBinding.inflate(inflater,container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String name = getArguments().getString("name");
        String url = "https://radar.weather.gov/ridge/standard/" +name+"_loop.gif";
        System.out.println(url);
        radarView = view.findViewById(R.id.radar_webView);
        radarView.getSettings().setJavaScriptEnabled(true);
        radarView.getSettings().setLoadWithOverviewMode(true);
        radarView.getSettings().setUseWideViewPort(true);
        radarView.getSettings().setBuiltInZoomControls(true);
        radarView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {

                super.onReceivedTitle(view, title);
            }
        });
        radarView.loadUrl(url);
    }
}
