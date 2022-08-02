/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.git.server.aspect;

import com.alibaba.nacos.common.model.RestResult;
import com.alibaba.nacos.config.server.exception.NacosWebException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 统一controller 请求拦截异常处理类.
 * @author yueshiqi
 */
@Aspect
@Order(0)
@Component
public class GitControllerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitControllerInterceptor.class);

    public static final String BASE_WEB_PACKAGE = "com.alibaba.nacos.git.server";

    @Autowired(required = false)
    private HttpServletResponse response;

    private static final String POINT_CUT = "execution( * " + BASE_WEB_PACKAGE + ".controller..*.*(..))";

    @Pointcut(POINT_CUT)
    public void controllerPoint() { }

    /**
     * controller aop .
     * @param joinPoint jp
     * @return Object Object
     * @throws Exception e
     */
    @Around("controllerPoint()")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Exception {

        Object returnObj = invokePoint(joinPoint);

        return returnObj;
    }

    /**
     * 拦截逻辑 .
     * @param joinPoint jp
     * @return Object
     * @throws Exception e
     */
    public Object invokePoint(ProceedingJoinPoint joinPoint) throws Exception {

        Object returnObj = null;
        Method method = null;
        String methodFullName = null;
        try {
            Signature sig = joinPoint.getSignature();

            MethodSignature msig = (MethodSignature) sig;
            Object target = joinPoint.getTarget();

            method = msig.getMethod();

            methodFullName = method.getDeclaringClass().getSimpleName() + "." + method.getName();

            //Object[] args = joinPoint.getArgs();

            //String paramsStr = showRequest(args);
            //LOGGER.debug("[Request] : {} => {}", methodFullName);
        } catch (Exception e) {
            LOGGER.error("invoke_joinPoint_error : ", e);
        }

        try {
            returnObj = joinPoint.proceed();
        } catch (Throwable t) {
            if (t instanceof NacosWebException) {
                LOGGER.warn("[ERROR CATCHED] - [" + methodFullName + "] : ", t);
            } else {
                LOGGER.error("[ERROR UNCATCHED] - [" + methodFullName + "] : ", t);
            }

            if (method != null) {
                Class<?> returnClass = method.getReturnType();
                if (returnClass != null && returnClass.isAssignableFrom(RestResult.class)) {
                    returnObj = NacosWebException.failResponse(t);
                }
            }
        }
        return returnObj;
    }

}
