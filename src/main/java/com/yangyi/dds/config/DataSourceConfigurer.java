package com.yangyi.dds.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置类,在tomcat启动时触发，在该类中生成多个数据源实例并将其注入到 ApplicationContext 中
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/01
 */
@Configuration
@EnableConfigurationProperties(MybatisProperties.class)
public class DataSourceConfigurer {
    /**
     * 日志logger句柄
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 自动注入环境类，用于获取配置文件的属性值
     */
    @Autowired
    private Environment evn;

    /**
     * mybatis 配置信息
     */
    private MybatisProperties mybatisProperties;

    /**
     * 通过 @EnableConfigurationProperties(MybatisProperties.class) 注入 mybatis 配置信息
     * @param mybatisProperties mybatis 配置信息
     */
    public DataSourceConfigurer(MybatisProperties mybatisProperties) {
        this.mybatisProperties = mybatisProperties;
    }

    /**
     * 读取配置信息，创建数据源对象
     * @param dbType 数据库类型
     * @return data source
     */
    private DruidDataSource createDataSource(String dbType) {
        //如果不指定数据库类型，则使用默认数据库连接
        String dbName = dbType.trim().isEmpty() ? "default" : dbType.trim();

        DruidDataSource dataSource = new DruidDataSource();
        String prefix = "db." + dbName +".";

        String dbUrl = evn.getProperty( prefix + "url-base")
                        + evn.getProperty( prefix + "host") + ":"
                        + evn.getProperty( prefix + "port") + "/"
                        + evn.getProperty( prefix + "dbname") + evn.getProperty( prefix + "url-other");

        if (logger.isDebugEnabled()) {
            logger.info("默认创建的数据源连接 url：" + dbUrl);
        }

        dataSource.setUrl(dbUrl);
        dataSource.setUsername(evn.getProperty( prefix + "username"));
        dataSource.setPassword(evn.getProperty( prefix + "password"));
        dataSource.setDriverClassName(evn.getProperty( prefix + "driver-class-name"));

        return dataSource;
    }

    /**
     * spring boot 启动后将自定义创建好的数据源对象放到 TargetDataSources 中用于后续的切换数据源用
     *             (比如：DynamicDataSourceContextHolder.setDataSourceKey("dbMall")，手动切换到dbMall数据源
     * 同时指定默认数据源连接
     *
     * @return 动态数据源对象
     */
    @Bean
    public DynamicDataSource dynamicDataSource() {
        //获取动态数据库的实例（单例方式）
        DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();

        //创建默认数据库连接对象
        DruidDataSource defaultDataSource = createDataSource("default");

        Map<Object,Object> map = new HashMap<>();
        //自定义数据源key值，将创建好的数据源对象，赋值到targetDataSources中,用于切换数据源时指定对应key即可切换
        map.put("default", defaultDataSource);

        dynamicDataSource.setTargetDataSources(map);

        //设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);

        return dynamicDataSource;
    }

    /**
     *　配置 mybatis 的s qlSession 连接动态数据源
     *
     * @param dynamicDataSource 动态数据源
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("dynamicDataSource") DataSource dynamicDataSource)
            throws Exception {

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dynamicDataSource);
        bean.setMapperLocations(mybatisProperties.resolveMapperLocations());
        bean.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());
        bean.setConfiguration(mybatisProperties.getConfiguration());

        return bean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory)
            throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 将动态数据源添加到事务管理器中，并生成新的bean
     * @return the platform transaction manager
     */
    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dynamicDataSource());
    }
}
