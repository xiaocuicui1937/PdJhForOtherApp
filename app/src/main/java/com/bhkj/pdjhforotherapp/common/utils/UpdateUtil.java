package com.bhkj.pdjhforotherapp.common.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import com.bhkj.pdjhforotherapp.R;
import com.bhkj.pdjhforotherapp.app.App;
import com.bhkj.pdjhforotherapp.common.Contact;
import com.bhkj.pdjhforotherapp.common.bean.UpdateBean;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;

import com.qiangxi.checkupdatelibrary.Q;
import com.qiangxi.checkupdatelibrary.bean.CheckUpdateInfo;
import com.qiangxi.checkupdatelibrary.callback.CheckUpdateCallback;
import com.qiangxi.checkupdatelibrary.dialog.ForceUpdateDialog;
import com.qiangxi.checkupdatelibrary.dialog.UpdateDialog;
import com.zhouyou.http.EasyHttp;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by hh on 2017/1/25.
 * 版本更新类
 */

public class UpdateUtil {
    private static CheckUpdateInfo mCheckUpdateInfo;

    /**
     * 检查版本更新，如果有新版本就直接更新，没有新版本的不显示更新dialog
     * 更新有强制更新和非强制更新
     *
     * @param isShowTip 是否显示toast，提示用户最新版本
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void checkUpdate(final Context ctx, final boolean isShowTip) {
        final Context context = App.getCtx();

        Q.checkUpdate(Q.GET, AppUtils.getAppVersionCode(), Contact.REFRESHURL, null, new CheckUpdateCallback() {
            @Override
            public void onCheckUpdateSuccess(String result, boolean hasUpdate) {
                UpdateBean bean = new Gson().fromJson(result, UpdateBean.class);
                showUpdateText(bean);//展示更新的信息如更新信息的描述、发布时间、apk大小等
                if (hasUpdate) {//如果有更新
                    if (mCheckUpdateInfo.getIsForceUpdate() == 0) {//新版本号等于0，非强制更新
                        UpdateDialogClick(ctx);
                    } else {// 1强制更新
                        forceUpdateDialogClick(ctx);
                    }
                } else {//如果没有更新，提示用户当前是最新版本
                    if (isShowTip) {
                        ToastUtils.showShort(context.getString(R.string.tip_new_version));
                    }
                }
            }


            @Override
            public void onCheckUpdateFailure(String failureMessage, int errorCode) {
                LogUtils.i(failureMessage+errorCode);
                ToastUtils.showShort(context.getString(R.string.tip_connect_error));
            }
        });


    }


    /**
     * 展示版本更新的详细信息 新版本内容详述、发布事件、apk大小、版本号
     */
    private static void showUpdateText(UpdateBean bean) {
        mCheckUpdateInfo = new CheckUpdateInfo();
        mCheckUpdateInfo.setAppName(bean.updateTitle)
                .setIsForceUpdate(bean.updateForce)//设置是否强制更新，1表示非强制更新
                .setNewAppReleaseTime(bean.releaseTime)//软件发布时间
                .setNewAppSize(bean.apkSize)//单位为M
                .setNewAppUrl(Contact.BASEPATHDOWNLOAD + bean.versionUrl)
                .setNewAppVersionCode(bean.newAppVersionCode)//新app的VersionCode
                .setNewAppVersionName(bean.newAppVersionName)
                .setNewAppUpdateDesc(bean.versionDescription);
    }

    /**
     * 强制更新
     */
    private static void forceUpdateDialogClick(Context ctx) {
        new ForceUpdateDialog(ctx).setAppSize(mCheckUpdateInfo.getNewAppSize())
                .setDownloadUrl(mCheckUpdateInfo.getNewAppUrl())
                .setTitle(mCheckUpdateInfo.getAppName() + "有更新啦")
                .setReleaseTime(mCheckUpdateInfo.getNewAppReleaseTime())
                .setVersionName(mCheckUpdateInfo.getNewAppVersionName())
                .setUpdateDesc(mCheckUpdateInfo.getNewAppUpdateDesc())
                .setFileName(App.getCtx().getString(R.string.app_name) + ".apk")
                .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/checkupdatelib").show();

    }

    /**
     * 非强制更新
     */
    private static void UpdateDialogClick(Context context) {
        new UpdateDialog(context)
                .setAppSize(mCheckUpdateInfo.getNewAppSize())
                .setDownloadUrl(mCheckUpdateInfo.getNewAppUrl())
                .setTitle(mCheckUpdateInfo.getAppName())
                .setReleaseTime(mCheckUpdateInfo.getNewAppReleaseTime())
                .setVersionName(mCheckUpdateInfo.getNewAppVersionName())
                .setUpdateDesc(mCheckUpdateInfo.getNewAppUpdateDesc())
                .setFileName(App.getCtx().getString(R.string.app_name) + ".apk")
                .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/checkupdatelib")
                //该方法需设为true,才会在通知栏显示下载进度,默认为false,即不显示
                //该方法只会控制下载进度的展示,当下载完成或下载失败时展示的通知不受该方法影响
                //即不管该方法是置为false还是true,当下载完成或下载失败时都会在通知栏展示一个通知
                .setShowProgress(true)
                .setIconResId(R.mipmap.ic_launcher)
                .setAppName(mCheckUpdateInfo.getAppName()).show();
    }
}
