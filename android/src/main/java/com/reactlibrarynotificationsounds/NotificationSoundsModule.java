package com.reactlibrarynotificationsounds;

import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;

public class NotificationSoundsModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public NotificationSoundsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "NotificationSounds";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    @ReactMethod
    public void getNotifications(String soundType, final Promise promise) {
        RingtoneManager manager = new RingtoneManager(this.reactContext);
        Integer ringtoneManagerType;

        if (soundType.equals("alarm")) {
            ringtoneManagerType = RingtoneManager.TYPE_ALARM;
        } else if (soundType.equals("ringtone")) {
            ringtoneManagerType = RingtoneManager.TYPE_RINGTONE;
        } else if (soundType.equals("notification")) {
            ringtoneManagerType = RingtoneManager.TYPE_NOTIFICATION;
        } else {
            ringtoneManagerType = RingtoneManager.TYPE_ALL;
        }

        manager.setType(ringtoneManagerType);
        Cursor cursor = manager.getCursor();
        WritableArray list = Arguments.createArray();

        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);

            WritableMap newSound = Arguments.createMap();
            newSound.putString("title", notificationTitle);
            newSound.putString("url", notificationUri + "/" + id);
            newSound.putString("soundID", id);

            list.pushMap(newSound);
            Log.d("getNotifications: ", notificationUri + id);
        }
        promise.resolve(list);
    }

    private Ringtone ringtone;

    @ReactMethod
    public void playSample(String uri, String soundType) {
        try {
            Uri notification;
            Integer usage;

            if (uri == null || uri.length() == 0) {
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            } else {
                notification = Uri.parse(uri);
            }

            if (soundType.equals("alarm")) {
                usage = AudioAttributes.USAGE_ALARM;
            } else if (soundType.equals("ringtone")) {
                usage = AudioAttributes.USAGE_NOTIFICATION_RINGTONE;
            } else if (soundType.equals("notification")) {
                usage = AudioAttributes.USAGE_NOTIFICATION;
            } else {
                usage = AudioAttributes.USAGE_UNKNOWN;
            }

            if (ringtone != null)
                ringtone.stop();

            ringtone = RingtoneManager.getRingtone(this.reactContext, notification);
            ringtone.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(usage)
                    .build());
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void stopSample() {
        try {
            if (ringtone != null)
                ringtone.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
