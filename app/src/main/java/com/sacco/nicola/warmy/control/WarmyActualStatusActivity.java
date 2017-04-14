package com.sacco.nicola.warmy.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sacco.nicola.warmy.R;
import com.sacco.nicola.warmy.model.Warmy;
import com.sacco.nicola.warmy.view.WarmyCardView;
import com.sacco.nicola.warmy.view.WarmyStatusView;

import org.eclipse.paho.client.mqttv3.MqttException;

public class WarmyActualStatusActivity extends AppCompatActivity {

    private String OFF_MODE = Warmy.OFF_MODE;
    private String MANUAL_MODE = Warmy.MANUAL_MODE;
    private String AUTO_MODE = Warmy.AUTO_MODE;
    private double desiredTempTarget = 0;
    private double INCREMENT = 0.5;
    private TextView desired_temp_seek_tooltip;
    private Warmy selectedWarmy = new Warmy();

    private WarmyStatusView warmyStatusView;

    private BroadcastReceiver onMQTTReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getExtras().getString("topic").contains("/warmy/" + selectedWarmy.getDevice_id())) {
                String topic = intent.getExtras().getString("topic");

                String payload = intent.getExtras().getString("payload");

                selectedWarmy.fromMQTT(topic, payload);

                if (selectedWarmy.getActualMode() == OFF_MODE) {
                    findViewById(R.id.edit_temp).setVisibility(View.GONE);
                    findViewById(R.id.warmy_settings_layout).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.edit_temp).setVisibility(View.VISIBLE);
                }

                updateData();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(onMQTTReceived);
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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedWarmy.setDevice_id(getIntent().getExtras().getString("device_id"));

        setContentView(R.layout.activity_warmy_actual_status);

        warmyStatusView = new WarmyStatusView(getBaseContext());

        ((LinearLayout) findViewById(R.id.warmy_status_layout)).addView(warmyStatusView);

        warmyStatusView.setCollapsed(false);

        desired_temp_seek_tooltip = ((TextView) findViewById(R.id.desired_temp_target));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedWarmy.getActualMode().equals(MANUAL_MODE)) {
                    findViewById(R.id.warmy_settings_layout).setVisibility(View.GONE);
                    ((WarmyApplication) getApplication()).publish("/warmy/" + selectedWarmy.getDevice_id() + "/mode/set", OFF_MODE);
                } else {
                    ((WarmyApplication) getApplication()).publish("/warmy/" + selectedWarmy.getDevice_id() + "/mode/set", MANUAL_MODE);
                }
            }
        });

        FloatingActionButton edit_temp_fab = (FloatingActionButton) findViewById(R.id.edit_temp);

        edit_temp_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.warmy_settings_layout).getVisibility() == View.GONE) {
                    desiredTempTarget = selectedWarmy.getDesiredTemperature();
                    findViewById(R.id.warmy_settings_layout).setVisibility(View.VISIBLE);
                    desired_temp_seek_tooltip.setText(String.valueOf(desiredTempTarget));
                    findViewById(R.id.edit_temp).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.warmy_settings_layout).setVisibility(View.GONE);
                }
            }
        });

        FloatingActionButton temp_less = (FloatingActionButton) findViewById(R.id.temp_less);

        temp_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desiredTempTarget -= INCREMENT;
                desired_temp_seek_tooltip.setText(String.valueOf(desiredTempTarget));
            }
        });

        FloatingActionButton temp_plus = (FloatingActionButton) findViewById(R.id.temp_plus);

        temp_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desiredTempTarget += INCREMENT;
                desired_temp_seek_tooltip.setText(String.valueOf(desiredTempTarget));
            }
        });

        FloatingActionButton temp_send = (FloatingActionButton) findViewById(R.id.temp_send);

        temp_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WarmyApplication) getApplication()).publish("/warmy/" + selectedWarmy.getDevice_id() + "/manual/desired_temperature/set", String.valueOf(desiredTempTarget));
                findViewById(R.id.warmy_settings_layout).setVisibility(View.GONE);
                findViewById(R.id.edit_temp).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.warmy_settings_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.warmy_settings_layout).setVisibility(View.GONE);
            }
        });

    }

    public void updateData() {

        warmyStatusView.setWarmy(selectedWarmy);

        if (selectedWarmy.getActualMode().equals(OFF_MODE)) {
            findViewById(R.id.manual_mode_settings).setVisibility(View.GONE);
            findViewById(R.id.edit_temp).setVisibility(View.GONE);
        }

        if (selectedWarmy.getActualMode().equals(MANUAL_MODE)) {
            findViewById(R.id.manual_mode_settings).setVisibility(View.VISIBLE);
            findViewById(R.id.edit_temp).setVisibility(View.VISIBLE);
        }

        if (!selectedWarmy.getName().equals(""))
            setTitle(selectedWarmy.getName());

    }
}
