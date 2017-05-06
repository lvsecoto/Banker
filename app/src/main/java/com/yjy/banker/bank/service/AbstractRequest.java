package com.yjy.banker.bank.service;

import java.io.Serializable;

public abstract class AbstractRequest implements Request, Serializable {
    public static final int RESULT_FAIL = 0;
    public static final int RESULT_SUCCEED = 1;
    public static final int RESULT_ILLEGAL_ACCOUNT_ID = 2;
    private transient BankService bankService;

    public BankService getBankService() {
        return bankService;
    }

    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

}
