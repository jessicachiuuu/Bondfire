package com.thenextmediumsizedthing.bondfire;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;


public class User_profile extends ActionBarActivity {

    private Button back;
    private TextView name;
    private List<Review> reviews;
    private ListView listView;
    private ListAdapter adapter;
    private Button createGroupBtn;
    private Intent createGroup;
    private ToggleButton groupsTab;
    private Boolean groupsBool;
    private ToggleButton downTab;
    private Boolean downBool;
    private ToggleButton reviewsTab;
    private Boolean reviewsBool;
    private TextView groupsTitle;
    private TextView downTitle;
    private Button watchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        name = (TextView)findViewById(R.id.name);
        name.setText("Clark");
        fillReviews();
        createGroup = new Intent(User_profile.this, Create_group.class);
        setBackButton();
        setWatchButton();
        fillListView();
        setCreateGroupButton();
        setTabs();
    }

    private void fillReviews() {
        reviews = new ArrayList<Review>();
        reviews.add(new Review(4, "Pretty chill guy."));
        reviews.add(new Review(5, "He did my math HW."));
        reviews.add(new Review(4, "Had a great time with him."));
        reviews.add(new Review(5, "Pushed my friend into a lake."));
        reviews.add(new Review(0, "He pushed me into a lake."));
        reviews.add(new Review(3, "He got too drunk."));
    }

    private void setTabs() {
        groupsTab = (ToggleButton) findViewById(R.id.groups);
        groupsBool = true;
        groupsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsTab.setChecked(true);
                downTab.setChecked(false);
                reviewsTab.setChecked(false);
                groupsBool = true;
                downBool = false;
                reviewsBool = false;
                updateChildrenViews();
            }
        });

        downTab = (ToggleButton) findViewById(R.id.down);
        downBool = false;
        downTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsTab.setChecked(false);
                downTab.setChecked(true);
                reviewsTab.setChecked(false);
                groupsBool = false;
                downBool = true;
                reviewsBool = false;
                updateChildrenViews();
            }
        });
        reviewsTab = (ToggleButton) findViewById(R.id.reviews);
        reviewsBool = false;
        reviewsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsTab.setChecked(false);
                downTab.setChecked(false);
                reviewsTab.setChecked(true);
                groupsBool = false;
                downBool = false;
                reviewsBool = true;
                updateChildrenViews();
            }
        });
        groupsTitle = (TextView) findViewById(R.id.groupsTitle);
        createGroupBtn = (Button) findViewById(R.id.addGroup);
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(createGroup);
            }
        });
        downTitle = (TextView) findViewById(R.id.downTitle);
        listView = (ListView) findViewById(R.id.reviewsList);
        updateChildrenViews();
    }

    private void updateChildrenViews() {
        if (groupsBool) {
            groupsTitle.setVisibility(View.VISIBLE);
            createGroupBtn.setVisibility(View.VISIBLE);
            downTitle.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        } else if (downBool) {
            groupsTitle.setVisibility(View.GONE);
            createGroupBtn.setVisibility(View.GONE);
            downTitle.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            groupsTitle.setVisibility(View.GONE);
            createGroupBtn.setVisibility(View.GONE);
            downTitle.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void setCreateGroupButton() {
        createGroupBtn = (Button)findViewById(R.id.addGroup);
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(createGroup);
            }
        });
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

    private void setWatchButton() {
        watchBtn = (Button)findViewById(R.id.watchBtn);
        watchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notifyIntent = new Intent(getApplicationContext(), NotifyWear.class);
                Random rn = new Random();
                notifyIntent.putExtra("groupid", rn.nextInt((13 - 4) + 1) + 4);
                startService(notifyIntent);
            }
        });
    }

    private void fillListView() {
        adapter = new ProfileReviewAdapter(this,R.layout.listobject_chat,reviews);
        listView = (ListView) findViewById(R.id.reviewsList);
        System.out.println(reviews);
        if (adapter != null) {
            listView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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
}
