package com.yangyi.dds.aop;

import com.yangyi.dds.config.DynamicDataSourceContextHolder;
import com.yangyi.dds.domain.dto.DataSourceDTO;
import com.yangyi.dds.exception.DataSourceConnectionException;
import com.yangyi.dds.service.DataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 动态数据源切换AOP
 * 因在事务内部进行数据源切换会导致事务无法使用，为了使事务能正常工作，
 * 需要在进行方法事务之前先完成数据源切换功能
 * <p>
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/28
 */
@Slf4j
@Order(1)
@Aspect
@Component
public class DataSourceChangeAop {

    @Qualifier("dataSourceService")
    @Autowired
    private DataSourceService dataSourceService;

    /**
     * 数据源切换表达式
     * 对 service.impl 包下的所有 public 方法都进行拦截，但排除 DataSourceServiceImpl.changeDataSource() 方法
     */
    @Pointcut("execution(* com.yangyi.dds.service.impl..*(..))" +
            " && !execution(* com.yangyi.dds.service.impl.DataSourceServiceImpl.changeDataSource(..)) ")
    private static void dataSourceChange() {
    }

    /**
     * 处理数据源切换的前置通知
     *
     * @param joinPoint：可获取 类、方法、方法参数等信息的对象
     */
    @Around("dataSourceChange()")
    public Object dataSourceChangeAround(ProceedingJoinPoint joinPoint) throws Throwable {

        // 切换结果
        boolean change = false;
        try {
            //获取方法参数列表
            List<Object> args = Arrays.asList(joinPoint.getArgs());
            for (Object arg : args) {

                // 如果存在 数据源类型 的参数，则进行数据源切换
                if (arg instanceof DataSourceDTO) {

                    // 数据源切换
                    DataSourceDTO dataSourceDTO = (DataSourceDTO) arg;
                    change = dataSourceService.changeDataSource(dataSourceDTO);
                    if (!change) {
                        throw new DataSourceConnectionException("数据源切换失败");
                    }
                    break;
                }
            }
            // 执行目标方法
            return joinPoint.proceed();
        } finally {
            // 对进行数据源还原
            if (change) {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
    }
}
