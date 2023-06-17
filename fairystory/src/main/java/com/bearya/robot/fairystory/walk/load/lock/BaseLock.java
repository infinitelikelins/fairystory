package com.bearya.robot.fairystory.walk.load.lock;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.protocol.ILock;
import com.bearya.robot.base.protocol.LockListener;
import com.bearya.robot.base.protocol.LockType;

public abstract class BaseLock<V> implements ILock<V> {
    private LockType lockType;
    protected LockListener<V> listener;
    protected int timeout;//timeout=0没有超时功能
    private final static int MAX_TIME = 30000;
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            BaseApplication.getInstance().getHandler().removeCallbacks(this);
            if (listener != null) {
                listener.onTimeout();
            }
        }
    };

    private Runnable maxTimeRunnable = new Runnable() {
        @Override
        public void run() {
            release();
            if (listener != null) {
                listener.onMaxTimeOver();
            }
        }
    };

    public LockType getType() {
        return lockType;
    }

    public BaseLock(LockType lockType, int timeout) {
        this.lockType = lockType;
        this.timeout = timeout;
    }

    protected void registerTimeout() {
        if (timeout > 0) {
            BaseApplication.getInstance().getHandler().postDelayed(task, timeout);
            BaseApplication.getInstance().getHandler().postDelayed(maxTimeRunnable, MAX_TIME);
        }
    }

    @Override
    public void release() {
        BaseApplication.getInstance().getHandler().removeCallbacks(task);
        BaseApplication.getInstance().getHandler().removeCallbacks(maxTimeRunnable);
    }

}
