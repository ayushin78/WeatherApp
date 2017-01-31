package com.example.weathercompanion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        Bundle mainData=getIntent().getExtras();

        if (mainData==null)
        {
            return;
        }

       // String Message=mainData.getString("Message");
        //final TextView tv=(TextView)findViewById(R.id.textView);
        //tv.setText(Message);
    }

}

