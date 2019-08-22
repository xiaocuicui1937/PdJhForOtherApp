package com.bhkj.pdjhforotherapp.core.self_start;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bhkj.pdjhforotherapp.common.simpledisk.DiskLruCacheHelper;
import com.bhkj.pdjhforotherapp.core.main.MainYwActivity;

import java.io.IOException;

/**
 * 接收系统启动后发送的广播
 * 然后跳转到业务页面
 */
public class SelfStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                //机器重启需要将保存的业务数据清除掉
                DiskLruCacheHelper.getInstance().delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intentStart = new Intent(context, MainYwActivity.class);
            intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentStart);
        }
    }
}
