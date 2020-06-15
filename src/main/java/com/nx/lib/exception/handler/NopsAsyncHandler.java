package com.nx.lib.exception.handler;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import com.nx.lib.NopsUtil;
import com.nx.lib.exception.BaseException;

public class NopsAsyncHandler implements AsyncUncaughtExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        writeLog(ex);
    }

    private void writeLog(Throwable ex) {
        String cause = ex.getClass() == null ? "UNKNOWN" : ex.getClass().getName();
        if (ex instanceof BaseException) {
            BaseException e = (BaseException) ex;
            logger.warn("Template-LOG > |{}|{}|{}|{}| ", NopsUtil.getIpAddress(), e.getClass(), e.getCode(),
                    e.getMessage(), e);
        } else {
            logger.error("Template-LOG > |{}|{}|{}|", NopsUtil.getIpAddress(), cause, ex.getMessage(), ex);
        }
    }
}
