package com.thenextmediumsizedthing.bondfire;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class OpenNotifyWear extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("wear")) {
            Log.i("0", "messaged received, starting MainActivityWear");
            Intent startIntent = new Intent(this, MainActivityWear.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.putExtra("groupid", messageEvent.getData());
            startActivity(startIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
}
