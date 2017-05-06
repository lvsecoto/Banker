package com.yjy.banker.bank.account;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SuperAccountTest {

    SuperAccount superAccount;

    @Before
    public void setUp() throws Exception {
        superAccount = new SuperAccount(new AccountID(0));
    }

    @Test
    public void paymentMakeNoEffectToBalance() throws Exception {
        int beforeBalance;
        int afterBalance;

        beforeBalance = superAccount.getBalance();
        assertTrue(superAccount.pay(Integer.MAX_VALUE));
        afterBalance = superAccount.getBalance();

        assertEquals(beforeBalance, afterBalance);

    }
}