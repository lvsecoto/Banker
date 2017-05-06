package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.bank.Bank;
import com.yjy.banker.bank.bank.BaseBank;
import com.yjy.banker.bank.communication.BankServer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestTransferMoneyTest {

    private AccountID fromAccount;
    private AccountID toAccount;
    private BankService bankService;

    @Before
    public void setUp() throws Exception {
        Bank bank = new BaseBank();
        bankService = bank.getBankService();
        AccountID supperAccount = bankService.applySupperAccount();
        fromAccount = bankService.applyAccount();
        toAccount = bankService.applyAccount();

        bankService.transferMoney(supperAccount, fromAccount, 100);

    }

    @Test
    public void transferBetweenTwoAccount() throws Exception {
        RequestTransferMoney transferMoneyRequire =
                new RequestTransferMoney(fromAccount, toAccount, 100);

        assertEquals(100, bankService.getBalance(fromAccount));
        assertEquals(0, bankService.getBalance(toAccount));

        transferMoneyRequire =
                (RequestTransferMoney) BankServer.executeRequireCommandAndGetResult(transferMoneyRequire, bankService);

        assertEquals(0, bankService.getBalance(fromAccount));
        assertEquals(100, bankService.getBalance(toAccount));

        assertEquals(AbstractRequest.RESULT_SUCCEED, transferMoneyRequire.getResult());

    }
}
