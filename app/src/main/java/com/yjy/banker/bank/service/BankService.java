package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.IllegalAccountIDException;
import com.yjy.banker.bank.account.Profile;

import java.util.HashMap;

public interface BankService {

    AccountID applyAccount();

    AccountID applySupperAccount();

    boolean transferMoney(AccountID superAccountID, AccountID accountID, int money) throws IllegalAccountIDException;

    int getBalance(AccountID accountID) throws IllegalAccountIDException;

    boolean setProfile(AccountID accountID, Profile profile);

    HashMap<AccountID, Integer> getBalanceList();

    HashMap<AccountID, Profile> getProfileList();

    long getBalanceListModifiedCounter();

    long getAccountIDListModifiedCounter();

    long getProfileListModifiedCounter();

}
