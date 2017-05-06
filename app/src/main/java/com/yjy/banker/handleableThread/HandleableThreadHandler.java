package com.yjy.banker.handleableThread;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @param <E> The data will be handle by {@link OnHandleListener}
 */
public class HandleableThreadHandler<E> extends Handler {
    private final WeakReference<OnHandleListener<E>> mOnUpdateListenerWeakReference;

    /**
     * The reference to listener is weakness, so you don't need to care about
     * the memory leaking
     */
    HandleableThreadHandler(OnHandleListener<E> OnHandleListener) {
        mOnUpdateListenerWeakReference =
                new WeakReference<>(OnHandleListener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleMessage(Message msg) {
        OnHandleListener OnHandleListener =
                mOnUpdateListenerWeakReference.get();
        if (OnHandleListener == null) {
            return;
        }

        E balanceList = (E) msg.obj;
        int what = msg.what;

        OnHandleListener.onUpdate(balanceList, what);
    }
}
