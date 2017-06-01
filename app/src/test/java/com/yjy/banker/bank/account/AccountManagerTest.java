package com.yjy.banker.bank.account;

import com.yjy.banker.bank.Persistence.BankDatabaseMocker;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountManagerTest {

    private AccountManager mAccountManager;
    private AccountID superAccountID;

    @Before
    public void setUp() throws Exception {
        mAccountManager = AccountManager.createFrom(new BankDatabaseMocker());
        superAccountID = mAccountManager.applySupperAccount();
    }

    @Test
    public void applyAccount() throws Exception {
        AccountID id = mAccountManager.applyAccount();
        assertTrue(mAccountManager.getBalanceManager().getBalanceList().containsKey(id));
        assertTrue(mAccountManager.getProfileManager().getProfileList().containsKey(id));
    }

    @Test
    public void onlyOneSuperAccount() throws Exception {
        assertEquals(superAccountID, mAccountManager.applySupperAccount());
    }
}