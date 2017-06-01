package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.AccountManager;
import com.yjy.banker.bank.account.IllegalAccountIDException;
import com.yjy.banker.bank.account.Profile;

import java.util.HashMap;

public class BankService {

    final private AccountManager accountManager;

    public BankService(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public AccountID applyAccount() {
        synchronized (accountManager) {
            return accountManager.applyAccount();
        }
    }

    public AccountID applySupperAccount() {
        synchronized (accountManager) {
            return accountManager.applySupperAccount();
        }
    }

    public boolean transferMoney(AccountID superAccountID, AccountID accountID, int money)
            throws IllegalAccountIDException {
        synchronized (accountManager) {
            return accountManager.getBalanceManager().transferMoney(superAccountID, accountID, money);
        }
    }

    public int getBalance(AccountID accountID) throws IllegalAccountIDException {
        synchronized (accountManager) {
            return accountManager.getBalanceManager().getBalanceList().get(accountID);
        }
    }

    public HashMap<AccountID, Integer> getBalanceList() {
        synchronized (accountManager) {
            return accountManager.getBalanceManager().getBalanceList();
        }
    }

    public long getBalanceListModifiedCounter() {
        synchronized (accountManager) {
            return accountManager.getBalanceManager().getBalanceListModifiedCounter();
        }
    }

    public long getAccountIDListModifiedCounter() {
        return 0;
    }

    public boolean setProfile(AccountID accountID, Profile profile) {
        synchronized (accountManager) {
            return accountManager.getProfileManager().setProfile(accountID, profile);
        }
    }

    public HashMap<AccountID, Profile> getProfileList() {
        synchronized (accountManager) {
            return accountManager.getProfileManager().getProfileList();
        }
    }

    public long getProfileListModifiedCounter() {
        synchronized (accountManager) {
            return accountManager.getProfileManager().getProfileListModifiedCounter();
        }
    }
}
