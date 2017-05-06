package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;

public class RequestApplyAccount extends AbstractRequest {

    private AccountID accountID;

    @Override
    public void execute() {
        accountID = this.getBankService().applyAccount();
    }

    public AccountID getAccountID() {
        return accountID;
    }
}
