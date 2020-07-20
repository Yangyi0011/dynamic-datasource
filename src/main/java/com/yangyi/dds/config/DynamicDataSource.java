package com.yangyi.dds.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据设置以及获取，本类属于单例
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/01
 */
@Component
public class DynamicDataSource extends AbstractRoutingDataSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 单例句柄
     */
    private volatile static DynamicDataSource instance;

    /**
     * 用于存储已实例的数据源map
     */
    private static Map<Object, Object> dataSourceMap = new HashMap<>();

    /**
     * 获取当前数据源
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {

        if (logger.isInfoEnabled()) {
            logger.info("Current DataSource is [{}]", DynamicDataSourceContextHolder.getDataSourceKey());
        }

        return DynamicDataSourceContextHolder.getDataSourceKey();
    }

    /**
     * 设置指定的数据源
     *
     * @param targetDataSources 目标数据源
     */
    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {

        dataSourceMap.putAll(targetDataSources);

        // 更新数据源 map
        super.setTargetDataSources(dataSourceMap);

        // 必须添加该句，否则新添加数据源无法识别到
        super.afterPropertiesSet();
    }

    /**
     * 获取存储已实例的数据源map
     *
     * @return Map<Object, Object>
     */
    public Map<Object, Object> getDataSourceMap() {
        return dataSourceMap;
    }

    /**
     * 通过 key 获取对应的数据源
     * @param key 数据源 key
     * @return
     */
    public DruidDataSource getDataSourceByKey(String key) {
        return (DruidDataSource) dataSourceMap.get(key);
    }

    /**
     * 获取数据源实例，单例方法
     *
     * @return DynamicDataSource
     */
    public static synchronized DynamicDataSource getInstance() {
        if (instance == null) {
            synchronized (DynamicDataSource.class) {
                if (instance == null) {
                    instance = new DynamicDataSource();
                }
            }
        }
        return instance;
    }

    /**
     * 是否存在当前 key 的 DataSource
     *
     * @param key
     * @return 存在返回 true, 不存在返回 false
     */
    public static boolean isExistDataSource(String key) {
        return dataSourceMap.containsKey(key);
    }
}
