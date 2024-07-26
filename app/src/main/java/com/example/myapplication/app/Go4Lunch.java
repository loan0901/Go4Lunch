package com.example.myapplication.app;

import android.app.Application;
import androidx.lifecycle.LifecycleObserver;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.myapplication.service.DailyResetWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Go4Lunch extends Application implements LifecycleObserver {

    @Override
    public void onCreate() {
        super.onCreate();

        // Constraints for the work
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(false)
                .setRequiresStorageNotLow(true)
                .build();

        // Schedule the work to run daily at 15:00
        WorkRequest dailyWorkRequest = new PeriodicWorkRequest.Builder(DailyResetWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueue(dailyWorkRequest);
    }

    private long calculateInitialDelay() {
        // Calculate the initial delay to the next 15:00
        Calendar now = Calendar.getInstance();
        Calendar next15 = (Calendar) now.clone();
        next15.set(Calendar.HOUR_OF_DAY, 15);
        next15.set(Calendar.MINUTE, 0);
        next15.set(Calendar.SECOND, 0);
        next15.set(Calendar.MILLISECOND, 0);

        if (now.after(next15)) {
            next15.add(Calendar.DAY_OF_MONTH, 1);
        }

        return next15.getTimeInMillis() - now.getTimeInMillis();
    }
}
