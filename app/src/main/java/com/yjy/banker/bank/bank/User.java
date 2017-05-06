package com.yjy.banker.bank.bank;

import android.support.annotation.Nullable;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;

import java.io.IOException;
import java.util.HashMap;

public interface User {
    AccountID getAccountID();

    int transferMoney(AccountID accountID, int money) throws IOException;

    HashMap<AccountID, Integer> getBalanceList() throws IOException;

    ModifiedCounters getModifiedCounters() throws IOException;

    @Nullable
    HashMap<AccountID, Profile> getProfileList() throws IOException;

    boolean setProfile(Profile profile) throws IOException;

    class ModifiedCounters {
        public long balanceList;
        public long accountList;
        public long profileList;
    }
}
