package com.gaurav.recorder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.widget.TextView;

/**
 * Created by gaurav on 11/07/17.
 */

public class SavedRecord extends Activity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView file_name;
    TextView file_path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_recording);

        file_name = (TextView) findViewById(R.id.file_name);
        file_path = (TextView) findViewById(R.id.file_path);
        pref = getApplicationContext().getSharedPreferences("Recorder",0);
        editor = pref.edit();
        String filePath = pref.getString("file_path",null);

        if (filePath != null){
            String[] file_parts = filePath.split(":");
            String fileName = file_parts[1];
            file_path.setText(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +fileName);
            file_name.setText(fileName);
        }else{
            file_path.append("");
            file_name.append("");
        }
    }
}
