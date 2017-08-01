package com.gaurav.recorder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by gaurav on 07/06/17.
 */

public class RecorderService extends Service {
    private static final int RECORDER_SAMPLERATE = 8000;
    private MediaRecorder recorder;
    private String filePath;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public RecorderService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Background Audio Recorder...")
                .setContentText("Background audio recording started...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(1234, notification);

        pref = getApplicationContext().getSharedPreferences("Recorder",0);
        editor = pref.edit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioSamplingRate(RECORDER_SAMPLERATE);

            filePath =Environment.getExternalStorageDirectory().getAbsolutePath() + "/:" + DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime()) + "AudioRecording.mp3";
            /*final File file = new File(filePath);
            file.getParentFile().mkdirs(); */
            recorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/eyEar/Audio/"+ DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime()) + "AudioRecording.mp3");
            editor.clear();
            editor.commit();
            editor.putString("file_path",filePath);
            editor.commit();

            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            recorder.stop();
            recorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(getApplicationContext(),RecorderService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,1,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager =(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+1000,pendingIntent);

        super.onTaskRemoved(rootIntent);
    }

}