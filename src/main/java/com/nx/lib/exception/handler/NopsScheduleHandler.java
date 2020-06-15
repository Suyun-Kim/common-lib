package com.nx.lib.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

import com.nx.lib.exception.BaseException;

public class NopsScheduleHandler implements ErrorHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handleError(Throwable ex) {
        writeLog(ex);
    }

    private void writeLog(Throwable ex) {
        String cause = ex.getClass() == null ? "UNKNOWN" : ex.getClass().getName();
        if (ex instanceof BaseException) {
            BaseException e = (BaseException) ex;
            logger.warn("Template-LOG > |{}|{}|{}|{}| ", "0.0.0.0", e.getClass(), e.getCode(),
                    e.getMessage(), e);
        } else {
            logger.error("Template-LOG > |{}|{}|{}|", "0.0.0.0", cause, ex.getMessage(), ex);
        }
    }
}
