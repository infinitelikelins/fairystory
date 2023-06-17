package com.bearya.sdk.bluetooth.v2;

public enum SocketConnectStatus {

    CONNECT_OPEN("蓝牙连接打开"),

    CONNECT_CLOSE("蓝牙连接关闭"),

    CONNECT_INIT("连接初始化"),

    CONNECT_WAITING("正在连接中"),

    CONNECT_SUCCESS("连接成功"),

    CONNECT_FAIL("连接失败"),

    CONNECT_BREAK("连接断开"),

    CONNECT_BOND_BONDING("正在配对"),

    CONNECT_BOND_BONDED("配对结束"),

    CONNECT_BOND_NONE("取消配对/未配对");

    private final String value;

    SocketConnectStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
