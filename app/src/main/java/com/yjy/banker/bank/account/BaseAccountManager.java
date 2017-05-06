package com.yjy.banker.bank.account;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class BaseAccountManager implements AccountManager {
    private final AccountIDFactory mAccountIDFactory;
    private final AccountID mSupperAccountID;

    private final Map<AccountID, Account> mAccountList;
    private HashMap<AccountID, Integer> mBalancesList;
    private final HashMap<AccountID, Profile> mProfileList;

    private long mAccountIDListModifiedCounter = 0L;
    private long mBalanceListModifiedCounter = 0L;
    private long mProfileListModifiedCounter;

    public BaseAccountManager() {
        mAccountIDFactory = new AccountIDFactory();
        mAccountList = new LinkedHashMap<>();
        mSupperAccountID = makeOnlyOneSuperAccount();
        mProfileList = new LinkedHashMap<>();
    }

    /**
     * This Modified Counter is added after create or delete account, include Super Account
     * creating. Because Account Manager creating after Super Account creating, so the Modified
     * Counter is initialed to 1.
     */
    @Override
    public long getAccountIDListModifiedCounter() {
        return mAccountIDListModifiedCounter;
    }

    /**
     * This Modified Counter added after change balance of account . And it is also added
     * after create or delete account , include Super Account creating. Because Account Manager
     * creating after Super Account creating, so the Modified Counter is initialed to 1.
     * <p>
     * Transferring money change both balances of two account, so the modified counter is added
     * twice.
     */
    @Override
    public long getBalanceListModifiedCounter() {
        return mBalanceListModifiedCounter;
    }

    @Override
    public AccountID applySupperAccount() {
        return mSupperAccountID;
    }

    @Override
    public AccountID applyAccount() {
        Account appliedAccount;
        appliedAccount = createAccount();
        addAccountToAccountList(appliedAccount);

        return appliedAccount.getID();
    }


    @Override
    public int getBalance(AccountID ID) throws IllegalAccountIDException {
        return getAccountFromAccountList(ID).getBalance();
    }

    @Override
    public int getCapacity(AccountID ID) throws IllegalAccountIDException {
        return getAccountFromAccountList(ID).getCapacity();
    }

    private AccountID makeOnlyOneSuperAccount() {
        Account supperAccount;
        supperAccount = createSuperAccount();
        addAccountToAccountList(supperAccount);

        return supperAccount.getID();
    }

    private Account createSuperAccount() {
        Account supperAccount;
        supperAccount = new SuperAccount(mAccountIDFactory.create());
        return supperAccount;
    }

    private Account createAccount() {
        Account appliedAccount;
        appliedAccount = new BaseAccount(mAccountIDFactory.create());
        return appliedAccount;
    }

    private void addAccountToAccountList(Account supperAccount) {
        mAccountList.put(supperAccount.getID(), supperAccount);
        updateBalanceList();
        mAccountIDListModifiedCounter++;
    }

    @Override
    public boolean deleteAccount(AccountID accountID) throws IllegalAccountIDException {
        boolean deleteFinished = (getBalance(accountID) == 0) && (mAccountList.remove(accountID) != null);
        if (deleteFinished) {
            updateBalanceList();
            deleteProfile(accountID);
            mAccountIDListModifiedCounter++;
        }
        return deleteFinished;
    }

    private Account getAccountFromAccountList(AccountID ID) throws IllegalAccountIDException {
        Account account;
        account = mAccountList.get(ID);

        if (account == null) {
            throw new IllegalAccountIDException("There is no BaseAccount with this AccountID.");
        }
        return account;
    }

    @Override
    public boolean transferMoney(AccountID fromID, AccountID toID, int money) throws
            IllegalAccountIDException {
        Account fromAccount;
        Account toAccount;
        fromAccount = getAccountFromAccountList(fromID);
        toAccount = getAccountFromAccountList(toID);
        boolean transferFinished = false;

        if (fromAccount.pay(money)) {
            if (toAccount.earn(money)) {
                transferFinished = true;
            } else {
                // Pay back money to fromAccount.
                fromAccount.earn(money);
            }
        }

        if (transferFinished) {
            updateBalanceList(fromAccount);
            updateBalanceList(toAccount);
        }

        return transferFinished;
    }

    @Override
    public boolean isExistent(AccountID ID) {
        return mAccountList.containsKey(ID);
    }

    @Override
    public Set<AccountID> getAccountIDList() {
        return mAccountList.keySet();
    }

    @Override
    public HashMap<AccountID, Integer> getBalanceList() {
        return mBalancesList;
    }

    private void updateBalanceList() {
        mBalancesList = new LinkedHashMap<>();
        for (Map.Entry<AccountID, Account> entry : mAccountList.entrySet()) {
            mBalancesList.put(entry.getKey(), entry.getValue().getBalance());
        }
        mBalanceListModifiedCounter++;
    }

    private void updateBalanceList(Account account) {
        AccountID accountID = account.getID();
        if (!mBalancesList.containsKey(accountID)) {
            return;
        }

        mBalancesList.put(accountID, account.getBalance());
        mBalanceListModifiedCounter++;
    }

    @Override
    public boolean setProfile(AccountID accountID, Profile profile) {
        if (!mAccountList.containsKey(accountID)) {
            return false;
        }
        mProfileList.put(accountID, profile);
        mProfileListModifiedCounter++;
        return true;
    }

    /**
     * Get a Profile list with key is accountID.
     */
    @Override
    public HashMap<AccountID, Profile> getProfileList() {
        return mProfileList;
    }

    private void deleteProfile(AccountID accountID) {
        if (mProfileList.remove(accountID) != null) {
            mProfileListModifiedCounter++;
        }
    }

    @Override
    public long getProfileListModifiedCounter() {
        return mProfileListModifiedCounter;
    }
}
