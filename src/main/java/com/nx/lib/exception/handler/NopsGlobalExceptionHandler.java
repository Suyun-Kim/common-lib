package com.nx.lib.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nx.lib.NopsUtil;
import com.nx.lib.exception.BadRequestException;
import com.nx.lib.exception.BaseException;
import com.nx.lib.exception.ForbiddenException;
import com.nx.lib.exception.NoContentException;
import com.nx.lib.exception.ResourceNotFoundException;
import com.nx.lib.exception.UnAuthorizedException;
import com.nx.lib.obj.Error;
import com.nx.lib.obj.ResponseEnvelop;

@RestController
@ControllerAdvice
public class NopsGlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(NoContentException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public ResponseEnvelop<Void> handleNoContentException(NoContentException e) throws Exception {
        // 로그 기록
        this.writeLog(e);

        // 클라이언트 반환
        Error error = new Error(e.getCode(), e.getMessage());
        ResponseEnvelop<Void> returnEnvelop = new ResponseEnvelop<Void>(false, error);
        return returnEnvelop;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ResponseEnvelop<Void> handleBadRequestException(BadRequestException e) {
        // 로그 기록
        this.writeLog(e);

        // 클라이언트 반환
        Error error = new Error(e.getCode(), e.getMessage());
        ResponseEnvelop<Void> returnEnvelop = new ResponseEnvelop<Void>(false, error);
        return returnEnvelop;
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public ResponseEnvelop<Void> handleUnAuthorizedException(UnAuthorizedException e) {
        // 로그 기록
        this.writeLog(e);

        // 클라이언트 반환
        Error error = new Error(e.getCode(), e.getMessage());
        ResponseEnvelop<Void> returnEnvelop = new ResponseEnvelop<Void>(false, error);
        return returnEnvelop;
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public ResponseEnvelop<Void> handleUnAuthorizedException(ForbiddenException e) {
        // 로그 기록
        this.writeLog(e);

        // 클라이언트 반환
        Error error = new Error(e.getCode(), e.getMessage());
        ResponseEnvelop<Void> returnEnvelop = new ResponseEnvelop<Void>(false, error);
        return returnEnvelop;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ResponseEnvelop<Void> handleResourceNotFoundException(ResourceNotFoundException e) {
        // 로그 기록
        this.writeLog(e);

        // 클라이언트 반환
        Error error = new Error(e.getCode(), e.getMessage());
        ResponseEnvelop<Void> returnEnvelop = new ResponseEnvelop<Void>(false, error);
        return returnEnvelop;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ResponseEnvelop<Void> handleException(Exception e) {
        this.writeLog(e);

        // 클라이언트 반환
        Error error = new Error("0000", "Internal Server Error");
        ResponseEnvelop<Void> returnEnvelop = new ResponseEnvelop<Void>(false, error);
        return returnEnvelop;
    }

    private void writeLog(Exception ex) {
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