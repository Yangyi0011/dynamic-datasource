package com.yangyi.dds.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/14
 */
public class OrderUtil {

    // SQL 查询排序规则
    public static Map<String,String> JDBC_ORDER = new HashMap<>();

    static {
        JDBC_ORDER.put("ASC", "升序");
        JDBC_ORDER.put("DESC", "降序");
    }
}
