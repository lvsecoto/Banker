package com.yjy.banker.bank.account;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BalanceManager {
    final private Map<AccountID, Account> mAccountList;
    final private LinkedHashMap<AccountID, Integer> mBalanceList;
    private long mBalanceListModifiedCounter;

    public BalanceManager() {
        mAccountList = new HashMap<>();
        mBalanceList = new LinkedHashMap<>();
        mBalanceListModifiedCounter = 0;
    }

    public LinkedHashMap<AccountID, Integer> getBalanceList() {
        return mBalanceList;
    }

    public long getBalanceListModifiedCounter() {
        return mBalanceListModifiedCounter;
    }

    public void add(Account account) {
        AccountID id = account.getID();
        mAccountList.put(id, account);
        mBalanceList.put(id, account.getBalance());

        mBalanceListModifiedCounter++;
    }

    public boolean transferMoney(AccountID fromID, AccountID toID, int money) {
        Account fromAccount;
        Account toAccount;
        fromAccount = mAccountList.get(fromID);
        toAccount = mAccountList.get(toID);
        boolean transferFinished = false;

        if (fromAccount.pay(money)) {
            if (toAccount.earn(money)) {
                transferFinished = true;
            } else {
                fromAccount.earn(money);
            }
        }

        if (transferFinished) {
            updateBalanceList(fromAccount);
            updateBalanceList(toAccount);
            mBalanceListModifiedCounter++;
        }

        return transferFinished;
    }

    private void updateBalanceList(Account account) {
        AccountID id = account.getID();
        int balance = account.getBalance();

        if (!mBalanceList.containsKey(id)) {
            return;
        }

        mBalanceList.put(id, balance);
        onBalanceListUpdate(id, balance);
    }

    protected void onBalanceListUpdate(AccountID id, int balance) {

    }

    public boolean remove(AccountID accountID) {
        mBalanceList.remove(accountID);
        mAccountList.remove(accountID);
        mBalanceListModifiedCounter++;
        return true;
    }
}
