package com.bearya.robot.base.protocol;

import com.bearya.robot.base.walk.ISection;

public interface ILoad {

    /**
     * 名称
     */
    String getName();

    /**
     * 获取OID范围
     */
    ISection getOidSection();

    /**
     * 中点
     */
    int getCenterOid();

    ILock getLock();

    void release();

}
