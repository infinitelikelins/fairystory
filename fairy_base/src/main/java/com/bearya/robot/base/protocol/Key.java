package com.bearya.robot.base.protocol;

public interface Key<V> {
    //自动解锁

    void create(KeyListener<V> listener);

    void release();

    void autoUnlock(V[] value);

}
