package com.bhkj.pdjhforotherapp.common.utils;


import java.util.HashMap;


/**
 * Created by all-cui on 2018/3/30.
 * 使用单例模式+HashMap的方式传递参数，因为app长时间不使用有可能会出现被杀死的情况，
 * 一定要对单例对象加上非空判断
 */

public class StaticDataIntent {
    private HashMap<Object, Object> MAP;

    public StaticDataIntent() {
        if (MAP == null) {
            MAP = new HashMap<>();
        }
    }

    public static class Holder {
        private static StaticDataIntent INSTANCE = new StaticDataIntent();
    }

    public static StaticDataIntent getInstance() {
        return Holder.INSTANCE;
    }

    public Object get(String key) {
        if (MAP == null) {
            return null;
        }
        return MAP.get(key);
    }

    public void put(String key, String value) {
        if (MAP == null) {
            return;
        }
        MAP.put(key, value);
    }

    public void put(String key, boolean value) {
        if (MAP == null) {
            return;
        }
        MAP.put(key, value);
    }

    public void put(String key, int value) {
        if (MAP == null) {
            return;
        }
        MAP.put(key, value);
    }

    public boolean containKey(String key) {
        if (MAP == null) {
            return false;
        }
        return MAP.containsKey(key);
    }

    public void clear() {
        if (MAP == null) {
            return;
        }
        MAP.clear();
    }

    public void clearObj(String key) {
        if (MAP == null) {
            return;
        }
        MAP.remove(key);
    }
}
