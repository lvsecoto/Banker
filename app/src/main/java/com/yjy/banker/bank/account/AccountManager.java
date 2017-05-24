package com.yjy.banker.bank.account;

public interface AccountManager {

    AccountID applySupperAccount();

    AccountID applyAccount();

    boolean deleteAccount(AccountID ID) throws IllegalAccountIDException;

    ProfileManager getProfileManager();

    BalanceManager getBalanceManager();

}
