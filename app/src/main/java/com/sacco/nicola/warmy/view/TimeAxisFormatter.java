package com.sacco.nicola.warmy.view;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nicolasacco on 14/04/17.
 */
public class TimeAxisFormatter implements IAxisValueFormatter {

    private long originTimestamp = Long.MIN_VALUE;

    public TimeAxisFormatter(Date origin) {
        super();
        originTimestamp = origin.getTime();
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return new SimpleDateFormat("HH:mm:ss").format(new Date((long) value + originTimestamp));
    }
}
