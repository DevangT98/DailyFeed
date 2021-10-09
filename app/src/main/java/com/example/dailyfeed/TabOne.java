package com.example.dailyfeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dailyfeed.Database.DailyFeedModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TabOne extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private ArrayList<ListItems> listItems;
    NewsAdapter newsAdapter;
    SharedPreferences sp;
    private String REQUEST_URL = "";
    //private static final String REQUEST_URL = "https://newsapi.org/v2/sources?apiKey=bfdf3e0e5847437facbf4092ba190098#";
    String country = "";
    //    String REQUEST_URL ="https://newsapi.org/v2/top-headlines?country="+country+"&apiKey=bfdf3e0e5847437facbf4092ba190098";
    SwipeRefreshLayout swipeRefreshLayout;
//    String country = "in";

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_1, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
       /* sp = this.getActivity().getSharedPreferences("countrycode", Context.MODE_PRIVATE);
        country = sp.getString("countryname", "in");
        Log.i("YAY", "COUNTRY TAB ONE-->" + country);
       */ //REQUEST_URL = "https://newsapi.org/v2/top-headlines?country="+country+"&apiKey=bfdf3e0e5847437facbf4092ba190098";
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh);
        sp = this.getActivity().getSharedPreferences("countrycode", Context.MODE_PRIVATE);
        country = sp.getString("countryname", "in");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listItems = new ArrayList<>();
        recyclerView.setAdapter(newsAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe1, R.color.swipe2, R.color.swipe3);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadRecyclerViewData();
                /*sp = getActivity().getSharedPreferences("countrycode", Context.MODE_PRIVATE);
                country = sp.getString("countryname","in");*/
            }
        });

        return v;
    }


    private void loadRecyclerViewData() {
        swipeRefreshLayout.setRefreshing(true);
        country = sp.getString("countryname", "in");
        //REQUEST_URL = "https://newsapi.org/v2/top-headlines?country=" + country + "&apiKey=bfdf3e0e5847437facbf4092ba190098";
        REQUEST_URL = "https://newsdata.io/api/1/news?apikey=pub_1681f97540bf4c9eda3e4933b8dbbb84c322&country="+country;
        Log.i("YAY", "COUNTRY TAB ONE-->" + country);
        Log.i("YAY", "URL TAB ONE-->" + REQUEST_URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject baseobject = new JSONObject(response);
                    JSONArray articles = baseobject.getJSONArray("results");

                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject jsonObject = articles.getJSONObject(i);
                        Log.i("YAY", String.valueOf(jsonObject));
                        ListItems listItem = new ListItems(jsonObject.getString("title"),
                                jsonObject.getString("description"),
                                jsonObject.getString("image_url"),
                                jsonObject.getString("link"),
                                jsonObject.getString("pubDate"));
                        listItems.add(listItem);
                        Log.i("YAY", String.valueOf(listItem));
                    }


                    NewsAdapter newsAdapter = new NewsAdapter(listItems, getActivity());
                    recyclerView.setAdapter(newsAdapter);

                    newsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("Content-Type", "application/json");
    //            params.put("country","us");
                return  params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    @Override
    public void onRefresh() {
        loadRecyclerViewData();


    }
}
