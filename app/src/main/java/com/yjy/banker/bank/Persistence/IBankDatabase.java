package com.yjy.banker.bank.Persistence;

import android.support.annotation.NonNull;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;

public interface IBankDatabase {
    void reset();

    boolean load(@NonNull IOnLoadListener onLoadListener);

    AccountID insertAccount();

    void updateBalance(AccountID id, int balance);

    long getBalance(AccountID id);

    void updateProfile(AccountID id, Profile profile);

    Profile getProfile(AccountID id);
}
