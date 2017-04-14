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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sacco.nicola.warmy.R;
import com.sacco.nicola.warmy.model.Warmy;

import java.text.DecimalFormat;
import java.util.ArrayList;

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

    /**
     *
     */
    private LineData tempHistory;

    private int samplesWindow = 50;

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


        /**
         * Adding value to default
         * LineDataSet (0-th one)
         */
        tempHistory.addEntry(
                new Entry(xCursor, d.getActualTemperature()),
                0);

        if (xCursor == 0)
            setupChart(chart, tempHistory, ContextCompat.getColor(this.getContext(), R.color.colorPrimaryDark));
        else {
            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(samplesWindow - 1);
            chart.moveViewTo(tempHistory.getEntryCount() - samplesWindow, 50f, YAxis.AxisDependency.LEFT);
        }

        xCursor += 1;
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
        //set.setValueTextSize(10f);

        /**
         * Initializing tempHistory LineData Object
         */
        tempHistory = new LineData(set);


    }

    public void setCollapsed(boolean collapsed) {
        if (collapsed) {
            findViewById(R.id.mode_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.mode_layout).setVisibility(View.VISIBLE);
        }
    }


    private int[] mColors = new int[]{
            Color.rgb(137, 230, 81),
            Color.rgb(240, 240, 30),
            Color.rgb(89, 199, 250),
            Color.rgb(250, 104, 104)
    };

    private void setupChart(LineChart chart, LineData data, int color) {

        ((LineDataSet) data.getDataSetByIndex(0)).setCircleColorHole(color);

        // no description text
        chart.getDescription().setEnabled(false);

        // mChart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimaryLighter));

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(10, 0, 10, 0);

        // add data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setSpaceTop(100);
        chart.getAxisLeft().setSpaceBottom(100);
        chart.getAxisRight().setEnabled(false);

        chart.getXAxis().setEnabled(false);

        // animate calls invalidate()...
        chart.animateX(2500);
    }


}
