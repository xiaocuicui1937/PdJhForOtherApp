package com.bhkj.pdjhforotherapp.common.simpledisk;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;


import com.bhkj.pdjhforotherapp.app.App;

import java.io.File;
import java.io.IOException;


/**
 * Created by all-cui on 2017/11/1.
 */

@SuppressWarnings({"ConstantConditions", "unchecked", "WeakerAccess"})
public class DiskLruCacheClient {
    private boolean isAvailable = false;
    /**
     * The default valueCount when open DiskLruCache.
     */
    private static final int DEFAULT_VALUE_COUNT = 1;

    private static final String TAG = "DiskLruCacheClient";

    private DiskLruCache mDiskLruCache;


    public DiskLruCacheClient(String dirName, int maxCount, File fileDir, boolean isAvailable) {
        this.isAvailable = isAvailable;
        if (fileDir!=null) {
            try {
                mDiskLruCache = generateCache(fileDir, maxCount);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mDiskLruCache = generateCache(dirName, maxCount);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private DiskLruCache generateCache(File dir, int maxCount) throws IOException {
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException(
                    dir + " is not a directory or does not exists. ");
        }
        final int appVersion = Utils.getAppVersion(App.getCtx());

        return DiskLruCache.open(
                dir,
                appVersion,
                DEFAULT_VALUE_COUNT,
                maxCount);
    }

    private DiskLruCache generateCache(String dirName, int maxCount) throws IOException {
        Context context = App.getCtx();
        return DiskLruCache.open(
                getDiskCacheDir(context, dirName),
                Utils.getAppVersion(context),
                DEFAULT_VALUE_COUNT,
                maxCount);
    }



    // =======================================
    // ============== 序列化 数据 读写 =============
    // =======================================

    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (isAvailable) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = context.getExternalCacheDir().getPath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
        } else {
            cachePath = context.getCacheDir().getPath();
        }


        return new File(cachePath + File.separator + uniqueName);
    }

    public DiskLruCache getDiskLruCache() {
        return mDiskLruCache;
    }
}
