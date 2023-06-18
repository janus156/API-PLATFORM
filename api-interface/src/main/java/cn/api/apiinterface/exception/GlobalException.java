package cn.api.apiinterface.exception;

import cn.api.apiinterface.common.BaseResponse;
import cn.api.apiinterface.common.ErrorCode;
import cn.api.apiinterface.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessException(BusinessException e){
        log.info("businessException"+e.getMessage());
        return ResultUtils.error(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeException(RuntimeException e){
        log.info("runtimeException"+e.getMessage());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage());
    }
}
