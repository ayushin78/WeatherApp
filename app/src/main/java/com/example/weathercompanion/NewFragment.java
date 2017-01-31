package com.example.weathercompanion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class NewFragment extends Fragment {

    Button b1,b2,b3,b4,b5,b6,b7,b8;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_layout, container, false);


        b1 = (Button)rootView.findViewById(R.id.b3);
        b2 = (Button)rootView.findViewById(R.id.b2);
        b3 = (Button)rootView.findViewById(R.id.b3);
        b4 = (Button)rootView.findViewById(R.id.b4);
        b5 = (Button)rootView.findViewById(R.id.b5);
        b6 = (Button)rootView.findViewById(R.id.b6);
        b7 = (Button)rootView.findViewById(R.id.b7);
        b8 = (Button)rootView.findViewById(R.id.b8);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.setData(Uri.parse("http://www.ripcurrents.noaa.gov/beach_hazards.shtml"));
                startActivity(Intent.createChooser(i,"open URL"));
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent();
                i.setData(Uri.parse("http://www.nws.noaa.gov/om/hurricane/index.shtml"));
                startActivity(Intent.createChooser(i,"open URL"));
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent();
                i.setData(Uri.parse("http://www.weather.gov/airquality/"));
                startActivity(Intent.createChooser(i,"open URL"));
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent();
                i.setData(Uri.parse("http://www.floodsafety.noaa.gov/"));
                startActivity(Intent.createChooser(i,"open URL"));
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent();
                i.setData(Uri.parse("http://www.nws.noaa.gov/om/fog/"));
                startActivity(Intent.createChooser(i,"open URL"));
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent();
                i.setData(Uri.parse("http://www.nws.noaa.gov/om/drought/index.shtml"));
                startActivity(Intent.createChooser(i,"open URL"));
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent();
                i.setData(Uri.parse("http://www.lightningsafety.noaa.gov/"));
                startActivity(Intent.createChooser(i,"open URL"));
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent();
                i.setData(Uri.parse("http://www.nws.noaa.gov/om/heat/index.shtml"));
                startActivity(Intent.createChooser(i,"open URL"));
            }
        });

        return rootView;
    }

}
