package com.yjy.banker.utils;


import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.bank.BaseUser;
import com.yjy.banker.bank.bank.User;

import java.io.IOException;

public class UserFactory {

    private final Context mContext;
    private final AccountID mAccountID;
    private final String mSeverAddress;
    private User mUser;

    @MainThread
    public UserFactory(Context context, AccountID accountID, String severAddress) {
        mContext = context.getApplicationContext();
        mAccountID = accountID;
        mSeverAddress = severAddress;
    }

    /**
     * Get User from factory, create it if it doesn't exist.
     * <p>
     * In this condition You can't get user instance:
     * <ul>
     * <li>Wifi doesn't connect.</li>
     * </ul>
     */
    @WorkerThread
    public User getUser() throws IOException {
        if (mUser == null) {
            mUser = new BaseUser(mAccountID, mSeverAddress);
        }

        if (!mSeverAddress.equals("127.0.0.1") && !NetWorks.isWifiConnected(mContext)) {
            throw new IOException("Only allow to connect in wifi");
        }

        return mUser;
    }
}
