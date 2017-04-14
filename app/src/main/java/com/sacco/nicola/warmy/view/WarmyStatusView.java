package com.sacco.nicola.warmy.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sacco.nicola.warmy.R;
import com.sacco.nicola.warmy.model.Warmy;

/**
 * Created by nicolasacco on 29/12/15.
 */
public class WarmyStatusView extends LinearLayout {

    private View layout;
    private TextView textWarmyDeviceId;
    private TextView textWarmyName;
    private TextView textWarmyActualTemperature;
    private TextView textWarmyDesiredTemperature;
    private ImageView imageWarmyLogo;
    private Warmy warmy;

    /**
     * @return
     */
    public Warmy getWarmy() {
        return warmy;
    }

    /**
     * @param d
     */
    public void setWarmy(Warmy d) {

        this.warmy = d;

        this.textWarmyDeviceId.setText(d.getDevice_id());

        this.textWarmyName.setText(d.getName());

        this.textWarmyActualTemperature.setText(String.valueOf(d.getActualTemperature()) + "°C");

        this.textWarmyDesiredTemperature.setText(String.valueOf(d.getDesiredTemperature()) + "°C");

        if (d.isWarming()) {
            this.imageWarmyLogo.setVisibility(View.VISIBLE);
        } else {
            this.imageWarmyLogo.setVisibility(View.INVISIBLE);
        }

        String actualModeString = getResources().getString(R.string.unknown);

        if (d.getActualMode().equals(Warmy.OFF_MODE)) {
            actualModeString = getResources().getString(R.string.off_mode);
        }

        if (d.getActualMode().equals(Warmy.MANUAL_MODE)) {
            actualModeString = getResources().getString(R.string.manual_mode);
        }

        if (d.getActualMode().equals(Warmy.AUTO_MODE)) {
            actualModeString = getResources().getString(R.string.auto_mode);
        }

        ((TextView) findViewById(R.id.actual_mode)).setText(String.valueOf(actualModeString));
    }

    /**
     * @param context
     */
    public WarmyStatusView(Context context) {

        super(context);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        /**
         *
         */
        this.layout = LayoutInflater.from(this.getContext())
                .inflate(R.layout.view_warmy_status, this, true);
        /**
         *
         */
        this.textWarmyDeviceId = (TextView) this.layout.findViewById(R.id.text_warmy_device_id);
        this.textWarmyName = (TextView) this.layout.findViewById(R.id.text_warmy_name);
        this.textWarmyActualTemperature = (TextView) this.layout.findViewById(R.id.text_warmy_temp);
        this.imageWarmyLogo = (ImageView) this.layout.findViewById(R.id.image_warmy_logo);
        this.textWarmyDesiredTemperature = ((TextView) findViewById(R.id.desired_temp));
    }

    public void setCollapsed(boolean collapsed) {
        if (collapsed) {
            findViewById(R.id.mode_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.mode_layout).setVisibility(View.VISIBLE);
        }
    }


}
