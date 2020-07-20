package com.yangyi.dds.config;


/**
 * 通过 ThreadLocal 获取和设置线程安全的数据源 key
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/01
 */
public class DynamicDataSourceContextHolder {

    /**
     * Maintain variable for every thread, to avoid effect other thread
     */
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>() {
        /**
         * 将 default 数据源的 key 作为默认数据源的 key
         */
        @Override
        protected String initialValue() {
            return "default";
        }
    };

    /**
     * To switch DataSource
     *
     * @param key the key
     */
    public static synchronized void setDataSourceKey(String key) {
         contextHolder.set(key);
    }

    /**
     * Get current DataSource
     *
     * @return data source key
     */
    public static String getDataSourceKey() {
         return contextHolder.get();
    }

    /**
     * To set DataSource as default
     */
    public static synchronized void clearDataSourceKey() {
         contextHolder.set("default");
    }
}
