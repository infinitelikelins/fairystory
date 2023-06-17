package com.bearya.robot.fairystory.walk.load.lock;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.protocol.Key;
import com.bearya.robot.base.protocol.LockListener;
import com.bearya.robot.base.protocol.LockType;
import com.bearya.robot.base.util.DebugUtil;

public class AdditionalLock extends BaseLock<Additional> {

    private final Additional additional;

    public AdditionalLock(Additional additional) {
        super(LockType.Additional, 0);
        this.additional = additional;
    }

    @Override
    public void unLock(Key<Additional> value, LockListener<Additional> l) {
        this.listener = l;
        if (listener != null) {
            listener.onLocking();
            DebugUtil.error("AdditionalLock unlock listener onLocking");
        }

        value.create(value1 -> {
            if (listener != null) {
                if (additional != null && additional.match(value1)) {
                    release();
                    listener.onSuccess(value1, null);
                    DebugUtil.error("AdditionalLock unlock listener onSuccess");
                    return true;
                } else {
                    listener.onFail(value1, null);
                    DebugUtil.error("AdditionalLock unlock listener onFail");
                    return false;
                }
            }
            return false;
        });
    }

    @Override
    public Additional[] getValues() {
        return new Additional[]{additional};
    }
}