package com.sacco.nicola.warmy.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sacco.nicola.warmy.R;
import com.sacco.nicola.warmy.model.Warmy;

import java.text.DecimalFormat;

/**
 * Created by nicolasacco on 29/12/15.
 */
public class WarmyCardView extends LinearLayout {

    private View card;
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

    private String roundNoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("##.#");
        return twoDForm.format(d);
    }

    /**
     * @param d
     */
    public void setWarmy(Warmy d) {

        this.warmy = d;

        this.textWarmyDeviceId.setText(d.getDevice_id());

        this.textWarmyName.setText(d.getName());

        this.textWarmyActualTemperature.setText(roundNoDecimals(d.getActualTemperature()));

        this.textWarmyDesiredTemperature.setText(roundNoDecimals(d.getDesiredTemperature()) + "°C");

        if (d.isWarming()) {
            this.imageWarmyLogo.setVisibility(View.VISIBLE);
        } else {
            this.imageWarmyLogo.setVisibility(View.GONE);
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
    public WarmyCardView(Context context) {

        super(context);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        /**
         *
         */
        this.card = LayoutInflater.from(this.getContext())
                .inflate(R.layout.view_warmy_card, this, true);
        /**
         *
         */
        this.textWarmyDeviceId = (TextView) this.card.findViewById(R.id.text_warmy_device_id);
        this.textWarmyName = (TextView) this.card.findViewById(R.id.text_warmy_name);
        this.textWarmyActualTemperature = (TextView) this.card.findViewById(R.id.text_warmy_temp);
        this.imageWarmyLogo = (ImageView) this.card.findViewById(R.id.image_warmy_logo);
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
