package com.yjy.banker.bank.account;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccountIDTest {
    @Test
    public void differentIDMeanDifferent() throws Exception {
        AccountID id1 = new AccountID(1);
        AccountID id2 = new AccountID(1);
        AccountID id3 = new AccountID(2);

        assertTrue(id1.equals(id2));
        assertFalse(id1.equals(id3));
    }

    @Test
    public void hashCodeEqualsToItsFieldOfID() throws Exception {
        AccountID id1 = new AccountID(1);

        assertEquals(1, id1.hashCode());
    }

    @Test
    public void accountIDToStringEqualsToFiledID() throws Exception {
        AccountID id = new AccountID(1);
        assertTrue(id.toString().equals("1"));

    }
}