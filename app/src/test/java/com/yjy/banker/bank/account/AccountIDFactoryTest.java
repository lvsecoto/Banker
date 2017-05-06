package com.yjy.banker.bank.account;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class AccountIDFactoryTest {

    private AccountIDFactory accountIDFactory;

    @Before
    public void setUp() throws Exception {
        accountIDFactory = new AccountIDFactory();
    }

    @Test
    public void GenerateDifferentAccountID() throws Exception {
        AccountID ID = accountIDFactory.create();

        for (int i = 0; i < 100; i++) {
            assertFalse(ID.equals(accountIDFactory.create()));
        }

    }
}