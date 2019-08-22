package com.bhkj.pdjhforotherapp.common.reservoir;

import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;

import java.io.IOException;
import java.lang.reflect.Type;

public class ReservoirUtils {

    public static void putObj(String key, Object obj) {
        try {
            Reservoir.put(key, obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getObj(String key, Class clazz) {
        Object obj = null;
        try {
            if (Reservoir.contains(key)) {
                obj = Reservoir.get(key, clazz);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }



    public static Object getObj(String key, Type type) {
        Object obj = null;
        try {
            if (Reservoir.contains(key)) {
                obj = Reservoir.get(key, type);
                Log.i("type",obj.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
