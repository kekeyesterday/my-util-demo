/**
 * aop 切面植入开始-结束日志
 */

package com.jimmy.common.util;  

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.saicmotor.telematics.framework.core.logger.Logger;
import com.saicmotor.telematics.framework.core.logger.LoggerFactory;
import com.zxq.iov.cloud.sp.parking.util.PkReqIdUtil;

/**
 *
 */
@Aspect
public class DaoLogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(DaoLogAspect.class);
    @Before("execution(* com.zxq.iov.cloud.sp.parking.dao..*DaoImpl.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        LOGGER.info(new StringBuilder(joinPoint.getTarget().getClass().getName())
        .append("****").append(joinPoint.getSignature().getName()).append(" start**** ==>reqId=").append(PkReqIdUtil.getReqId()).toString());
    }
    @After("execution(* com.zxq.iov.cloud.sp.parking.dao..*DaoImpl.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        LOGGER.info(new StringBuilder(joinPoint.getTarget().getClass().getName())
        .append("****").append(joinPoint.getSignature().getName()).append(" end**** ==>reqId=").append(PkReqIdUtil.getReqId()).toString());

    }
}
  
