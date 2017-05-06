package com.yjy.banker.bank.communication;

import com.yjy.banker.bank.service.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class BankClient {
    private InetAddress serverAddress;
    private int port = 10800;

    public BankClient(String serverAddress, int port) throws UnknownHostException {
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.port = port;
    }

    public Request require(Request request) throws IOException, ClassNotFoundException {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            socket = new Socket(serverAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(request);
            request = (Request) in.readObject();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }

                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return request;
    }
}
