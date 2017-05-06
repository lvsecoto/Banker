package com.yjy.banker.bank.bank;

import com.yjy.banker.bank.service.BankService;

import java.io.IOException;

public interface Bank {
    void startServerWithThread();

    void stopServer() throws IOException;

    String getServerAddress();

    BankService getBankService();
}
