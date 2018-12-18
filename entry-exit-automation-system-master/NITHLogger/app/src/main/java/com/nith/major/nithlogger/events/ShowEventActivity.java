package com.nith.major.nithlogger.events;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nith.major.nithlogger.CustomLocalStorage;
import com.nith.major.nithlogger.R;
import com.nith.major.nithlogger.SerializeToString;
import com.nith.major.nithlogger.ServerRestClient;
import com.nith.major.nithlogger.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ShowEventActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;
    private Context context;
    private static Activity currentActivity;
    private EventList list;
    private EventItemAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentActivity = this;
        context = getApplicationContext();
        try {
            list = (EventList) SerializeToString.fromString(CustomLocalStorage.getString(currentActivity, "elist"));
        } catch (Exception e) {
            list = new EventList();
            Log.e("Event", "passedtry");
        }

        Event obj = new Event();
        obj.setEvent_id(1);
        obj.setDesc("Lorem Ipsum");
        obj.setOnDate("2018-12-19");
        HashMap<Integer, Event> new_m = new HashMap<>();
        new_m.put(obj.getEvent_id(), obj);
        list.setEvents(new_m);
        Log.d("list",""+list);

        mRecyclerView = (RecyclerView)findViewById(R.id.event_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerAdapter = new EventItemAdapter(list);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshRatingContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchEventAsync();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fetchEventAsync();
        swipeContainer.setRefreshing(false);
    }

    public void clearList() {
        mRecyclerAdapter.clearList();
    }

    public void refreshAdapterData() {
        mRecyclerAdapter.addAll(list);
    }

    public void fetchEventAsync() {

        Log.e("Event", "passedtry");
        HashMap<String, String> user_params = new HashMap<>();
        RequestParams user = new RequestParams();
        user_params.put("roll", User.getInstance(currentActivity).getRoll());
        user.put("user",user_params);
        Log.e("user",""+user);
        ServerRestClient.post("event",user , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray event = (JSONArray) response.get("event");
                    HashMap<Integer, Event> new_event = new HashMap<>();

                    for (int i = 0; i < event.length(); ++i) {
                        Event p = new Event(event.getJSONObject(i));
                        new_event.put(p.getEvent_id(), p);
                    }
                    Log.d("tag", response.toString());
                    list.setEvents(new_event);
                    refreshAdapterData();

                    //Save menu to memory
                    CustomLocalStorage.set(currentActivity, "elist", SerializeToString.toString(list));

                } catch (JSONException e) {
                    //problem with server. probably.
                    e.printStackTrace();
                    Toast.makeText(currentActivity.getApplicationContext(), "Server error...", Toast.LENGTH_SHORT).show();
                } catch (IOException io) {
                    //error serializing menu object
                    io.printStackTrace();
                    Log.e("Serialize", "Couldn't save Menu to memory");
                } finally {
                    swipeContainer.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String error, Throwable throwable) {
                Log.e("FAILURE:", error);
                Toast.makeText(context, "Server not available...", Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
