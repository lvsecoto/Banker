package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.bank.Bank;
import com.yjy.banker.bank.bank.BaseBank;
import com.yjy.banker.bank.communication.BankServer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestApplyAccountTest {

    private BankService bankService;

    @Before
    public void setUp() throws Exception {
        Bank bank = new BaseBank();
        bankService = bank.getBankService();

    }

    @Test
    public void applyAccountIDFromBankService() throws Exception {
        RequestApplyAccount applyAccountRequire =
                new RequestApplyAccount();

        applyAccountRequire =
                (RequestApplyAccount) BankServer.executeRequireCommandAndGetResult(applyAccountRequire, bankService);

        AccountID accountID = applyAccountRequire.getAccountID();
        assertEquals(2, accountID.getID());
    }
}
