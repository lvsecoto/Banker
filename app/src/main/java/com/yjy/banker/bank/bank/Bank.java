package com.yjy.banker.bank.bank;

import com.yjy.banker.bank.Persistence.BankDatabaseMocker;
import com.yjy.banker.bank.account.AccountManager;
import com.yjy.banker.bank.communication.BankServer;
import com.yjy.banker.bank.service.BankService;

import java.io.IOException;

public class Bank {
    private final BankService bankService;
    private final BankServer bankServer;

    public Bank(AccountManager accountManager) {
        bankService = new BankService(accountManager);
        bankServer = new BankServer(bankService);
    }

    public Bank() {
        this(AccountManager.createFrom(new BankDatabaseMocker()));
    }

    public void startServerWithThread() {
        new Thread(bankServer).start();
    }

    public void stopServer() throws IOException {
        bankServer.close();
    }

    public String getServerAddress() {
        return bankServer.getAddressName();
    }

    public BankService getBankService() {
        return bankService;
    }
}
