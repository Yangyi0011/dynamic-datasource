package com.yangyi.dds.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.yangyi.dds.config.DynamicDataSource;
import com.yangyi.dds.config.DynamicDataSourceContextHolder;
import com.yangyi.dds.domain.dto.DataSourceDTO;
import com.yangyi.dds.exception.DataSourceCreateException;
import com.yangyi.dds.exception.FindException;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
@Accessors(chain = true)
public class DataSourceUtil {

    /**
     * 相关数据源的名称
     **/
    public static final String MYSQL_NAME = "MySQL";
    public static final String ORACLE_NAME = "Oracle";
    public static final String POSTGRESQL_NAME = "PostgreSQL";
    public static final String MONGODB_NAME = "MongoDB";
    public static final String SQLSERVER__NAME = "SQLServer";

    /**
     * 相关数据源的连接前缀，MongoDB是客户端连接，没有连接前缀
     **/
    public static final String MYSQL_CONNECTION_PREFIX = "jdbc:mysql://";
    public static final String ORACLE_CONNECTION_PREFIX = "jdbc:oracle:thin:@";
    public static final String POSTGRESQL_CONNECTION_PREFIX = "jdbc:postgresql://";
    public static final String SQLSERVER_CONNECTION_PREFIX = "jdbc:sqlserver://";

    /**
     * 相关数据源的连接后缀
     **/
    public static final String MYSQL_CONNECTION_SUFFIX = "?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2b8";
    public static final String ORACLE_CONNECTION_SUFFIX = "";
    public static final String POSTGRESQL_CONNECTION_SUFFIX = "";
    public static final String SQLSERVER_CONNECTION_SUFFIX = "";

    /**
     * 相关数据源的连接驱动类全名
     **/
    public static final String MYSQL_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    public static final String ORACLE_DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
    public static final String MONGODB_DRIVER_CLASS = "com.mongodb.MongoClient";
    public static final String POSTGRESQL_DRIVER_CLASS = "org.postgresql.Driver";
    public static final String SQLSERVER_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";


    /**
     * 查询所有数据库：目标列名
     * 相关数据源代表 数据库（schema）名称 的字段（列名），配合 schema源 使用可以查询出数据库的名称
     **/
    public static final String MYSQL_TARGET_SCHEMA_FIELD = "schema_name";
    public static final String POSTGRESQL_TARGET_SCHEMA_FIELD = "datname";

    /**
     * 查询所有数据库：目标表名
     * 相关数据源代表 schema源 的表名，用来从该表中查询所有数据库名称
     **/
    public static final String MYSQL_TARGET_SCHEMA_SOURCE = "information_schema.schemata";
    public static final String POSTGRESQL_TARGET_SCHEMA_SOURCE = "pg_database";

    /**
     * 相关数据源默认的 catalog
     **/
    public static final String MYSQL_DEFAULT_CATALOG = "def";
    public static final String POSTGRESQL_DEFAULT_CATALOG = "postgres";

    /**
     * 查询字段类型：目标列名
     * 相关数据源代表字段类型的列名
     **/
    public static final String MYSQL_TARGET_FIELD_TYPE = "column_type";
    public static final String POSTGRESQL_TARGET_FIELD_TYPE = "udt_name";


    /**
     * 数据源容器，用来存储各类型数据源名称及其相关信息
     * key：数据源类型，value：数据源信息
     */
    public static Map<String, Object> dataSourceMap;

    static {
        dataSourceMap = new ConcurrentHashMap<>(4);

        Map<Object, Object> mysql = new HashMap<>();
        mysql.put("driverClass", MYSQL_DRIVER_CLASS);
        mysql.put("prefix", MYSQL_CONNECTION_PREFIX);
        mysql.put("suffix", MYSQL_CONNECTION_SUFFIX);
        dataSourceMap.put(MYSQL_NAME, mysql);

        Map<Object, Object> postgre = new HashMap<>();
        postgre.put("driverClass", POSTGRESQL_DRIVER_CLASS);
        postgre.put("prefix", POSTGRESQL_CONNECTION_PREFIX);
        postgre.put("suffix", POSTGRESQL_CONNECTION_SUFFIX);
        dataSourceMap.put(POSTGRESQL_NAME, postgre);

        /*Map<Object, Object> oracle = new HashMap<>();
        oracle.put("driverClass", ORACLE_DRIVER_CLASS);
        oracle.put("prefix", ORACLE_CONNECTION_PREFIX);
        oracle.put("suffix", ORACLE_CONNECTION_SUFFIX);
        dataSourceMap.put(ORACLE_NAME, oracle);*/

        /*Map<Object, Object> sqlServer = new HashMap<>();
        sqlServer.put("driverClass", SQLSERVER_DRIVER_CLASS);
        sqlServer.put("prefix", SQLSERVER_CONNECTION_PREFIX);
        sqlServer.put("suffix", SQLSERVER_CONNECTION_SUFFIX);
        dataSourceMap.put(SQLSERVER__NAME, sqlServer);*/
    }

    public DataSourceUtil() {
    }

