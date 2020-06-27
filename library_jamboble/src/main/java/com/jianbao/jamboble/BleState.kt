package com.jianbao.jamboble

enum class BleState {
    NOT_FOUND,
    SCAN_START,
    SCAN_STOP,
    CONNECTED,
    CONNECT_FAILED,
    CONNECTING,
    DISCONNECT,
    TIMEOUT
}