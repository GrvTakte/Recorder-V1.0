package com.demo.recorder.recorder;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Date;


/**
 * Created by Gaurav on 10/06/17.
 **/


public class CameraService extends Service implements SurfaceHolder.Callback {
    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private Camera camera = null;
    private MediaRecorder mediaRecorder = null;


    @Override
    public void onCreate() {
        // Start foreground service to avoid unexpected kill

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Background Video Recorder")
                .setContentText("")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(1234, notification);
        // Create new SurfaceView, set its size to 1x1, move it to the top left corner and set this service as a callback


        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(1, 1,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );

        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(surfaceView, layoutParams);
        surfaceView.getHolder().addCallback(this);
    }


    /**
     *
     * @param surfaceHolder
     */
    // Method called right after Surface created (initializing and starting MediaRecorder)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open();
        mediaRecorder = new MediaRecorder();
        camera.unlock();
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory()+"/"+DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime())+".mp4");

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }


    /**
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    // Stop recording and remove SurfaceView

    @Override
    public void onDestroy() {
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        camera.lock();
        camera.release();
        windowManager.removeView(surfaceView);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(getApplicationContext(),CameraService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,1,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager =(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+1000,pendingIntent);

        super.onTaskRemoved(rootIntent);
    }
}