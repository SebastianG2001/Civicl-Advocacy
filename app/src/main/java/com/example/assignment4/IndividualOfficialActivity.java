package com.example.assignment4;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class IndividualOfficialActivity extends AppCompatActivity {
    String facebookId = "";
    String twitterId = "";
    String youtubeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_official);
        TextView tvCurrentLocation = findViewById(R.id.tvCurrentLocation);
        TextView tvRepName = (TextView) findViewById(R.id.tvRepName);
        TextView tvOffice = (TextView) findViewById(R.id.tvOffice);
        TextView tvRepParty = (TextView) findViewById(R.id.tvRepParty);
        ImageView imgTwitter = (ImageView) findViewById(R.id.imgTwitter);
        ImageView imgYoutube = (ImageView) findViewById(R.id.imgYoutube);
        ImageView imgFacebook = (ImageView) findViewById(R.id.imgFacebook);
        ImageView imgParty = (ImageView) findViewById(R.id.imgParty);
        ImageView imgRep = (ImageView) findViewById(R.id.imgRep);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        TextView tvPhone = (TextView) findViewById(R.id.tvPhone);
        TextView tvEmail = (TextView) findViewById(R.id.tvEmail);
        imgRep.setOnClickListener(
                view -> {
                    Intent intent = new Intent(this, PhotoDetailActivity.class);
                    intent.putExtra("currentLocation", tvCurrentLocation.getText());
                    if (getIntent().hasExtra("officeTitle")) {
                        String officeTitle = getIntent().getStringExtra("officeTitle");
                        tvOffice.setText(officeTitle);
                        intent.putExtra("officeTitle", officeTitle);
                    }
                    if (getIntent().hasExtra("official")) {
                        Official official = (Official) getIntent().getSerializableExtra("official");
                        intent.putExtra("official", official);
                    }
                    startActivity(intent);
                }
        );

        LinearLayout topLayout = (LinearLayout) findViewById(R.id.topLayout);
        Official official = null;
        if (getIntent().hasExtra("official")) {
            official = (Official) getIntent().getSerializableExtra("official");
            tvRepName.setText(official.name);
            tvRepParty.setText("(" + official.party + ")");
            if (official.channels != null) {
                Channel[] channels = official.channels;
                for (Channel channel : channels) {
                    if ("Facebook".equals(channel.type)) {
                        imgFacebook.setVisibility(View.VISIBLE);
                        facebookId = channel.id;
                    } else if ("Twitter".equals(channel.type)) {
                        imgTwitter.setVisibility(View.VISIBLE);
                        twitterId = channel.id;
                    } else if ("Youtube".equals(channel.type)) {
                        imgYoutube.setVisibility(View.VISIBLE);
                        youtubeId = channel.id;
                    }
                }
            } else {
                imgFacebook.setVisibility(View.INVISIBLE);
                imgTwitter.setVisibility(View.INVISIBLE);
                imgYoutube.setVisibility(View.INVISIBLE);
            }
            if (official.party.equals("Democratic Party")) {
                imgParty.setBackgroundResource(R.drawable.dem_logo);
                topLayout.setBackgroundColor(Color.BLUE);
            } else if (official.party.equals("Republican Party")) {
                topLayout.setBackgroundColor(Color.RED);
                imgParty.setBackgroundResource(R.drawable.rep_logo);
            } else {
                imgParty.setVisibility(View.GONE);
                topLayout.setBackgroundColor(Color.BLACK);
            }
            if (official.address != null) {
                String ad = official.address[0].line1 +
                        ", " +
                        official.address[0].line2 +
                        ", " +
                        official.address[0].city +
                        ", " +
                        official.address[0].state +
                        " " +
                        official.address[0].zip;

                tvAddress.setText(ad);
            }
            StringBuilder ph = new StringBuilder();
            if (official.phones != null) {
                for (String phone : official.phones)
                    ph.append(phone).append(" ");
                tvPhone.setText(ph);
            } else
                tvPhone.setText("NA");
            StringBuilder eml = new StringBuilder();
            if (official.emails != null) {
                for (String email : official.emails)
                    eml.append(email).append(" ");
                tvEmail.setText(eml);
            } else
                tvEmail.setText("NA");
            if (official.urls != null) {
                StringBuilder urls = new StringBuilder();
                for (String url : official.urls) {
                    addUrl(url);
                }
            }
            if (official.photoUrl != null) {
                String url = official.photoUrl.replace("http://", "https://");
                Glide.with(imgRep.getContext()).load(url).placeholder(R.drawable.brokenimage).into(imgRep);
            }
        }
        if (getIntent().hasExtra("officeTitle")) {
            String officeTitle = getIntent().getStringExtra("officeTitle");
            tvOffice.setText(officeTitle);
        }
        if (getIntent().hasExtra("currentLocation")) {
            String currentLocation = getIntent().getStringExtra("currentLocation");
            tvCurrentLocation.setText(currentLocation);
        }
    }

    private void addUrl(String url) {
        TextView tv = new TextView(this);
        tv.setText(url);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setGravity(Gravity.CENTER);
        LinearLayout urlLinks = (LinearLayout) findViewById(R.id.urlLinks);
        urlLinks.setVisibility(View.VISIBLE);
        tv.setTextColor(Color.WHITE);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setAutoLinkMask(Linkify.ALL);
        tv.setTextSize(14);
        urlLinks.addView(tv);
    }

    public void facebookClicked(View v) {
        String name = youtubeId;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.facebook");
            intent.setData(Uri.parse("https://www.facebook.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + name)));
        }
    }

    public void youTubeClicked(View v) {
        String name = youtubeId;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void twitterClicked(View v) {
        String name = youtubeId;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.twitter");
            intent.setData(Uri.parse("https://www.twitter.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/" + name)));
        }
    }
}