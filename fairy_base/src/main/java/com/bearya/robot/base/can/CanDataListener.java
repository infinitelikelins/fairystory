package com.bearya.robot.base.can;

public interface CanDataListener {
    void onFrontOid(int oid);
    void onBackOid(int oid);
    void onTouchBody(Body body);
//    void onTalkpenOid(int oid);
//    void onPower(int power);
//    void onHeaderBlock();
//    void onLeftHandBlock();
//    void onRightHandBlock();
//    void onCanConnected();
}
