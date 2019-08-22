package com.bhkj.pdjhforotherapp.common.bean;

/**
 * Created by hh on 2017/1/24.
 * 更新实体类
 */

public class UpdateBean {
    /**
     * updateTitle : 石家庄通行证测试更新
     * newAppVersionCode : 2
     * updateForce : 0---非强制更新 1---强制更新
     * versionDescription : 1,优化下载逻辑
     2,修复一些bug
     3,完全实现强制更新与非强制更新逻辑
     4,非强制更新状态下进行下载,默认在后台进行下载
     5,当下载成功时,会在通知栏显示一个通知,点击该通知,进入安装应用界面
     6,当下载失败时,会在通知栏显示一个通知,点击该通知,会重新下载该应用
     7,当下载中,会在通知栏显示实时下载进度,但前提要dialog.setShowProgress(true).
     * versionUrl : /app-debug.apk
     * releaseTime : 2017年1月24号 14:43
     * apkSize : 11M
     * newAppVersionName : 2.0
     * {
     *   updateTitle:"检测到新版本",
     *   versionDescription:"修复了一些bug",
     *   versionUrl:"ParkingLot.apk",
     *   releaseTime:"2017-12-25 10:00:00",
     *   newAppVersionCode: 2,
     *   updateForce: 0,
     *   newAppVersionName:"2.0"
     * }
     *
     */

    public String updateTitle;
    public int newAppVersionCode;
    public int updateForce;
    public String versionDescription;
    public String versionUrl;
    public String releaseTime;
    public float apkSize;
    public String newAppVersionName;
}
