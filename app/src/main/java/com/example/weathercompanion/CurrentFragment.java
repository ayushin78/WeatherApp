package com.example.weathercompanion;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;


public class CurrentFragment extends Fragment {

    ProgressDialog pDialog;
    ImageView weatherimg;
    static String toSpeak;
    TextView placeTv,latlonTv,mainTempTv,minmaxTv,humidityTv,pressTv,weatherTv,speedTv,degTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.current_layout, container, false);
        weatherimg=(ImageView)rootView.findViewById(R.id.weathericon);
        placeTv=(TextView)rootView.findViewById(R.id.placeTv);
        minmaxTv=(TextView)rootView.findViewById(R.id.minmax);
        mainTempTv=(TextView)rootView.findViewById(R.id.mainTemp);
        latlonTv=(TextView)rootView.findViewById(R.id.coords);
        humidityTv=(TextView)rootView.findViewById(R.id.humidity);
        pressTv=(TextView)rootView.findViewById(R.id.press);
        weatherTv=(TextView)rootView.findViewById(R.id.weatherdesc);
        speedTv=(TextView)rootView.findViewById(R.id.speed);
        degTv=(TextView)rootView.findViewById(R.id.deg);

        SharedPreferences prefs = getActivity().getSharedPreferences("cities",Context.MODE_PRIVATE);
        String city=prefs.getString("name","Default");
        new OpenWeatherMapTask(
                city).execute();
        return rootView;
    }



    public class OpenWeatherMapTask extends AsyncTask<Void, Void, String> {

        String cityName;

        String ourplace= "";
        String ourcoords="";
        String maintemp="";
        String minmax="";
        String pressure="";
        String humid="";
        String wspeed="";
        String wdeg="";
        String cweather="";
        String Appid = "67746b7c3b4542644cde56475ee0cda1";
        String queryWeather = "http://api.openweathermap.org/data/2.5/weather?q=";
        String queryDummyKey = "&appid=" + Appid;

        OpenWeatherMapTask(String cityName){  // TextView tvResult){
            this.cityName = cityName;
           // this.tvResult = tvResult;
        }

        public void setAllData()
        {
            placeTv.setText(ourplace);
            latlonTv.setText(ourcoords);
            mainTempTv.setText(maintemp);
            minmaxTv.setText(minmax);
            pressTv.setText(pressure);
            humidityTv.setText(humid);
            weatherTv.setText(cweather);
            speedTv.setText(wspeed);
            degTv.setText(wdeg);
            switch (cweather){
                case "Rain":
                    weatherimg.setImageResource(R.drawable.rain_icon);
                    break;
                case "Clear":
                    weatherimg.setImageResource(R.drawable.clear_icon);
                    break;
                case "Thunderstorm":
                    weatherimg.setImageResource(R.drawable.thunderstorm_icon);
                    break;
                case "Mist":
                    weatherimg.setImageResource(R.drawable.mist);
                    break;
                case "Snow":
                    weatherimg.setImageResource(R.drawable.snow_icon);
                    break;
                case "Clouds":
                    weatherimg.setImageResource(R.drawable.cloudy_icon);
                    break;
                case "Haze":
                    weatherimg.setImageResource(R.drawable.haze_icon);
                    break;
                case "Drizzle":
                    weatherimg.setImageResource(R.drawable.drizzle_icon);
                    break;
                default:
                    weatherimg.setImageResource(R.drawable.clear_icon);
                    break;
            }
        }


        @Override
        protected String  doInBackground(Void... params) {
            String queryReturn;

            String query = null;
            try {
                query = queryWeather + URLEncoder.encode(cityName, "UTF-8") + queryDummyKey;
                queryReturn = sendQuery(query);
                ParseJSON(queryReturn);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                queryReturn = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                queryReturn = e.getMessage();
            }

            return null;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Please Wait");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
           // tvResult.setText(s);
            setAllData();
        }
        public String formatTemp(double temp) {

                temp = temp - 273.15;
            return Integer.toString(((int) temp));
        }

        private String sendQuery(String query) throws IOException {
            String result = "";

            URL searchURL = new URL(query);

            HttpURLConnection httpURLConnection = (HttpURLConnection)searchURL.openConnection();
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader,
                        8192);

                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }

                bufferedReader.close();
            }

            return result;
        }
        private void ParseJSON(String json){

            String mist="mist";
            String haze="haze";
            String clouds="clouds";
            String rain="rain";
            String thunderstorm="thunderstorm";
            String drizzle="drizzle";
            String snow="snow";
            String clear="clear";


            try {
                JSONObject JsonObject = new JSONObject(json);
                String cod = jsonHelperGetString(JsonObject, "cod");

                if(cod != null){
                    if(cod.equals("200"))
                    {

                        ourplace += jsonHelperGetString(JsonObject, "name") +", ";
                        JSONObject sys = jsonHelperGetJSONObject(JsonObject, "sys");
                        if(sys != null){
                            ourplace += jsonHelperGetString(sys, "country") ;

                        }

                        JSONObject coord = jsonHelperGetJSONObject(JsonObject, "coord");
                        if(coord != null){
                            String lon = jsonHelperGetString(coord, "lon");
                            String lat = jsonHelperGetString(coord, "lat");
                            ourcoords += "Latitude: " + lat + "  ";
                            ourcoords += "Longitude: " + lon ;
                        }

                        JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
                        if(weather != null){
                            for(int i=0; i<weather.length(); i++){
                                JSONObject thisWeather = weather.getJSONObject(i);
                                cweather += jsonHelperGetString(thisWeather, "main");

                                String desc=jsonHelperGetString(thisWeather, "main");

                                //Define sound URI
                                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                if(desc.toLowerCase().indexOf(thunderstorm.toLowerCase()) != -1)
                                {
                                    String[] events = new String[6];

                                    events[0] = "It's Stormy outside !";
                                    events[1] = "It's better to stay at home.";

                                    Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.thunderstorm);

                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity()).setAutoCancel(true)
                                            .setContentTitle("Thunderstorm")
                                            .setSmallIcon(R.drawable.image3).setLargeIcon(icon1)
                                            .setVibrate(new long[] { 1000, 1000 }).setSound(soundUri)
                                            .setContentText("It's Stormy Outside !");

                                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                                    // Sets a title for the Inbox style big view
                                    inboxStyle.setBigContentTitle("ThunderStorm");

                                    // Moves events into the big view
                                    for (int j = 0; j < events.length; j++) {

                                        inboxStyle.addLine(events[j]);
                                    }
                                    // Moves the big view style object into the notification object.
                                    mBuilder.setStyle(inboxStyle);

                                    // Creates an explicit intent for an Activity in your app
                                    Intent resultIntent = new Intent(getContext(), ResultActivity.class);

                                    // The stack builder object will contain an artificial back stack for
                                    // the
                                    // started Activity.
                                    // This ensures that navigating backward from the Activity leads out of
                                    // your application to the Home screen.
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

                                    // Adds the back stack for the Intent (but not the Intent itself)
                                    stackBuilder.addParentStack(getActivity());

                                    // Adds the Intent that starts the Activity to the top of the stack
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(resultPendingIntent);

                                    NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                                    // mId allows you to update the notification later on.
                                    mNotificationManager.notify(100, mBuilder.build());
                                     toSpeak = "Stay at Home, clouds are angry with you, Actually a thunderstorm";

                                }

                                if(desc.toLowerCase().indexOf(drizzle.toLowerCase()) != -1)
                                {
                                    String[] events = new String[6];

                                    events[0] = "It's Drizzling outside !";
                                    events[1] = "Take an umbrella with you.";

                                    Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.drizzle);

                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity()).setAutoCancel(true)
                                            .setContentTitle("Drizzling")
                                            .setSmallIcon(R.drawable.image3).setLargeIcon(icon1)
                                            .setVibrate(new long[] { 1000, 1000 }).setSound(soundUri)
                                            .setContentText("It's Drizzling Outside !");

                                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                                    // Sets a title for the Inbox style big view
                                    inboxStyle.setBigContentTitle("Drizzling");

                                    // Moves events into the big view
                                    for (int j = 0; j < events.length; j++) {

                                        inboxStyle.addLine(events[j]);
                                    }
                                    // Moves the big view style object into the notification object.
                                    mBuilder.setStyle(inboxStyle);

                                    // Creates an explicit intent for an Activity in your app
                                    Intent resultIntent = new Intent(getActivity(), ResultActivity.class);

                                    // The stack builder object will contain an artificial back stack for
                                    // the
                                    // started Activity.
                                    // This ensures that navigating backward from the Activity leads out of
                                    // your application to the Home screen.
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

                                    // Adds the back stack for the Intent (but not the Intent itself)
                                    stackBuilder.addParentStack(getActivity());

                                    // Adds the Intent that starts the Activity to the top of the stack
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(resultPendingIntent);

                                    NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                                    // mId allows you to update the notification later on.
                                    mNotificationManager.notify(100, mBuilder.build());
                                   toSpeak = "It might rain,pls take an umbrella with u and pick your clothes from outside if you left them for drying !";

                                }

                                if(desc.toLowerCase().indexOf(snow.toLowerCase()) != -1)
                                {
                                    String[] events = new String[6];

                                    events[0] = "It's Snowing outside !";
                                    events[1] = "Take an umbrella with you.";
                                    events[2] = "Wear a heavy jacket/coat.";

                                    Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.snow);

                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity()).setAutoCancel(true)
                                            .setContentTitle("Snowing")
                                            .setSmallIcon(R.drawable.image3).setLargeIcon(icon1)
                                            .setVibrate(new long[] { 1000, 1000 }).setSound(soundUri)
                                            .setContentText("It's Snowing Outside !");

                                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                                    // Sets a title for the Inbox style big view
                                    inboxStyle.setBigContentTitle("Snowing");

                                    // Moves events into the big view
                                    for (int j = 0; j < events.length; j++) {

                                        inboxStyle.addLine(events[j]);
                                    }
                                    // Moves the big view style object into the notification object.
                                    mBuilder.setStyle(inboxStyle);

                                    // Creates an explicit intent for an Activity in your app
                                    Intent resultIntent = new Intent(getActivity(), ResultActivity.class);

                                    // The stack builder object will contain an artificial back stack for
                                    // the
                                    // started Activity.
                                    // This ensures that navigating backward from the Activity leads out of
                                    // your application to the Home screen.
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

                                    // Adds the back stack for the Intent (but not the Intent itself)
                                    stackBuilder.addParentStack(getActivity());

                                    // Adds the Intent that starts the Activity to the top of the stack
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(resultPendingIntent);

                                    NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                                    // mId allows you to update the notification later on.
                                    mNotificationManager.notify(100, mBuilder.build());
                                     toSpeak = "It's snowing outside,do wear a heavy jacket !";

                                }

                                if(desc.toLowerCase().indexOf(clear.toLowerCase()) != -1)
                                {
                                    String[] events = new String[6];

                                    events[0] = "It's Clear outside !";
                                    events[1] = "Go out and enjoy the weather !";

                                    Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.sunny);

                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity()).setAutoCancel(true)
                                            .setContentTitle("Clear")
                                            .setSmallIcon(R.drawable.image3).setLargeIcon(icon1)
                                            .setVibrate(new long[] { 1000, 1000 }).setSound(soundUri)
                                            .setContentText("It's clear Outside !");

                                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                                    // Sets a title for the Inbox style big view
                                    inboxStyle.setBigContentTitle("Clear");

                                    // Moves events into the big view
                                    for (int j = 0; j < events.length; j++) {

                                        inboxStyle.addLine(events[j]);
                                    }
                                    // Moves the big view style object into the notification object.
                                    mBuilder.setStyle(inboxStyle);

                                    // Creates an explicit intent for an Activity in your app
                                    Intent resultIntent = new Intent(getActivity(), ResultActivity.class);

                                    // The stack builder object will contain an artificial back stack for
                                    // the
                                    // started Activity.
                                    // This ensures that navigating backward from the Activity leads out of
                                    // your application to the Home screen.
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

                                    // Adds the back stack for the Intent (but not the Intent itself)
                                    stackBuilder.addParentStack(getActivity());

                                    // Adds the Intent that starts the Activity to the top of the stack
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(resultPendingIntent);

                                    NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                                    // mId allows you to update the notification later on.
                                    mNotificationManager.notify(100, mBuilder.build());
                                    toSpeak = "Wow,the weather is clear outside !";

                                }

                                if(desc.toLowerCase().indexOf(haze.toLowerCase()) != -1)
                                {
                                    String[] events = new String[6];

                                    events[0] = "It's Hazy outside !";

                                    Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.hazy);

                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity()).setAutoCancel(true)
                                            .setContentTitle("Hazy")
                                            .setSmallIcon(R.drawable.image3).setLargeIcon(icon1)
                                            .setVibrate(new long[] { 1000, 1000 }).setSound(soundUri)
                                            .setContentText("It's Hazy Outside !");

                                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                                    // Sets a title for the Inbox style big view
                                    inboxStyle.setBigContentTitle("Hazy");

                                    // Moves events into the big view
                                    for (int j = 0; j < events.length; j++) {

                                        inboxStyle.addLine(events[j]);
                                    }
                                    // Moves the big view style object into the notification object.
                                    mBuilder.setStyle(inboxStyle);

                                    // Creates an explicit intent for an Activity in your app
                                    Intent resultIntent = new Intent(getActivity(), ResultActivity.class);

                                    // The stack builder object will contain an artificial back stack for
                                    // the
                                    // started Activity.
                                    // This ensures that navigating backward from the Activity leads out of
                                    // your application to the Home screen.
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

                                    // Adds the back stack for the Intent (but not the Intent itself)
                                    stackBuilder.addParentStack(getActivity());

                                    // Adds the Intent that starts the Activity to the top of the stack
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(resultPendingIntent);

                                    NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                                    // mId allows you to update the notification later on.
                                    mNotificationManager.notify(100, mBuilder.build());
                                     toSpeak = "It's Hazy today !";

                                }

                                if(desc.toLowerCase().indexOf(mist.toLowerCase()) != -1)
                                {
                                    String[] events = new String[6];

                                    events[0] = "It's Foggy outside !";

                                    Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.fog);

                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity()).setAutoCancel(true)
                                            .setContentTitle("Mist")
                                            .setSmallIcon(R.drawable.image3).setLargeIcon(icon1)
                                            .setVibrate(new long[] { 1000, 1000 }).setSound(soundUri)
                                            .setContentText("It's Foggy outside !");

                                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                                    // Sets a title for the Inbox style big view
                                    inboxStyle.setBigContentTitle("Mist");

                                    // Moves events into the big view
                                    for (int j = 0; j < events.length; j++) {

                                        inboxStyle.addLine(events[j]);
                                    }
                                    // Moves the big view style object into the notification object.
                                    mBuilder.setStyle(inboxStyle);

                                    // Creates an explicit intent for an Activity in your app
                                    Intent resultIntent = new Intent(getActivity(), ResultActivity.class);

                                    // The stack builder object will contain an artificial back stack for
                                    // the
                                    // started Activity.
                                    // This ensures that navigating backward from the Activity leads out of
                                    // your application to the Home screen.
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

                                    // Adds the back stack for the Intent (but not the Intent itself)
                                    stackBuilder.addParentStack(getActivity());

                                    // Adds the Intent that starts the Activity to the top of the stack
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(resultPendingIntent);

                                    NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                                    // mId allows you to update the notification later on.
                                    mNotificationManager.notify(100, mBuilder.build());
                                     toSpeak = "It's Foggy outside ! So don't drive";

                                }

                                else if(desc.toLowerCase().indexOf(clouds.toLowerCase()) != -1)
                                {
                                    String[] events = new String[6];

                                    events[0] = "It's Cloudy outside !";
                                    events[1] = "It might rain today. ";
                                    events[2] = "Take an umbrella with u.";

                                    Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.cloudy);

                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity()).setAutoCancel(true)
                                            .setContentTitle("Cloudy")
                                            .setSmallIcon(R.drawable.image3).setLargeIcon(icon1)
                                            .setVibrate(new long[] { 1000, 1000 }).setSound(soundUri)
                                            .setContentText("It's Cloudy Outside !");

                                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                                    // Sets a title for the Inbox style big view
                                    inboxStyle.setBigContentTitle("Cloudy");

                                    // Moves events into the big view
                                    for (int j = 0; j < events.length; j++) {

                                        inboxStyle.addLine(events[j]);
                                    }
                                    // Moves the big view style object into the notification object.
                                    mBuilder.setStyle(inboxStyle);

                                    // Creates an explicit intent for an Activity in your app
                                    Intent resultIntent = new Intent(getActivity(), ResultActivity.class);

                                    // The stack builder object will contain an artificial back stack for
                                    // the
                                    // started Activity.
                                    // This ensures that navigating backward from the Activity leads out of
                                    // your application to the Home screen.
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

                                    // Adds the back stack for the Intent (but not the Intent itself)
                                    stackBuilder.addParentStack(getActivity());

                                    // Adds the Intent that starts the Activity to the top of the stack
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(resultPendingIntent);

                                    NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                                    // mId allows you to update the notification later on.
                                    mNotificationManager.notify(100, mBuilder.build());
                                    toSpeak = "It's cloudy";
                                }

                                else if(desc.toLowerCase().indexOf(rain.toLowerCase()) != -1)
                                {
                                    String[] events = new String[6];

                                    events[0] = "It's Raining outside !";
                                    events[1] = "Pls pick ur clothes from outside if u left";
                                    events[2] = "them to dry.";

                                    Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                                            R.drawable.showers);

                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity()).setAutoCancel(true)
                                            .setContentTitle("Rain")
                                            .setSmallIcon(R.drawable.image3).setLargeIcon(icon1)
                                            .setVibrate(new long[] { 1000, 1000 }).setSound(soundUri)
                                            .setContentText("It's Rainy Outside !");

                                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                                    // Sets a title for the Inbox style big view
                                    inboxStyle.setBigContentTitle("Raining");

                                    // Moves events into the big view
                                    for (int j = 0; j < events.length; j++) {

                                        inboxStyle.addLine(events[j]);
                                    }
                                    // Moves the big view style object into the notification object.
                                    mBuilder.setStyle(inboxStyle);

                                    // Creates an explicit intent for an Activity in your app
                                    Intent resultIntent = new Intent(getActivity(), ResultActivity.class);

                                    // The stack builder object will contain an artificial back stack for
                                    // the
                                    // started Activity.
                                    // This ensures that navigating backward from the Activity leads out of
                                    // your application to the Home screen.
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

                                    // Adds the back stack for the Intent (but not the Intent itself)
                                    stackBuilder.addParentStack(getActivity());

                                    // Adds the Intent that starts the Activity to the top of the stack
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(resultPendingIntent);

                                    NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                                    // mId allows you to update the notification later on.
                                    mNotificationManager.notify(100, mBuilder.build());
                                   toSpeak = "Please take an umbrella with u when u go out and pick your clothes from outside if you left them for drying !";

                                }

                            }

                        }

                        JSONObject main = jsonHelperGetJSONObject(JsonObject, "main");
                        if(main != null){
                            maintemp += formatTemp(Double.parseDouble(jsonHelperGetString(main, "temp"))) +" \u00B0C";
                            pressure += jsonHelperGetString(main, "pressure") + "MB";
                            humid += jsonHelperGetString(main, "humidity") + "%";
                            minmax +=formatTemp(Double.parseDouble(jsonHelperGetString(main, "temp_min"))) + " \u00B0C | " +formatTemp(Double.parseDouble(jsonHelperGetString(main, "temp_max")))+" \u00B0C";
                        }

                        JSONObject wind = jsonHelperGetJSONObject(JsonObject, "wind");
                        if(wind != null){
                            wspeed += "Speed: " + jsonHelperGetString(wind, "speed") + "m/s";
                            wdeg += "Degrees: " + jsonHelperGetString(wind, "deg");
                        }

                    }else if(cod.equals("404")){
                        String message = jsonHelperGetString(JsonObject, "message");
                        ourplace += "cod 404: " + message;
                    }
                }else{
                    ourplace += "cod == null\n";
                }

            } catch (JSONException e) {
                e.printStackTrace();
                ourplace += e.getMessage();
            }

            //return jsonResult;
        }


        private String jsonHelperGetString(JSONObject obj, String k)
        {
            String v = null;
            try
            {
                v = obj.getString(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return v;
        }

        private JSONObject jsonHelperGetJSONObject(JSONObject obj, String k){
            JSONObject o = null;

            try {
                o = obj.getJSONObject(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return o;
        }

        private JSONArray jsonHelperGetJSONArray(JSONObject obj, String k){
            JSONArray a = null;

            try {
                a = obj.getJSONArray(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return a;
        }
    }
    public static String getToSpeak()
    {
        return toSpeak;
    }

}
