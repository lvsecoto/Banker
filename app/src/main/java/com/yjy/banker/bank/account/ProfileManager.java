package com.yjy.banker.bank.account;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ProfileManager {
    private LinkedHashMap<AccountID, Profile> mProfileList;
    private long mProfileListModifiedCounter = 0;

    public ProfileManager() {
        mProfileList = new LinkedHashMap<>();
    }

    public HashMap<AccountID, Profile> getProfileList() {
        return mProfileList;
    }

    /**
     * <p><b>Modified Counter</b> update when method {@link #setProfile(AccountID, Profile)} and
     * {@link #add(AccountID, Profile)} invoke successfully.
     */
    public long getProfileListModifiedCounter() {
        return mProfileListModifiedCounter;
    }

    /**
     * Add a new profile with passing parameter of accountID.
     */
    public void add(AccountID accountID) {
        add(accountID, new Profile());
    }

    public void add(AccountID accountID, Profile profile) {
        mProfileList.put(accountID, profile);
        mProfileListModifiedCounter++;
    }

    /**
     * @return Return false if the account ID is non-existent in the instance
     * of {@link ProfileManager}
     */
    public boolean setProfile(AccountID accountID, @NonNull Profile profile) {
        if (!mProfileList.containsKey(accountID)) {
            return false;
        }
        mProfileList.put(accountID, profile);
        onProfileUpdate(accountID, profile);
        mProfileListModifiedCounter++;
        return true;
    }

    protected void onProfileUpdate(AccountID accountID, Profile profile) {

    }

    public void remove(AccountID accountID) {
        mProfileList.remove(accountID);
        mProfileListModifiedCounter++;
    }
}
