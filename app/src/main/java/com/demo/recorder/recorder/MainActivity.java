package com.demo.recorder.recorder;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends Activity implements SurfaceHolder.Callback{

    public static SurfaceView mSurfaceView;
    public static SurfaceHolder mSurfaceHolder;
    public static final int RequestPermissionCode = 1;
    private String video_path=Environment.getExternalStorageDirectory()+"/"+DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime())+".mp4";
    private String audio_path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime()) + "AudioRecording.mp3";

    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean cameraServiceStatus = isServiceStart(CameraService.class,this);
        Log.d("Camera service status",""+cameraServiceStatus);

        boolean audioServiceStatus = isServiceStart(RecorderService.class,this);
        Log.d("Audio service status",""+audioServiceStatus);

        if (cameraServiceStatus){
            setContentView(R.layout.stop_recording_video);
        }else if(audioServiceStatus){
            setContentView(R.layout.stop_recording_audio);
        }
        else{
            setContentView(R.layout.activity_main);
        }

        if (!cameraServiceStatus && !audioServiceStatus) {
                mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
                mSurfaceHolder = mSurfaceView.getHolder();
                mSurfaceHolder.addCallback(this);
                mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkDrawOverlayPermission();
        }
    }


    public void playAudio(View v){
        if(checkPermission()) {
            Toast.makeText(getApplicationContext(), "Audio Recording started", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, RecorderService.class);
            startService(intent);
            setContentView(R.layout.stop_recording_audio);
        }else{
            requestPermission();
        }
    }


    public void stopAudio(View v){
       // Toast.makeText(getApplicationContext(),"Audio Recording stopped",Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),audio_path,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, RecorderService.class);
        stopService(intent);
        setContentView(R.layout.activity_main);
    }


    public void playVideo(View view){
        if (checkPermission()) {
            Toast.makeText(getApplicationContext(), "Video Recording started", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, CameraService.class);
            startService(intent);
            setContentView(R.layout.stop_recording_video);
        }else{
            requestPermission();
        }
    }


    public void stopVideo(View view){
        //Toast.makeText(this, "Video Recording stopped", Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),video_path,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, CameraService.class);
        stopService(intent);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean CameraPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    //boolean WindowPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission && CameraPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA);
       //int result3 = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.SYSTEM_ALERT_WINDOW);
        return (result == PackageManager.PERMISSION_GRANTED) && (result1 == PackageManager.PERMISSION_GRANTED) && (result2 == PackageManager.PERMISSION_GRANTED);
    }


    private void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, CAMERA, SYSTEM_ALERT_WINDOW}, RequestPermissionCode);
    }


    public final static int REQUEST_CODE = -1010101;


    @TargetApi(Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
            if (!Settings.canDrawOverlays(this)) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_CODE);
            }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted
            }
        }
    }


    private boolean isServiceStart(Class<?> serviceClass, Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
                if (serviceClass.getName().equals(service.service.getClassName())){
                    return true;
                }
        }
        return false;
    }
}