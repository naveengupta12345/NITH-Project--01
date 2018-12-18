package com.nith.major.nithlogger.user;

import android.app.Activity;
import android.util.Log;
import com.nith.major.nithlogger.CustomLocalStorage;
import java.io.IOException;
import java.io.Serializable;

public class User implements Serializable {

    private String roll, name, gender, branch, contact, year;
    /**
     * User is singleton
     */
    private static User instance = null;

    public static User getInstance(Activity a) {
        if(instance == null)
            getSavedUser(a);
        return instance;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public static User getInstance() {
        return instance;
    }

    public static void setInstance(User instance) {
        User.instance = instance;
    }

    public static void getSavedUser(Activity a){
        try {
            instance = CustomLocalStorage.getUser(a);
        } catch (Exception e) {
            Log.d("user", "couldnt get user from storage");
            instance = new User();
        }
    }

    public static void saveInstance(Activity a) {
        try {
            CustomLocalStorage.saveUser(a,instance);
        } catch (IOException e) {
            Log.e("user","couldn't save user");
        }
    }
}

