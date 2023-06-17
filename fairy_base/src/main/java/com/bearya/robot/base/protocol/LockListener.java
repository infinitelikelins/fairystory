package com.bearya.robot.base.protocol;

public interface LockListener<V> {
    void onLocking();
    void onSuccess(V value, Object param);
    void onFail(V value, Object param);
    void onTimeout();
    void onMaxTimeOver();
}
