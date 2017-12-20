package com.example.jackhammer.olaplaystudios;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "~~MainActivity~~";

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog Loading;

    private ArrayList<DataObject> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (checkInternetConnection()){

            getData();
        }
        else{
            mAdapter = new RecyclerViewAdapter(data);
            mRecyclerView.setAdapter(mAdapter);

            String message = "Check your internet connection!";
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

        }

    }

    private void getData() {

        Loading = ProgressDialog.show(this, "Please wait...", "Fetching...", false, false);

        String url = Config.GET_DATA_URL;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Loading.dismiss();
                Log.d(TAG, "Response received :"+response);
                parseJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"ONERRORRESPONSE : " + error.getMessage());
                        //Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //This is where the JSON response is Parsed
    private void parseJSON(String response) {

        data.clear();

        try {
            JSONArray result = new JSONArray(response);

            for(int i=0;i<result.length();i++) {
                JSONObject jsonObject = result.getJSONObject(i);

                DataObject dataObject = new DataObject();
                dataObject.setSongName(jsonObject.getString(Config.SONG_NAME_KEY));
                dataObject.setSongURL(jsonObject.getString(Config.SONG_URL_KEY));
                dataObject.setSongArtists(jsonObject.getString(Config.SONG_ARTISTS_KEY));
                dataObject.setCoverImageURL(jsonObject.getString(Config.COVER_IMAGE_URL_KEY));

                data.add(i,dataObject);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter = new RecyclerViewAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
    }

    //This method checks if there is an internet connection or not!
    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_item));

        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHint("Search");
        searchEditText.setHintTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorWhite)); //This changes the color of the hint
        searchEditText.setBackgroundResource(R.drawable.abc_textfield_search_default_mtrl_alpha); //Gives the underline on the edittext

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
