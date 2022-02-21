package com.fh.server.controller;

import com.fh.server.pojo.Admin;
import com.fh.server.pojo.AdminLoginParam;
import com.fh.server.pojo.RespBean;
import com.fh.server.service.IAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * 登录控制层
 *
 * @author fanghao on 2022/1/12
 */
@Api(tags = "LoginController")
@RestController
public class LoginController {

    @Autowired
    private IAdminService adminService;

    @ApiOperation(value = "登录-返回token")
    @PostMapping("/login")
    public RespBean login(@RequestBody AdminLoginParam adminLoginParam, HttpServletRequest request) {
        return adminService.login(adminLoginParam.getUsername(), adminLoginParam.getPassword(), request);
    }


    @ApiOperation(value = "获取当前登录用户的信息")
    @GetMapping("/admin/info")
    public Admin getAdminInfo(Principal principal) { // 通过 Principal 对象，获取当前登录用户对象
        if (null == principal) {

            return null;
        }
        String username = principal.getName();
        Admin admin = adminService.getAdminByUserName(username); // 新建方法 getAdminByUserName 根据用户名获取用户
        admin.setPassword(null); // 安全起见，不给前端返回用户密码
        return admin;
    }


    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    public RespBean logout() {
        return RespBean.success("退出成功！");
    }


}