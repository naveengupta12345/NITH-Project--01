package com.nith.major.nithlogger.events;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static java.lang.Float.parseFloat;

public class Event implements Parcelable, Serializable {

    private int event_id;
    private String desc;
    private String onDate;

    public Event(int event_id, String desc, String onDate) {
        this.event_id = event_id;
        this.desc = desc;
        this.onDate = onDate;
    }

    public Event(JSONObject jo) {
        try {
            event_id = jo.getInt("event_id");
            desc = jo.getString("description");
            onDate = jo.getString("ondate");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.event_id);
        dest.writeString(this.desc);
        dest.writeString(this.onDate);
    }

    public Event() {
    }

    protected Event(Parcel in) {
        this.event_id = in.readInt();
        this.desc = in.readString();
        this.onDate = in.readString();
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOnDate() {
        return onDate;
    }

    public void setOnDate(String onDate) {
        this.onDate = onDate;
    }
}