package com.jianbao.jamboble.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by chenchuan on 2017/12/20.
 */

public class GsonHelper {
    private static Gson gson;

    static {
        gson = new GsonBuilder().create();
    }

    public static <T> String beanToString(T bean) {
        return gson.toJson(bean);
    }

    public static <T> T stringToBean(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T stringToMap(String json) {
        return gson.fromJson(json, new TypeToken<HashMap<String,String>>() {
        }.getType());
    }

}
