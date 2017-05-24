package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.AccountManager;
import com.yjy.banker.bank.account.IllegalAccountIDException;
import com.yjy.banker.bank.account.Profile;

import java.util.HashMap;

public class BaseBankService implements BankService {

    final private AccountManager accountManager;

    public BaseBankService(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Override
    public AccountID applyAccount() {
        synchronized (accountManager) {
            return accountManager.applyAccount();
        }
    }

    @Override
    public AccountID applySupperAccount() {
        synchronized (accountManager) {
            return accountManager.applySupperAccount();
        }
    }

    @Override
    public boolean transferMoney(AccountID superAccountID, AccountID accountID, int money)
            throws IllegalAccountIDException {
        synchronized (accountManager) {
            return accountManager.getBalanceManager().transferMoney(superAccountID, accountID, money);
        }
    }

    @Override
    public int getBalance(AccountID accountID) throws IllegalAccountIDException {
        synchronized (accountManager) {
            return accountManager.getBalanceManager().getBalanceList().get(accountID);
        }
    }

    @Override
    public HashMap<AccountID, Integer> getBalanceList() {
        synchronized (accountManager) {
            return accountManager.getBalanceManager().getBalanceList();
        }
    }

    @Override
    public long getBalanceListModifiedCounter() {
        synchronized (accountManager) {
            return accountManager.getBalanceManager().getBalanceListModifiedCounter();
        }
    }

    @Override
    public long getAccountIDListModifiedCounter() {
        return 0;
    }

    @Override
    public boolean setProfile(AccountID accountID, Profile profile) {
        synchronized (accountManager) {
            return accountManager.getProfileManager().setProfile(accountID, profile);
        }
    }

    @Override
    public HashMap<AccountID, Profile> getProfileList() {
        synchronized (accountManager) {
            return accountManager.getProfileManager().getProfileList();
        }
    }

    @Override
    public long getProfileListModifiedCounter() {
        synchronized (accountManager) {
            return accountManager.getProfileManager().getProfileListModifiedCounter();
        }
    }
}
