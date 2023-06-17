package com.bearya.robot.base.protocol;

public enum DriveResult {
    Success,//成功
    FailObstacleAdditionalLost, // 道具卡片缺失
    FailObstacleAdditionalUnMatch,// 道具卡片不匹配
    FailNoEntry,//禁止通行如火山
    FailMoreAction,//动作比路多
    FailLessAction,//路比动作多
    FailEndLoadUnMatch,//到达的终点与选择的主题不匹配
    FailLostEquipmentLoads,//到达终点但缺少装备地垫
}