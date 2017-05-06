package com.yjy.banker.bank.bank;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.bank.communication.BankClient;
import com.yjy.banker.bank.communication.BankServer;
import com.yjy.banker.bank.service.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class BaseUser implements User {
    private BankClient bankClient;

    private AccountID mAccountID;
    private final RequestGetModifiedCounters mGetModifiedCountersRequire
            = new RequestGetModifiedCounters();
    private final RequestGetProfileList mGetProfileListRequire
            = new RequestGetProfileList();
    private final RequestGetBalanceList mGetBalanceListRequire
            = new RequestGetBalanceList();

    public BaseUser(@NonNull String serverAddress, int port) throws IOException {
        bankClient = new BankClient(serverAddress, port);
        createAccountID();
    }

    public BaseUser(@NonNull String serverAddress) throws IOException {
        bankClient = new BankClient(serverAddress, BankServer.DEFAULT_PORT);
        createAccountID();
    }

    public BaseUser(@NonNull AccountID accountID, @NonNull String serverAddress) throws
            UnknownHostException {
        bankClient = new BankClient(serverAddress, BankServer.DEFAULT_PORT);
        this.mAccountID = accountID;
    }

    public BaseUser(@NonNull AccountID accountID, @NonNull String serverAddress, int port) throws
            UnknownHostException {
        bankClient = new BankClient(serverAddress, port);
        this.mAccountID = accountID;
    }

    private void createAccountID() throws IOException {
        RequestApplyAccount require =
                new RequestApplyAccount();
        try {
            require = (RequestApplyAccount) bankClient.require(require);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        mAccountID = require.getAccountID();
    }

    @Override
    public AccountID getAccountID() {
        return mAccountID;
    }

    @Override
    public int transferMoney(AccountID accountID, int money) throws IOException {
        RequestTransferMoney require =
                new RequestTransferMoney(this.mAccountID, accountID, money);

        try {
            require = (RequestTransferMoney) bankClient.require(require);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return require.getResult();
    }

    @Override
    public HashMap<AccountID, Integer> getBalanceList() throws IOException {
        HashMap<AccountID, Integer> balanceList;
        try {
            RequestGetBalanceList require;
            require = (RequestGetBalanceList) bankClient.require(mGetBalanceListRequire);
            balanceList = require.getBalancesList();
        } catch (ClassNotFoundException e) {
            Logger.e("Can not cast Request received from server", e);
            balanceList = null;
        }
        return balanceList;
    }

    @Override
    public ModifiedCounters getModifiedCounters() throws IOException {
        RequestGetModifiedCounters require = null;
        try {
            require = (RequestGetModifiedCounters) bankClient.require(mGetModifiedCountersRequire);
        } catch (ClassNotFoundException e) {
            Logger.e("Can not cast Request received from server", e);
        }

        ModifiedCounters modifiedCounters = new ModifiedCounters();

        if (require != null) {
            modifiedCounters.accountList = require.getAccountIDListModifiedCounter();
            modifiedCounters.balanceList = require.getBalanceListModifiedCounter();
            modifiedCounters.profileList = require.getProfileListModifiedCounter();
        }

        return modifiedCounters;
    }


    @Override
    @Nullable
    public HashMap<AccountID, Profile> getProfileList() throws IOException {
        HashMap<AccountID, Profile> profileList;
        try {
            RequestGetProfileList require;
            require =
                    (RequestGetProfileList) bankClient.require(mGetProfileListRequire);
            profileList = require.getProfileList();
        } catch (ClassNotFoundException e) {
            Logger.e("Can not cast require class received from server", e);
            profileList = null;
        }

        return profileList;
    }

    @Override
    public boolean setProfile(Profile profile) throws IOException {
        RequestSetProfile require =
                new RequestSetProfile(mAccountID, profile);
        boolean isSuccessful;
        try {
            require = (RequestSetProfile) bankClient.require(require);
            isSuccessful = require.isSuccess();
        } catch (ClassNotFoundException e) {
            Logger.e("Can not cast require class received from server", e);
            isSuccessful = false;
        }

        return isSuccessful;
    }

}
