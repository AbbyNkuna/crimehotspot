package com.example.crimehotspotapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyBackgroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Perform any initialization or setup tasks here
        // This method is called when the service is started

        // Return START_STICKY to indicate that the service should be restarted if it gets terminated by the system
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Perform any cleanup tasks here
        // This method is called when the service is stopped or destroyed
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Return null as this is not a bound service
        return null;
    }
}
