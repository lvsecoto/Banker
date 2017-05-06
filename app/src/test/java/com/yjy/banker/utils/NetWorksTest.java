package com.yjy.banker.utils;

import android.support.annotation.Nullable;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NetWorksTest {
    @Test
    public void isBelongToNetWork() throws Exception {
        NetWorks netWorks = new NetWorks() {
            @Nullable
            @Override
            public String getNetWorkAddress() {
                return "192.168.2.";
            }
        };

        assertTrue(netWorks.isBelongToNetWork("192.168.2.8"));
        assertTrue(netWorks.isBelongToNetWork("192.168.2.9"));
        assertFalse(netWorks.isBelongToNetWork("192.168.1.9"));
        assertFalse(netWorks.isBelongToNetWork("92.168.1.9"));
        assertFalse(netWorks.isBelongToNetWork("92.1.1.9"));

    }

}