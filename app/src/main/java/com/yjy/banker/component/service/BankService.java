package com.yjy.banker.component.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.bank.Bank;
import com.yjy.banker.bank.bank.BaseBank;

import java.io.IOException;

public class BankService extends Service {

    private Bank mBank;

    public class Binder extends android.os.Binder {
    }

    public static void startAndBindService(Context context, ServiceConnection connection) {
        Intent intent = new Intent(context, BankService.class);
        context.startService(intent);
        context.bindService(intent, connection, 0);
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, BankService.class);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("Server created.");
        mBank = new BaseBank();
        mBank.startServerWithThread();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("Server stop.");
        try {
            mBank.stopServer();
        } catch (IOException e) {
            Logger.e("Bank Server closed with error", e);
        }
    }

}
