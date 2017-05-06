package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.bank.bank.Bank;
import com.yjy.banker.bank.bank.BaseBank;
import com.yjy.banker.bank.communication.BankServer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestGetModifiedCountersTest {

    private BankService mBankService;

    @Before
    public void setUp() throws Exception {
        Bank bank = new BaseBank();
        mBankService = bank.getBankService();
        AccountID supperAccount = mBankService.applySupperAccount();
        AccountID fromAccount = mBankService.applyAccount();
        AccountID toAccount = mBankService.applyAccount();

        mBankService.setProfile(toAccount, new Profile());
        mBankService.transferMoney(supperAccount, fromAccount, 100);

    }

    @Test
    public void name() throws Exception {
        RequestGetModifiedCounters modifiedCounterRequire =
                new RequestGetModifiedCounters();
        modifiedCounterRequire = (RequestGetModifiedCounters)
                BankServer.executeRequireCommandAndGetResult(modifiedCounterRequire, mBankService);

        assertEquals(5, modifiedCounterRequire.getBalanceListModifiedCounter());
        assertEquals(3, modifiedCounterRequire.getAccountIDListModifiedCounter());
        assertEquals(1, modifiedCounterRequire.getProfileListModifiedCounter());

    }

}
