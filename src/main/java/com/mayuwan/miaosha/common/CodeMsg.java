package com.mayuwan.miaosha.common;


public class CodeMsg {
    private int code;
    private String msg;

    public static CodeMsg SUCCESS = new CodeMsg(0,"成功");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务器异常");
//    public static CodeMsg SUCCESS = new CodeMsg(0,"成功");


    private CodeMsg(int code,String msg){
        this.code= code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
