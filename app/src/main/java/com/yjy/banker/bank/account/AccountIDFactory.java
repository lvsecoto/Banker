package com.yjy.banker.bank.account;

public class AccountIDFactory {
    private int idCounter = 0;

    public AccountID create() {
        return new AccountID(idCounter++);
    }
}
