package com.mayuwan.miaosha.common;

public class RespBaseVo {
    protected int code;
    protected String msg;

    //各种码值信息
    //通用code
    public static RespBaseVo SUCCESS = new RespBaseVo(0,"成功");
    public static RespBaseVo SERVER_ERROR = new RespBaseVo(500100,"服务器错误");
    public static RespBaseVo BIND_ERROR = new RespBaseVo(500101,"参数校验异常，%s");
    public static RespBaseVo FRONTEND_ERROR = new RespBaseVo(500102,"前端传送数据错误");
    //登录模块5002XX
    public static RespBaseVo MOBILE_EMPTY = new RespBaseVo(500200,"手机号不能为空");
    public static RespBaseVo PASSWORD_EMPTY = new RespBaseVo(500201,"密码不能为空");
    public static RespBaseVo MOBILE_UNEXIST = new RespBaseVo(500202,"手机号码不存在");
    public static RespBaseVo MOBILE_FORMAT_ERROR = new RespBaseVo(500203,"手机号格式错误");
    public static RespBaseVo PASSWORD_ERROR = new RespBaseVo(500204,"密码错误");
    public static RespBaseVo NOT_LOGIN = new RespBaseVo(500205,"会话过期，请重新登录");

    //订单模块5003XX
    public static RespBaseVo ORDER_NOT_EXIST = new RespBaseVo(500300, "订单不存在");
    //秒杀模块5004XX
    public static RespBaseVo MIAOSHA_OVER = new RespBaseVo(500401,"秒杀结束");
    public static RespBaseVo MIAOSHA_REPEAT_ORDER = new RespBaseVo(500402,"不能重复下单");
    public static RespBaseVo REQUEST_ILLEGAL = new RespBaseVo(500403,"请求非法");
    public static RespBaseVo VERIFY_CODE = new RespBaseVo(500404,"验证码错误");
    public static RespBaseVo TOO_MUCH_REQUEST = new RespBaseVo(500404,"请求次数过多，请稍后重试");


    /**
     * 返回带有业务信息的错误异常*/
    public RespBaseVo fillArgs(Object... args){
        int code= this.code;
        String message = String.format(msg,args);
        return new RespBaseVo(code,message);
    }

    private RespBaseVo(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    protected RespBaseVo(){}

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
