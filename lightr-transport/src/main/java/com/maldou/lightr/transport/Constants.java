package com.maldou.lightr.transport;
public interface Constants {
 
    int YEAR_L=0x6; //63  年所占存储位数
    int MONTH_L=0x4; //15月所占存储位数
    int DAY_L=0x5;   //31日所占存储位数
    int HOUR_L=0x5; //31小时所占存储位数
    int MINUTE_L=0x6; //63分钟所占存储位数
    int SECOND_L=0x6;//63秒所占存储位数
    int SERVCE_L=0x5;  //31服务器编号所占存储位数
    int SERVERRANGER = 0x2f; //服务器编号的取值范围
    int SEQ_L=0x4; //15 序列所占存储位数
    int SLATREANGER =0xff; //
    int FIXYEAR=2000;  //截取年份固定值
}