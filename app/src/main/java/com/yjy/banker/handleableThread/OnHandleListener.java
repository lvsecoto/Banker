package com.yjy.banker.handleableThread;

import android.support.annotation.Nullable;

public interface OnHandleListener<E> {
    void onUpdate(@Nullable E data, int what);
}
