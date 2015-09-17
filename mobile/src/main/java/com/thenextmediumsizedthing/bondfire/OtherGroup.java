package com.thenextmediumsizedthing.bondfire;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class OtherGroup extends Activity {

    private Integer groupid;
    private ImageView photo;
    private TextView name;
    private TextView plan;
    private TextView blurb;
    private TextView interest;
    private Button back;
    private TextView distance;
    private RatingBar ratingBar;

    JSONParser jsonParser = new JSONParser();
    JSONParser jsonParser2 = new JSONParser();
    private static String urlGetGroup = "http://cgarcia.site.nfoservers.com/phpmyadmin/get_group.php";
    private static String urlGetGroupAverage = "http://cgarcia.site.nfoservers.com/phpmyadmin/get_average_group_rating.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_group);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setBackButton();

        Bundle b = getIntent().getExtras();
        groupid = b.getInt("groupid");

        name = (TextView) findViewById(R.id.group);
        plan = (TextView) findViewById(R.id.plan_text);
        plan.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        blurb = (TextView) findViewById(R.id.blurb_text);
        interest = (TextView) findViewById(R.id.tags);
        distance = (TextView) findViewById(R.id.distance);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        photo = (ImageView)findViewById(R.id.group_image);
        ratingBar.setStepSize((float) 0.5);

        new LoadGroup().run();
        new LoadRating().run();

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.rgb(255, 99, 71), PorterDuff.Mode.SRC_ATOP);

    }

    private void setBackButton() {
        back = (Button)findViewById(R.id.backarrow);
        back.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_own_existing_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickEdit(View v){
        Intent edit_button = new Intent(this, edit_group.class);
        startActivity(edit_button);
    }

    public class LoadGroup extends Thread {

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
//                            Log.d("interest: ", interestStrings[finalI]);
                        System.out.println(groupid);
                        params.add(new BasicNameValuePair("groupid", groupid.toString()));

                        JSONObject json = jsonParser.makeHttpRequest(
                                urlGetGroup, "GET", params);
//                            Log.d("json: ", String.valueOf(json));
                        Log.d("json", String.valueOf(json));
                        JSONArray groupObj = json
                                .getJSONArray("group");
                        JSONObject jsonInfo = groupObj.getJSONObject(0);
                        name.setText(jsonInfo.getString("name"));
                        plan.setText(jsonInfo.getString("plan"));
                        blurb.setText(jsonInfo.getString("blurb"));
                        interest.setText(jsonInfo.getString("interest"));
                        Location locationB = new Location("point B");
                        locationB.setLatitude(jsonInfo.getDouble("latitude"));
                        locationB.setLongitude(jsonInfo.getDouble("longitude"));
                        double dist = MainActivity.locationA.distanceTo(locationB);
                        dist = dist * 0.000621371;
                        distance.setText(String.format("%.2f", dist)+" mi.");
                        photo.setImageBitmap(BitmapFactory.decodeFile("/storage/emulated/0/"+groupid.toString()+".jpg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                   }
            });
        }

        public void main(String args[]) {
            (new LoadGroup()).start();
        }

    }

    public class LoadRating extends Thread {

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                        params2.add(new BasicNameValuePair("groupid", groupid.toString()));

                        JSONObject json2 = jsonParser2.makeHttpRequest(
                                urlGetGroupAverage, "GET", params2);
                        Log.d("json2", String.valueOf(json2));
                        if (json2.getString("average") == "null") {
                            ratingBar.setRating(Float.valueOf(0));
                        } else {
                            ratingBar.setRating(Float.valueOf(json2.getString("average")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void main(String args[]) {
            (new LoadGroup()).start();
        }

    }

}
