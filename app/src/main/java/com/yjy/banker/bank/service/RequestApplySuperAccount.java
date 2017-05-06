package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;

public class RequestApplySuperAccount extends AbstractRequest {

    private AccountID superAccountID;

    @Override
    public void execute() {
        superAccountID = this.getBankService().applySupperAccount();
    }

    public AccountID getSuperAccountID() {
        return superAccountID;
    }

}
