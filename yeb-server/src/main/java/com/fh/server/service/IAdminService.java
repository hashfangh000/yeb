package com.fh.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fh.server.pojo.Admin;
import com.fh.server.pojo.RespBean;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fanghao
 * @since 2022-01-11
 */
public interface IAdminService extends IService<Admin> {

    /**
     * 登录之后返回token
     * @param username
     * @param password
     * @param request
     * @return
     */
    RespBean login(String username, String password, HttpServletRequest request);
}
