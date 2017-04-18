package com.sacco.nicola.warmy.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sacco.nicola.warmy.R;
import com.sacco.nicola.warmy.model.Warmy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private LineChart chart;
    private Warmy warmy;


    private Date originDate;

    /**
     *
     */
    private LineData tempHistory;

    /**
     * Six hours at 1 sample/second.
     */
    private int samplesWindow = 6 * 60 * 60 * 1000;

    private int xCursor = 0;

    private String roundOneDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("##.#");
        return twoDForm.format(d);
    }

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

        this.textWarmyActualTemperature.setText(roundOneDecimals(d.getActualTemperature()) + "°C");

        this.textWarmyDesiredTemperature.setText(roundOneDecimals(d.getDesiredTemperature()) + "°C");

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

        float value = d.getActualTemperature();
        if (value != Float.MIN_VALUE) {
            /**
             * Adding value to default
             * LineDataSet (0-th one)
             */
            tempHistory.addEntry(
                    new Entry(new Date().getTime() - originDate.getTime(), value),
                    0);

            if (xCursor == 0)
                setupChart(chart, tempHistory, ContextCompat.getColor(this.getContext(), R.color.colorPrimaryDark));
            else {
                chart.notifyDataSetChanged();
                chart.setVisibleXRangeMaximum(samplesWindow + 5);
                chart.moveViewTo(tempHistory.getEntryCount() - samplesWindow, 50f, YAxis.AxisDependency.LEFT);
            }

            xCursor += 1;
        }
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
        this.chart = (LineChart) this.layout.findViewById(R.id.temp_chart);
        this.chart.setVisibility(View.VISIBLE);


        LineDataSet set = new LineDataSet(new ArrayList<Entry>(), "Temperature");

        set.setLineWidth(2.5f);
        set.setCircleRadius(1.5f);
        set.setColor(Color.rgb(240, 99, 99));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawValues(false);

        /**
         * Initializing tempHistory LineData Object
         */
        tempHistory = new LineData(set);

        /**
         *
         */
        originDate = new Date();
    }

    public void setCollapsed(boolean collapsed) {
        if (collapsed) {
            findViewById(R.id.mode_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.mode_layout).setVisibility(View.VISIBLE);
        }
    }

    private void setupChart(LineChart chart, LineData data, int color) {


        ((LineDataSet) data.getDataSetByIndex(0)).setCircleColorHole(color);

        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setAutoScaleMinMaxEnabled(true);

        chart.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimaryLighter));

        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setValueFormatter(new TimeAxisFormatter(originDate));
        chart.getXAxis().setGranularity(30 * 30 * 1000);
        chart.setData(data);
        chart.animateX(2500);
    }


}
