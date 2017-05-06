package com.yjy.banker.bank.account;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BaseAccountTest {

    private BaseAccount baseAccount;

    @Before
    public void setUp() throws Exception {
        baseAccount = new BaseAccount(new AccountID(1));
    }

    @Test
    public void accountEarnMoney() throws Exception {
        baseAccount.earn(1500);
        assertEquals(1500, baseAccount.getBalance());
    }

    @Test
    public void accountPayMoney() throws Exception {
        baseAccount.earn(1500);
        baseAccount.pay(500);
        assertEquals(1000, baseAccount.getBalance());
    }

    @Test
    public void payMoneyFailedIfItsNotEnoughBalance() throws Exception {
        baseAccount.earn(100);
        assertFalse(baseAccount.pay(101));
        assertEquals(100, baseAccount.getBalance());
    }

    @Test
    public void earnMoneyFailedIfTooManyMoneyToAccountBalance() throws Exception {
        assertTrue(baseAccount.earn(baseAccount.getCapacity()));
        assertFalse(baseAccount.earn(1));
        assertEquals(baseAccount.getCapacity(), baseAccount.getBalance());
    }

    @Test
    public void hashCodeEqualsItsAccountID() throws Exception {
        assertEquals(baseAccount.getID().getID(), baseAccount.hashCode());
    }

}
