package com.yjy.banker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetWorks {

    @Nullable
    private final String mNetWorkAddress;

    public NetWorks() {
        mNetWorkAddress = getNetWorkAddress();
    }

    private static final Pattern mIPAddressPattern = Pattern.compile("((\\d{1,3}\\.){3})");

    /**
     * Get net address like "192.168.2.0" which is the current net work address.
     */
    @Nullable
    public String getNetWorkAddress() {
        String localAddress = getLocalHostAddress();

        if (localAddress == null) {
            return null;
        }

        Matcher matcher =
                Pattern.compile("(((\\d{1,3})\\.){3})").matcher(localAddress);

        if (!matcher.find()) {
            return null;
        }

        return matcher.group(0) + "0";
    }

    /**
     * @return Return true if the passing host address is belong to the current net work
     */
    public boolean isBelongToNetWork(@Nullable String hostAddress) {
        return isBelongToNetWork(hostAddress, mNetWorkAddress);
    }

    /**
     * @return Return true if the passing ip address is belong to the current net work
     */
    public static boolean isBelongToNetWork(@Nullable String hostAddress, @Nullable String netWorkAddress)
            throws
            IllegalArgumentException {
        Matcher matcher;
        String prefixAddress;

        if (hostAddress == null || netWorkAddress == null) {
            return false;
        }

        matcher = mIPAddressPattern.matcher(netWorkAddress);
        if (!matcher.lookingAt()) {
            return false;
        }

        prefixAddress = matcher.group(0);

        matcher = Pattern.compile(Pattern.quote(prefixAddress) + "\\d{1,3}")
                .matcher(hostAddress);

        return matcher.lookingAt();
    }

    @SuppressWarnings("deprecation")
    public static boolean isWifiConnected(@Nullable Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isConnected();
            }
        }
        return false;
    }

    @Nullable
    public static String getLocalHostAddress() {
        Enumeration<NetworkInterface> networkInterfaces;

        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return null;
        }

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    return inetAddress.toString().substring(1);
                }
            }
        }
        return null;
    }
}