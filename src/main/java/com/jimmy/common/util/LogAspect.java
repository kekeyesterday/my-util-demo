
package com.jimmy.common.util;  

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * service method切面,添加日志
 * 
 * @author zghdo
 *
 */

@Aspect
@Component
@Order(100)
public class LogAspect {
	static Logger  logger = LoggerFactory.getLogger(LogAspect.class);
	private static final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();  
	@Pointcut("execution(* com.zebra.carcloud.trip.service.*.*.*(..))")  
	public void executeService(){  
	} 
    @Before("executeService()")
    public void logBefore(JoinPoint joinPoint) {
    	try {
        	if(ReqIdUtil.getReqId() == null) {
        		ReqIdUtil.setReqId(ReqIdUtil.getReqIdForService());
        	}
        	StringBuilder startLog = new StringBuilder(joinPoint.getTarget().getClass().getName())
            .append(" ****").append(joinPoint.getSignature().getName()).append(" 方法执行开始...****   REQ_ID:").append(ReqIdUtil.getReqId()).append("  参数为:");
        	
        	//获取拦截接口方法
        	MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();  
            Method method = methodSignature.getMethod();
            String[] anonymousParams = null;
            if (method.isAnnotationPresent(ParamAnonymous.class)) {
            	ParamAnonymous data = method.getAnnotation(ParamAnonymous.class);
            	anonymousParams = data.params();
            }
            String[] paramName = parameterNameDiscoverer.getParameterNames(method);//getFieldsName(joinPoint.getTarget().getClass(),joinPoint.getTarget().getClass().getName(),method);
            Map<String,Object> paramMap = new HashMap<String,Object>();
            int idx = 0;
            Object[] values = joinPoint.getArgs();
            for(String name : paramName) {
            	if(anonymousParams == null || Arrays.binarySearch(anonymousParams, name) < 0) {
            		paramMap.put(name,values[idx++]);
            	}
            }
        	logger.info(startLog.append(StringUtil.getJsonByObj(paramMap)).toString());
        	startLog = null;
    	} catch(Exception e) {
    		
    	}

    }
    @After("executeService()")
    public void logAfter(JoinPoint joinPoint) {
    	logger.info(new StringBuilder(joinPoint.getTarget().getClass().getName())
        .append(" ****").append(joinPoint.getSignature().getName()).append(" 方法执行结束...****   REQ_ID:").append(ReqIdUtil.getReqId()).toString());
    }
    /** 
     * 得到方法参数的名称 
     * @throws NotFoundException 
     */  
//    private static String[] getFieldsName(Class<?> cls, String clazzName, String methodName) throws NotFoundException{  
//        ClassPool pool = ClassPool.getDefault();  
//        ClassClassPath classPath = new ClassClassPath(cls);  
//        pool.insertClassPath(classPath);  
//          
//        CtClass cc = pool.get(clazzName);  
//        CtMethod cm = cc.getDeclaredMethod(methodName);  
//        MethodInfo methodInfo = cm.getMethodInfo();  
//        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
//        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
//        String[] paramNames = new String[cm.getParameterTypes().length];  
//        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
//        for (int i = 0; i < paramNames.length; i++){  
//            paramNames[i] = attr.variableName(i + pos); 
//        }  
//        return paramNames;  
//    } 

}
  
