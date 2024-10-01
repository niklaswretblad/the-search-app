package com.example.thesearch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

    private TheSearchAuthenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new TheSearchAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}

