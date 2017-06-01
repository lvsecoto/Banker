package com.yjy.banker.bank.account;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import com.yjy.banker.bank.Persistence.BankDatabase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@MediumTest
public class AccountManagerAndroidTest {

    private AccountManager mAccountManager;
    private BankDatabase mBankDatabase;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        mBankDatabase = new BankDatabase(appContext);
        mAccountManager = AccountManager.createFrom(mBankDatabase);
    }

    @Test
    public void applyAccountFromManager() throws Exception {
        AccountID id = mAccountManager.applyAccount();
        assertEquals(2, id.getID());
    }

    @Test
    public void applySuperAccountFromManager() throws Exception {
        AccountID superId = mAccountManager.applySupperAccount();
        assertEquals(1, superId.getID());
    }

    @Test
    public void updateDatabaseAfterMoneyChanged() throws Exception {
        AccountID superId = mAccountManager.applySupperAccount();
        AccountID id = mAccountManager.applyAccount();
        int transferMoney = 100;

        BalanceManager balanceManager = mAccountManager.getBalanceManager();
        balanceManager.transferMoney(superId, id, transferMoney);

        int balance = (int) mBankDatabase.getBalance(id);
        assertEquals(transferMoney, balance);
    }
}