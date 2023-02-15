package com.example.assignment4;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected LocationManager locationManager;
    TextView tvCurrentLocation;
    final String civicinfoURL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyCVmn8sbriUdin_NtW_Y0uX7EsuyDZa0V8&address=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLocation();
    }

    private void setLocation() {
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);

        LocationListener locationListener = location -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                readcivicinfo(civicinfoURL + addresses.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000L, 100L, locationListener);


    }

    private void readcivicinfo(String civicinfoURL) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, civicinfoURL, response -> {
            NormalizedInput normalizedInput = new NormalizedInput();

            try {
                JSONObject jsonObj = new JSONObject(response);
                JSONObject nInputObj = jsonObj.getJSONObject("normalizedInput");
                normalizedInput.line1 = nInputObj.getString("line1");
                normalizedInput.city = nInputObj.getString("city");
                normalizedInput.state = nInputObj.getString("state");
                normalizedInput.zip = nInputObj.getString("zip");

                JSONArray offices = jsonObj.getJSONArray("offices");
                Office[] officeArr = new Office[offices.length()];
                for (int i = 0; i < offices.length(); i++) {
                    JSONObject officeObj = offices.getJSONObject(i);
                    Office newOffice = new Office();
                    newOffice.name = officeObj.getString("name");
                    newOffice.officialIndices = toStringArray(officeObj.getJSONArray("officialIndices"));
                    officeArr[i] = newOffice;
                }

                JSONArray officials = jsonObj.getJSONArray("officials");
                Official[] official1 = new Official[officials.length()];
                Official newOfficial;
                for (int i = 0; i < officials.length(); i++) {
                    JSONObject officialObj = officials.getJSONObject(i);
                    newOfficial = new Official();
                    if (officialObj.has("name"))
                        newOfficial.name = officialObj.getString("name");
                    if (officialObj.has("address"))
                        newOfficial.address = toAddressArray(officialObj.getJSONArray("address"));
                    if (officialObj.has("party"))
                        newOfficial.party = officialObj.getString("party");
                    if (officialObj.has("phones"))
                        newOfficial.phones = toStringArray(officialObj.getJSONArray("phones"));
                    if (officialObj.has("urls"))
                        newOfficial.urls = toStringArray(officialObj.getJSONArray("urls"));
                    if (officialObj.has("emails"))
                        newOfficial.emails = toStringArray(officialObj.getJSONArray("emails"));
                    if (officialObj.has("photoUrl"))
                        newOfficial.photoUrl = officialObj.getString("photoUrl");
                    if (officialObj.has("channels"))
                        newOfficial.channels = toChannelsArray(officialObj.getJSONArray("channels"));
                    official1[i] = newOfficial;
                }
                CivicInfo civicInfo = new CivicInfo(normalizedInput, officeArr, official1);
                showData(civicInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        mRequestQueue.add(mStringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuLocation:
                getAddressManually();
                return true;
            case R.id.menuAbout:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getAddressManually() {
        showDailog();

    }

    private void showDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Address");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String manualAddress = input.getText().toString();
            readcivicinfo(civicinfoURL + manualAddress);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    public String[] toStringArray(JSONArray array) {
        if (array == null)
            return new String[0];

        String[] arr = new String[array.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = array.optString(i);
        }
        return arr;
    }

    private RepAddress[] toAddressArray(JSONArray array) throws JSONException {
        if (array == null)
            return new RepAddress[0];
        RepAddress[] arr = new RepAddress[array.length()];
        RepAddress newAddress;
        for (int i = 0; i < arr.length; i++) {
            JSONObject currAddress = array.getJSONObject(i);
            newAddress = new RepAddress();
            newAddress.line1 = currAddress.getString("line1");
            if (currAddress.has("line2"))
                newAddress.line2 = currAddress.getString("line2");
            newAddress.city = currAddress.getString("city");
            newAddress.state = currAddress.getString("state");
            newAddress.zip = currAddress.getString("zip");
            arr[i] = newAddress;
        }
        return arr;
    }

    private Channel[] toChannelsArray(JSONArray array) throws JSONException {
        if (array == null)
            return new Channel[0];
        Channel[] arr = new Channel[array.length()];
        Channel newChannel;
        for (int i = 0; i < arr.length; i++) {
            JSONObject currAddress = array.getJSONObject(i);
            newChannel = new Channel();
            newChannel.type = currAddress.getString("type");
            newChannel.id = currAddress.getString("id");
            arr[i] = newChannel;
        }
        return arr;
    }

    private void showData(CivicInfo civicInfo) {
        String ad = civicInfo.normalizedInput.line1 + " " + civicInfo.normalizedInput.city + " " + civicInfo.normalizedInput.state + " " + civicInfo.normalizedInput.zip;
        tvCurrentLocation.setText(ad);
        CivicInfoData civicInfoData;
        CivicInfoData[] civicInfoDataArr = new CivicInfoData[civicInfo.officials.length];
        for (int i = 0; i < civicInfo.officials.length; i++) {
            civicInfoData = new CivicInfoData();
            civicInfoData.imageUrl = civicInfo.officials[i].photoUrl;
            civicInfoData.officeTitle = getOfficeTitle(i, civicInfo.offices);
            civicInfoData.official = civicInfo.officials[i];
            civicInfoData.representativeDetails = civicInfo.officials[i].name + " (" + civicInfo.officials[i].party + ")";
            civicInfoDataArr[i] = civicInfoData;
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        CivicInfoAdapter adapter = new CivicInfoAdapter(civicInfoDataArr, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private String getOfficeTitle(int i, Office[] offices) {
        for (Office office : offices) {
            for (String officialIndice : office.officialIndices) {
                if (officialIndice.equals(String.valueOf(i)))
                    return office.name;
            }
        }
        return "";
    }
}