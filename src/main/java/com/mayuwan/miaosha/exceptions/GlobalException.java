package com.mayuwan.miaosha.exceptions;

import com.mayuwan.miaosha.common.RespBaseVo;
import org.omg.SendingContext.RunTime;

public class GlobalException extends RuntimeException {

    private RespBaseVo ex;
    public GlobalException(RespBaseVo respBaseVo){
        this.ex = respBaseVo;
    }

    public RespBaseVo getEx() {
        return ex;
    }
}
