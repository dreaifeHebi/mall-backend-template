package com.macro.mall.portal.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 用户注册参数
 * Created by macro on 2018/8/3.
 */
public class MemberRegisterParam {
    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    
    @ApiModelProperty(value = "手机号码")
    private String telephone;
    
    @ApiModelProperty(value = "邮箱地址", required = true)
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
