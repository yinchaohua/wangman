package com.mg.sn.utils.common;

import java.util.UUID;

public class StringUtils {

    /**
     * 判断两个String字符串是否相等
     * @param str1
     * @param str2
     * @return
     */
    public static boolean stringEquals (String str1, String str2) {
        if (str1.equals(str2) || str1 == str2) {
            return true;
        }
        return false;
    }

    /**
     * 生成UUID
     * @return
     */
    public static String uuid () {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public static void main (String[] args) {
        String uuid = uuid();
        System.out.println(uuid);
    }
}
