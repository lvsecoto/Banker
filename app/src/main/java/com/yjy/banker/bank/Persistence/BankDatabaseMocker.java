package com.yjy.banker.bank.Persistence;

import android.content.Context;
import android.support.annotation.NonNull;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;

import java.util.LinkedHashMap;

public class BankDatabaseMocker implements IBankDatabase {

    private LinkedHashMap<Long, Long> mBalanceList;
    private LinkedHashMap<Long, Profile> mProfileList;
    private long mIdCounter;

    public BankDatabaseMocker() {
        mBalanceList = new LinkedHashMap<>();
        mProfileList = new LinkedHashMap<>();
        mIdCounter = 0;
    }

    @SuppressWarnings("unused")
    public BankDatabaseMocker(Context context) {
        new BankDatabaseMocker();
    }

    @Override
    public void reset() {
        mBalanceList = new LinkedHashMap<>();
        mProfileList = new LinkedHashMap<>();
        mIdCounter = 0;
    }

    @Override
    public boolean load(@NonNull IOnLoadListener onLoadListener) {
        return false;
    }

    @Override
    public AccountID insertAccount() {
        mIdCounter++;
        mBalanceList.put(mIdCounter, 0L);
        mProfileList.put(mIdCounter, new Profile());

        return new AccountID(mIdCounter);
    }

    @Override
    public void updateBalance(AccountID id, int balance) {
        mBalanceList.put(id.getID(), (long) balance);
    }

    @Override
    public long getBalance(AccountID id) {
        return mBalanceList.get(id.getID());
    }

    @Override
    public void updateProfile(AccountID id, Profile profile) {
        mProfileList.put(id.getID(), profile);
    }

    @Override
    public Profile getProfile(AccountID id) {
        return mProfileList.get(id.getID());
    }
}
