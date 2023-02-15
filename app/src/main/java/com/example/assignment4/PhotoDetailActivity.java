package com.example.assignment4;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PhotoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        TextView tvCurrentLocation = findViewById(R.id.tvCurrentLocation);
        TextView tvRepName = (TextView) findViewById(R.id.tvRepName);
        TextView tvOffice = (TextView) findViewById(R.id.tvOffice);
        ImageView imgParty = (ImageView) findViewById(R.id.imgParty);
        ImageView imgRep = (ImageView) findViewById(R.id.imgRep);
        LinearLayout photoDetailActivityLayout = (LinearLayout) findViewById(R.id.photoDetailActivityLayout);
        if (getIntent().hasExtra("officeTitle")) {
            String officeTitle = getIntent().getStringExtra("officeTitle");
            tvOffice.setText(officeTitle);
        }
        if (getIntent().hasExtra("currentLocation")) {
            String currentLocation = getIntent().getStringExtra("currentLocation");
            tvCurrentLocation.setText(currentLocation);
        }
        if (getIntent().hasExtra("official")) {
            Official official = (Official) getIntent().getSerializableExtra("official");
            tvRepName.setText(official.name);
            if (official.party.equals("Democratic Party")) {
                imgParty.setBackgroundResource(R.drawable.dem_logo);
                photoDetailActivityLayout.setBackgroundColor(Color.BLUE);
            } else if (official.party.equals("Republican Party")) {
                photoDetailActivityLayout.setBackgroundColor(Color.RED);
                imgParty.setBackgroundResource(R.drawable.rep_logo);
            } else {
                imgParty.setVisibility(View.GONE);
                photoDetailActivityLayout.setBackgroundColor(Color.BLACK);
            }

            if (official.photoUrl != null) {
                String url = official.photoUrl.replace("http://", "https://");
                Glide.with(imgRep.getContext()).load(url).placeholder(R.drawable.brokenimage).into(imgRep);
            }
        }
    }
}