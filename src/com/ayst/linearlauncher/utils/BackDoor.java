package com.ayst.linearlauncher.utils;

import android.view.KeyEvent;

public class BackDoor {
    public final static int[] DOORKEY_HIDE = {KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_MENU};
    public final static int[] DOORKEY_RESET = {KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_MENU};

    private int[] mCurBackDoorKeyList = DOORKEY_HIDE;
    private int mCurMacthKeyCount = 0;
    private boolean isKeyDown = false;

    public BackDoor(int[] backDoorKeyList) {
        mCurBackDoorKeyList = backDoorKeyList;
    }

    /**
     * 是否符合后门按键规则
     *
     * @param event
     * @return
     */
    public boolean isAsRule(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (KeyEvent.ACTION_DOWN == event.getAction()) {
            if (!isKeyDown) {
                if (mCurMacthKeyCount == mCurBackDoorKeyList.length - 1
                        && mCurBackDoorKeyList[mCurMacthKeyCount] == keyCode) {
                    mCurMacthKeyCount = 0;
                    return true;
                }
                if (mCurMacthKeyCount < mCurBackDoorKeyList.length
                        && mCurBackDoorKeyList[mCurMacthKeyCount] == keyCode) {
                    isKeyDown = true;
                } else {
                    mCurMacthKeyCount = 0;
                    isKeyDown = false;
                }
            }

        } else if (KeyEvent.ACTION_UP == event.getAction()) {
            if (isKeyDown) {
                if (mCurMacthKeyCount < mCurBackDoorKeyList.length
                        && mCurBackDoorKeyList[mCurMacthKeyCount] == keyCode) {
                    mCurMacthKeyCount++;
                } else {
                    mCurMacthKeyCount = 0;
                }
                isKeyDown = false;
            }
        }

        return false;
    }
}
