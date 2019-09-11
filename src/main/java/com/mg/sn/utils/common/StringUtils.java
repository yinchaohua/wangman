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

    /**
     * <p>
     * 判断字符串是否为空
     * </p>
     *
     * @param cs 需要判断字符串
     * @return 判断结果
     */
    public static boolean isEmpty(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * 判断字符串是否不为空
     * </p>
     *
     * @param cs 需要判断字符串
     * @return 判断结果
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }
}
