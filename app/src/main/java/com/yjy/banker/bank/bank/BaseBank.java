package com.yjy.banker.bank.bank;

import com.yjy.banker.bank.Persistence.BankDatabaseMocker;
import com.yjy.banker.bank.account.AccountManager;
import com.yjy.banker.bank.account.BaseAccountManager;
import com.yjy.banker.bank.communication.BankServer;
import com.yjy.banker.bank.service.BankService;
import com.yjy.banker.bank.service.BaseBankService;

import java.io.IOException;

public class BaseBank implements Bank {
    private final BankService bankService;
    private final BankServer bankServer;

    public BaseBank(AccountManager accountManager) {
        bankService = new BaseBankService(accountManager);
        bankServer = new BankServer(bankService);
    }

    public BaseBank() {
        this(BaseAccountManager.createFrom(new BankDatabaseMocker()));
    }

    @Override
    public void startServerWithThread() {
        new Thread(bankServer).start();
    }

    @Override
    public void stopServer() throws IOException {
        bankServer.close();
    }

    @Override
    public String getServerAddress() {
        return bankServer.getAddressName();
    }

    @Override
    public BankService getBankService() {
        return bankService;
    }
}
