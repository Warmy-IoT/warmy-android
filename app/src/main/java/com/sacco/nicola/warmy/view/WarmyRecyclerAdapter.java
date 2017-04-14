package com.sacco.nicola.warmy.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sacco.nicola.warmy.R;
import com.sacco.nicola.warmy.model.Warmy;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by nicolasacco on 28/12/15.
 */
public class WarmyRecyclerAdapter extends RecyclerView.Adapter<WarmyRecyclerAdapter.ViewHolder> {

    public ArrayList<Warmy> getDataset() {
        return dataset;
    }

    public void setDataset(ArrayList<Warmy> dataset) {
        this.dataset = dataset;
    }

    protected ArrayList<Warmy> dataset = new ArrayList<>();

    private Context mContext = null;

    public static String WARMY_SELECTED_EVENT = "com.sacco.nicola.WARMY_SELECTED_EVENT";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private WarmyCardView view = null;

        public ViewHolder(WarmyCardView v) {
            super(v);
            this.view = v;

        }

        public void setDevice(final Warmy d) {
            this.view.setWarmy(d);

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(WARMY_SELECTED_EVENT);
                    i.putExtra("device_id", d.getDevice_id());
                    WarmyRecyclerAdapter.this.mContext.sendBroadcast(i);
                }
            });

        }

    }

    public WarmyRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public WarmyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        WarmyCardView v = new WarmyCardView(parent.getContext());

        v.setCollapsed(false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(WarmyRecyclerAdapter.ViewHolder holder, int position) {
        holder.setDevice(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
