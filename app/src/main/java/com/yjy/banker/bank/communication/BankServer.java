package com.yjy.banker.bank.communication;

import com.yjy.banker.bank.service.BankService;
import com.yjy.banker.bank.service.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class BankServer implements Runnable {

    public static final int DEFAULT_PORT = 10800;

    private final int port;
    private final BankService bankService;
    private ServerSocket serverSocket;

    private static final int SOCKET_READ_TIMEOUT = 5000;

    public BankServer(BankService bankService, int port) {
        this.port = port;
        this.bankService = bankService;
        createServerSocket();
    }

    public BankServer(BankService bankService) {
        this(bankService, DEFAULT_PORT);
    }

    public String getAddressName() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
            return "0.0.0.0";
        }
    }

    @Override
    public void run() {
        while (true) {
            if (serverSocket.isClosed()) {
                break;
            }
            getRequireFromClientBySocket();
        }
        System.out.printf("Bank server closed.");
    }

    public void close() throws IOException {
        serverSocket.close();
    }

    public int getPort() {
        return port;
    }

    private void getRequireFromClientBySocket() {
        Socket socket;
        try {
            socket = acceptSocket();
            configSocket(socket);
            createProcessSocketThread(socket);
        } catch (SocketException e) {
            System.out.printf(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Socket acceptSocket() throws IOException {
        return serverSocket.accept();
    }

    private void createServerSocket() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configSocket(Socket socket) throws SocketException {
        socket.setSoTimeout(SOCKET_READ_TIMEOUT);
    }

    private void createProcessSocketThread(final Socket socket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectInputStream inputStream = null;
                ObjectOutputStream outputStream = null;
                try {
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    processRequire(inputStream, outputStream);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void processRequire(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        Request receivedRequest = (Request) in.readObject();
        Request executedRequest = executeRequireCommandAndGetResult(receivedRequest, bankService);
        out.writeObject(executedRequest);
    }

    public static Request executeRequireCommandAndGetResult(Request request, BankService bankService) {
        request.setBankService(bankService);
        request.execute();
        return request;
    }

}
