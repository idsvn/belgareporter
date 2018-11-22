package be.belga.reporter.mobile.reporter.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class UploadFilesService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here
        return super.onStartCommand(intent, flags, startId);
    }
}
