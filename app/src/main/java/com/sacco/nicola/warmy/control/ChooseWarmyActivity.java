package com.sacco.nicola.warmy.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sacco.nicola.warmy.R;
import com.sacco.nicola.warmy.model.Warmy;
import com.sacco.nicola.warmy.view.VarColumnGridLayoutManager;
import com.sacco.nicola.warmy.view.WarmyRecyclerAdapter;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseWarmyActivity extends AppCompatActivity {

    private HashMap<String, Warmy> devices = new HashMap<>();

    private BroadcastReceiver onWarmySelectedEventReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent i = new Intent(ChooseWarmyActivity.this, WarmyActualStatusActivity.class);
            i.putExtra("device_id", intent.getExtras().getString("device_id"));
            startActivity(i);
        }
    };

    private BroadcastReceiver onMQTTReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getExtras().getString("topic").contains("/warmy")) {
                try {
                    String topic = intent.getExtras().getString("topic");

                    String payload = intent.getExtras().getString("payload");

                    String device_id = topic.split("/")[2];

                    if (!devices.containsKey(device_id)) {
                        Warmy a = new Warmy();
                        a.setDevice_id(device_id);
                        devices.put(device_id, a);
                    } else {
                        devices.get(device_id).fromMQTT(topic, payload);
                    }

                    mAdapter.setDataset(new ArrayList<Warmy>(devices.values()));

                    mAdapter.notifyDataSetChanged();

                } catch (NullPointerException e) {

                }

            }
        }
    };


    private RecyclerView mRecyclerView;
    private WarmyRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_warmy);

        setTitle(getString(R.string.choose_a_warmy));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.reloading_warmys_list), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                devices.clear();
                mAdapter.setDataset(new ArrayList<Warmy>(devices.values()));
                mAdapter.notifyDataSetChanged();

                try {
                    ((WarmyApplication) getApplication()).connectClientIfNot();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.warmy_recycler_view);
        mLayoutManager = new VarColumnGridLayoutManager(this, 1000);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new WarmyRecyclerAdapter(getBaseContext());
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(onMQTTReceived);
        unregisterReceiver(onWarmySelectedEventReceived);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ((WarmyApplication) getApplication()).connectClientIfNot();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        IntentFilter i = new IntentFilter();
        i.addAction(WarmyApplication.NEW_MESSAGE_EVENT);
        registerReceiver(onMQTTReceived, i);
        i = new IntentFilter();
        i.addAction(WarmyRecyclerAdapter.WARMY_SELECTED_EVENT);
        registerReceiver(onWarmySelectedEventReceived, i);
    }
}
