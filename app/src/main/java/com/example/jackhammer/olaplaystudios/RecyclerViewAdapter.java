package com.example.jackhammer.olaplaystudios;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jackhammer on 16/12/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DataObjectHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static String TAG = "~~RecyclerViewAdapter~~";
    private ArrayList<DataObject> mDataset;
    private ArrayList<DataObject> duplicateDataset;
    private Context context;

    public RecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        this.mDataset = myDataset;
        this.duplicateDataset = new ArrayList<DataObject>();
        this.duplicateDataset = this.mDataset;

    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {

        final String songName=mDataset.get(position).getSongName();
        final String songArtists=mDataset.get(position).getSongArtists();
        final String songURL;
        String coverURL;
//        final String dirPath;
//        final String fname=songName;

        holder.SongNameTextview.setText(songName);
        songURL = mDataset.get(position).getSongURL();
        holder.SongArtistsTextview.setText("Artists: " + songArtists);
        coverURL = mDataset.get(position).getCoverImageURL();

        Glide.with(holder.CoverImageview.getContext())
                .load(coverURL)
                .override(80, 80)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.CoverImageview);

        holder.PlayImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Play Button Clicked",
                        Toast.LENGTH_LONG).show();
            }
        });

        holder.DownloadImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "Download Button Clicked",
//                        Toast.LENGTH_LONG).show();
                context=view.getContext();

                if(isWriteStorageAllowed()){
                    //If permission is already having then showing the toast
                    //Toast.makeText(context,"You already have the permission",Toast.LENGTH_LONG).show();

                    new DownloadSongs(view.getContext(),songName,songURL);
                }

                //If the app has not the permission then asking for the permission
                requestStoragePermission();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        this.mDataset = new ArrayList<DataObject>();
        if (charText.length() == 0) {
            this.mDataset.addAll(this.duplicateDataset);
        } else {
            for (DataObject item : this.duplicateDataset) {
                if (item.getSongName().toLowerCase(Locale.getDefault()).startsWith(charText.toLowerCase())) {
                    this.mDataset.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView SongNameTextview;
        TextView SongArtistsTextview;
        ImageView CoverImageview;
        ImageView PlayImageview;
        ImageView DownloadImageview;

        public DataObjectHolder(View itemView) {
            super(itemView);
            SongNameTextview = (TextView) itemView.findViewById(R.id.song_name_textview);
            SongArtistsTextview = (TextView) itemView.findViewById(R.id.song_artists_textview);
            CoverImageview = (ImageView) itemView.findViewById(R.id.cover_imageview);
            PlayImageview = (ImageView) itemView.findViewById(R.id.play_icon);
            DownloadImageview = (ImageView) itemView.findViewById(R.id.download_icon);

            Log.i(TAG, "Adding Listener");

        }
    }

    private boolean isWriteStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission(){

        Activity activity = (Activity) context;

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == 500){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(context,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(context,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

}
