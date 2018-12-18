package com.nith.major.nithlogger.events;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;

public class EventList implements Parcelable, Serializable {
    protected HashMap<Integer, Event> events;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.events);
    }

    public EventList() {
    }

    protected EventList(Parcel in) {
        this.events = (HashMap<Integer, Event>) in.readSerializable();
    }

    public static final Parcelable.Creator<EventList> CREATOR = new Parcelable.Creator<EventList>() {
        @Override
        public EventList createFromParcel(Parcel source) {
            return new EventList(source);
        }

        @Override
        public EventList[] newArray(int size) {
            return new EventList[size];
        }
    };

    public HashMap<Integer, Event> getEvents() {
        return events;
    }

    public void setEvents(HashMap<Integer, Event> events) {
        this.events = events;
    }
}