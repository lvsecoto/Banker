package com.yjy.banker.bank.communication;

import com.yjy.banker.bank.account.BaseAccountManager;
import com.yjy.banker.bank.service.AbstractRequest;
import com.yjy.banker.bank.service.BaseBankService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class RequestBetweenServerAndClientTest {

    private BankClient bankClient;
    private BankServer bankServer;

    @Before
    public void setUp() throws Exception {
        bankServer = new BankServer(new BaseBankService(new BaseAccountManager()));
        bankClient = new BankClient(
                bankServer.getAddressName(),
                bankServer.getPort()
        );

        Thread thread = new Thread(bankServer);
        thread.start();
    }

    @Test
    public void PostCommandFromClientToServer() throws Exception {
        TestRequest command = new TestRequest();
        assertFalse(command.isExecuted());
        assertEquals(0, command.getTimeOfExecute());

        command = (TestRequest) bankClient.require(command);

        assertEquals(true, command.isExecuted());
        assertEquals(1, command.getTimeOfExecute());
    }

    @Test
    public void PostSomeCommandFromClientToServer() throws Exception {
        for (int i = 0; i < 100; i++) {
            PostCommandFromClientToServer();
        }
    }

    @After
    public void tearDown() throws Exception {
        bankServer.close();
    }
}

class TestRequest extends AbstractRequest {
    private boolean isExecuted = false;

    boolean isExecuted() {
        return isExecuted;
    }

    int getTimeOfExecute() {
        return timeOfExecute;
    }

    private int timeOfExecute = 0;

    @Override
    public void execute() {
        isExecuted = true;
        timeOfExecute++;
    }
}
