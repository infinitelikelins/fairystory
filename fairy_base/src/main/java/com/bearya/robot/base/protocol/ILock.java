package com.bearya.robot.base.protocol;

public interface ILock<V> {
    void unLock(Key<V> key,LockListener<V> listener);
    LockType getType();
    void release();
    V[] getValues();
}
