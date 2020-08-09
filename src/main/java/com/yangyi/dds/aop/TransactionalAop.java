package com.yangyi.dds.aop;

import com.yangyi.dds.util.DataSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
@Order(2)
@Aspect
@Component
public class TransactionalAop {

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    /**
     * 可重入锁
     */
    private Lock lock = new ReentrantLock();

    /**
     * 数据源切换表达式
     * 对 service.impl 包下的所有 public 方法都进行拦截，但排除 DataSourceServiceImpl.changeDataSource() 方法
     */
    @Pointcut("execution(* com.yangyi.dds.service.impl..*(..))")
    private static void transactional() {
    }

    /**
     * 处理数据源切换的前置通知
     *
     * @param joinPoint：可获取 类、方法、方法参数等信息的对象
     */
    @Around("transactional()")
    public Object transactionalAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 上锁
        lock.lock();
        try {
            // 返回结果
            Object result;
            // 获取方法信息
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            if (method.isAnnotationPresent(Transactional.class)) {
                // 进行手动事务处理
                // 事务管理器切换
                setTransactionManagerDataSource();

                // 事务属性，包括隔离级别、传播行为等。
                TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
                try {
                    // 执行目标方法
                    result = joinPoint.proceed();

                    // 事务手动提交
                    transactionManager.commit(status);
                } catch (Exception e) {
                    // 事务回滚
                    transactionManager.rollback(status);
                    throw e;
                }
                return result;
            } else {
                // 执行目标方法
                return joinPoint.proceed();
            }
        } finally {
            // 解锁
            lock.unlock();
        }
    }

    /**
     * 设置 事务管理器的 数据源对象
     */
    private void setTransactionManagerDataSource() {
        // 对事务管理器进行动态切换
        DataSource currentDataSource = DataSourceUtil.getCurrentDataSource();
        transactionManager.setDataSource(currentDataSource);
        transactionManager.afterPropertiesSet();
    }
}
