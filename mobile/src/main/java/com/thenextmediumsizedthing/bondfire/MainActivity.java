package com.thenextmediumsizedthing.bondfire;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {

    private Button chat;
    private Intent chatIntent;
    private ListView mListView;
    private int numSelected;
    private ImageView startImg;
    private ImageView profile;
    private Intent profileIntent;
    private Intent startIntent;
    private TextView coverLabel;
    private EditText search;
    public static  Location locationA;
    private LocationListener locationListener;
    private LocationManager locationManager;
    public static Integer userid;

    JSONParser jsonParser = new JSONParser();
    ArrayList<Integer> interestCounts = new ArrayList<Integer>();
    private ArrayList<InterestCard> interestCards = new ArrayList<InterestCard>();
    private ArrayList <HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
    private String urlGroupCount = "http://cgarcia.site.nfoservers.com/phpmyadmin/get_count_group.php";
    private String[] interestStrings = { "Sightseeing", "Chilling", "Nature", "Concerts", "Sports", "Food", "Bar-Hop", "Shopping"};
    private ArrayList<String> interests = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        interestCounts.add(0);
        interestCounts.add(0);
        interestCounts.add(0);
        interestCounts.add(0);
        interestCounts.add(0);
        interestCounts.add(0);
        interestCounts.add(0);
        interestCounts.add(0);
        userid = 12;

        chatIntent = new Intent(MainActivity.this,ChatActivity.class);
        setChatButton();
        profileIntent = new Intent(MainActivity.this,User_profile.class);
        setProfileButton();

        search = (EditText)findViewById(R.id.search);
        search.setEnabled(false);

        // Loading counts in Background Thread
        new LoadGroupCounts().execute();

        startImg = (ImageView)findViewById(R.id.start_adventure);
        startImg.setAlpha(0.0f);//default have this invisible
        startImg.setEnabled(false);

        numSelected = 0;

        //Retrieve the listView
        mListView = (ListView)findViewById(R.id.interestList);

        coverLabel = (TextView) findViewById(R.id.selectedLabel);
        coverLabel.setVisibility(View.INVISIBLE);

        //set an event listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get back our Hashmap
//                HashMap<String, String> map = (HashMap<String, String>)mListView.getItemAtPosition(position);
                //int imgID = Integer.parseInt(map.get("img"));
                System.out.println(position);
                InterestCard i = interestCards.get(position);

                if (i.isSelected() && numSelected <= 8) { //toggle selected and returns boolean on current (post-click) state
                    view.findViewById(R.id.img).setAlpha(0.6f);
                    //change cover to reflect selection
                    interests.add(interestStrings[position]);

                    if (numSelected == 0) {//then this is first instance of selecting something. Get rid of DEFAULT "selected all" textview label
                        coverLabel.setAlpha(0.0f);
                        startImg.setAlpha(1.0f); //show startAdventure image
                        startImg.setEnabled(true);
                    }
                    numSelected++;

                } else {//it was de-selected
                    view.findViewById(R.id.img).setAlpha(1);//return to full opacity
                    for (int j = 0; j < interests.size(); j++) {
                        String tempName = interests.get(j);
                        if (tempName.equals(interestStrings[position])) {
                            interests.remove(i);
                        }
                    }
                    interests.remove(interestStrings[position]);
                    numSelected--;
                    if (numSelected == 0) {//then there are currently NO items selected. Show DEFAULT "selected all" textview label
                        coverLabel.setAlpha(1.0f);
                        startImg.setAlpha(0.0f);//hide startAdventure image
                        startImg.setEnabled(false);
                    }
                }
                System.out.println(interests);

            }
        });
        setStartButton();
        setReloadButton();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, locationListener);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationA = new Location("point A");
        locationA.setLatitude(lastLocation.getLatitude());
        locationA.setLongitude(lastLocation.getLongitude());
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            locationA = new Location("point A");
            locationA.setLatitude(location.getLatitude());
            locationA.setLongitude(location.getLongitude());
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

    private void setProfileButton() {
        profile = (ImageView)findViewById(R.id.profileButton);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(profileIntent);
            }
        });
    }

    private void setReloadButton() {
        TextView bondfire = (TextView)findViewById(R.id.homeText);
        bondfire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadGroupCounts().execute();
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

    private void setStartButton() {
        startImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Selected On Home", String.valueOf(interests));
                startIntent = new Intent();
                startIntent.setClass(getApplicationContext(), BrowseGroupsActivity.class);
                startIntent.putExtra("interests", interests);
                startActivity(startIntent);
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

    /**
     * Background Async Task to Load all interest counts by making HTTP Request
     * */
    class LoadGroupCounts extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting all counts from url
         * */
        protected String doInBackground(String... args) {
            // updating UI from Background Thread
            for (int i = 0; i <= 7; i++) {
                final int finalI = i;
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {

                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            Log.d("interest: ", interestStrings[finalI]);
                            params.add(new BasicNameValuePair("interest", interestStrings[finalI]));

                            JSONObject json = jsonParser.makeHttpRequest(
                                    urlGroupCount, "GET", params);
                            Log.d("json: ", String.valueOf(json));
                            System.out.println(interestCounts.get(finalI));
                            interestCounts.set(finalI, json.getInt("count"));
                            System.out.println(interestCounts.get(finalI));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            return null;
        }

        /**
         * After loading counts, insert into cards
         * **/
        protected void onPostExecute(String file_url) {
            InterestCard sightseeing = new InterestCard(1, "Sightseeing", interestCounts.get(0));
            InterestCard chilling = new InterestCard(2, "Chilling", interestCounts.get(1));
            InterestCard nature = new InterestCard(3, "Nature", interestCounts.get(2));
            InterestCard concerts = new InterestCard(4,"Concerts", interestCounts.get(3));
            InterestCard sports = new InterestCard(5,"Sports", interestCounts.get(4));
            InterestCard food = new InterestCard(6,"Food", interestCounts.get(5));
            InterestCard barhop = new InterestCard(7,"Bar Hopping", interestCounts.get(6));
            InterestCard shopping = new InterestCard(8,"Shopping", interestCounts.get(7));
            Log.d("refresh", String.valueOf(interestCounts.get(3)));
            interestCards = new ArrayList<InterestCard>();
//            if (interestCards.size() == 0) {
                interestCards.add(sightseeing);
                interestCards.add(chilling);
                interestCards.add(nature);
                interestCards.add(concerts);
                interestCards.add(sports);
                interestCards.add(food);
                interestCards.add(barhop);
                interestCards.add(shopping);
//            } else {
//                interestCards.set(0, sightseeing);
//                interestCards.set(1, chilling);
//                interestCards.set(2, nature);
//                interestCards.set(3, concerts);
//                interestCards.set(4, sports);
//                interestCards.set(5, food);
//                interestCards.set(6, barhop);
//                interestCards.set(7, shopping);
//            }

            //arraylist of background image R.ids
            ArrayList<Integer> backgrounds = new ArrayList<Integer>();
            backgrounds.add(R.drawable.sightseeing);
            backgrounds.add(R.drawable.chill);
            backgrounds.add(R.drawable.nature);
            backgrounds.add(R.drawable.concert);
            backgrounds.add(R.drawable.sports2);
            backgrounds.add(R.drawable.food);
            backgrounds.add(R.drawable.barhopping);
            backgrounds.add(R.drawable.shopping);
            listItem = new ArrayList<HashMap<String, String>>();
            //each listItem is an arraylist that contains a hashmap. listItems to be used to fill listView.
            HashMap<String, String> map = new HashMap<String, String>();
            for (int i = 0; i <= 7; i++) {
                map = new HashMap<>();//make a new hashmap each time..seems like everyone does this
                InterestCard ic = interestCards.get(i);//current interest card
                map.put("title", ic.getTitle());
                map.put("labelActiveGroups", ic.getLabelActiveGroups());
                map.put("img", Integer.toString(backgrounds.get(i)));
                listItem.add(map);
            }
            String[] from = {"img", "title", "labelActiveGroups"};

            //IDs used by ListView layout
            int[] to = {R.id.img, R.id.title, R.id.labelActiveGroups};

            //ListAdapter listAdapter = new ListAdapter(this, listItem);
            SimpleAdapter sa = new SimpleAdapter(getApplicationContext(), listItem, R.layout.interest_element, from, to);
            mListView.setAdapter(sa);
        }
    }
}
