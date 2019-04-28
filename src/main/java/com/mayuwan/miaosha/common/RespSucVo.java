package com.mayuwan.miaosha.common;

public class RespSucVo<T> extends RespBaseVo {
    private T data;

    public static <T> RespSucVo success(T data){
        return new RespSucVo(data);
    }

    private RespSucVo(T data){
        super();
        this.code = RespBaseVo.SUCCESS.getCode();
        this.msg = RespBaseVo.SUCCESS.getMsg();
        this.data = data;
    }

    public T getData() {
        return data;
    }

}
