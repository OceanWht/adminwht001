package com.wht.admin.demo.utils;


public class StringUtils {

    /**
     * 判断对象是否为空
     *
     * @param obj 对象
     * @param <T> 泛型
     * @return 是否为空
     */
    public static <T extends Object> boolean isNull(T obj) {
        return obj == null;
    }
}
