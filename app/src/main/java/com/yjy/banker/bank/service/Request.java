package com.yjy.banker.bank.service;

public interface Request {
    BankService getBankService();

    void setBankService(BankService bankService);

    void execute();
}
