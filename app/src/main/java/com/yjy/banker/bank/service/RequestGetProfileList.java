package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;

import java.util.HashMap;

public class RequestGetProfileList extends AbstractRequest {
    private HashMap<AccountID, Profile> mProfileList;

    @Override
    public void execute() {
        mProfileList = this.getBankService().getProfileList();
    }

    public HashMap<AccountID, Profile> getProfileList() {
        return mProfileList;
    }

}
