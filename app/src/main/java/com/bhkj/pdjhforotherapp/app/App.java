package com.bhkj.pdjhforotherapp.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;

import com.anupcowkur.reservoir.Reservoir;
import com.bhkj.pdjhforotherapp.common.Contact;
import com.bhkj.pdjhforotherapp.common.simpledisk.DiskLruCacheHelper;
import com.blankj.utilcode.util.CrashUtils;
import com.zhouyou.http.EasyHttp;

import java.io.IOException;

public class App extends Application {
    private static Context mCtx;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashUtils.init();
        mCtx = this;
        try {
            Reservoir.init(this,2048*1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DiskLruCacheHelper.init();
        EasyHttp.init(this);
        EasyHttp.getInstance().setBaseUrl(Contact.BASE_URL);
    }

    public static Context getCtx() {
        return mCtx;
    }
}
