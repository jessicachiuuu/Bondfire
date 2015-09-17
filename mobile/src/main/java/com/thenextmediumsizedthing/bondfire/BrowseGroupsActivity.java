package com.thenextmediumsizedthing.bondfire;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowseGroupsActivity extends Activity {

    private Button chat;
    private Intent chatIntent;
    private ListView mListView;
    private ImageView profile;
    private Intent profileIntent;
    private ArrayList<String> interests;
    private ProgressDialog pDialog;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;
    private Intent viewGroupIntent;

    JSONParser jsonParser = new JSONParser();
    public final ArrayList<GroupCard> GroupCards = new ArrayList<GroupCard>();
    public final ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
    private String urlGroups = "http://cgarcia.site.nfoservers.com/phpmyadmin/get_all_groups.php";
    private final String[] interestStrings = { "Sightseeing", "Chilling", "Nature", "Concerts", "Sports", "Food", "Bar-Hop", "Shopping"};
    private final ArrayList<String> names = new ArrayList<String>();
    private final ArrayList<Integer> groupids = new ArrayList<Integer>();
    private final ArrayList<String> photos = new ArrayList<String>();
    private final ArrayList<Double> distances = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationListener = new MyLocationListener();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, locationListener);
//        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        locationA = new Location("point A");
//        locationA.setLatitude(lastLocation.getLatitude());
//        locationA.setLongitude(lastLocation.getLongitude());
        System.out.println(lastLocation);
        Bundle b = getIntent().getExtras();
        interests = b.getStringArrayList("interests");
        Log.d("Selected Interests", String.valueOf(interests));
        chatIntent = new Intent(BrowseGroupsActivity.this,ChatActivity.class);
        setChatButton();
        profileIntent = new Intent(BrowseGroupsActivity.this,User_profile.class);
        setProfileButton();


        //Retrieve the listView
        mListView = (ListView)findViewById(R.id.interestList);

        // Loading counts in Background Thread
        new LoadGroupsFaster().run();

        //set an event listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                Log.d("Selected On Home", String.valueOf(interests));
                viewGroupIntent = new Intent();
                viewGroupIntent.setClass(getApplicationContext(), OtherGroup.class);
                viewGroupIntent.putExtra("groupid", groupids.get(position));
                startActivity(viewGroupIntent);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        MainActivity.locationA.setLatitude(lastLocation.getLatitude());
        MainActivity.locationA.setLongitude(lastLocation.getLongitude());
 }

    private void setProfileButton() {
        profile = (ImageView)findViewById(R.id.profileButton);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(profileIntent);
            }
        });
    }

    private void setChatButton() {
        chat = (Button)findViewById(R.id.chatBtn);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(chatIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public class LoadGroupsFaster extends Thread {

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < interests.size(); i++) {
                        try {
                            System.out.println("Loading count for " + interests.get(i));
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            Log.d("interest: ", (String) interests.get(i));
                            params.add(new BasicNameValuePair("interest", (String) interests.get(i)));
                            JSONObject json = jsonParser.makeHttpRequest(
                                    urlGroups, "GET", params);
                            Log.d("json: ", String.valueOf(json));
                            JSONArray groups = json.getJSONArray("groups");
                            for (int j = 0; j < groups.length(); j++) {
                                JSONObject c = groups.getJSONObject(j);
                                names.add(c.getString("name"));
                                groupids.add(c.getInt("groupid"));
                                photos.add(c.getString("photo"));
                                Log.d("locationA", String.valueOf(MainActivity.locationA));
                                Location locationB = new Location("point B");
                                locationB.setLatitude(c.getDouble("latitude"));
                                locationB.setLongitude(c.getDouble("longitude"));
                                Log.d("locationB", String.valueOf(locationB));
                                double dist = MainActivity.locationA.distanceTo(locationB);
                                dist = dist * 0.000621371;
                                distances.add(dist);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(distances);
                    System.out.println(groupids);
                    System.out.println(names);
                    ArrayList<String> BGS = new ArrayList<String>();
                    keySort(distances, distances, groupids, names, photos);
                    Log.d("photos", String.valueOf(photos));
                    for (int i = 0; i < groupids.size(); i++) {
                        BGS.add(photos.get(i));
                        GroupCards.add(new GroupCard(groupids.get(i), names.get(i), distances.get(i)));
                    }

                    //each listItem is an arraylist that contains a hashmap. listItems to be used to fill listView.
                    HashMap<String, String> map = new HashMap<String, String>();
                    for (int i = 0; i < groupids.size(); i++) {
                        File file = null;
                        try {
                            System.out.println("loading photo");
                            URL url = new URL(photos.get(i));
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            Bitmap myBitmap = BitmapFactory.decodeStream(input, null, options);
                            file = new File(Environment.getExternalStorageDirectory(), URLUtil.guessFileName(photos.get(i), null, null));
                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(file);
                                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                                // PNG is a lossless format, the compression factor (100) is ignored
                                myBitmap.recycle();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (out != null) {
                                        out.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            System.out.println(file.getAbsolutePath());
                            out.close();
                        } catch (IOException e) {
                        }
                        map = new HashMap<>();//make a new hashmap each time..seems like everyone does this
                        GroupCard ic = GroupCards.get(i);//current interest card
                        map.put("title", ic.getName());
                        map.put("labelDistance", ic.getLabelDistance());
                        map.put("img", file.getAbsolutePath());
                        listItem.add(map);
                    }
                    String[] from = {"img", "title", "labelDistance"};

                    //IDs used by ListView layout
                    int[] to = {R.id.img, R.id.title, R.id.labelDistance};

                    //ListAdapter listAdapter = new ListAdapter(this, listItem);
                    SimpleAdapter sa = new SimpleAdapter(getApplicationContext(), listItem, R.layout.group_element, from, to);
                    mListView.setAdapter(sa);
                }
            });
        }

        public void main(String args[]) {
            (new LoadGroupsFaster()).start();
        }

    }

    public static <T extends Comparable<T>> void keySort(
            final List<T> key, List<?>... lists){
        // Create a List of indices
        List<Integer> indices = new ArrayList<Integer>();
        for(int i = 0; i < key.size(); i++)
            indices.add(i);

        // Sort the indices list based on the key
        Collections.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(Integer i, Integer j) {
                return key.get(i).compareTo(key.get(j));
            }
        });

        // Create a mapping that allows sorting of the List by N swaps.
        Map<Integer,Integer> swapMap = new HashMap<Integer, Integer>(indices.size());

        // Only swaps can be used b/c we cannot create a new List of type <?>
        for(int i = 0; i < indices.size(); i++){
            int k = indices.get(i);
            while(swapMap.containsKey(k))
                k = swapMap.get(k);

            swapMap.put(i, k);
        }

        // for each list, swap elements to sort according to key list
        for(Map.Entry<Integer, Integer> e : swapMap.entrySet())
            for(List<?> list : lists)
                Collections.swap(list, e.getKey(), e.getValue());
    }



    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
//            locationA = new Location("point A");
            MainActivity.locationA.setLatitude(location.getLatitude());
            MainActivity.locationA.setLongitude(location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
