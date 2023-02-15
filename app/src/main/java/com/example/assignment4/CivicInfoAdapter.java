package com.example.assignment4;

import static android.os.FileUtils.copy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class CivicInfoAdapter extends RecyclerView.Adapter<CivicInfoAdapter.ViewHolder> {
    private static final int IO_BUFFER_SIZE = 4096;
    private final CivicInfoData[] listdata;
    private Context context;

    public CivicInfoAdapter(CivicInfoData[] listdata, Context context) {
        this.context = context;
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public CivicInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CivicInfoAdapter.ViewHolder holder, int position) {
        final CivicInfoData myListData = listdata[position];
        //holder.image.setImageResource(R.drawable.missing);
        if (myListData.official.photoUrl != null) {
            String url = myListData.official.photoUrl.replace("http://", "https://");
            Glide.with(holder.image.getContext()).load(url).placeholder(R.drawable.brokenimage).into(holder.image);
        }

        holder.tvOfficeTitle.setText(myListData.officeTitle);
        holder.tvRepresentativeDetails.setText(myListData.representativeDetails);
        holder.linearLayout.setOnClickListener(view -> {
            //Toast.makeText(view.getContext(), "click on item: " + myListData.officeTitle, Toast.LENGTH_LONG).show()
            Intent intent = new Intent(context, IndividualOfficialActivity.class);
            intent.putExtra("official", myListData.official);
            intent.putExtra("officeTitle", myListData.officeTitle);
            TextView tvCurrentLocation = view.getRootView().findViewById(R.id.tvCurrentLocation);
            String currentLocation = tvCurrentLocation.getText().toString();
            intent.putExtra("currentLocation", currentLocation);
            try {
                context.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView tvOfficeTitle;
        public TextView tvRepresentativeDetails;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.image);
            this.tvOfficeTitle = (TextView) itemView.findViewById(R.id.tvOfficeTitle);
            this.tvRepresentativeDetails = (TextView) itemView.findViewById(R.id.tvRepresentativeDetails);
            this.linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }
    }

}
