package com.demo.recorder.recorder;

import android.content.SharedPreferences;

/**
 * Created by gaurav on 20/06/17.
 */

public class Status {

    private boolean status;
    public SharedPreferences pref;


    public Status(boolean status){
        this.status=status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
