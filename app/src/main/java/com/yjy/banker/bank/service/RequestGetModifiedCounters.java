package com.yjy.banker.bank.service;

public class RequestGetModifiedCounters extends AbstractRequest {
    private long mBalanceListModifiedCounter;
    private long mAccountIDListModifiedCounter;
    private long mProfileListModifiedCounter;

    @Override
    public void execute() {
        mAccountIDListModifiedCounter =
                this.getBankService().getAccountIDListModifiedCounter();
        mBalanceListModifiedCounter =
                this.getBankService().getBalanceListModifiedCounter();
        mProfileListModifiedCounter =
                this.getBankService().getProfileListModifiedCounter();
    }

    public long getBalanceListModifiedCounter() {
        return mBalanceListModifiedCounter;
    }

    public long getAccountIDListModifiedCounter() {
        return mAccountIDListModifiedCounter;
    }

    public long getProfileListModifiedCounter() {
        return mProfileListModifiedCounter;
    }
}
