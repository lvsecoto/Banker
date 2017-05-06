package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.IllegalAccountIDException;

public class RequestTransferMoney extends AbstractRequest {

    private final AccountID mFromAccountID;
    private final AccountID mToAccountID;
    private final int mMoney;
    private int mResult;

    public RequestTransferMoney(AccountID from, AccountID to, int money) {
        mFromAccountID = from;
        mToAccountID = to;
        this.mMoney = money;
    }

    public int getResult() {
        return mResult;
    }

    @Override
    public void execute() {
        try {
            if (this.getBankService().transferMoney(mFromAccountID, mToAccountID, mMoney)) {
                mResult = RESULT_SUCCEED;
            } else {
                mResult = RESULT_FAIL;
            }
        } catch (IllegalAccountIDException e) {
            mResult = RESULT_ILLEGAL_ACCOUNT_ID;
        }
    }
}
