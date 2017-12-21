package com.example.jackhammer.olaplaystudios;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jackhammer on 19/12/17.
 */

public class DownloadSongs {

    private static final String TAG = "Download Task";
    private Context context;
    private String downloadUrl = "", downloadFileName = "";
    private String filepath;
    private String SongName;

    public DownloadSongs(Context ctext,String songName, String dUrl) {
        this.context = ctext;
        this.downloadUrl = dUrl;
        SongName=songName;

        downloadFileName=songName.replaceAll("\\s","");
        filepath = Environment.getExternalStorageDirectory().getPath()+"/Download/"+downloadFileName+".mp3";

        Log.e(TAG, filepath);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs
                //buttonText.setText("Download Failed…");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection

                String redirect = c.getHeaderField("Location");
                if (redirect != null){
                    url = new URL(redirect);
                    c = (HttpURLConnection) url.openConnection();
                }

                c.setRequestMethod("GET");//Set Request Method to "GET" since we are getting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }


                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) {

                    apkStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + Config.downloadDirectory);
                } else
                    Toast.makeText(context,
                            "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName+".mp3");//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(context,
//                                    "Download Complete", Toast.LENGTH_SHORT).show();

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                            mBuilder.setSmallIcon(R.drawable.ic_file_download);
                            mBuilder.setContentTitle("Download Complete");
                            //mBuilder.setContentText();
                            mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(SongName + " has been downloaded at " +
                                    Environment.getExternalStorageDirectory() + "/"
                                    + Config.downloadDirectory));
                            mBuilder.setSound(RingtoneManager
                                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                            mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                            mBuilder.setAutoCancel(true);

                            Uri path= Uri.parse(Environment.getExternalStorageDirectory() + "/"
                                    + Config.downloadDirectory);
                            Intent resultIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            resultIntent.setDataAndType(path, "resource/folder");           //or "text/csv" for Other Types**


                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(MainActivity.class);

                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder
                                    .getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);


                            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                            mNotificationManager.notify(0,mBuilder.build());

                        }
                    });
                }
                else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "Song Already Present!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception :: " + e.getMessage());
            }

            return null;
        }
    }
}
