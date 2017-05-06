package com.yjy.banker.bank.account;

import java.util.HashMap;
import java.util.Set;


public interface AccountManager {
    long getBalanceListModifiedCounter();

    AccountID applySupperAccount();

    AccountID applyAccount();

    int getBalance(AccountID ID) throws IllegalAccountIDException;

    int getCapacity(AccountID ID) throws IllegalAccountIDException;

    boolean deleteAccount(AccountID ID) throws IllegalAccountIDException;

    boolean transferMoney(AccountID fromID, AccountID toID, int money) throws
            IllegalAccountIDException;

    boolean isExistent(AccountID ID);

    Set<AccountID> getAccountIDList();

    HashMap<AccountID, Integer> getBalanceList();

    long getAccountIDListModifiedCounter();

    /**
     * Add the specified profile with it's belong to the specified accountID.
     *
     * @return Null if the specified accountID doesn't contain in AccountManager.
     */
    boolean setProfile(AccountID accountID, Profile profile);

    HashMap<AccountID, Profile> getProfileList();

    long getProfileListModifiedCounter();
}
