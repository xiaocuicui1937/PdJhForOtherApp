package com.bhkj.pdjhforotherapp.common.parse;

import com.google.gson.Gson;

/**
 * Created by all-cui on 2017/12/6.
 */

public class GsonProvider {
    private Gson mGson;
    private GsonProvider(){
        mGson = new Gson();
    }
    public static GsonProvider getInstance(){
        return GsonHolder.INSTANCE;
    }

    /**
     * @author cui
     * @function 静态内部类实现单例模式，这个是当前实现单例模式最好的方法
     */
    private static class GsonHolder{
        static GsonProvider INSTANCE = new GsonProvider();
    }

    public Gson getGson(){
        return mGson;
    }

}