    /**
     * 测试SQL数据库连接，如mySQL、Oracle、PostgreSQL、SQLServer等数据库
     *
     * @param driverClass 驱动类全类名
     * @param url         数据库连接url
     * @param username    数据库连接用户名
     * @param password    数据库连接密码
     * @return 连接成功返回 true，连接失败返回 false
     */
    public static Boolean testConnection(String driverClass, String url, String username, String password) {
        DruidDataSource druidDataSource = null;

        try {
            // 加载启动类，若对应的驱动类不存在，则会抛异常
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.warn("数据源连接测试【失败】，驱动类：{} 加载失败", driverClass);

            // 连接失败
            return false;
        }

        try {
            // 若用户没有配置数据源，则使用默认的 C3P0 数据源
            druidDataSource = new DruidDataSource();
            druidDataSource.setDriverClassName(driverClass);
            druidDataSource.setUrl(url);
            druidDataSource.setUsername(username);
            druidDataSource.setPassword(password);

            //初始化连接池大小
            druidDataSource.setInitialSize(5);
            //设置最大的链接数
            druidDataSource.setMaxActive(10);
            //设置最小连接数
            druidDataSource.setMinIdle(1);
            //设置最大连接等待时间 3 秒
            druidDataSource.setLoginTimeout(3);
            // 获取一个connection的超时时间，单位毫秒
            druidDataSource.setMaxWait(2000);
            // 如果设为true那么在取得连接的同时将校验连接的有效性。Default: false
            druidDataSource.setTestOnReturn(true);
            // true表示pool向数据库请求连接失败后标记整个pool为block并close，
            // 就算后端数据库恢复正常也不进行重连，客户端对pool的请求都拒绝掉。
            // 否则测试失败时会一直抛异常
            druidDataSource.setBreakAfterAcquireFailure(true);

            if (log.isDebugEnabled()) {
                log.debug("数据源连接测试【开始】，连接超时时间为：{} 毫秒", druidDataSource.getMaxWait());
            }
            druidDataSource.getConnection();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("数据源连接测试【失败】，失败原因：{}", e.getMessage());
            }
            // 连接失败
            return false;
        } finally {
            if (druidDataSource != null) {
                druidDataSource.close();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("数据源连接测试【成功】");
        }
        // 连接不出异常就是成功
        return true;
    }

    /**
     * 获取当前数据源实例
     * @return DataSource
     */
    public static DataSource getCurrentDataSource() {
        String dataSourceKey = DynamicDataSourceContextHolder.getDataSourceKey();
        DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
        return dynamicDataSource.getDataSourceByKey(dataSourceKey);
    }

    /**
     * 获取当前线程的数据源类型
     *
     * @return 数据源类型，如: mysql、postsql
     */
    public static synchronized String getCurrentDataSourceType() {

        // 获取当前数据源 key
        String dataSourceKey = DynamicDataSourceContextHolder.getDataSourceKey();

        // 获取对应的数据源信息
        DruidDataSource dataSource = DynamicDataSource.getInstance().getDataSourceByKey(dataSourceKey);

        if (dataSource == null) {
            log.error("错误，数据源map中获取不到当前的数据源，key={}", dataSourceKey);
            throw new FindException("操作失败，当前数据源不存在或已被删除");
        }

        // 获取数据源连接url
        String url = dataSource.getUrl();

        // 获取数据源类型
        return DataSourceUtil.getTypeByUrl(url);
    }

    /**
     * 获取当前数据源的数据源DTO信息
     *
     * @return DataSourceDTO
     */
    public static synchronized DataSourceDTO getCurrentDataSourceDTO() {
        // 获取当前数据源 key
        String dataSourceKey = DynamicDataSourceContextHolder.getDataSourceKey();
        // 获取对应的数据源信息
        DruidDataSource dataSource = DynamicDataSource.getInstance().getDataSourceByKey(dataSourceKey);

        // 获取数据源信息
        String dataSourceType = getTypeByUrl(dataSource.getUrl());
        String ip = getIpByUrl(dataSource.getUrl());
        String port = getPortByUrl(dataSource.getUrl());
        String database = getDatabaseByUrl(dataSource.getUrl());

        // TODO：此处需要根据URL解析出对应的值
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        dataSourceDTO.setType(dataSourceType)
                .setIp(ip)
                .setPort(port)
                .setDatabase(database)
                .setDataSourceKey(dataSourceKey)
                .setUsername(dataSource.getUsername())
                .setPassword(dataSource.getPassword());

        return dataSourceDTO;
    }

    /**
     * 通过数据源连接 url 提取出数据库的类型
     *
     * @param url 数据源连接 url
     * @return 数据源类型
     */
    public static String getTypeByUrl(String url) {
        Assert.isTrue(!StringUtils.isEmpty(url), "url 不能为空");
        String type = url.substring(url.indexOf(':') + 1, url.length());
        type = type.substring(0, type.indexOf(':'));
        return type;
    }

