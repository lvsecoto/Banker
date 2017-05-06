package com.yjy.banker.bank.account;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractAccountTest {

    private class MockAccount extends AbstractAccount {
        private MockAccount(AccountID ID) {
            super(ID);
        }

        @Override
        public boolean earn(int money) {
            return true;
        }

        @Override
        public boolean pay(int money) {
            return true;
        }
    }

    @Test
    public void sameAccountIDMeantSameAccount() throws Exception {
        AbstractAccount account1 = new MockAccount(new AccountID(1));
        AbstractAccount account2 = new MockAccount(new AccountID(1));
        AbstractAccount account3 = new MockAccount(new AccountID(2));

        assertTrue(account1.equals(account2));
        assertFalse(account1.equals(account3));

    }
}