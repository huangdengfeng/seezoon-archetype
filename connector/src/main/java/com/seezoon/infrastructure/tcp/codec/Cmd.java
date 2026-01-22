package com.seezoon.infrastructure.tcp.codec;

public interface Cmd {

    // 离线
    short System_Offline = 0x00;

    // 登录
    short C_Login = 0x01;
    // 服务器在线指令发送
    short S_Online = 0x80;
    // 服务器在线指令客户端回包
    short C_Online_Resp = 0x21;
    // 心跳
    short C_Ping = 0x13;
    // gps 位置上报
    short C_GPS_Position = 0xA0;
    // LBS多基站定位
    short C_LBS_Position = 0xA1;
    // 报警包
    short C_Alarm = 0xA4;
    // 服务端回包
    short S_Alarm_Resp = 0x26;
    // 终端较时
    short C_Time = 0x8A;
    // 终端通用信息上报
    short C_Info_Report = 0x94;

}