    /**
     * 通过数据源连接 url 提取出数据库的ip
     *
     * @param url 数据源连接 url
     * @return 数据源类型
     */
    public static String getIpByUrl(String url) {
        Assert.isTrue(!StringUtils.isEmpty(url), "url 不能为空");
        return url.substring(url.indexOf("/") + 2, url.lastIndexOf(":"));
    }

    /**
     * 通过数据源连接 url 提取出数据库的端口号
     *
     * @param url 数据源连接 url
     * @return 数据源类型
     */
    public static String getPortByUrl(String url) {
        Assert.isTrue(!StringUtils.isEmpty(url), "url 不能为空");
        return url.substring(url.lastIndexOf(":") + 1, url.lastIndexOf("/"));
    }

    /**
     * 通过数据源连接 url 提取出此url链接的数据库
     *
     * @param url 数据源连接 url
     * @return 数据源类型
     */
    public static String getDatabaseByUrl(String url) {
        Assert.isTrue(!StringUtils.isEmpty(url), "url 不能为空");
        return url.substring(url.lastIndexOf(":") + 1, url.lastIndexOf("/"));
    }

    /**
     * 通过数据源类型获取数据源连接驱动类
     *
     * @param type 数据源类型
     * @return 返回驱动类的全类名
     */
    public static String getDriverClassByType(String type) {

        if (!DataSourceUtil.dataSourceMap.containsKey(type)) {
            throw new FindException("错误，未知的数据源类型：" + type);
        }

        if (DataSourceUtil.POSTGRESQL_NAME.equalsIgnoreCase(type)) {
            // postgre
            return DataSourceUtil.POSTGRESQL_DRIVER_CLASS;
        } else {
            // mysql
            return DataSourceUtil.MYSQL_DRIVER_CLASS;
        }
    }

    /**
     * 拼接数据源连接 url
     *
     * @param dto 数据源dto
     * @return 返回url
     */
    public static String getUrlByDataSourceDTO(DataSourceDTO dto) {

        if (!DataSourceUtil.dataSourceMap.containsKey(dto.getType())) {
            throw new FindException("错误，未知的数据源类型：" + dto.getType());
        }

        StringBuffer url = new StringBuffer();
        if (DataSourceUtil.POSTGRESQL_NAME.equalsIgnoreCase(dto.getType())) {
            // postgre
            url.append(DataSourceUtil.POSTGRESQL_CONNECTION_PREFIX)
                    .append(dto.getIp()).append(":")
                    .append(dto.getPort()).append("/")
                    .append(dto.getDatabase())
                    .append(DataSourceUtil.POSTGRESQL_CONNECTION_SUFFIX);
        } else {
            // mysql
            url.append(DataSourceUtil.MYSQL_CONNECTION_PREFIX)
                    .append(dto.getIp()).append(":")
                    .append(dto.getPort()).append("/")
                    .append(dto.getDatabase())
                    .append(DataSourceUtil.MYSQL_CONNECTION_SUFFIX);
        }
        return url.toString();
    }

    /**
     * 创建数据源对象
     *
     * @param dto 数据源dto
     * @return data source
     */
    public static synchronized DruidDataSource createDataSource(DataSourceDTO dto) {

        if (dto == null) {
            return null;
        }

        // 目前只支持 mysql 和 postgreSql
        if (!DataSourceUtil.dataSourceMap.containsKey(dto.getType())) {
            throw new FindException("错误，未知的数据源类型：" + dto.getType() + "，目前只支持 MySQL 和 PostgreSQL");
        }

        // 拼接URL
        String url = DataSourceUtil.getUrlByDataSourceDTO(dto);

        // 设置数据源的key，默认采用：jdbc的连接url（不带后缀） 作为key，如：jdbc:mysql://127.0.0.1:3306/test
        /*if (StringUtils.isEmpty(dto.getDataSourceKey())) {
            int i = url.indexOf("?");
            String key = url.substring(0,i == -1 ? url.length() : i);
            dto.setDataSourceKey(key);
        }*/

        // 不再支持手动指定 dataSourceKey
        int i = url.indexOf("?");
        String key = url.substring(0, i == -1 ? url.length() : i);
        dto.setDataSourceKey(key);

        // 判断储存动态数据源实例的map中key值是否存在
        if (DynamicDataSource.isExistDataSource(dto.getDataSourceKey())) {
            throw new DataSourceCreateException("数据源已存在，请指定key后重试");
        }

        DruidDataSource dataSource = new DruidDataSource();

        // 获取对应的驱动类
        String driverClass = DataSourceUtil.getDriverClassByType(dto.getType());

        if (log.isDebugEnabled()) {
            log.info("创建的数据源连接url = " + url);
        }

        dataSource.setUrl(url);
        dataSource.setUsername(dto.getUsername());
        dataSource.setPassword(dto.getPassword());
        dataSource.setDriverClassName(driverClass);

        //将创建的数据源，新增到targetDataSources中
        Map<Object, Object> map = new HashMap<>();
        map.put(dto.getDataSourceKey(), dataSource);
        DynamicDataSource.getInstance().setTargetDataSources(map);

        return dataSource;
    }
}