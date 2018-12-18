package com.nith.major.nithlogger.events;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nith.major.nithlogger.R;
import java.util.ArrayList;

public class EventItemAdapter extends RecyclerView.Adapter<EventItemAdapter.ViewHolder> {

    public Context context;
    private ArrayList<Event> dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View rView;
        public ViewHolder(View v) {
            super(v);
            rView = v;
        }
    }

    public EventItemAdapter(EventList dataset) {
        this.dataset = new ArrayList<>(dataset.getEvents().values());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView _event_id = (TextView) holder.rView.findViewById(R.id.textViewid);
        TextView _desc = (TextView) holder.rView.findViewById(R.id.textViewdesc);
        TextView _onDate = (TextView) holder.rView.findViewById(R.id.textViewdate);


        final int eventid = dataset.get(position).getEvent_id();
        final String desc = dataset.get(position).getDesc();
        final String onDate = dataset.get(position).getOnDate();


        _event_id.setText(eventid+"");
        _desc.setText(desc);
        _onDate.setText(onDate);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        context = parent.getContext();
        return vh;
    }

    @Override
    public int getItemCount() {

        return dataset.size();
        //return 1;
    }

    public void addAll(EventList pm) {
        this.dataset = new ArrayList<>(pm.getEvents().values());
        this.notifyDataSetChanged();
    }


    public void clearList(){
        this.dataset.clear();
        this.notifyDataSetChanged();
    }

}
