package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;

import java.util.HashMap;

public class RequestGetBalanceList extends AbstractRequest {
    private HashMap<AccountID, Integer> balancesList;

    @Override
    public void execute() {
        balancesList = this.getBankService().getBalanceList();
    }

    public HashMap<AccountID, Integer> getBalancesList() {
        return balancesList;
    }

}
