package com.mmall.util;


import org.apache.commons.lang3.StringUtils;

/**
 * @author BaiYe
 * @create 2020-09-24 17:15
 */
public class LevelUtil {
    public final static String SEPARATOR = ".";

    public final static String ROOT ="0";

    public static String calculateLevel(String parentLevel, int parentId){
        if (StringUtils.isBlank(parentLevel)){
            return ROOT;
        }else {
            return StringUtils.join(parentLevel,SEPARATOR,parentId);
        }
    }
}
