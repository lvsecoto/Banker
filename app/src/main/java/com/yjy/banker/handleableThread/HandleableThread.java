package com.yjy.banker.handleableThread;

import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * You should override {@link #run()} to write the code witch you want to run at thread.
 * And use {@link #sendData(Object)} send the data you required. Then the data
 * will be handled by {@link OnHandleListener}.
 *
 * @param <E> The data implements {@link Serializable} you want to require in {@link #run()}.
 */
public class HandleableThread<E> extends Thread {
    public static final int MESSAGE_WHAT_OK = 0;
    public static final int MESSAGE_WHAT_IO_EXCEPTION = 1;

    @Nullable
    private HandleableThreadHandler<E> mHandleableThreadHandler;

    @Nullable
    protected HandleableThreadHandler<E> getHandleableThreadHandler() {
        return mHandleableThreadHandler;
    }

    /**
     * The reference to listener is weakness, so you don't need to care about
     * the memory leaking.
     *
     * @param OnHandleListener The data you get will be handle by it. Set Null to Ignore the date.
     */
    public HandleableThread(String name, @Nullable OnHandleListener<E> OnHandleListener) {
        super(name);
        if (OnHandleListener != null) {
            mHandleableThreadHandler =
                    new HandleableThreadHandler<>(OnHandleListener);
        }
    }


    /**
     * Send the data, and handle it by {@link OnHandleListener}.
     *
     * @param data The data you get in {@link #run()}.
     */
    protected final void sendData(E data) {
        sendData(data, MESSAGE_WHAT_OK);
    }

    /**
     * Send the data, and handle it by {@link OnHandleListener}.
     *
     * @param data The data you get in {@link #run()}.
     * @param what parameter to message
     * @return Return true if the data was successfully send.
     */
    public final boolean sendData(E data, int what) {
        return mHandleableThreadHandler == null ||
                mHandleableThreadHandler.sendMessage(mHandleableThreadHandler.obtainMessage(what, data));
    }

    /**
     * Put you requirement code here.
     */
    @SuppressWarnings("EmptyMethod")
    @Override
    public void run() {
        super.run();
    }
}
