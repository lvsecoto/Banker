package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Message;
import com.yjy.banker.bank.account.MessageManager;

import java.util.ArrayList;

public class RequestGetMessage extends AbstractRequest {
    AccountID mAccountID;
    private ArrayList<Message> mMessages;

    public RequestGetMessage(AccountID accountID) {
        mAccountID = accountID;
    }

    @Override
    public void execute() {
        MessageManager messageManager = getBankService().getMessageManager();
        mMessages = messageManager.getMessages(mAccountID);
    }

    public ArrayList<Message> getMessages() {
        return mMessages;
    }
}
