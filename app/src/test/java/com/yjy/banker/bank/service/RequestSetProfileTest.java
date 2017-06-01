package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.bank.bank.Bank;
import com.yjy.banker.bank.communication.BankServer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RequestSetProfileTest {

    private BankService mBankService;

    @Before
    public void setUp() throws Exception {
        Bank bank = new Bank();
        mBankService = bank.getBankService();
    }

    @Test
    public void setProfileToAccountID() throws Exception {
        AccountID accountID = mBankService.applyAccount();
        RequestSetProfile setProfileRequire =
                new RequestSetProfile(accountID, new Profile());
        RequestSetProfile requireResult =
                (RequestSetProfile) BankServer.executeRequireCommandAndGetResult(
                        setProfileRequire,
                        mBankService);
        assertTrue(requireResult.isSuccess());
        accountID = new AccountID(Long.MAX_VALUE);

        setProfileRequire =
                new RequestSetProfile(accountID, new Profile());
        requireResult =
                (RequestSetProfile) BankServer.executeRequireCommandAndGetResult(
                        setProfileRequire,
                        mBankService);
        assertFalse(requireResult.isSuccess());
    }
}
