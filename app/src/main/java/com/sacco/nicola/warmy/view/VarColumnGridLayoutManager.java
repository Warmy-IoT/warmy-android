package com.sacco.nicola.warmy.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by nicolasacco on 14/04/17.
 */

public class VarColumnGridLayoutManager extends GridLayoutManager {

    /**
     * The minimum width for every grid item.
     */
    private int minItemWidth;


    /**
     * Constructor
     *
     * @param context      The Context instance
     * @param minItemWidth The minimum width for every grid item
     */
    public VarColumnGridLayoutManager(Context context, int minItemWidth) {
        super(context, 1);
        this.minItemWidth = minItemWidth;
    }

    /**
     * @param recycler
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler,
                                 RecyclerView.State state) {
        updateSpanCount();
        super.onLayoutChildren(recycler, state);
    }

    /**
     * Updates the count of spans
     * w.r.t. minItemWidth
     */
    private void updateSpanCount() {
        int spanCount = getWidth() / minItemWidth;
        if (spanCount < 1) {
            spanCount = 1;
        }
        this.setSpanCount(spanCount);
    }
}
