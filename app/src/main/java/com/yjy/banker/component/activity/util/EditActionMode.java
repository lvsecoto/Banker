package com.yjy.banker.component.activity.util;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.yjy.banker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>The Edit Action Mode is used to toggle mode between view and edit.
 * <p>
 * <p>To start EditActionMode, invoke method {@link #start()}.
 * <p>
 * <p>Before you start the Edit Action Mode, you can {@link #add(EditText)} the {@link EditText}
 * into it.
 * After these {@link EditText}s will be set to editable in Edit Action Mode and no editable
 * the Edit Action Mode.
 */
public class EditActionMode {

    public interface onActionModeFinishedListener {
        /**
         * Method invoke after action mode finish.
         *
         * @param isDone True if user press the done button.
         */
        void onFinished(Boolean isDone);
    }

    private onActionModeFinishedListener mOnActionModeFinishedListener = null;

    private final AppCompatActivity mActivity;

    private final List<android.widget.EditText> mEditTexts;

    private final InputMethodManager mInputMethodManager;

    private Boolean mIsDone = false;

    private final ActionMode.Callback mCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.edit_action_mode, menu);
            setEditable(true);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.done:
                    mIsDone = true;
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            setEditable(false);

            if (mOnActionModeFinishedListener != null) {
                mOnActionModeFinishedListener.onFinished(mIsDone);
            }
        }
    };

    public EditActionMode(AppCompatActivity activity) {
        mActivity = activity;
        mEditTexts = new ArrayList<>();
        mInputMethodManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void setOnActionModeFinishedListener(
            onActionModeFinishedListener onActionModeFinishedListener) {
        mOnActionModeFinishedListener = onActionModeFinishedListener;
    }

    /**
     * Start the Edit Action Mode.
     */
    public void start() {
        mActivity.startSupportActionMode(mCallback);
    }

    /**
     * These {@link EditText}s will be set to editable in Edit Action Mode and no editable
     * out Edit Action Mode.
     */
    public void add(EditText editText) {
        mEditTexts.add(editText);
    }

    private void setEditable(boolean editable) {
        if (mEditTexts.isEmpty()) {
            return;
        }

        for (EditText editText : mEditTexts) {
            editText.setEnabled(editable);
            editText.setFocusable(editable);
            editText.setFocusableInTouchMode(editable);
        }

        if (editable) {
            mEditTexts.get(0).requestFocusFromTouch();
            openSoftKeyboard();
        }
    }

    private void openSoftKeyboard() {
        mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
