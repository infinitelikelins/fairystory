package com.bearya.robot.fairystory.walk.load.lock;

import com.bearya.robot.base.protocol.Key;
import com.bearya.robot.base.protocol.LockListener;
import com.bearya.robot.base.protocol.LockType;

public class DirectorPlayLock extends BaseLock<String> {

    public DirectorPlayLock() {
        super(LockType.DirectorPlay, 0);
    }

    @Override
    public void unLock(Key<String> key, LockListener<String> listener) {

    }

    @Override
    public String[] getValues() {
        return new String[0];
    }

}