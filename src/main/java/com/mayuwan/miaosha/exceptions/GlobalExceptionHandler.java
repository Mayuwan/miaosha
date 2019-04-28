package com.mayuwan.miaosha.exceptions;

import com.mayuwan.miaosha.common.RespBaseVo;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 全局异常处理拦截器
 * */
@ControllerAdvice//全局处理控制器里的异常，作用于@RequestMapping methods.
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public RespBaseVo exceptionHandler(HttpServletRequest requeset, Exception e){

        e.printStackTrace();//抛出异常，用于后台查日志

        if(e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return ex.getEx();
        }
        else if(e instanceof BindException){
            BindException bx = (BindException)e;
            List<ObjectError> objErrs=  bx.getAllErrors();
            ObjectError objectError =  objErrs.get(0);
            return RespBaseVo.BIND_ERROR.fillArgs(objectError.getDefaultMessage());
        }
        else{
            return RespBaseVo.SERVER_ERROR;
        }
    }
}
