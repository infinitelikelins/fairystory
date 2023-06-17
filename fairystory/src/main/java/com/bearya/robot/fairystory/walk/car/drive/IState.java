package com.bearya.robot.fairystory.walk.car.drive;

public interface IState {
    void makeFaceStartLoad();
    void recognitionLoad();//识别道路
    void newLoad();//两个读头进入地垫
    void computeExitPath();//规划出口路径
    void unLocking();//解锁中
    void travel();//行走
    void arriveTarget();//行程完成
    void exitLoad();//走出当前地垫
    void inObstacle();//进入避障区
    void outOfLoad();//走出地垫
    void reset();
    void makeFaceEndLoad();
}