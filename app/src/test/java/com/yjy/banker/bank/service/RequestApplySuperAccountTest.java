package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.bank.Bank;
import com.yjy.banker.bank.bank.BaseBank;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestApplySuperAccountTest {

    private BankService bankService;

    @Before
    public void setUp() throws Exception {
        Bank bank = new BaseBank();
        bankService = bank.getBankService();
    }

    @Test
    public void applySupperAccountFromBankService() throws Exception {
        RequestApplySuperAccount applySuperAccountRequire;
        applySuperAccountRequire = new RequestApplySuperAccount();
        applySuperAccountRequire.setBankService(bankService);
        applySuperAccountRequire.execute();
        AccountID accountID = applySuperAccountRequire.getSuperAccountID();
        assertEquals(1, accountID.getID());
    }

}
