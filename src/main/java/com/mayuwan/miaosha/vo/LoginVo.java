package com.mayuwan.miaosha.vo;

import com.mayuwan.miaosha.validator.IsMobile;

import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LoginVo {
    @NotNull
    @IsMobile //自定义注解
    private String mobile;
    @NotNull
    private String password;


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
